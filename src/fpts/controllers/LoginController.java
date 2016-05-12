package fpts.controllers;

import fpts.FPTSRunner;
import fpts.data.Portfolio;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * Controller Class to handle logging into the fpts app
 * @author Philip Bedward
 */
public class LoginController {

    @FXML
    // button name matches the fx:id in login.fxml
    // references the same button that is in the gui
    private Button myBt;

    @FXML
    TextField username;

    @FXML
    PasswordField password;

    private FPTSRunner application;

    /**
     * A setter for the main app.
     * @param app The main app.
     */
    public void setMainApp(FPTSRunner app){
        this.application = app;
    }

    /**
     * Used for a regular login
     */
    @FXML
    public void login(){
        try{
            Portfolio.load(username.getText(), password.getText());
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(FPTSRunner.class.getResource("views/home.fxml"));
                Parent newRoot = loader.load();
                //Pane newLayout = loader;

                HomeController hc = loader.getController();
                hc.setMainApp(application);
                application.setAndShow(new Scene(newRoot));

                setUserIDToTitle();
            }catch (IOException e){
                e.printStackTrace();
            }



        }catch(Exception e){
            String errorMsg = e.getMessage();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("An error occured when you tried to log in");
            alert.setContentText(errorMsg);

            alert.showAndWait();
        }
    }

    /**
     * Called if a user just registered with the fpts
     * @param username - User's username
     * @param password - User's password
     */
    public void loginAfterSignUp(String username, String password){

        try {
            //Portfolio.load(username, password);

            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(FPTSRunner.class.getResource("views/home.fxml"));
                Parent newRoot = loader.load();
                //Pane newLayout = loader;
                HomeController hc = loader.getController();
                hc.setMainApp(application);
                application.setAndShow(new Scene(newRoot));
                setUserIDToTitle();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            String errorMsg = e.getMessage();

        }


    }

    /**
     * If the user clicks sign up then this event handler will change the views
     */
    @FXML
    public void handleSignUpAction() {
        try {
            //sets the new scene and changes the controller
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FPTSRunner.class.getResource("views/signup.fxml"));
            Parent newRoot = loader.load();

            // get the associated controller
            SignUpController controller = loader.getController();

            // provide the controller with the data it needs
            controller.setMainAndLogin(application,this);
            application.setAndShow(new Scene(newRoot));
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    /**
     * Private helper function that sets the title of the gui to say:
     * "[user]'s Portfolio" when you login
     */
    public void setUserIDToTitle(){
        Stage stage = application.getPrimaryStage();
        String userID = Portfolio.currentPortfolio.getUserID();
        userID = userID.substring(0,1).toUpperCase() + userID.substring(1);
        stage.setTitle(userID+"'s Portfolio");
        application.setPrimaryStage(stage);
    }
}
