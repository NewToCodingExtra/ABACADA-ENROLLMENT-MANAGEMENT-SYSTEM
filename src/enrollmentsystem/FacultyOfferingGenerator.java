package enrollmentsystem;

import java.sql.*;
import java.util.*;

/**
 * Utility to automatically create course offerings for faculty who have none
 * Ensures every active faculty member gets assigned courses based on their specialization
 */
public class FacultyOfferingGenerator {
    
    private String semesterId;
    private Map<String, List<String>> facultySpecializations;
    private Map<String, Integer> facultyCurrentLoad;
    private int minLoadPerFaculty = 2; // Minimum classes per faculty
    private int maxLoadPerFaculty = 6; // Maximum classes per faculty
    
    public FacultyOfferingGenerator(String semesterId) {
        this.semesterId = semesterId;
        this.facultySpecializations = new HashMap<>();
        this.facultyCurrentLoad = new HashMap<>();
    }
    
    /**
     * Generate course offerings for underutilized faculty
     */
    public boolean generateOfferingsForFaculty() {
        System.out.println("\n=== FACULTY OFFERING GENERATOR ===");
        System.out.println("Semester: " + semesterId);
        System.out.println("Min load per faculty: " + minLoadPerFaculty);
        System.out.println("Max load per faculty: " + maxLoadPerFaculty);
        
        // Step 1: Analyze current faculty loads
        Map<String, FacultyLoad> facultyLoads = analyzeFacultyLoads();
        
        if (facultyLoads.isEmpty()) {
            System.out.println("No active faculty found!");
            return false;
        }
        
        System.out.println("\nFound " + facultyLoads.size() + " active faculty members");
        
        // Step 2: Identify underutilized faculty
        List<FacultyLoad> underutilized = new ArrayList<>();
        for (FacultyLoad load : facultyLoads.values()) {
            if (load.currentLoad < minLoadPerFaculty) {
                underutilized.add(load);
            }
        }
        
        if (underutilized.isEmpty()) {
            System.out.println("All faculty have adequate teaching loads!");
            return true;
        }
        
        System.out.println("Found " + underutilized.size() + " underutilized faculty members");
        
        // Step 3: Get available sections that need instructors
        List<SectionNeed> sectionNeeds = getSectionNeeds();
        
        if (sectionNeeds.isEmpty()) {
            System.out.println("\nNo available sections need instructors.");
            System.out.println("Consider creating more section/course combinations.");
            return false;
        }
        
        System.out.println("Found " + sectionNeeds.size() + " section/course combinations available");
        
        // Step 4: Assign offerings to underutilized faculty
        int offeringsCreated = assignOfferingsToFaculty(underutilized, sectionNeeds);
        
        System.out.println("\n=== GENERATION COMPLETE ===");
        System.out.println("Created " + offeringsCreated + " new course offerings");
        
        return offeringsCreated > 0;
    }
    
    /**
     * Analyze current faculty teaching loads
     */
    private Map<String, FacultyLoad> analyzeFacultyLoads() {
        Map<String, FacultyLoad> loads = new HashMap<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT f.faculty_id, f.first_name, f.last_name, " +
                          "f.department, f.specialization, f.max_teaching_hours, " +
                          "COUNT(DISTINCT co.offering_id) as current_load " +
                          "FROM faculty f " +
                          "LEFT JOIN course_offerings co ON f.faculty_id = co.faculty_id " +
                          "  AND co.semester_id = ? AND co.status = 'Open' " +
                          "JOIN users u ON f.user_id = u.user_id " +
                          "WHERE u.is_active = 1 " +
                          "GROUP BY f.faculty_id " +
                          "ORDER BY current_load ASC";
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, semesterId);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    String facultyId = rs.getString("faculty_id");
                    FacultyLoad load = new FacultyLoad(
                        facultyId,
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("department"),
                        rs.getString("specialization"),
                        rs.getInt("max_teaching_hours"),
                        rs.getInt("current_load")
                    );
                    loads.put(facultyId, load);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error analyzing faculty loads: " + e.getMessage());
        }
        
        return loads;
    }
    
    /**
     * Get sections and courses that need instructors
     */
    private List<SectionNeed> getSectionNeeds() {
        List<SectionNeed> needs = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection()) {
            // Find courses for the program year levels that don't have enough sections
            String query = "SELECT c.course_id, c.course_code, c.course_title, " +
                          "c.program_id, c.year_level, c.semester, " +
                          "s.section_id, s.section_name, " +
                          "COUNT(co.offering_id) as offering_count " +
                          "FROM courses c " +
                          "CROSS JOIN section s " +
                          "LEFT JOIN course_offerings co ON c.course_id = co.course_id " +
                          "  AND s.section_id = co.section_id " +
                          "  AND co.semester_id = ? " +
                          "WHERE c.program_id = s.program_id " +
                          "  AND c.semester = (SELECT name FROM semester WHERE semester_id = ?) " +
                          "GROUP BY c.course_id, s.section_id " +
                          "HAVING offering_count = 0 " +
                          "ORDER BY c.year_level, c.course_code";
            
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, semesterId);
                ps.setString(2, semesterId);
                ResultSet rs = ps.executeQuery();
                
                while (rs.next()) {
                    SectionNeed need = new SectionNeed(
                        rs.getString("course_id"),
                        rs.getString("course_code"),
                        rs.getString("course_title"),
                        rs.getString("section_id"),
                        rs.getString("section_name"),
                        rs.getString("program_id"),
                        rs.getInt("year_level")
                    );
                    needs.add(need);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting section needs: " + e.getMessage());
        }
        
        return needs;
    }
    
    /**
     * Assign course offerings to underutilized faculty
     */
    private int assignOfferingsToFaculty(List<FacultyLoad> underutilized, List<SectionNeed> needs) {
        int created = 0;
        
        // Sort faculty by current load (ascending) - most underutilized first
        underutilized.sort((a, b) -> Integer.compare(a.currentLoad, b.currentLoad));
        
        System.out.println("\n=== ASSIGNING OFFERINGS ===");
        
        for (FacultyLoad faculty : underutilized) {
            int neededClasses = minLoadPerFaculty - faculty.currentLoad;
            
            if (neededClasses <= 0) continue;
            
            System.out.println("\nFaculty: " + faculty.name + " (" + faculty.facultyId + ")");
            System.out.println("  Current load: " + faculty.currentLoad);
            System.out.println("  Needs: " + neededClasses + " more classes");
            System.out.println("  Department: " + faculty.department);
            System.out.println("  Specialization: " + faculty.specialization);
            
            int assigned = 0;
            
            Iterator<SectionNeed> iterator = needs.iterator();
            while (iterator.hasNext() && assigned < neededClasses) {
                SectionNeed need = iterator.next();
                
                if (matchesFacultyExpertise(faculty, need)) {
                    String offeringId = createCourseOffering(faculty.facultyId, need);
                    
                    if (offeringId != null) {
                        System.out.println("  ✓ Assigned: " + need.courseCode + 
                                         " (" + need.sectionName + ")");
                        iterator.remove(); 
                        assigned++;
                        created++;
                    }
                }
            }
            
            if (assigned < neededClasses) {
                System.out.println("  ⚠ Only assigned " + assigned + "/" + neededClasses + 
                                 " classes (no matching courses available)");
            }
        }
        
        return created;
    }
    
    private boolean matchesFacultyExpertise(FacultyLoad faculty, SectionNeed need) {
        String coursePrefix = need.courseCode.replaceAll("[^A-Z]", "");
        
        // Check department
        if (faculty.department != null) {
            if (faculty.department.toLowerCase().contains("computer") && 
                (coursePrefix.contains("IT") || coursePrefix.contains("CS") || 
                 coursePrefix.contains("COMSCI"))) {
                return true;
            }
            
            if (faculty.department.toLowerCase().contains("education") && 
                (coursePrefix.contains("ED") || coursePrefix.contains("ELEM"))) {
                return true;
            }
        }
        
        if (faculty.specialization != null) {
            String spec = faculty.specialization.toLowerCase();
            String courseTitle = need.courseTitle.toLowerCase();
            
            if (spec.contains("database") && courseTitle.contains("database")) return true;
            if (spec.contains("network") && courseTitle.contains("network")) return true;
            if (spec.contains("programming") && courseTitle.contains("programming")) return true;
            if (spec.contains("web") && courseTitle.contains("web")) return true;
            if (spec.contains("software") && courseTitle.contains("software")) return true;
        }
        
        // Default: accept any IT/CS course for IT department faculty
        if (faculty.department != null && 
            faculty.department.toLowerCase().contains("information")) {
            return coursePrefix.contains("IT") || coursePrefix.contains("CS");
        }
        
        return false;
    }
    
    private String createCourseOffering(String facultyId, SectionNeed need) {
        try (Connection conn = DBConnection.getConnection()) {
           
            String offeringId = generateOfferingId();
            
            int capacity = getDefaultCapacity(need.courseId);
            
            String sql = "INSERT INTO course_offerings " +
                        "(offering_id, course_id, faculty_id, semester_id, section_id, " +
                        "capacity, enrolled_count, status, is_auto_generated) " +
                        "VALUES (?, ?, ?, ?, ?, ?, 0, 'Open', 1)";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, offeringId);
                ps.setString(2, need.courseId);
                ps.setString(3, facultyId);
                ps.setString(4, semesterId);
                ps.setString(5, need.sectionId);
                ps.setInt(6, capacity);
                
                int rows = ps.executeUpdate();
                return rows > 0 ? offeringId : null;
            }
            
        } catch (SQLException e) {
            System.err.println("Error creating course offering: " + e.getMessage());
            return null;
        }
    }
    
    private String generateOfferingId() {
        int year = java.time.LocalDate.now().getYear();
        int random = (int)(Math.random() * 90000) + 10000;
        return "OFF-" + year + "-" + random;
    }
    
    private int getDefaultCapacity(String courseId) {
        try (Connection conn = DBConnection.getConnection()) {
            String query = "SELECT requires_laboratory, min_room_capacity FROM courses WHERE course_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, courseId);
                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    boolean isLab = rs.getBoolean("requires_laboratory");
                    int minCap = rs.getInt("min_room_capacity");
                    return isLab ? Math.min(minCap, 30) : Math.min(minCap, 40);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting capacity: " + e.getMessage());
        }
        return 35; // Default
    }
    
    // Helper Classes
    
    private static class FacultyLoad {
        String facultyId;
        String name;
        String department;
        String specialization;
        int maxTeachingHours;
        int currentLoad;
        
        FacultyLoad(String facultyId, String name, String department, 
                   String specialization, int maxTeachingHours, int currentLoad) {
            this.facultyId = facultyId;
            this.name = name;
            this.department = department;
            this.specialization = specialization;
            this.maxTeachingHours = maxTeachingHours;
            this.currentLoad = currentLoad;
        }
    }
    
    private static class SectionNeed {
        String courseId;
        String courseCode;
        String courseTitle;
        String sectionId;
        String sectionName;
        String programId;
        int yearLevel;
        
        SectionNeed(String courseId, String courseCode, String courseTitle,
                   String sectionId, String sectionName, String programId, int yearLevel) {
            this.courseId = courseId;
            this.courseCode = courseCode;
            this.courseTitle = courseTitle;
            this.sectionId = sectionId;
            this.sectionName = sectionName;
            this.programId = programId;
            this.yearLevel = yearLevel;
        }
    }
}