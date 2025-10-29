package enrollmentsystem;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Enhanced AutoScheduler with Fair Faculty Load Distribution
 * Ensures every active faculty member gets assigned classes
 */
public class FairAutoScheduler {
    
    private String semesterId;
    private List<TimeSlot> availableTimeSlots;
    private List<Room> availableRooms;
    private Map<String, List<String>> facultySchedule;
    private Map<String, List<String>> roomSchedule;
    private Map<String, List<String>> sectionSchedule;
    private Map<String, Integer> facultyLoadCount; // Track faculty teaching load
    private Map<String, FacultyInfo> facultyInfoMap;
    private int successCount = 0;
    private int failCount = 0;
    private List<String> errors = new ArrayList<>();
    private List<String> warnings = new ArrayList<>();
    
    public FairAutoScheduler(String semesterId) {
        this.semesterId = semesterId;
        this.facultySchedule = new HashMap<>();
        this.roomSchedule = new HashMap<>();
        this.sectionSchedule = new HashMap<>();
        this.facultyLoadCount = new HashMap<>();
        this.facultyInfoMap = new HashMap<>();
    }
    
    public boolean generateSchedules() {
        long startTime = System.currentTimeMillis();
        
        System.out.println("\n=== FAIR AUTO SCHEDULER STARTED ===");
        System.out.println("Semester: " + semesterId);
        System.out.println("Start time: " + LocalDateTime.now());
        
        // Load resources
        availableTimeSlots = TimeSlot.loadAll();
        availableRooms = Room.loadAll();
        
        if (availableTimeSlots.isEmpty() || availableRooms.isEmpty()) {
            System.err.println("ERROR: No time slots or rooms available!");
            return false;
        }
        
        System.out.println("\nResources loaded:");
        System.out.println("  - Time slots: " + availableTimeSlots.size());
        System.out.println("  - Rooms: " + availableRooms.size());
        
        loadFacultyInfo();
        System.out.println("  - Active faculty: " + facultyInfoMap.size());
        
        loadExistingSchedules();
        
        List<CourseOffering> offerings = getUnscheduledOfferings();
        System.out.println("  - Unscheduled offerings: " + offerings.size());
        
        if (offerings.isEmpty()) {
            System.out.println("\nNo unscheduled offerings found!");
            checkFacultyDistribution();
            return true;
        }
        
        System.out.println("\n=== PHASE 1: Priority Assignment (Faculty with 0 classes) ===");
        List<CourseOffering> unassignedFacultyOfferings = 
            prioritizeUnassignedFaculty(offerings);
        
        if (!unassignedFacultyOfferings.isEmpty()) {
            System.out.println("Prioritizing " + unassignedFacultyOfferings.size() + 
                             " offerings for underloaded faculty");
            scheduleOfferings(unassignedFacultyOfferings);
        }
   
        System.out.println("\n=== PHASE 2: Fair Load Distribution ===");
        offerings = getUnscheduledOfferings(); 
        
        if (!offerings.isEmpty()) {
            offerings.sort((a, b) -> {
                int loadA = facultyLoadCount.getOrDefault(a.getFacultyId(), 0);
                int loadB = facultyLoadCount.getOrDefault(b.getFacultyId(), 0);
                
                if (loadA != loadB) {
                    return Integer.compare(loadA, loadB);
                }
                
                CourseRequirements reqA = getCourseRequirements(a.getCourseId());
                CourseRequirements reqB = getCourseRequirements(b.getCourseId());
                int labCompare = Boolean.compare(reqB.requiresLab, reqA.requiresLab);
                if (labCompare != 0) return labCompare;
                
                return Integer.compare(b.getCapacity(), a.getCapacity());
            });
            
            scheduleOfferings(offerings);
        }
        
        long endTime = System.currentTimeMillis();
        int executionTime = (int) (endTime - startTime);
        
        logGeneration(offerings.size(), successCount, executionTime);
        
        printSummary(executionTime);
        
        checkFacultyDistribution();
        
        return failCount == 0;
    }
    
    private void loadFacultyInfo() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT f.faculty_id, f.first_name, f.last_name, " +
                          "f.department, f.specialization, f.max_teaching_hours, " +
                          "COUNT(DISTINCT co.offering_id) as current_load " +
                          "FROM faculty f " +
                          "LEFT JOIN course_offerings co ON f.faculty_id = co.faculty_id " +
                          "  AND co.semester_id = ? AND co.status = 'Open' " +
                          "JOIN users u ON f.user_id = u.user_id " +
                          "WHERE u.is_active = 1 " +
                          "GROUP BY f.faculty_id";
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, semesterId);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    FacultyInfo info = new FacultyInfo(
                        rs.getString("faculty_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("department"),
                        rs.getString("specialization"),
                        rs.getInt("max_teaching_hours"),
                        rs.getInt("current_load")
                    );
                    facultyInfoMap.put(info.facultyId, info);
                    facultyLoadCount.put(info.facultyId, info.currentLoad);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading faculty info: " + e.getMessage());
        }
    }
  
    private List<CourseOffering> prioritizeUnassignedFaculty(List<CourseOffering> offerings) {
        Set<String> facultyWithClasses = new HashSet<>(facultyLoadCount.keySet());
        facultyWithClasses.removeIf(id -> facultyLoadCount.get(id) == 0);
        
        List<CourseOffering> priorityOfferings = new ArrayList<>();
        for (CourseOffering offering : offerings) {
            int load = facultyLoadCount.getOrDefault(offering.getFacultyId(), 0);
            if (load == 0) {
                priorityOfferings.add(offering);
            }
        }
    
        priorityOfferings.sort((a, b) -> Integer.compare(b.getCapacity(), a.getCapacity()));
        
        return priorityOfferings;
    }
    
    private void scheduleOfferings(List<CourseOffering> offerings) {
        int processed = 0;
        
        for (CourseOffering offering : offerings) {
            processed++;
            System.out.print("\r  [" + processed + "/" + offerings.size() + "] Processing...");
            
            if (scheduleOffering(offering)) {
                successCount++;
                String facultyId = offering.getFacultyId();
                facultyLoadCount.put(facultyId, facultyLoadCount.getOrDefault(facultyId, 0) + 1);
            } else {
                failCount++;
                String error = "Failed: " + offering.getOfferingId() + 
                              " (Faculty: " + offering.getFacultyId() + 
                              ", Course: " + offering.getCourseId() + 
                              ", Section: " + offering.getSectionId() + ")";
                errors.add(error);
            }
        }
        
        System.out.println();  
    }
    
    private void checkFacultyDistribution() {
        System.out.println("\n=== FACULTY TEACHING LOAD DISTRIBUTION ===");
        
        int totalFaculty = facultyInfoMap.size();
        int facultyWithClasses = 0;
        int facultyWithoutClasses = 0;
        int totalClasses = 0;
        int minLoad = Integer.MAX_VALUE;
        int maxLoad = 0;
        
        List<FacultyLoadReport> reports = new ArrayList<>();
        
        for (FacultyInfo info : facultyInfoMap.values()) {
            int load = facultyLoadCount.getOrDefault(info.facultyId, 0);
            totalClasses += load;
            
            if (load > 0) {
                facultyWithClasses++;
                minLoad = Math.min(minLoad, load);
                maxLoad = Math.max(maxLoad, load);
            } else {
                facultyWithoutClasses++;
            }
            
            reports.add(new FacultyLoadReport(info, load));
        }
        
        if (minLoad == Integer.MAX_VALUE) minLoad = 0;
        
        double avgLoad = totalFaculty > 0 ? (double) totalClasses / totalFaculty : 0;
        
        // Print statistics
        System.out.println("Total Faculty: " + totalFaculty);
        System.out.println("Faculty with classes: " + facultyWithClasses);
        System.out.println("Faculty WITHOUT classes: " + facultyWithoutClasses);
        System.out.println("Total classes assigned: " + totalClasses);
        System.out.println("Average load: " + String.format("%.2f", avgLoad) + " classes/faculty");
        System.out.println("Load range: " + minLoad + " - " + maxLoad + " classes");
        
        // Print detailed report
        System.out.println("\nDetailed Faculty Load Report:");
        System.out.println("─".repeat(80));
        System.out.printf("%-15s %-25s %-20s %10s%n", 
                         "Faculty ID", "Name", "Department", "Classes");
        System.out.println("─".repeat(80));
        
        reports.sort((a, b) -> Integer.compare(a.load, b.load));
        
        for (FacultyLoadReport report : reports) {
            String status = report.load == 0 ? " ⚠ NO CLASSES" : "";
            System.out.printf("%-15s %-25s %-20s %10d%s%n",
                             report.info.facultyId,
                             truncate(report.info.name, 25),
                             truncate(report.info.department, 20),
                             report.load,
                             status);
            
            if (report.load == 0) {
                warnings.add("Faculty " + report.info.facultyId + " (" + 
                           report.info.name + ") has NO classes assigned");
            }
        }
        System.out.println("─".repeat(80));
        
        if (!warnings.isEmpty()) {
            System.out.println("\n⚠ WARNINGS:");
            for (String warning : warnings) {
                System.out.println("  - " + warning);
            }
            
            System.out.println("\nRECOMMENDATIONS:");
            System.out.println("  1. Review course offerings - add more offerings for underutilized faculty");
            System.out.println("  2. Check faculty specializations match available courses");
            System.out.println("  3. Consider adjusting faculty availability schedules");
            System.out.println("  4. Verify faculty are not marked as inactive");
        }
    }
    
    private String truncate(String str, int maxLen) {
        if (str == null) return "";
        return str.length() <= maxLen ? str : str.substring(0, maxLen - 3) + "...";
    }
    
    private void loadExistingSchedules() {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT faculty_id, room_id, section_id, time_slot_id " +
                          "FROM course_offerings " +
                          "WHERE semester_id = ? AND time_slot_id IS NOT NULL " +
                          "AND status = 'Open'";
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, semesterId);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    String facultyId = rs.getString("faculty_id");
                    String roomId = rs.getString("room_id");
                    String sectionId = rs.getString("section_id");
                    String timeSlotId = rs.getString("time_slot_id");
                    
                    if (facultyId != null && timeSlotId != null) {
                        markFacultyOccupied(facultyId, timeSlotId);
                    }
                    if (roomId != null && timeSlotId != null) {
                        markRoomOccupied(roomId, timeSlotId);
                    }
                    if (sectionId != null && timeSlotId != null) {
                        markSectionOccupied(sectionId, timeSlotId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading existing schedules: " + e.getMessage());
        }
    }
  
    private boolean scheduleOffering(CourseOffering offering) {
        CourseRequirements requirements = getCourseRequirements(offering.getCourseId());
        List<TimeSlot> facultyAvailableSlots = getFacultyAvailableSlots(offering.getFacultyId());
        
        List<TimeSlot> slotsToTry = facultyAvailableSlots.isEmpty() ? 
                                    availableTimeSlots : facultyAvailableSlots;
        
        for (TimeSlot timeSlot : slotsToTry) {
            if (!isFacultyAvailable(offering.getFacultyId(), timeSlot.getTimeSlotId())) {
                continue;
            }
            
            if (!isSectionAvailable(offering.getSectionId(), timeSlot.getTimeSlotId())) {
                continue;
            }
            
            Room suitableRoom = findSuitableRoom(timeSlot.getTimeSlotId(), 
                                                 requirements, 
                                                 offering.getCapacity());
            
            if (suitableRoom != null) {
                boolean assigned = assignSchedule(offering, timeSlot, suitableRoom);
                if (assigned) {
                    markFacultyOccupied(offering.getFacultyId(), timeSlot.getTimeSlotId());
                    markRoomOccupied(suitableRoom.getRoomId(), timeSlot.getTimeSlotId());
                    markSectionOccupied(offering.getSectionId(), timeSlot.getTimeSlotId());
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean isFacultyAvailable(String facultyId, String timeSlotId) {
        if (facultyId == null) return true;
        if (!facultySchedule.containsKey(facultyId)) return true;
        return !facultySchedule.get(facultyId).contains(timeSlotId);
    }

    private void markFacultyOccupied(String facultyId, String timeSlotId) {
        if (facultyId == null) return;
        facultySchedule.computeIfAbsent(facultyId, k -> new ArrayList<>()).add(timeSlotId);
    }
    
    private boolean isSectionAvailable(String sectionId, String timeSlotId) {
        if (sectionId == null) return true;
        if (!sectionSchedule.containsKey(sectionId)) return true;
        return !sectionSchedule.get(sectionId).contains(timeSlotId);
    }
    
    private void markSectionOccupied(String sectionId, String timeSlotId) {
        if (sectionId == null) return;
        sectionSchedule.computeIfAbsent(sectionId, k -> new ArrayList<>()).add(timeSlotId);
    }
    
    private void markRoomOccupied(String roomId, String timeSlotId) {
        if (roomId == null) return;
        roomSchedule.computeIfAbsent(roomId, k -> new ArrayList<>()).add(timeSlotId);
    }
    
    private boolean isRoomAvailable(String roomId, String timeSlotId) {
        if (!roomSchedule.containsKey(roomId)) return true;
        return !roomSchedule.get(roomId).contains(timeSlotId);
    }
    
    private Room findSuitableRoom(String timeSlotId, CourseRequirements requirements, int capacity) {
        for (Room room : availableRooms) {
            if (!room.meetsRequirements(requirements.requiresLab, 
                                       requirements.requiresComputer, 
                                       capacity)) {
                continue;
            }
            
            if (!isRoomAvailable(room.getRoomId(), timeSlotId)) {
                continue;
            }
            
            return room;
        }
        return null;
    }
    
    private boolean assignSchedule(CourseOffering offering, TimeSlot timeSlot, Room room) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return false;
            
            String sql = "UPDATE course_offerings SET " +
                        "time_slot_id = ?, room_id = ?, " +
                        "schedule_day = ?, schedule_time = ?, room = ?, " +
                        "is_auto_generated = 1 " +
                        "WHERE offering_id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, timeSlot.getTimeSlotId());
                pstmt.setString(2, room.getRoomId());
                pstmt.setString(3, timeSlot.getDayOfWeek());
                pstmt.setString(4, timeSlot.getStartTime() + "-" + timeSlot.getEndTime());
                pstmt.setString(5, room.getRoomCode());
                pstmt.setString(6, offering.getOfferingId());
                
                return pstmt.executeUpdate() > 0;
            }
            
        } catch (SQLException e) {
            System.err.println("Error assigning schedule: " + e.getMessage());
            return false;
        }
    }
    
    private List<CourseOffering> getUnscheduledOfferings() {
        List<CourseOffering> offerings = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return offerings;
            
            String sql = "SELECT * FROM course_offerings " +
                        "WHERE semester_id = ? AND status = 'Open' " +
                        "AND (time_slot_id IS NULL OR room_id IS NULL)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
            }
            
        } catch (SQLException e) {
            System.err.println("Error getting unscheduled offerings: " + e.getMessage());
        }
        
        return offerings;
    }
    
    private CourseRequirements getCourseRequirements(String courseId) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return new CourseRequirements();
            
            String sql = "SELECT requires_laboratory, requires_computer, min_room_capacity " +
                        "FROM courses WHERE course_id = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, courseId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    return new CourseRequirements(
                        rs.getBoolean("requires_laboratory"),
                        rs.getBoolean("requires_computer"),
                        rs.getInt("min_room_capacity")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting course requirements: " + e.getMessage());
        }
        return new CourseRequirements();
    }
    
    private List<TimeSlot> getFacultyAvailableSlots(String facultyId) {
        List<TimeSlot> slots = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return slots;
            
            String sql = "SELECT DISTINCT ts.* FROM time_slots ts " +
                        "JOIN faculty_availability fa ON ts.day_of_week = fa.day_of_week " +
                        "WHERE fa.faculty_id = ? AND fa.is_unavailable = 0 " +
                        "AND ts.start_time >= fa.start_time AND ts.end_time <= fa.end_time " +
                        "AND ts.is_available = 1";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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
            }
        } catch (SQLException e) {
            System.err.println("Error getting faculty availability: " + e.getMessage());
        }
        
        return slots;
    }
    
    private void logGeneration(int total, int scheduled, int executionTime) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return;
            
            String sql = "INSERT INTO schedule_generation_log " +
                        "(semester_id, generated_by, status, total_offerings, " +
                        "scheduled_offerings, conflicts_found, execution_time_ms, error_message) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, semesterId);
                pstmt.setInt(2, SessionManager.getInstance().getUserId());
                pstmt.setString(3, failCount == 0 ? "Success" : (successCount > 0 ? "Partial" : "Failed"));
                pstmt.setInt(4, total);
                pstmt.setInt(5, scheduled);
                pstmt.setInt(6, failCount);
                pstmt.setInt(7, executionTime);
                
                String errorMsg = errors.isEmpty() ? null : String.join("; ", errors);
                if (!warnings.isEmpty()) {
                    errorMsg = (errorMsg != null ? errorMsg + "; " : "") + 
                              "Warnings: " + String.join("; ", warnings);
                }
                pstmt.setString(8, errorMsg);
                
                pstmt.executeUpdate();
            }
            
        } catch (SQLException e) {
            System.err.println("Error logging generation: " + e.getMessage());
        }
    }
    
    private void printSummary(int executionTime) {
        System.out.println("\n=== SCHEDULE GENERATION COMPLETE ===");
        System.out.println("✓ Scheduled: " + successCount);
        System.out.println("✗ Failed: " + failCount);
        System.out.println("Execution time: " + executionTime + "ms (" + 
                          String.format("%.2f", executionTime / 1000.0) + "s)");
        
        if (!errors.isEmpty()) {
            System.out.println("\n=== FAILED OFFERINGS ===");
            errors.forEach(System.err::println);
        }
    }
    
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
    
    private static class CourseOffering {
        private String offeringId;
        private String courseId;
        private String facultyId;
        private String semesterId;
        private String sectionId;
        private int capacity;
        
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
    
    private static class FacultyInfo {
        String facultyId;
        String name;
        String department;
        String specialization;
        int maxTeachingHours;
        int currentLoad;
        
        FacultyInfo(String facultyId, String name, String department, 
                   String specialization, int maxTeachingHours, int currentLoad) {
            this.facultyId = facultyId;
            this.name = name;
            this.department = department;
            this.specialization = specialization;
            this.maxTeachingHours = maxTeachingHours;
            this.currentLoad = currentLoad;
        }
    }
    
    private static class FacultyLoadReport {
        FacultyInfo info;
        int load;
        
        FacultyLoadReport(FacultyInfo info, int load) {
            this.info = info;
            this.load = load;
        }
    }
}