package enrollmentsystem;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EnrolleeDataLoader {

    public static Enrollee loadEnrolleeData() {
        String enrolleeId = SessionManager.getInstance().getEnrolleeId();
        System.out.println("From loader class enrollID:"+enrolleeId);
        Integer userId = SessionManager.getInstance().getUserId();

        if (enrolleeId == null || userId == null || userId == 0) {
            System.out.println("No enrollee session found, starting fresh form");
            return new Enrollee();
        }

        Enrollee enrollee = fetchEnrolleeFromDatabase(enrolleeId, userId);
        if (enrollee != null) {
            System.out.println("Loaded existing enrollee data for: " + enrolleeId);
            return enrollee;
        } else {
            System.out.println("No existing data found, creating new enrollee");
            Enrollee newEnrollee = new Enrollee();
            newEnrollee.setEnrolleeId(enrolleeId);
            newEnrollee.setUserId(userId);
            return newEnrollee;
        }
    }

    private static Enrollee fetchEnrolleeFromDatabase(String enrolleeId, int userId) {
        String query = "SELECT e.*, u.username, u.email, u.password, u.access, u.created_at, u.is_active " +
                       "FROM enrollees e " +
                       "JOIN users u ON e.user_id = u.user_id " +
                       "WHERE e.enrollee_id = ? AND e.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, enrolleeId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return createEnrolleeFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error loading enrollee data: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private static Enrollee createEnrolleeFromResultSet(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");
        String access = rs.getString("access");
        LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
        boolean isActive = rs.getBoolean("is_active");

        String enrolleeId = rs.getString("enrollee_id");
        String firstName = rs.getString("first_name");
        String middleName = rs.getString("middle_name");
        String lastName = rs.getString("last_name");
        String suffix = rs.getString("suffix");
        Date birthDateSql = rs.getDate("birth_date");
        LocalDate birthDate = birthDateSql != null ? birthDateSql.toLocalDate() : null;
        String gender = rs.getString("gender");
        String address = rs.getString("address");
        String province = rs.getString("province");
        String city = rs.getString("city");
        String contactNumber = rs.getString("contact_number");
        String emailAddress = rs.getString("email_address");
        String guardianName = rs.getString("guardian_name");
        String guardianContact = rs.getString("guardian_contact");
        String lastSchoolAttended = rs.getString("last_school_attended");
        String lastSchoolYear = rs.getString("last_school_year");
        String programAppliedFor = rs.getString("program_applied_for");
        String enrollmentStatus = rs.getString("enrollment_status");
        Timestamp dateAppliedTs = rs.getTimestamp("date_applied");
        LocalDateTime dateApplied = dateAppliedTs != null ? dateAppliedTs.toLocalDateTime() : null;
        Integer reviewedBy = (Integer) rs.getObject("reviewed_by");
        Timestamp reviewedOnTs = rs.getTimestamp("reviewed_on");
        LocalDateTime reviewedOn = reviewedOnTs != null ? reviewedOnTs.toLocalDateTime() : null;
        boolean hasFilledUpForm = rs.getBoolean("has_filled_up_form");

        Enrollee enrollee = new Enrollee(userId, username, email, password, access, createdAt, isActive,
                enrolleeId, firstName, middleName, lastName, suffix, birthDate, gender,
                address, province, city, contactNumber, emailAddress,
                guardianName, guardianContact, lastSchoolAttended, lastSchoolYear,
                programAppliedFor, enrollmentStatus, dateApplied, reviewedBy, reviewedOn);
        enrollee.setHasFilledUpForm(hasFilledUpForm);
        return enrollee;
    }
}
