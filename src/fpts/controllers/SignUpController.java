package fpts.controllers;

import fpts.FPTSRunner;
import fpts.controllers.LoginController;
import fpts.data.Holding;
import fpts.data.Portfolio;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class is the controller for the signup up view.
 * @author Philip Bedward
 */
public class SignUpController {

    // contains a reference to the application and
    // the loginController
    private FPTSRunner application;
    private LoginController loginC;


    /**
     * the FXML decorators allow you to use the
     * fx:id's that you create inorder to manipulate data in
     * the fx componenets
     */
    @FXML
    Label fileLabel, fileQuestion;

    @FXML
    RadioButton upload, noUpload;

    @FXML
    TextField username, fileString;

    @FXML
    PasswordField password;

    @FXML
    Button fileChooser;

    ToggleGroup toggleGroup;

    /**
     * Automatically called by javaFX.
     */
    @FXML
    public void initialize(){
        toggleGroup = new ToggleGroup();

        upload.setToggleGroup(toggleGroup);
        noUpload.setToggleGroup(toggleGroup);
        noUpload.setSelected(true);

        setHandler();
    }

    /**
     * When the User needs to create a new account then
     * they are reirected from the login views to the sign up views
     *
     * This gives the controller the objects it needs.
     * @param app - the current application
     * @param controller - the LoginController
     */
    public void setMainAndLogin( FPTSRunner app,LoginController controller ){
        this.application = app;
        loginC = controller;
    }

    /**
     * This is the event handler called when the user clicks "Sign Up"
     */
    @FXML
    public void register(){
        Portfolio p = new Portfolio( username.getText(), password.getText() );
        Portfolio.currentPortfolio = p;
        Portfolio.currentPortfolio.save();

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FPTSRunner.class.getResource("views/home.fxml"));
            Parent newRoot = loader.load();
            //Pane newLayout = loader;
            HomeController hc = loader.getController();
            hc.setMainApp(application);
            application.setAndShow(new Scene(newRoot));
            loginC.setUserIDToTitle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //loginC.loginAfterSignUp( username.getText(), password.getText() );
    }

    /**
     * This function is responsible for canceling the login
     * loader when signing up
     */
    @FXML
    public void cancel(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FPTSRunner.class.getResource("views/login.fxml"));
            Parent newRoot = loader.load();
            loginC = loader.getController();
            loginC.setMainApp(application);
            application.setAndShow(new Scene(newRoot));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Helper function to be used when the user wants to
     * import existing holdings and transactions
     *
     * Not used
     */
    private void importAndRegister(){
        Portfolio p = new Portfolio(username.getText(),password.getText(), fileString.getText());
        Portfolio.currentPortfolio = p;
        Portfolio.currentPortfolio.save();
        //loginC.loginAfterSignUp(username.getText(),password.getText());
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FPTSRunner.class.getResource("views/home.fxml"));
            Parent newRoot = loader.load();
            //Pane newLayout = loader;
            HomeController hc = loader.getController();
            hc.setMainApp(application);
            application.setAndShow(new Scene(newRoot));
            loginC.setUserIDToTitle();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileString.clear();

    }

    /**
     * used to decide which registration method to call.
     */
    public void delegateRegister(){
        if(upload.isSelected()){
            importAndRegister();
        }
        else if(noUpload.isSelected()){
            register();
        }
    }

    /**
     * Use to show and hide the  input field when the user clicks the radio buttons
     */
    public void show(){
        fileQuestion.setText("Would you like to import existing holdings and transactions?");

        if( upload.isSelected()  ){
            fileString.setVisible(true);
            fileLabel.setVisible(true);
            fileChooser.setVisible(true);
        }
        else if( noUpload.isSelected() ){
            fileString.setVisible(false);
            fileLabel.setVisible(false);
            fileChooser.setVisible(false);
        }
    }

    /**
     * Creates an eventHandler for a file chooser button
     */
    private void setHandler(){
        fileChooser.setOnAction(new EventHandler<ActionEvent>() {

            @Override


            public void handle(ActionEvent event) {


                FileChooser fileChooser = new FileChooser();


                fileChooser.setTitle("Select File");


                //Set extension filter


                FileChooser.ExtensionFilter extFilter =

                        new FileChooser.ExtensionFilter("*", "*");

                fileChooser.getExtensionFilters().add(extFilter);




                //Show open file dialog

                File file = fileChooser.showOpenDialog(null);

                if(file!=null)

                    fileString.setText(file.getPath());

            }
        });
    }
}






