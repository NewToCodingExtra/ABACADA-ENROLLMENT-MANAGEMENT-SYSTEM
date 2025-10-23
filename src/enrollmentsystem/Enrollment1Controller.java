package enrollmentsystem;

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
    @FXML private TextField ageField;
    @FXML private TextField contactField;
    @FXML private TextField provinceField;
    @FXML private TextField cityField;
    @FXML private TextField barangayField;
    @FXML private TextField streetField;
    @FXML private TextField houseNoField;
    @FXML private Button homeButton;
    @FXML private Button nextButton;
    
    @FXML
    private void homeBtnAction(ActionEvent event) {
        
        if (validateAllFields()) {
            ProceedDialogHelper.navigateToLogin();
        } else {
            int choice = JOptionPane.showConfirmDialog(null, "Back to login page?", "Warning", JOptionPane.YES_NO_CANCEL_OPTION);
            if(choice == JOptionPane.YES_OPTION) {
                ProceedDialogHelper.navigateToLogin();
            }
            else return;
        }
    }
    
    @FXML
    private void nextBtnAction(ActionEvent event) {
      
        if (validateAllFields()) {

            WindowOpener.openSceneWithCSS(
                "/enrollmentsystem/Enrollment2.fxml", 
                "/enrollment.css", 
                "ABAKADA UNIVERSITY - ENROLLEE FORM", 
                902, 504
            );
        } else {
            JOptionPane.showMessageDialog(null, "A field is empty, continue?", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
   
    private boolean validateAllFields() {
        List<String> emptyFields = new ArrayList<>();
        
        // Check each field and collect empty field names
        if (isFieldEmpty(lastNameField)) {
            emptyFields.add("Last Name");
        }
        if (isFieldEmpty(firstNameField)) {
            emptyFields.add("First Name");
        }
        if (isFieldEmpty(middleNameField)) {
            emptyFields.add("Middle Name");
        }
        if (isFieldEmpty(ageField)) {
            emptyFields.add("Age");
        }
        if (isFieldEmpty(contactField)) {
            emptyFields.add("Contact Number");
        }
        if (isFieldEmpty(provinceField)) {
            emptyFields.add("Province");
        }
        if (isFieldEmpty(cityField)) {
            emptyFields.add("City");
        }
        if (isFieldEmpty(barangayField)) {
            emptyFields.add("Barangay");
        }
        if (isFieldEmpty(streetField)) {
            emptyFields.add("Street");
        }
        if (isFieldEmpty(houseNoField)) {
            emptyFields.add("House Number");
        }
        
        // If there are empty fields, show dialog
        if (!emptyFields.isEmpty()) {
            showValidationDialog(emptyFields);
            return false;
        }
        
        return true;
    }

    private boolean isFieldEmpty(TextField field) {
        return field == null || field.getText() == null || field.getText().trim().isEmpty();
    }
    
    /**
     * Shows a dialog with the list of empty fields
     * @param emptyFields List of field names that are empty
     */
    private void showValidationDialog(List<String> emptyFields) {
        StringBuilder message = new StringBuilder("Please fill in the following required fields:\n\n");
        
        for (String fieldName : emptyFields) {
            message.append("• ").append(fieldName).append("\n");
        }
        
        javax.swing.JOptionPane.showMessageDialog(
            null,
            message.toString(),
            "Incomplete Form",
            javax.swing.JOptionPane.WARNING_MESSAGE
        );
    }
    
    /**
     * Alternative: Simple validation that shows generic message
     * @return true if all fields are filled, false otherwise
     */
    private boolean validateAllFieldsSimple() {
        boolean allFilled = !isFieldEmpty(lastNameField) &&
                           !isFieldEmpty(firstNameField) &&
                           !isFieldEmpty(middleNameField) &&
                           !isFieldEmpty(ageField) &&
                           !isFieldEmpty(contactField) &&
                           !isFieldEmpty(provinceField) &&
                           !isFieldEmpty(cityField) &&
                           !isFieldEmpty(barangayField) &&
                           !isFieldEmpty(streetField) &&
                           !isFieldEmpty(houseNoField);
        
        if (!allFilled) {
            javax.swing.JOptionPane.showMessageDialog(
                null,
                "Please fill in all required fields before proceeding.",
                "Incomplete Form",
                javax.swing.JOptionPane.WARNING_MESSAGE
            );
        }
        
        return allFilled;
    }
    
    /**
     * Enhanced validation with specific field highlighting
     * @return true if all fields are valid, false otherwise
     */
    private boolean validateAllFieldsWithHighlight() {
        List<TextField> emptyFields = new ArrayList<>();
        
        // Reset all field styles first
        resetFieldStyles();
        
        // Check each field
        if (isFieldEmpty(lastNameField)) emptyFields.add(lastNameField);
        if (isFieldEmpty(firstNameField)) emptyFields.add(firstNameField);
        if (isFieldEmpty(middleNameField)) emptyFields.add(middleNameField);
        if (isFieldEmpty(ageField)) emptyFields.add(ageField);
        if (isFieldEmpty(contactField)) emptyFields.add(contactField);
        if (isFieldEmpty(provinceField)) emptyFields.add(provinceField);
        if (isFieldEmpty(cityField)) emptyFields.add(cityField);
        if (isFieldEmpty(barangayField)) emptyFields.add(barangayField);
        if (isFieldEmpty(streetField)) emptyFields.add(streetField);
        if (isFieldEmpty(houseNoField)) emptyFields.add(houseNoField);
        
        if (!emptyFields.isEmpty()) {
            // Highlight empty fields with red border
            for (TextField field : emptyFields) {
                field.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            }
            
            javax.swing.JOptionPane.showMessageDialog(
                null,
                "Please fill in all required fields (highlighted in red).",
                "Incomplete Form",
                javax.swing.JOptionPane.WARNING_MESSAGE
            );
            
            return false;
        }
        
        return true;
    }
    
    /**
     * Resets the styling of all form fields
     */
    private void resetFieldStyles() {
        lastNameField.setStyle("");
        firstNameField.setStyle("");
        middleNameField.setStyle("");
        ageField.setStyle("");
        contactField.setStyle("");
        provinceField.setStyle("");
        cityField.setStyle("");
        barangayField.setStyle("");
        streetField.setStyle("");
        houseNoField.setStyle("");
    }
}

