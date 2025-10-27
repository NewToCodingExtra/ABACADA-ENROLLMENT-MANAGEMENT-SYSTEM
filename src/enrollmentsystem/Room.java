package enrollmentsystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a classroom/room for scheduling
 */
public class Room {
    private String roomId;
    private String roomCode;
    private String building;
    private Integer floor;
    private int capacity;
    private String roomType;
    private boolean hasProjector;
    private boolean hasComputer;
    private boolean hasAircon;
    private boolean isAvailable;
    
    public Room() {}
    
    public Room(String roomId, String roomCode, String building, Integer floor,
               int capacity, String roomType, boolean hasProjector, 
               boolean hasComputer, boolean hasAircon, boolean isAvailable) {
        this.roomId = roomId;
        this.roomCode = roomCode;
        this.building = building;
        this.floor = floor;
        this.capacity = capacity;
        this.roomType = roomType;
        this.hasProjector = hasProjector;
        this.hasComputer = hasComputer;
        this.hasAircon = hasAircon;
        this.isAvailable = isAvailable;
    }
    
    // Getters and Setters
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    
    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }
    
    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }
    
    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }
    
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    
    public String getRoomType() { return roomType; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    
    public boolean hasProjector() { return hasProjector; }
    public void setHasProjector(boolean hasProjector) { this.hasProjector = hasProjector; }
    
    public boolean hasComputer() { return hasComputer; }
    public void setHasComputer(boolean hasComputer) { this.hasComputer = hasComputer; }
    
    public boolean hasAircon() { return hasAircon; }
    public void setHasAircon(boolean hasAircon) { this.hasAircon = hasAircon; }
    
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    
    /**
     * Check if room meets course requirements
     */
    public boolean meetsRequirements(boolean requiresLab, boolean requiresComputer, int minCapacity) {
        if (!isAvailable) return false;
        if (capacity < minCapacity) return false;
        if (requiresLab && !roomType.equals("Laboratory")) return false;
        if (requiresComputer && !hasComputer) return false;
        return true;
    }
    
    /**
     * Load all available rooms
     */
    public static List<Room> loadAll() {
        List<Room> rooms = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return rooms;
            
            String sql = "SELECT * FROM rooms WHERE is_available = 1 ORDER BY building, room_code";
            pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Room room = new Room(
                    rs.getString("room_id"),
                    rs.getString("room_code"),
                    rs.getString("building"),
                    (Integer) rs.getObject("floor"),
                    rs.getInt("capacity"),
                    rs.getString("room_type"),
                    rs.getBoolean("has_projector"),
                    rs.getBoolean("has_computer"),
                    rs.getBoolean("has_aircon"),
                    rs.getBoolean("is_available")
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            System.err.println("Error loading rooms: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return rooms;
    }
    
    /**
     * Load room by ID
     */
    public static Room loadById(String roomId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return null;
            
            String sql = "SELECT * FROM rooms WHERE room_id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, roomId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Room(
                    rs.getString("room_id"),
                    rs.getString("room_code"),
                    rs.getString("building"),
                    (Integer) rs.getObject("floor"),
                    rs.getInt("capacity"),
                    rs.getString("room_type"),
                    rs.getBoolean("has_projector"),
                    rs.getBoolean("has_computer"),
                    rs.getBoolean("has_aircon"),
                    rs.getBoolean("is_available")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error loading room: " + e.getMessage());
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
    
    /**
     * Check if room is available at specific time slot
     */
    public boolean isAvailableAt(String timeSlotId, String semesterId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            if (conn == null) return false;
            
            String sql = "SELECT COUNT(*) as count FROM course_offerings " +
                        "WHERE room_id = ? AND time_slot_id = ? AND semester_id = ? AND status = 'Open'";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, roomId);
            pstmt.setString(2, timeSlotId);
            pstmt.setString(3, semesterId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count") == 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking room availability: " + e.getMessage());
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return "Room{" +
               "roomId='" + roomId + '\'' +
               ", roomCode='" + roomCode + '\'' +
               ", building='" + building + '\'' +
               ", capacity=" + capacity +
               ", roomType='" + roomType + '\'' +
               '}';
    }
}