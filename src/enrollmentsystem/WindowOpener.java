package enrollmentsystem;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Modality;

public class WindowOpener {
   
    private WindowOpener() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
  
    public static void openScene(String fxmlPath, String title, double width, double height) {
        try {
            Parent root = FXMLLoader.load(WindowOpener.class.getResource(fxmlPath));
            Scene scene = new Scene(root, width, height);
            
            if(fxmlPath.equals("/enrollmentsystem/Register.fxml")) {
                scene.getStylesheets().add(
                    WindowOpener.class.getResource("/sigupui.css").toExternalForm()
                );
            } else if(fxmlPath.equals("/enrollmentsystem/NewLoginUI.fxml")) {
                scene.getStylesheets().add(
                    WindowOpener.class.getResource("/loginui.css").toExternalForm()
                );
            }
           
            Stage stage = EnrollmentSystem.mainStage;
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - width) / 2);
            stage.setY((screenBounds.getHeight() - height) / 2);
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading scene: " + fxmlPath);
        }
    }
    
    public static void openSceneWithCSS(String fxmlPath, String cssPath, String title, double width, double height) {
        try {
            Parent root = FXMLLoader.load(WindowOpener.class.getResource(fxmlPath));
            Scene scene = new Scene(root, width, height);
            
            if (cssPath != null && !cssPath.isEmpty()) {
                scene.getStylesheets().add(
                    WindowOpener.class.getResource(cssPath).toExternalForm()
                );
            }
           
            Stage stage = EnrollmentSystem.mainStage;
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - width) / 2);
            stage.setY((screenBounds.getHeight() - height) / 2);
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading scene: " + fxmlPath);
        }
    }

    public static void openLogin() {
        openScene("/enrollmentsystem/NewLoginUI.fxml", 
                  "ABAKADA UNIVERSITY - LOGIN PAGE", 
                  898, 543);
    }
    
    public static void openSignup() {
        openScene("/enrollmentsystem/Register.fxml", 
                  "ABAKADA UNIVERSITY - SIGN UP", 
                  898, 543);
    }
    
    public static FXMLLoader openSceneWithLoader(String fxmlPath, String title, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(WindowOpener.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            
            if(fxmlPath.equals("/enrollmentsystem/Register.fxml")) {
                scene.getStylesheets().add(
                    WindowOpener.class.getResource("/sigupui.css").toExternalForm()
                );
            } else if(fxmlPath.equals("/enrollmentsystem/NewLoginUI.fxml")) {
                scene.getStylesheets().add(
                    WindowOpener.class.getResource("/loginui.css").toExternalForm()
                );
            }
           
            Stage stage = EnrollmentSystem.mainStage;
            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX((screenBounds.getWidth() - width) / 2);
            stage.setY((screenBounds.getHeight() - height) / 2);
            
            stage.show();
            
            return loader;
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading scene: " + fxmlPath);
            return null;
        }
    }
    
    public static void openDialogWithCSS(String fxmlPath, String cssPath, String title, double width, double height) {
        openDialogWithCSSAndOwner(fxmlPath, cssPath, title, width, height, EnrollmentSystem.mainStage);
    }
    
    public static void openDialogWithCSSAndOwner(String fxmlPath, String cssPath, String title, double width, double height, Stage owner) {
        try {
            // Verify resources exist before attempting to load
            if (WindowOpener.class.getResource(fxmlPath) == null) {
                System.err.println("FXML resource not found: " + fxmlPath);
                return;
            }
            
            if (cssPath != null && !cssPath.isEmpty() && WindowOpener.class.getResource(cssPath) == null) {
                System.err.println("CSS resource not found: " + cssPath);
                // Continue without CSS rather than failing completely
                openDialogWithOwner(fxmlPath, title, width, height, owner);
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(WindowOpener.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            // Verify root was loaded successfully
            if (root == null) {
                System.err.println("Failed to load root from FXML: " + fxmlPath);
                return;
            }
            
            Scene scene = new Scene(root, width, height);

            if (cssPath != null && !cssPath.isEmpty()) {
                scene.getStylesheets().add(WindowOpener.class.getResource(cssPath).toExternalForm());
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            dialogStage.initOwner(owner);
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            dialogStage.setX((screenBounds.getWidth() - width) / 2);
            dialogStage.setY((screenBounds.getHeight() - height) / 2);

            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading dialog with CSS: " + fxmlPath);
            System.err.println("IOException message: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error loading dialog: " + fxmlPath);
            System.err.println("Exception message: " + e.getMessage());
        }
    }
    
    public static void openDialog(String fxmlPath, String title, double width, double height) {
        openDialogWithOwner(fxmlPath, title, width, height, EnrollmentSystem.mainStage);
    }
    
    public static void openDialogWithOwner(String fxmlPath, String title, double width, double height, Stage owner) {
        try {
            // Verify resource exists
            if (WindowOpener.class.getResource(fxmlPath) == null) {
                System.err.println("FXML resource not found: " + fxmlPath);
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(WindowOpener.class.getResource(fxmlPath));
            Parent root = loader.load();
            
            // Verify root was loaded successfully
            if (root == null) {
                System.err.println("Failed to load root from FXML: " + fxmlPath);
                return;
            }
            
            Scene scene = new Scene(root, width, height);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);
            dialogStage.initOwner(owner);  
            dialogStage.initModality(Modality.WINDOW_MODAL); 

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            dialogStage.setX((screenBounds.getWidth() - width) / 2);
            dialogStage.setY((screenBounds.getHeight() - height) / 2);

            dialogStage.showAndWait();  
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading dialog: " + fxmlPath);
            System.err.println("IOException message: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Unexpected error loading dialog: " + fxmlPath);
            System.err.println("Exception message: " + e.getMessage());
        }
    }
}