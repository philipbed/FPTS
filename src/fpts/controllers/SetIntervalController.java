package fpts.controllers;

import fpts.data.EquityInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Created by Phil on 4/10/2016.
 */
public class SetIntervalController {

    @FXML
    Button saveBttn;

    @FXML
    TextField timeInput;

    Stage stage;

    /**
     * A setter for the main stage.
     * @param stage The main stage.
     */
    public void setMainStage(Stage stage){
        this.stage = stage;
    }

    /**
     * A method to set the time interval between metadata value updates.
     */
    public void setTimeInterval(){
        Double timeInterval = Double.parseDouble(timeInput.getText());
        EquityInfo.startUpdateThread(timeInterval);
        stage.close();
    }
}
