package enrollmentsystem;

import java.sql.SQLException;

public interface AccountCreation {
    void createAdmin(User user) throws SQLException;
    void createCashier(User user) throws SQLException;
    void createFaculty(User user) throws SQLException;
    void createStudent(User user) throws SQLException;
}
