package fpts.controllers;

import fpts.command.Command;
import fpts.command.RemoveEquityCommand;
import fpts.command.UndoRedoCaretaker;
import fpts.data.Equity;
import fpts.data.Portfolio;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.util.ArrayList;

/**
 * This will be the controller for the 'User Equities' tab
 * @author Philip Bedward
 */
public class UserEquityController {

    // user equityTable
    @FXML
    TableView<Equity> userEquityTable;
    @FXML
    TableColumn<Equity, String> userTickerCol, userEquityName, userEquityMarketIndex;
    @FXML
    TableColumn<Equity, Number>  userEquityValue;

    @FXML
    TextField userTickerField, userIndexField,userEquityField;

    @FXML
    Button removeSelectedEquity, selectUserEquity;

    @FXML
    MenuItem undoItem, redoItem;

    ObservableList<Equity> userEquityData = FXCollections.observableArrayList();

    private HomeController homeController;
    private BuySellController buySellController;

    private UndoRedoCaretaker careTaker = UndoRedoCaretaker.getCaretakerInstance();

    /**
     * A setter for the controllers.
     * @param homeController The home controller.
     * @param buySellController The buy sell controller.
     */
    public void setControllers(HomeController homeController,BuySellController buySellController){
        this.homeController = homeController;
        this.buySellController = buySellController;
    }

    /**
     * Automatically called by javaFX.
     */
    public void initialize(){
        initUserEquityTable();
        addActionListeners();
    }

    /**
     * This function is responsible for initializing
     * the equity table with all the data from the
     * current portfolio.
     */
    private void initUserEquityTable(){
        userEquityData.addAll(Portfolio.currentPortfolio.getEquities());
        userEquityTable.setItems(userEquityData);
        String userID = Portfolio.currentPortfolio.getUserID();
        userID = userID.substring(0,1).toUpperCase() + userID.substring(1);

        userTickerCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Equity, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Equity, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return p.getValue().equityTickerProperty();
            }
        });

        userEquityName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Equity, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Equity, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return p.getValue().equityNameProperty();
            }
        });

        userEquityMarketIndex.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Equity, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Equity, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return p.getValue().equityMarketIndex();
            }
        });

        userEquityValue.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Equity, Number>, ObservableValue<Number>>() {
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<Equity, Number> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return p.getValue().equityValueProperty();
            }
        });

        addActionListeners();
    }

    /**
     * Removes the equity the user just selected from their own collection
     * of equities
     */
    public void removeSelectedEquity(){
        TablePosition pos = userEquityTable.getSelectionModel().getSelectedCells().get(0);
        int row = pos.getRow();
        Equity selectedEquity = userEquityTable.getItems().get(row);

        Command removeEquity = new RemoveEquityCommand(Portfolio.currentPortfolio,selectedEquity);
        removeEquity.execute();
        careTaker.setUndoEmptyProperty();
        careTaker.setRedoEmptyProperty();
        //currentPortfolio.removeHolding(userEquityTable.getItems().get(row));
        updateUserEquityData();
        homeController.updateEquityChart();
        homeController.updateStats();
    }

    /**
     * Event Handler that finds the table row and sends
     * the selected data to appropriate fields and removes the tab until
     * needed again by the user.
     */
    public void selectUserEquities(){

        TablePosition pos = userEquityTable.getSelectionModel().getSelectedCells().get(0);
        int row = pos.getRow();

        // Item here is the table view type:
        Equity selectedEquity = userEquityTable.getItems().get(row);


        // set the text of the field

        homeController.tabPane.getSelectionModel().select( homeController.buySellTab );
        selectUserEquity.setVisible(false);
        removeSelectedEquity.setVisible(true);
        buySellController.sellLossField.setText(selectedEquity.getName());
        buySellController.selling = selectedEquity;

    }

    /**
     * A function that updates all the equities and clears
     * data on the current portfolio
     */
    public void updateUserEquityData(){
        userEquityData.clear();
        userEquityData.addAll(Portfolio.currentPortfolio.getEquities());
        reapplyUserEquityTableSortOrder(userEquityTable);

    }

    /**
     * Re sorts the table based on the new content after an update.
     * @param table - the table that needs to be re-sorted
     */
    public void reapplyUserEquityTableSortOrder(TableView table){
        ArrayList<TableColumn<Equity, ?>> sortOrder = new ArrayList<TableColumn<Equity, ?>>(table.getSortOrder());
        table.getSortOrder().clear();
        table.getSortOrder().addAll(sortOrder);
    }

    /**
     * This function adds action listeners for each
     * holding (ticker field, index field, and equity
     * field).
     */
    private void addActionListeners(){
        userTickerField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateUserEquityResults();
            }
        });

        userIndexField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateUserEquityResults();
            }
        });


        userEquityField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateUserEquityResults();
            }
        });




    }

    /**
     * A helper function to update the table of user's equities using the
     *  static Search method defined the Portolio class
     */
    public void updateUserEquityResults(){
        userEquityData.clear();
        String equityName, tickerSymbol, marketIndex;
        equityName = userEquityField.getText();
        tickerSymbol = userTickerField.getText();
        marketIndex = userIndexField.getText();

        ArrayList<Equity> filteredEquities = Portfolio.currentPortfolio.getMatchingEquities(equityName,tickerSymbol,marketIndex);
        userEquityData.addAll( filteredEquities);

        reapplyUserEquityTableSortOrder(userEquityTable);
    }
}
