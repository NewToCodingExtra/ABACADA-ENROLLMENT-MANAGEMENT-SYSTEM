
package enrollmentsystem;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDateTime; 

public class EnrollmentSystem extends Application {
    public static Stage mainStage;
    @Override
    public void start(Stage primaryStage) {
        try {
//            String semesterId = "SEM25-01";
//            AutoScheduler scheduler = new AutoScheduler(semesterId);
//            boolean success = scheduler.generateSchedules();
//
//            if (success) {
//                System.out.println("Schedules generated successfully!");
//            }
            mainStage = primaryStage;  
            Parent root = FXMLLoader.load(getClass().getResource("/enrollmentsystem/NewLoginUI.fxml"));
            Scene scene = new Scene(root, 898, 543);
            scene.getStylesheets().add(getClass().getResource("/loginui.css").toExternalForm());
//            scene.getStylesheets().add(getClass().getResource("/fonts.css").toExternalForm());
            primaryStage.setTitle("ABAKADA UNIVERSITY ENROLLMENT PAGE");
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false);
            primaryStage.show();
            
        } catch (IOException ex) {
            System.getLogger(EnrollmentSystem.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        } 
    }

 
    public static void main(String[] args) {
        launch(args);
    }

}
