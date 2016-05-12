package fpts.controllers;

import fpts.FPTSRunner;
import fpts.command.AddEquityCommand;
import fpts.command.Command;
import fpts.command.UndoRedoCaretaker;
import fpts.data.EquityInfo;
import fpts.data.Portfolio;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller for 'Add' tab
 *
 * @author Philip Bedward
 */
public class AddEquityController {

    public static final int ADD = 2;

    private UndoRedoCaretaker careTaker = UndoRedoCaretaker.getCaretakerInstance();

    public EquityInfo adding;

    @FXML
    TextField equityDisplayField, addNumShares;

    private HomeController homeController;
    private UserEquityController userEquityController;
    private BuySellController buySellController;
    private WatchlistController watchlistController;

    private FPTSRunner application;

    /**
     * A method to set references to controllers.
     * @param hc The home controller.
     * @param uec The user equity controller.
     * @param bsc The buy sell controller.
     * @param wls The watch list controller.
     */
    public void setController(HomeController hc, UserEquityController uec, BuySellController bsc, WatchlistController wls){
        homeController = hc;
        userEquityController = uec;
        buySellController = bsc;
        watchlistController = wls;
    }

    /**
     * A method to set the reference to the main app.
     * @param app The main app.
     */
    public void setMainApp(FPTSRunner app){
        application = app;
    }

    /**
     * This function is responsible for adding a new Equity.
     */
    public void addEquity(){
        Command addEq = new AddEquityCommand(Portfolio.currentPortfolio, adding, adding.getValue(), getDateString(), Integer.parseInt(addNumShares.getText()), false);
        addEq.execute();
        careTaker.setUndoEmptyProperty();


        //Portfolio.currentPortfolio.importEquity(adding, adding.getValue(), getDateString(), Integer.parseInt(addNumShares.getText()), false);
        userEquityController.updateUserEquityData();
        homeController.updateEquityChart();
        addNumShares.setText("");
        equityDisplayField.setText("");
        homeController.tabPane.getSelectionModel().select(homeController.equityTab);
        homeController.updateStats();
    }

    /**
     * This event handler will switch to the appropriate
     * tab when an equity is clicked in add.
     */
    public void changeTabChooseEquityAdd(){
        //searchTab.setDisable(false);
        //tabPane.getSelectionModel().select( searchTab );
        //eqButton.setVisible(true);

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FPTSRunner.class.getResource("views/search.fxml"));
            Parent newRoot = loader.load();
            //Pane newLayout = loader;
            EquitySearchController equitySearchController = loader.getController();
            equitySearchController.setMainApp(application);
            equitySearchController.setControllers(homeController,buySellController, watchlistController);
            equitySearchController.setAddEquityController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(newRoot));
            equitySearchController.setMainStage(stage);
            equitySearchController.eqButton.setVisible(true);
            equitySearchController.returnTo = ADD;
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper method to format a date of the current date
     * and return it as a string
     * @return - the current date as a string
     */
    private String getDateString(){

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy @ hh:mm:ss a");
        //get current date time with Date()
        Date date = new Date();
        String dateString = dateFormat.format(date);

        return dateString;
    }
}
