package fpts.controllers;


import fpts.*;
import fpts.command.*;
import fpts.data.CashAccount;
import fpts.data.Equity;
import fpts.data.EquityInfo;
import fpts.data.Holding;
import fpts.data.Portfolio;
import fpts.data.Transaction;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Text;
import javafx.stage.*;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class is intended to be the controller for main portfolio view when users login
 * @author Philip Bedward, Ryan Hartzell, William Estey
 */
public class HomeController extends LogoutParent{


    private FPTSRunner application;


    @FXML
    Tab buySellTab, acctTab, homeTab,
            simulationResultTab, equityTab, addTab, watchTab;
    @FXML
    Text acctVal, eqVal, totalPortfolioVal;


    @FXML
    Button  removeSelectedEquity, selectUserEquity;


    @FXML
    TabPane tabPane;




    @FXML
    TextField equityDisplayField, addNumShares;

    // transaction history table
    @FXML
    TableView<Transaction> transTable;
    @FXML
    TableColumn<Transaction, String> transHistory;


    @FXML
    PieChart equityChart, acctChart;

    @FXML
    MenuItem undoItem, redoItem, saveItem, setIntervalItem;

    @FXML
    private CashAccountController acctTabController;
    @FXML
    private BuySellController buySellTabController;
    @FXML
    private WatchlistController watchTabController;
    @FXML
    private UserEquityController equityTabController;
    @FXML
    private AddEquityController addTabController;

    ProgressBar pb;

    // a list of cash account data whose changes will be observable to the user.
    ObservableList<Equity> userEquityData = FXCollections.observableArrayList();


    ObservableList<PieChart.Data> equityPiechartData = FXCollections.observableArrayList();

    ObservableList<PieChart.Data> acctPiechartData = FXCollections.observableArrayList();


    public EquityInfo adding;

    private double currAcctVal;

    private double currEquityVal;

    private Portfolio currentPortfolio = Portfolio.currentPortfolio;

    private UndoRedoCaretaker careTaker = UndoRedoCaretaker.getCaretakerInstance();



    /**
     * Sets the current application that is being viewed
     * @param app
     */
    public void setMainApp(FPTSRunner app) {
        application = app;
    }

    /**
     * This function is the first one called. It sets ip the view
     * after it is loaded.
     */
    @FXML
    public void initialize() {


        // Give the CashAccountController a reference to the home controller
        acctTabController.setMainController(this);
        acctTabController.setBuySellController(buySellTabController);

        buySellTabController.setCashAccountController(acctTabController);
        buySellTabController.setHomeController(this);
        buySellTabController.setWatchlistController(watchTabController);
        buySellTabController.initBuySell(currentPortfolio);

        equityTabController.setControllers(this,buySellTabController);

        watchTabController.setHomeController(this);
        watchTabController.initWatchlist(currentPortfolio);


        addTabController.setController(this,equityTabController,buySellTabController,watchTabController);


        Image image = new Image(getClass().getResourceAsStream("undo.png"),12,12,false,false);
        undoItem.setGraphic(new ImageView(image));
        undoItem.setDisable(careTaker.isUndoStackEmpty().get());

        image = new Image(getClass().getResourceAsStream("redo icon.jpg"),19,19,false,false);
        redoItem.setGraphic(new ImageView(image));
        redoItem.setDisable(careTaker.isRedoStackEmpty().get());

        saveItem.setAccelerator(KeyCombination.keyCombination("Ctrl+S"));

        setIntervalItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {

                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(FPTSRunner.class.getResource("views/setInterval.fxml"));
                    Parent newRoot = loader.load();

                    //Pane newLayout = loader;
                    SetIntervalController  intervalController = loader.getController();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(newRoot));
                    intervalController.setMainStage(stage);


                    stage.show();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        // initialize other tables

        buySellTabController.initTransactionTable();
        tabPane.getTabs().remove(simulationResultTab);


        addActionListeners();

        initEquityPieChart();
        initAccountPieChart();

        updateStats();
    }



    /**
     * Method to sort the table when the results are returned
     */
    public void reapplyTableSortOrder(TableView table) {
        ArrayList<TableColumn<EquityInfo, ?>> sortOrder = new ArrayList<TableColumn<EquityInfo, ?>>(table.getSortOrder());
        table.getSortOrder().clear();
        table.getSortOrder().addAll(sortOrder);
    }

    /**
     * Make a call to the logout method
     */
    public void logout(){
        super.logout(this.application);
    }

    /**
     * An event handler to save a portfolio if the 'save
     * button has been clicked
     */
    public void savePortfolio(){
        Portfolio currP = Portfolio.currentPortfolio;
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if(currP.hasChanged()){
            Portfolio.currentPortfolio.save();

            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Your changes have been saved");


        }
        else{
            alert.setTitle("Information Dialog");
            alert.setHeaderText(null);
            alert.setContentText("You have made no changes to your portfolio");
        }

        alert.showAndWait();
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

    /**
     * This function adds action listeners for each
     * holding (ticker field, index field, and equity
     * field).
     */
    private void addActionListeners(){

        careTaker.isUndoStackEmpty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                // if the stack was not empty and now it is
                // then disable the undo menu item
                if( oldValue != newValue)  {
                    undoItem.setDisable( newValue );
                }

                // if the stack was empty and now it is not
                // then enable the undo menu item
                else if( oldValue != newValue ){
                    undoItem.setDisable( newValue );
                }
            }
        });

        careTaker.isRedoStackEmpty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {

                // if the stack was not empty and now it is
                // then disable the undo menu item
                if( !oldValue && newValue)  {
                    redoItem.setDisable( newValue );
                }

                // if the stack was empty and now it is not
                // then enable the undo menu item
                else if( oldValue && !newValue){
                    redoItem.setDisable( newValue );
                }
            }
        });
        
        
    }

    /**
     * Method made to export files for the user to save
     * when the export button is clicked
     */
    public void exportHandle(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");

        fileChooser.setInitialFileName("export.csv");
        File savedFile = fileChooser.showSaveDialog(null);

        if (savedFile != null) {
            Portfolio.currentPortfolio.export(savedFile.toString());
        }


    }

    /**
     * Initialize the Equity pie chart
     */
    private void initEquityPieChart(){
        for(Equity eq : userEquityData){
            String ticker = eq.getEquityInfo().getTS();
            double shareVal = eq.getShareValue();
            equityPiechartData.add(new PieChart.Data(ticker,shareVal));
        }

        equityChart.setData(equityPiechartData);
    }

    /**
     * Initialize the Cash Account Pie Chart
     */
    private void initAccountPieChart(){
        for(CashAccount ca : acctTabController.cashAcctData){
            double amount = ca.getTotalValue();
            String name = ca.getName();
            acctPiechartData.add(new PieChart.Data(name,amount));
        }

        acctChart.setData(acctPiechartData);

    }

    /**
     * Helper method used to update the pie Chart of equities
     * owned by the user.
     */
    public void updateEquityChart(){
        equityPiechartData.clear();
        for(Equity eq : userEquityData){
            String ticker = eq.getEquityInfo().getTS();
            double shareVal = eq.getShareValue();
            equityPiechartData.add(new PieChart.Data(ticker,shareVal));
        }
    }

    /**
     * Helper method used to update the pie chart of cash accounts
     * owned by the user.
     */
    public void updateAccountChart(){
        acctPiechartData.clear();
        for(CashAccount ca : acctTabController.cashAcctData){
            double amount = ca.getTotalValue();
            String name = ca.getName();
            acctPiechartData.add(new PieChart.Data(name,amount));
        }
    }

    /**
     * Method to calculate the total value of the holdings provided
     *
     * @param observableList - collection of Holdings
     * @return - a double representing the monetary value of the current holding
     */
    private double calcTotalHoldingVal(ObservableList observableList){
        Visitor aggVisitor = new AggregateVisitor();

        double sum = 0;
        for(Object obj: observableList){
            Holding holding = (Holding) obj;
            sum += holding.accept(aggVisitor);
        }

        return sum;
    }

    /**
     * Method used to update all statistics when actions are performed
     */
    public void updateStats(){
        currAcctVal = calcTotalHoldingVal(acctTabController.cashAcctData);
        acctVal.setText(String.format("%.2f",currAcctVal));

        currEquityVal = calcTotalHoldingVal(userEquityData);
        eqVal.setText(String.format("%.2f",currEquityVal));

        totalPortfolioVal.setText( String.format("%.2f", currAcctVal + currEquityVal ));
    }

    /**
     * Method to undo the last Command that was executed
     */
    public void undo(){
        if(!undoItem.isDisable()) {
            Command command = careTaker.popFromUndo();
            careTaker.setUndoEmptyProperty();
            command.unExecute();


            careTaker.setRedoEmptyProperty();

            buySellTabController.updateTransactions();
            acctTabController.updateCashAccounts();
            this.userEquityUpdate();
            this.updateEquityChart();
            this.updateAccountChart();
            this.updateStats();
        }
    }

    /**
     * Method to undo the last Command that was executed
     */
    public void redo(){
        if(!redoItem.isDisable()) {
            Command command = careTaker.popFromRedo();
            careTaker.setRedoEmptyProperty();
            command.execute();

            careTaker.setUndoEmptyProperty();


            buySellTabController.updateTransactions();
            acctTabController.updateCashAccounts();
            this.userEquityUpdate();
            this.updateEquityChart();
            this.updateAccountChart();
            this.updateStats();
        }
    }

    /**
     * A function that updates all the equities and clears
     * data on the current portfolio
     */
    public void userEquityUpdate(){
        equityTabController.updateUserEquityData();

    }


}


