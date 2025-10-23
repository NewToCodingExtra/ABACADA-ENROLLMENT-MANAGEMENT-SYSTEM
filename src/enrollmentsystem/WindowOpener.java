/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package enrollmentsystem;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;



/**
 * Utility class for opening windows/scenes in the application
 * All methods are static - no need to create an instance
 */
public class WindowOpener {
   
    // Private constructor to prevent instantiation
    private WindowOpener() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    
    /**
     * Opens a new scene with the specified FXML file
     * @param fxmlPath Path to the FXML file
     * @param title Window title
     * @param width Window width
     * @param height Window height
     */
    public static void openScene(String fxmlPath, String title, double width, double height) {
        try {
            Parent root = FXMLLoader.load(WindowOpener.class.getResource(fxmlPath));
            Scene scene = new Scene(root, width, height);
            
            // Apply stylesheets based on the FXML path
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
            
            // Center the window on screen
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
            
            // Center the window on screen
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
}

