package enrollmentsystem;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javax.swing.JOptionPane;

public class Enrollment1Controller {

    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private TextField middleNameField;
    @FXML private TextField contactField;
    @FXML private TextField provinceField;
    @FXML private TextField cityField;
    @FXML private TextField barangayField;
    @FXML private TextField streetField;
    @FXML private TextField houseNoField;
    @FXML private Button homeButton;
    @FXML private Button nextButton;
    @FXML private DatePicker datePicker;

    private Enrollee currentEnrollee;

    public void initialize() {
        loadEnrolleeData();
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                calculateAge(newValue);
            }
        });
    }

    private void loadEnrolleeData() {
    currentEnrollee = EnrolleeDataLoader.loadEnrolleeData();
    if (currentEnrollee != null) {
        populateFieldsFromEnrollee(currentEnrollee);
    }
}


    private void populateFieldsFromEnrollee(Enrollee enrollee) {
        if (enrollee.getFirstName() != null) firstNameField.setText(enrollee.getFirstName());
        if (enrollee.getMiddleName() != null) middleNameField.setText(enrollee.getMiddleName());
        if (enrollee.getLastName() != null) lastNameField.setText(enrollee.getLastName());
        if (enrollee.getProvince() != null) provinceField.setText(enrollee.getProvince());
        if (enrollee.getCity() != null) cityField.setText(enrollee.getCity());
        if (enrollee.getContactNumber() != null) contactField.setText(enrollee.getContactNumber());

        if (enrollee.getAddress() != null && !enrollee.getAddress().trim().isEmpty()) {
            parseAndFillAddress(enrollee.getAddress());
        }

        if (enrollee.getBirthDate() != null) {
            datePicker.setValue(enrollee.getBirthDate());
            calculateAge(enrollee.getBirthDate());
        }
    }

    private void calculateAge(LocalDate birthDate) {
        if (birthDate != null) {
            int age = Period.between(birthDate, LocalDate.now()).getYears();
        }
    }

    private void parseAndFillAddress(String address) {
        String[] parts = address.split(",");
        if (parts.length >= 1) houseNoField.setText(parts[0].trim());
        if (parts.length >= 2) streetField.setText(parts[1].trim());
        if (parts.length >= 3) barangayField.setText(parts[2].trim());
    }

    private void updateEnrolleeFromFields() {
        currentEnrollee.setFirstName(firstNameField.getText().trim());
        currentEnrollee.setMiddleName(middleNameField.getText().trim());
        currentEnrollee.setLastName(lastNameField.getText().trim());
        currentEnrollee.setProvince(provinceField.getText().trim());
        currentEnrollee.setCity(cityField.getText().trim());
        currentEnrollee.setContactNumber(contactField.getText().trim());

        String fullAddress = String.format("%s, %s, %s",
                houseNoField.getText().trim(),
                streetField.getText().trim(),
                barangayField.getText().trim()
        );
        currentEnrollee.setAddress(fullAddress);

        LocalDate birthDate = datePicker.getValue();
        currentEnrollee.setBirthDate(birthDate);
    }

    @FXML
    private void homeBtnAction(ActionEvent event) {
        if (validateAllFields()) {
            ProceedDialogHelper.navigateToLogin();
        } else {
            int choice = JOptionPane.showConfirmDialog(null,
                    "You have unsaved changes. Back to login page?",
                    "Warning",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                ProceedDialogHelper.navigateToLogin();
            }
        }
    }

    @FXML
    private void nextBtnAction(ActionEvent event) {
        if (validateAllFields()) {
            updateEnrolleeFromFields();

            if (saveEnrollee(currentEnrollee)) {
                WindowOpener.openSceneWithCSS(
                        "/enrollmentsystem/Enrollment2.fxml",
                        "/enrollment.css",
                        "ABAKADA UNIVERSITY - ENROLLEE FORM",
                        902, 504
                );
            } else {
                JOptionPane.showMessageDialog(null,
                        "Failed to save enrollment data. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null,
                    "Please fill in all required fields before proceeding.",
                    "Incomplete Form",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean saveEnrollee(Enrollee enrollee) {
        if (enrollee.getEnrolleeId() == null) {
            System.err.println("No enrollee ID found");
            return false;
        }

        if (checkIfEnrolleeExists(enrollee.getEnrolleeId(), enrollee.getUserId())) {
            return updateEnrolleeInDatabase(enrollee);
        } else {
            return insertEnrolleeInDatabase(enrollee);
        }
    }

    private boolean checkIfEnrolleeExists(String enrolleeId, int userId) {
        String query = "SELECT COUNT(*) FROM enrollees WHERE enrollee_id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, enrolleeId);
            ps.setInt(2, userId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error checking enrollee existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private boolean insertEnrolleeInDatabase(Enrollee enrollee) {
        String query = "INSERT INTO enrollees (enrollee_id, user_id, first_name, middle_name, last_name, " +
                "birth_date, address, province, city, contact_number, has_filled_up_form) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, enrollee.getEnrolleeId());
            ps.setInt(2, enrollee.getUserId());
            ps.setString(3, enrollee.getFirstName());
            ps.setString(4, enrollee.getMiddleName());
            ps.setString(5, enrollee.getLastName());
            ps.setDate(6, enrollee.getBirthDate() != null ? Date.valueOf(enrollee.getBirthDate()) : null);
            ps.setString(7, enrollee.getAddress());
            ps.setString(8, enrollee.getProvince());
            ps.setString(9, enrollee.getCity());
            ps.setString(10, enrollee.getContactNumber());
            ps.setBoolean(11, false);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Enrollee data inserted successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error inserting enrollee data: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private boolean updateEnrolleeInDatabase(Enrollee enrollee) {
        String query = "UPDATE enrollees SET first_name = ?, middle_name = ?, last_name = ?, birth_date = ?, " +
                "address = ?, province = ?, city = ?, contact_number = ? " +
                "WHERE enrollee_id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, enrollee.getFirstName());
            ps.setString(2, enrollee.getMiddleName());
            ps.setString(3, enrollee.getLastName());
            ps.setDate(4, enrollee.getBirthDate() != null ? Date.valueOf(enrollee.getBirthDate()) : null);
            ps.setString(5, enrollee.getAddress());
            ps.setString(6, enrollee.getProvince());
            ps.setString(7, enrollee.getCity());
            ps.setString(8, enrollee.getContactNumber());
            ps.setString(9, enrollee.getEnrolleeId());
            ps.setInt(10, enrollee.getUserId());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Enrollee data updated successfully");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error updating enrollee data: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    private boolean validateAllFields() {
        List<String> emptyFields = new ArrayList<>();

        if (isFieldEmpty(lastNameField)) emptyFields.add("Last Name");
        if (isFieldEmpty(firstNameField)) emptyFields.add("First Name");
        if (isFieldEmpty(middleNameField)) emptyFields.add("Middle Name");
        if (datePicker.getValue() == null) emptyFields.add("Birthday");
        if (isFieldEmpty(contactField)) emptyFields.add("Contact Number");
        if (isFieldEmpty(provinceField)) emptyFields.add("Province");
        if (isFieldEmpty(cityField)) emptyFields.add("City");
        if (isFieldEmpty(barangayField)) emptyFields.add("Barangay");
        if (isFieldEmpty(streetField)) emptyFields.add("Street");
        if (isFieldEmpty(houseNoField)) emptyFields.add("House Number");

        if (datePicker.getValue() != null) {
            if (datePicker.getValue().isAfter(LocalDate.now())) {
                emptyFields.add("Birthday (cannot be in the future)");
            }
            int age = Period.between(datePicker.getValue(), LocalDate.now()).getYears();
            if (age < 12) {
                emptyFields.add("Birthday (age must be at least 12 year)");
            } else if (age > 150) {
                emptyFields.add("Birthday (age cannot exceed 150 years)");
            }
        }
        
        if (!emptyFields.isEmpty()) {
            showValidationDialog(emptyFields);
            return false;
        }
        return true;
    }

    private boolean isFieldEmpty(TextField field) {
        return field == null || field.getText() == null || field.getText().trim().isEmpty();
    }

    private void showValidationDialog(List<String> emptyFields) {
        StringBuilder message = new StringBuilder("Please fill in the following required fields:\n\n");

        for (String fieldName : emptyFields) {
            message.append("• ").append(fieldName).append("\n");
        }

        JOptionPane.showMessageDialog(
                null,
                message.toString(),
                "Incomplete Form",
                JOptionPane.WARNING_MESSAGE
        );
    }
}
