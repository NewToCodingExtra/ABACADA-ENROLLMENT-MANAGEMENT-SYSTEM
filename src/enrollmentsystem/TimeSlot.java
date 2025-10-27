package enrollmentsystem;

import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a time slot for scheduling
 */
public class TimeSlot {
    private String timeSlotId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String slotLabel;
    private boolean isAvailable;
    
    public TimeSlot() {}
    
    public TimeSlot(String timeSlotId, String dayOfWeek, LocalTime startTime, 
                   LocalTime endTime, String slotLabel, boolean isAvailable) {
        this.timeSlotId = timeSlotId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.slotLabel = slotLabel;
        this.isAvailable = isAvailable;
    }
    
    // Getters and Setters
    public String getTimeSlotId() { return timeSlotId; }
    public void setTimeSlotId(String timeSlotId) { this.timeSlotId = timeSlotId; }
    
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    
    public String getSlotLabel() { return slotLabel; }
    public void setSlotLabel(String slotLabel) { this.slotLabel = slotLabel; }
    
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    
    /**
     * Check if this time slot overlaps with another
     */
    public boolean overlapsWith(TimeSlot other) {
        if (!this.dayOfWeek.equals(other.dayOfWeek)) {
            return false;
        }
        return this.startTime.isBefore(other.endTime) && this.endTime.isAfter(other.startTime);
    }
    
    /**
     * Load all available time slots
     */
    public static List<TimeSlot> loadAll() {
        List<TimeSlot> slots = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return slots;
            
            String sql = "SELECT * FROM time_slots WHERE is_available = 1 ORDER BY day_of_week, start_time";
            pstmt = conn.prepareStatement(sql);
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
            System.err.println("Error loading time slots: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return slots;
    }
    
    /**
     * Load time slot by ID
     */
    public static TimeSlot loadById(String timeSlotId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return null;
            
            String sql = "SELECT * FROM time_slots WHERE time_slot_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, timeSlotId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new TimeSlot(
                    rs.getString("time_slot_id"),
                    rs.getString("day_of_week"),
                    rs.getTime("start_time").toLocalTime(),
                    rs.getTime("end_time").toLocalTime(),
                    rs.getString("slot_label"),
                    rs.getBoolean("is_available")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error loading time slot: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "TimeSlot{" +
               "timeSlotId='" + timeSlotId + '\'' +
               ", dayOfWeek='" + dayOfWeek + '\'' +
               ", startTime=" + startTime +
               ", endTime=" + endTime +
               ", slotLabel='" + slotLabel + '\'' +
               ", isAvailable=" + isAvailable +
               '}';
    }
}