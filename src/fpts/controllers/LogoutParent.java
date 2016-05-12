package fpts.controllers;

import fpts.FPTSRunner;
import fpts.data.Portfolio;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;



import java.io.IOException;
import java.util.Optional;

/**
 * To be inheritied by every controller that will want to give its view
 * a logout capability
 *
 * @author Philip Bedward
 */
public class LogoutParent {

    /**
     * A logout method to be used by every controller other than the login and signup controllers
     *
     * @param application - the current gui
     */
    @FXML
    public void logout(FPTSRunner application){

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setContentText("Are you sure you want to logout?");

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll( yesButton,cancelButton );

        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == yesButton) {

            Portfolio.currentPortfolio = null;

            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(FPTSRunner.class.getResource("views/login.fxml"));
                Parent newRoot = loader.load();
                LoginController loginC = loader.getController();
                loginC.setMainApp(application);
                application.setAndShow(new Scene(newRoot));
                Stage stage = application.getPrimaryStage();
                stage.setTitle("Welcome to FPTS");
                application.setPrimaryStage(stage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
