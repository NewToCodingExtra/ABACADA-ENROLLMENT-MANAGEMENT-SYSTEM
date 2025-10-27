package enrollmentsystem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Main auto-scheduling engine
 * Generates conflict-free schedules for courses
 */
public class AutoScheduler {
    
    private String semesterId;
    private List<TimeSlot> availableTimeSlots;
    private List<Room> availableRooms;
    private Map<String, List<String>> facultySchedule; // faculty_id -> list of time_slot_ids
    private Map<String, List<String>> roomSchedule; // room_id -> list of time_slot_ids
    private int successCount = 0;
    private int failCount = 0;
    private List<String> errors = new ArrayList<>();
    
    public AutoScheduler(String semesterId) {
        this.semesterId = semesterId;
        this.facultySchedule = new HashMap<>();
        this.roomSchedule = new HashMap<>();
    }
    
    /**
     * Main method to generate schedules
     */
    public boolean generateSchedules() {
        long startTime = System.currentTimeMillis();
        
        System.out.println("Starting schedule generation for semester: " + semesterId);
        
        // Load resources
        availableTimeSlots = TimeSlot.loadAll();
        availableRooms = Room.loadAll();
        
        if (availableTimeSlots.isEmpty() || availableRooms.isEmpty()) {
            System.err.println("No time slots or rooms available!");
            return false;
        }
        
        System.out.println("Loaded " + availableTimeSlots.size() + " time slots");
        System.out.println("Loaded " + availableRooms.size() + " rooms");
        
        // Get unscheduled course offerings
        List<CourseOffering> offerings = getUnscheduledOfferings();
        System.out.println("Found " + offerings.size() + " unscheduled offerings");
        
        if (offerings.isEmpty()) {
            System.out.println("No unscheduled offerings found!");
            return true;
        }
        
        // Sort offerings by priority (required courses, larger capacity first)
        offerings.sort((a, b) -> {
            // Priority: larger capacity first, then by course code
            int capacityCompare = Integer.compare(b.getCapacity(), a.getCapacity());
            if (capacityCompare != 0) return capacityCompare;
            return a.getCourseId().compareTo(b.getCourseId());
        });
        
        // Schedule each offering
        for (CourseOffering offering : offerings) {
            if (scheduleOffering(offering)) {
                successCount++;
                System.out.println("✓ Scheduled: " + offering.getOfferingId());
            } else {
                failCount++;
                String error = "✗ Failed to schedule: " + offering.getOfferingId() + 
                              " - No suitable time/room found";
                errors.add(error);
                System.err.println(error);
            }
        }
        
        long endTime = System.currentTimeMillis();
        int executionTime = (int) (endTime - startTime);
        
        // Log the generation
        logGeneration(offerings.size(), successCount, executionTime);
        
        System.out.println("\n=== Schedule Generation Complete ===");
        System.out.println("Total offerings: " + offerings.size());
        System.out.println("Scheduled: " + successCount);
        System.out.println("Failed: " + failCount);
        System.out.println("Execution time: " + executionTime + "ms");
        
        return failCount == 0;
    }
    
    /**
     * Schedule a single offering
     */
    private boolean scheduleOffering(CourseOffering offering) {
        // Get course requirements
        CourseRequirements requirements = getCourseRequirements(offering.getCourseId());
        
        // Get faculty availability
        List<TimeSlot> facultyAvailableSlots = getFacultyAvailableSlots(offering.getFacultyId());
        
        // Try each time slot
        for (TimeSlot timeSlot : availableTimeSlots) {
            // Check if faculty is available at this time
            if (!isFacultyAvailable(offering.getFacultyId(), timeSlot.getTimeSlotId())) {
                continue;
            }
            
            // Find suitable room
            Room suitableRoom = findSuitableRoom(timeSlot.getTimeSlotId(), 
                                                 requirements, 
                                                 offering.getCapacity());
            
            if (suitableRoom != null) {
                // Assign schedule
                boolean assigned = assignSchedule(offering, timeSlot, suitableRoom);
                if (assigned) {
                    // Mark as occupied
                    markFacultyOccupied(offering.getFacultyId(), timeSlot.getTimeSlotId());
                    markRoomOccupied(suitableRoom.getRoomId(), timeSlot.getTimeSlotId());
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Check if faculty is available at time slot
     */
    private boolean isFacultyAvailable(String facultyId, String timeSlotId) {
        if (!facultySchedule.containsKey(facultyId)) {
            return true;
        }
        return !facultySchedule.get(facultyId).contains(timeSlotId);
    }
    
    /**
     * Mark faculty as occupied at time slot
     */
    private void markFacultyOccupied(String facultyId, String timeSlotId) {
        facultySchedule.computeIfAbsent(facultyId, k -> new ArrayList<>()).add(timeSlotId);
    }
    
    /**
     * Mark room as occupied at time slot
     */
    private void markRoomOccupied(String roomId, String timeSlotId) {
        roomSchedule.computeIfAbsent(roomId, k -> new ArrayList<>()).add(timeSlotId);
    }
    
    /**
     * Find suitable room for the offering
     */
    private Room findSuitableRoom(String timeSlotId, CourseRequirements requirements, int capacity) {
        for (Room room : availableRooms) {
            // Check if room meets requirements
            if (!room.meetsRequirements(requirements.requiresLab, 
                                       requirements.requiresComputer, 
                                       capacity)) {
                continue;
            }
            
            // Check if room is available at this time
            if (!isRoomAvailable(room.getRoomId(), timeSlotId)) {
                continue;
            }
            
            return room;
        }
        return null;
    }
    
    /**
     * Check if room is available at time slot
     */
    private boolean isRoomAvailable(String roomId, String timeSlotId) {
        if (!roomSchedule.containsKey(roomId)) {
            return true;
        }
        return !roomSchedule.get(roomId).contains(timeSlotId);
    }
    
    /**
     * Assign schedule to offering in database
     */
    private boolean assignSchedule(CourseOffering offering, TimeSlot timeSlot, Room room) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            
            String sql = "UPDATE course_offerings SET " +
                        "time_slot_id = ?, room_id = ?, " +
                        "schedule_day = ?, schedule_time = ?, room = ?, " +
                        "is_auto_generated = 1 " +
                        "WHERE offering_id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, timeSlot.getTimeSlotId());
            pstmt.setString(2, room.getRoomId());
            pstmt.setString(3, timeSlot.getDayOfWeek());
            pstmt.setString(4, timeSlot.getSlotLabel());
            pstmt.setString(5, room.getRoomCode());
            pstmt.setString(6, offering.getOfferingId());
            
            int rows = pstmt.executeUpdate();
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error assigning schedule: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    /**
     * Get unscheduled course offerings for the semester
     */
    private List<CourseOffering> getUnscheduledOfferings() {
        List<CourseOffering> offerings = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return offerings;
            
            String sql = "SELECT * FROM course_offerings " +
                        "WHERE semester_id = ? AND status = 'Open' " +
                        "AND (time_slot_id IS NULL OR room_id IS NULL)";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, semesterId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                CourseOffering offering = new CourseOffering();
                offering.setOfferingId(rs.getString("offering_id"));
                offering.setCourseId(rs.getString("course_id"));
                offering.setFacultyId(rs.getString("faculty_id"));
                offering.setSemesterId(rs.getString("semester_id"));
                offering.setSectionId(rs.getString("section_id"));
                offering.setCapacity(rs.getInt("capacity"));
                offerings.add(offering);
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting unscheduled offerings: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return offerings;
    }
    
    /**
     * Get course requirements
     */
    private CourseRequirements getCourseRequirements(String courseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return new CourseRequirements();
            
            String sql = "SELECT requires_laboratory, requires_computer, min_room_capacity " +
                        "FROM courses WHERE course_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, courseId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new CourseRequirements(
                    rs.getBoolean("requires_laboratory"),
                    rs.getBoolean("requires_computer"),
                    rs.getInt("min_room_capacity")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getting course requirements: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return new CourseRequirements();
    }
    
    /**
     * Get faculty available time slots
     */
    private List<TimeSlot> getFacultyAvailableSlots(String facultyId) {
        List<TimeSlot> slots = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return slots;
            
            String sql = "SELECT ts.* FROM time_slots ts " +
                        "JOIN faculty_availability fa ON ts.day_of_week = fa.day_of_week " +
                        "WHERE fa.faculty_id = ? AND fa.is_unavailable = 0 " +
                        "AND ts.start_time >= fa.start_time AND ts.end_time <= fa.end_time";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, facultyId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                TimeSlot slot = new TimeSlot(
                    rs.getString("time_slot_id"),
                    rs.getString("day_of_week"),
                    rs.getTime("start_time").toLocalTime(),
                    rs.getTime("end_time").toLocalTime(),
                    rs.getString("slot_label"),
                    rs.getBoolean("is_available")
                );
                slots.add(slot);
            }
        } catch (SQLException e) {
            System.err.println("Error getting faculty availability: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        
        // If no availability defined, return all slots
        return slots.isEmpty() ? availableTimeSlots : slots;
    }
    
    /**
     * Log generation to database
     */
    private void logGeneration(int total, int scheduled, int executionTime) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return;
            
            String sql = "INSERT INTO schedule_generation_log " +
                        "(semester_id, generated_by, status, total_offerings, " +
                        "scheduled_offerings, conflicts_found, execution_time_ms, error_message) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, semesterId);
            pstmt.setInt(2, SessionManager.getInstance().getUserId());
            pstmt.setString(3, failCount == 0 ? "Success" : (successCount > 0 ? "Partial" : "Failed"));
            pstmt.setInt(4, total);
            pstmt.setInt(5, scheduled);
            pstmt.setInt(6, failCount);
            pstmt.setInt(7, executionTime);
            pstmt.setString(8, errors.isEmpty() ? null : String.join("; ", errors));
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.err.println("Error logging generation: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
    }
    
    // Inner class for course requirements
    private static class CourseRequirements {
        boolean requiresLab;
        boolean requiresComputer;
        int minCapacity;
        
        CourseRequirements() {
            this.requiresLab = false;
            this.requiresComputer = false;
            this.minCapacity = 30;
        }
        
        CourseRequirements(boolean requiresLab, boolean requiresComputer, int minCapacity) {
            this.requiresLab = requiresLab;
            this.requiresComputer = requiresComputer;
            this.minCapacity = minCapacity;
        }
    }
    
    // Inner class for course offering
    private static class CourseOffering {
        private String offeringId;
        private String courseId;
        private String facultyId;
        private String semesterId;
        private String sectionId;
        private int capacity;
        
        // Getters and setters
        public String getOfferingId() { return offeringId; }
        public void setOfferingId(String offeringId) { this.offeringId = offeringId; }
        
        public String getCourseId() { return courseId; }
        public void setCourseId(String courseId) { this.courseId = courseId; }
        
        public String getFacultyId() { return facultyId; }
        public void setFacultyId(String facultyId) { this.facultyId = facultyId; }
        
        public String getSemesterId() { return semesterId; }
        public void setSemesterId(String semesterId) { this.semesterId = semesterId; }
        
        public String getSectionId() { return sectionId; }
        public void setSectionId(String sectionId) { this.sectionId = sectionId; }
        
        public int getCapacity() { return capacity; }
        public void setCapacity(int capacity) { this.capacity = capacity; }
    }
}