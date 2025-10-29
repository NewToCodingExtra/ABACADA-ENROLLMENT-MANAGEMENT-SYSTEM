package enrollmentsystem;

import java.sql.*;

/**
 * Complete test runner for scheduling system
 * 1. Generates offerings for underutilized faculty
 * 2. Runs fair auto-scheduler
 * 3. Verifies results
 */
public class CompleteSchedulerTest {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║     COMPLETE SCHEDULING SYSTEM TEST                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        // Set admin user for logging
        SessionManager.getInstance().setUserId(6);
        SessionManager.getInstance().setUserAccess("Admin");
        
        String semesterId = "SEM2025-1ST";
        
        System.out.println("\nTarget Semester: " + semesterId);
        System.out.println("═".repeat(60));
        
        // STEP 1: Check current status
        System.out.println("\n[STEP 1] Checking current system status...");
        printCurrentStatus(semesterId);
        
        // STEP 2: Generate offerings for underutilized faculty
        System.out.println("\n[STEP 2] Generating course offerings for underutilized faculty...");
        System.out.println("─".repeat(60));
        
        FacultyOfferingGenerator offeringGen = new FacultyOfferingGenerator(semesterId);
        boolean offeringsCreated = offeringGen.generateOfferingsForFaculty();
        
        if (offeringsCreated) {
            System.out.println("\n✓ Course offerings generated successfully!");
        } else {
            System.out.println("\n⚠ No new offerings were needed or created.");
        }
        
        // STEP 3: Run fair auto-scheduler
        System.out.println("\n[STEP 3] Running Fair Auto-Scheduler...");
        System.out.println("─".repeat(60));
        
        FairAutoScheduler scheduler = new FairAutoScheduler(semesterId);
        boolean schedulingSuccess = scheduler.generateSchedules();
        
        // STEP 4: Verify and display results
        System.out.println("\n[STEP 4] Verification & Results");
        System.out.println("─".repeat(60));
        
        verifyScheduling(semesterId);
        
        // STEP 5: Final summary
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        if (schedulingSuccess) {
            System.out.println("║  ✓ SYSTEM TEST COMPLETED SUCCESSFULLY                      ║");
        } else {
            System.out.println("║  ⚠ SYSTEM TEST COMPLETED WITH WARNINGS                     ║");
        }
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        
        printRecommendations();
    }
    
    /**
     * Print current system status
     */
    private static void printCurrentStatus(String semesterId) {
        try (Connection conn = DBConnection.getConnection()) {
            
            // Count faculty
            String facultyQuery = "SELECT COUNT(*) as count FROM faculty f " +
                                 "JOIN users u ON f.user_id = u.user_id WHERE u.is_active = 1";
            int totalFaculty = getCount(conn, facultyQuery);
            
            // Count sections
            String sectionQuery = "SELECT COUNT(*) as count FROM section WHERE academic_year_id = " +
                                "(SELECT academic_year_id FROM semester WHERE semester_id = ?)";
            PreparedStatement ps = conn.prepareStatement(sectionQuery);
            ps.setString(1, semesterId);
            ResultSet rs = ps.executeQuery();
            int totalSections = rs.next() ? rs.getInt("count") : 0;
            
            // Count course offerings
            String offeringsQuery = "SELECT COUNT(*) as total, " +
                                   "COUNT(CASE WHEN time_slot_id IS NOT NULL THEN 1 END) as scheduled " +
                                   "FROM course_offerings WHERE semester_id = ? AND status = 'Open'";
            ps = conn.prepareStatement(offeringsQuery);
            ps.setString(1, semesterId);
            rs = ps.executeQuery();
            
            int totalOfferings = 0;
            int scheduledOfferings = 0;
            
            if (rs.next()) {
                totalOfferings = rs.getInt("total");
                scheduledOfferings = rs.getInt("scheduled");
            }
            
            System.out.println("Current Status:");
            System.out.println("  • Total Active Faculty: " + totalFaculty);
            System.out.println("  • Total Sections: " + totalSections);
            System.out.println("  • Total Course Offerings: " + totalOfferings);
            System.out.println("  • Scheduled Offerings: " + scheduledOfferings);
            System.out.println("  • Unscheduled Offerings: " + (totalOfferings - scheduledOfferings));
            
        } catch (SQLException e) {
            System.err.println("Error checking status: " + e.getMessage());
        }
    }
    
    /**
     * Verify scheduling results
     */
    private static void verifyScheduling(String semesterId) {
        try (Connection conn = DBConnection.getConnection()) {
            
            // 1. Check scheduled offerings
            String scheduledQuery = "SELECT COUNT(*) as count FROM course_offerings " +
                                   "WHERE semester_id = ? AND status = 'Open' " +
                                   "AND time_slot_id IS NOT NULL AND room_id IS NOT NULL";
            PreparedStatement ps = conn.prepareStatement(scheduledQuery);
            ps.setString(1, semesterId);
            ResultSet rs = ps.executeQuery();
            int scheduledCount = rs.next() ? rs.getInt("count") : 0;
            
            // 2. Check unscheduled offerings
            String unscheduledQuery = "SELECT COUNT(*) as count FROM course_offerings " +
                                     "WHERE semester_id = ? AND status = 'Open' " +
                                     "AND (time_slot_id IS NULL OR room_id IS NULL)";
            ps = conn.prepareStatement(unscheduledQuery);
            ps.setString(1, semesterId);
            rs = ps.executeQuery();
            int unscheduledCount = rs.next() ? rs.getInt("count") : 0;
            
            // 3. Check for time conflicts (faculty)
            String facultyConflictQuery = 
                "SELECT faculty_id, time_slot_id, COUNT(*) as conflict_count " +
                "FROM course_offerings " +
                "WHERE semester_id = ? AND status = 'Open' AND time_slot_id IS NOT NULL " +
                "GROUP BY faculty_id, time_slot_id HAVING COUNT(*) > 1";
            ps = conn.prepareStatement(facultyConflictQuery);
            ps.setString(1, semesterId);
            rs = ps.executeQuery();
            
            int facultyConflicts = 0;
            while (rs.next()) {
                facultyConflicts++;
                System.err.println("  ✗ Faculty Conflict: " + rs.getString("faculty_id") + 
                                 " at " + rs.getString("time_slot_id") + 
                                 " (" + rs.getInt("conflict_count") + " classes)");
            }
            
            // 4. Check for room conflicts
            String roomConflictQuery = 
                "SELECT room_id, time_slot_id, COUNT(*) as conflict_count " +
                "FROM course_offerings " +
                "WHERE semester_id = ? AND status = 'Open' AND time_slot_id IS NOT NULL " +
                "GROUP BY room_id, time_slot_id HAVING COUNT(*) > 1";
            ps = conn.prepareStatement(roomConflictQuery);
            ps.setString(1, semesterId);
            rs = ps.executeQuery();
            
            int roomConflicts = 0;
            while (rs.next()) {
                roomConflicts++;
                System.err.println("  ✗ Room Conflict: " + rs.getString("room_id") + 
                                 " at " + rs.getString("time_slot_id") + 
                                 " (" + rs.getInt("conflict_count") + " classes)");
            }
            
            // 5. Check for section conflicts
            String sectionConflictQuery = 
                "SELECT section_id, time_slot_id, COUNT(*) as conflict_count " +
                "FROM course_offerings " +
                "WHERE semester_id = ? AND status = 'Open' AND time_slot_id IS NOT NULL " +
                "GROUP BY section_id, time_slot_id HAVING COUNT(*) > 1";
            ps = conn.prepareStatement(sectionConflictQuery);
            ps.setString(1, semesterId);
            rs = ps.executeQuery();
            
            int sectionConflicts = 0;
            while (rs.next()) {
                sectionConflicts++;
                System.err.println("  ✗ Section Conflict: " + rs.getString("section_id") + 
                                 " at " + rs.getString("time_slot_id") + 
                                 " (" + rs.getInt("conflict_count") + " classes)");
            }
            
            // Print summary
            System.out.println("\nVerification Results:");
            System.out.println("  ✓ Scheduled Offerings: " + scheduledCount);
            System.out.println("  " + (unscheduledCount > 0 ? "⚠" : "✓") + 
                             " Unscheduled Offerings: " + unscheduledCount);
            System.out.println("  " + (facultyConflicts > 0 ? "✗" : "✓") + 
                             " Faculty Conflicts: " + facultyConflicts);
            System.out.println("  " + (roomConflicts > 0 ? "✗" : "✓") + 
                             " Room Conflicts: " + roomConflicts);
            System.out.println("  " + (sectionConflicts > 0 ? "✗" : "✓") + 
                             " Section Conflicts: " + sectionConflicts);
            
            // Calculate success rate
            double successRate = scheduledCount * 100.0 / (scheduledCount + unscheduledCount);
            System.out.println("\n  Success Rate: " + String.format("%.1f%%", successRate));
            
        } catch (SQLException e) {
            System.err.println("Error verifying scheduling: " + e.getMessage());
        }
    }
    
    /**
     * Print recommendations
     */
    private static void printRecommendations() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  RECOMMENDATIONS                                           ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("To further optimize the scheduling system:");
        System.out.println();
        System.out.println("1. Add more faculty availability records in faculty_availability table");
        System.out.println("   - This helps the scheduler find suitable time slots");
        System.out.println();
        System.out.println("2. Add more rooms if capacity is an issue");
        System.out.println("   - Especially laboratory rooms for lab courses");
        System.out.println();
        System.out.println("3. Review faculty specializations");
        System.out.println("   - Ensure they match the courses being offered");
        System.out.println();
        System.out.println("4. Consider increasing time slots during peak hours");
        System.out.println("   - Morning and early afternoon slots fill up quickly");
        System.out.println();
        System.out.println("5. Run the scheduler again if new offerings are added:");
        System.out.println("   - FairAutoScheduler scheduler = new FairAutoScheduler(\"SEM2025-1ST\");");
        System.out.println("   - scheduler.generateSchedules();");
        System.out.println();
    }
    
    /**
     * Helper method to get count from query
     */
    private static int getCount(Connection conn, String query) {
        try (PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt("count") : 0;
        } catch (SQLException e) {
            return 0;
        }
    }
}