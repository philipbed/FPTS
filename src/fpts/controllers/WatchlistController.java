package fpts.controllers;

import fpts.FPTSRunner;
import fpts.command.AddToWatchlistCommand;
import fpts.command.Command;
import fpts.command.RemoveFromWatchlistCommand;
import fpts.command.UndoRedoCaretaker;
import fpts.data.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A helper class to assist the Home controller with watchlist functionality
 *
 * @author Daniil Vasin
 */
public class WatchlistController{

    public int returnTo;
    
    public static final int WATCH = 3;
    private final int NOTRIGGER = -1000;

    public static WatchlistController watchlistController;
    public EquityInfo adding;

    @FXML
    TableView<WatchedEquity> watchEquityTable;

    @FXML
    TableColumn<WatchedEquity,String> watchTickerCol, watchEquityName;
    @FXML
    TableColumn<WatchedEquity, Number> watchEquityValue;
    @FXML
    TableColumn<WatchedEquity, String> equityStatus;
    @FXML
    Button browseEquities, addWatchEquity, removeWatchEquity;
    @FXML
    RadioButton toggleOn, toggleOff;
    @FXML
    TextField addWatchField, minField, maxField;
    @FXML
    private ToggleGroup onOffToggle = new ToggleGroup();


    private ObservableList<WatchedEquity> watchEquityData = FXCollections.observableArrayList();

    private FPTSRunner application;

    private EquitySearchController equitySearchController;
    private HomeController homeController;
    private BuySellController buySellController;

    private Portfolio currentPortfolio;

    private UndoRedoCaretaker careTaker = UndoRedoCaretaker.getCaretakerInstance();

    /**
     * A setter for home controller.
     * @param hc The home controller.
     */
    public void setHomeController(HomeController hc){
        homeController = hc;
    }

    /**
     * Initializes the watchlist.
     * @param currentP The current portfolio.
     */
    public void initWatchlist(Portfolio currentP){
        currentPortfolio = currentP;
        toggleOff.setToggleGroup(onOffToggle);
        toggleOn.setToggleGroup(onOffToggle);
        toggleOn.setSelected(true);

        initWatchlistTable();
    }

    /**
     * Initializeds the watchlist table.
     */
    public void initWatchlistTable(){


        watchEquityData.addAll(currentPortfolio.getWatchlist());
        watchEquityTable.setItems(watchEquityData);


        watchTickerCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WatchedEquity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WatchedEquity, String> param) {
                return param.getValue().equityTickerProperty();
            }
        });

        watchEquityName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WatchedEquity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WatchedEquity, String> param) {
            	try{
            		return param.getValue().equityNameProperty();
            	}catch(Exception e){
            		return new SimpleStringProperty("fake");
            	}
            }
        });

        watchEquityValue.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WatchedEquity, Number>, ObservableValue<Number>>() {
            @Override
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<WatchedEquity, Number> param) {
                return param.getValue().equityValueProperty();
            }
        });

        equityStatus.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<WatchedEquity, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<WatchedEquity, String> param) {
                return param.getValue().getStatusMessage();
            }
        });




    }

    /**
     * Chooses an Equity to Watch
     */
    public void changeTabChooseWatchedEquity (){

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FPTSRunner.class.getResource("views/search.fxml"));
            Parent newRoot = loader.load();

            //Pane newLayout = loader;
            equitySearchController = loader.getController();
            equitySearchController.setControllers(homeController, buySellController, this);
            Stage stage = new Stage();
            stage.setScene(new Scene(newRoot));
            equitySearchController.setMainStage(stage);
            equitySearchController.eqButton.setVisible(true);
            equitySearchController.returnTo = WATCH;
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This function allows user to add an equity to their watchlist
     */
    public void addToWatchlist() {

        double toggle;
        double minimum;
        double maximum;

        if (toggleOn.isSelected()){
            toggle = 1.0;
        }else{
            toggle = 0.0;
        }

        if( minField.getText().equals("") ){
            minimum =  NOTRIGGER;
        }
        else {
            minimum = Double.parseDouble(minField.getText());
        }

        if ( maxField.getText().equals("") ){
            maximum = NOTRIGGER;
        }
        else {
            maximum = Double.parseDouble(maxField.getText());
        }

        double[] triggers = {minimum,toggle,maximum};
        EquityInfo copyEquity = adding;
        AddToWatchlistCommand watchlistCommand = new AddToWatchlistCommand(Portfolio.currentPortfolio,copyEquity,triggers);
        watchlistCommand.execute();
        careTaker.setUndoEmptyProperty();
        careTaker.setRedoEmptyProperty();
        updateWatchEquities();
        addWatchField.setText("");
        minField.setText("");
        maxField.setText("");
        homeController.updateStats();
    }

    /**
     *  Removes an equity to the watchlist
     */
    public void removeWatchedEquity(){
        TablePosition pos = watchEquityTable.getSelectionModel().getSelectedCells().get(0);
        int row = pos.getRow();
    	WatchedEquity selectedEquity = watchEquityTable.getItems().get(row);
        Command removeWatchedEquity = new RemoveFromWatchlistCommand(Portfolio.currentPortfolio, selectedEquity);
    	removeWatchedEquity.execute();
        careTaker.setUndoEmptyProperty();
        careTaker.setRedoEmptyProperty();
        updateWatchEquities();
    }

    /**
     * Updates the watched equities on the table.
     */
    public void updateWatchEquities(){
        watchEquityData.clear();
        watchEquityData.addAll( Portfolio.currentPortfolio.getWatchlist());
        reapplyTableSortOrder(watchEquityTable);
    }

    /**
     * Re sorts the table.
     */
    public void reapplyTableSortOrder(TableView table) {
        ArrayList<TableColumn<EquityInfo, ?>> sortOrder = new ArrayList<TableColumn<EquityInfo, ?>>(table.getSortOrder());
        table.getSortOrder().clear();
        table.getSortOrder().addAll(sortOrder);
    }

}
