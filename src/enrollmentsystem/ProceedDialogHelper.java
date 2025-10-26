package enrollmentsystem;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProceedDialogHelper {
 
    public static int showProceedDialog(Stage currentStage) {
        try {
            System.out.println("=== Starting showProceedDialog ===");
            System.out.println("Current stage: " + currentStage);
            System.out.println("Main stage: " + EnrollmentSystem.mainStage);
            
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ProceedDialogHelper.class.getResource("/enrollmentsystem/ProceedUI.fxml"));
            
            System.out.println("FXML Location: " + loader.getLocation());
            
            if (loader.getLocation() == null) {
                System.err.println("ERROR: ProceedUI.fxml not found!");
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "Error: ProceedUI.fxml not found in /enrollmentsystem/", 
                    "File Not Found", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return -1;
            }
            
            Parent root = loader.load();
            System.out.println("FXML loaded successfully");
            
            // Get the controller
            ProceedUIController controller = loader.getController();
            System.out.println("Controller obtained: " + controller);
            
            // Create dialog stage
            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            Stage owner = currentStage != null ? currentStage : EnrollmentSystem.mainStage;
            System.out.println("Setting owner: " + owner);
            dialogStage.initOwner(owner);
            
            dialogStage.setTitle("Choose Your Path");
            dialogStage.setResizable(false);
            
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            
            System.out.println("Dialog stage created, about to show...");
            
            // Center the dialog on parent window
            if (owner != null && owner.isShowing()) {
                // Calculate center position based on parent window
                double centerX = owner.getX() + (owner.getWidth() / 2) - (scene.getWidth() / 2);
                double centerY = owner.getY() + (owner.getHeight() / 2) - (scene.getHeight() / 2);
                
                dialogStage.setX(centerX);
                dialogStage.setY(centerY);
            } else {
                javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
                dialogStage.setX((screenBounds.getWidth() - scene.getWidth()) / 2);
                dialogStage.setY((screenBounds.getHeight() - scene.getHeight()) / 2);
            }
            
            dialogStage.showAndWait();
            
            System.out.println("Dialog closed with result: " + controller.getResult());
            
            return controller.getResult();
            
        } catch (IOException e) {
            System.err.println("IOException in showProceedDialog:");
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, 
                "Error loading dialog: " + e.getMessage(), 
                "Load Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return -1;
        } catch (Exception e) {
            System.err.println("Unexpected exception in showProceedDialog:");
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, 
                "Unexpected error: " + e.getMessage(), 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }
 
    public static void navigateToEnrollment() {
        WindowOpener.openSceneWithCSS("/enrollmentsystem/Enrollment1.fxml", "/enrollment.css", "ABAKADA UNIVERSITY - SIGN UP", 900, 520);
    }
   

    public static void navigateToLogin() {
        try {
            Parent root = FXMLLoader.load(ProceedDialogHelper.class.getResource("/enrollmentsystem/NewLoginUI.fxml"));
            Scene scene = new Scene(root, 898, 543);
            scene.getStylesheets().add(ProceedDialogHelper.class.getResource("/loginui.css").toExternalForm());
            
            Stage stage = EnrollmentSystem.mainStage;
            stage.setScene(scene);
            stage.setTitle("ABAKADA UNIVERSITY - LOGIN PAGE");
            stage.setResizable(false);
            
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - 898) / 2);
            stage.setY((screenBounds.getHeight() - 543) / 2);
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showDialogAndNavigate(Stage currentStage) {
        int result = showProceedDialog(currentStage);
        
        switch (result) {
            case 0:  
                System.out.println("User chose Enrollment");
                navigateToEnrollment();
                break;
            case 1:  
                System.out.println("User chose Login");
                navigateToLogin();
                break;
            default:  
                System.out.println("No selection made");
                break;
        }
    }
}

