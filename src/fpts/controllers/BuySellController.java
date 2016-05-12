package fpts.controllers;

import fpts.*;
import fpts.command.TransactionCommand;
import fpts.command.UndoRedoCaretaker;
import fpts.data.CashAccount;
import fpts.data.Equity;
import fpts.data.EquityInfo;
import fpts.data.Portfolio;
import fpts.data.Transaction;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

import javax.sound.sampled.Port;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller for the Buy/Sell Tab
 *
 * @author Philip Bedward
 */
public class BuySellController {
    public int returnTo;
    public static final int BUYING = 0;
    public static final int SELLING = 1;
    public static final int ADD = 2;

    public CashAccount withdrawing;
    public CashAccount depositing;
    public EquityInfo buying;
    public Equity selling;
    public EquityInfo adding;

    @FXML
    Button buyChooseAcctButton, buyChooseEqButton,
            SellButton, buyButton, sellChooseEqButton, sellChooseAcctButton;

    @FXML
    TextField sellNumShares;

    @FXML
    Text lowerText, upperText;

    @FXML
    Label sellText, buyText;

    @FXML
    TextField buyLossField, buyGainField,sellLossField, sellGainField,buyNumShares;

    private ToggleGroup buySellToggle = new ToggleGroup();
    @FXML
    RadioButton buyRadio, sellRadio;


    // a list of transactions whose changes will be observable to the user.
    ObservableList<Transaction> transactionData = FXCollections.observableArrayList();

    private HomeController homeController;
    private CashAccountController cashAccountController;
    private EquitySearchController equitySearchController;
    private WatchlistController watchlistController;
    private UserEquityController userEquityController;

    private Portfolio currentPortfolio;

    private UndoRedoCaretaker careTaker = UndoRedoCaretaker.getCaretakerInstance();

    /**
     * A setter for home controller.
     * @param hc The home controller.
     */
    public void setHomeController(HomeController hc){
        this.homeController = hc;
    }

    /**
     * A setter for equity controller.
     * @param uec The user equity controller.
     */
    public void setEquityControllers(UserEquityController uec){
        this.userEquityController = uec;
    }

    /**
     * A setter for cash account controller.
     * @param cac The cash account controller.
     */
    public void setCashAccountController(CashAccountController cac){this.cashAccountController = cac;}

    /**
     * A setter for the watchlist controller.
     * @param wat The watchlist controller.
     */
    public void setWatchlistController(WatchlistController wat){this.watchlistController = wat;};
   
    /**
     * Helper method to initialize
     */
    public void initBuySell(Portfolio currentP){
        currentPortfolio = currentP;
        buyRadio.setToggleGroup(buySellToggle);
        buyRadio.setSelected(true);
        sellRadio.setToggleGroup(buySellToggle);
        buyOrSell();
    }

    /**
     * This function is repsonsible for initializing the
     * transaction table with all the data from the
     * current portfolio.
     */
    public void initTransactionTable(){

        transactionData.addAll(Portfolio.currentPortfolio.getTransactions());
        homeController.transTable.setItems(transactionData);

        homeController.transHistory.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Transaction, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Transaction, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return p.getValue().transactionProperty();
            }
        });
    }

    /**
     * This event handler will switch to the appropriate
     * tab when a button is clicked in the buy/sell tab.
     */
    public void changeTabChooseAcctBuy(){
        returnTo = BUYING;
        homeController.tabPane.getSelectionModel().select( homeController.acctTab );
        cashAccountController.selectAcctBttn.setVisible(true);
        cashAccountController.removeButton.setVisible(false);


    }

    /**
     * This event handler will switch to the appropriate
     * tab when a button is clicked in the buy/sell tab.
     */
    public void changeTabChooseAcctSell(){
        returnTo = SELLING;
        homeController.tabPane.getSelectionModel().select( homeController.acctTab );
        cashAccountController.selectAcctBttn.setVisible(true);
        cashAccountController.removeButton.setVisible(false);
    }

    /**
     * This event handler will switch to the appropriate
     * tab when an equity is clicked in buy.
     */
    public void changeTabChooseEquityBuy(){

        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(FPTSRunner.class.getResource("views/search.fxml"));
            Parent newRoot = loader.load();

            //Pane newLayout = loader;
            equitySearchController = loader.getController();
            equitySearchController.setControllers(homeController, this, watchlistController);
            Stage stage = new Stage();

            stage.setScene(new Scene(newRoot));
            equitySearchController.setMainStage(stage);
            equitySearchController.eqButton.setVisible(true);
            equitySearchController.returnTo = BUYING;
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This event handler will switch to the appropriate
     * tab when an equity is clicked in sell.
     */
    public void changeTabChooseEquitySell(){
        returnTo = SELLING;
        homeController.tabPane.getSelectionModel().select( homeController.equityTab );
        homeController.selectUserEquity.setVisible(true);
        homeController.removeSelectedEquity.setVisible(false);
    }


    /**
     * This function allows the user to "buy" an equity and records
     * this as a transaction with the cash account in the portfolio.
     */
    public void buyEquity() {

        int numOfEqs = Integer.parseInt(buyNumShares.getText());
        CashAccount copyAcct = withdrawing;
        EquityInfo copyEquity = buying;

        double valueLost = numOfEqs * copyEquity.getValue();

        double sharesGained = valueLost/copyEquity.getValue();

        String dateString = getDateString();

        CashAccount transAcct = new CashAccount(valueLost,copyAcct.getName(),dateString);
        Equity transEquity = new Equity(copyEquity,copyEquity.getValue(),dateString,sharesGained,true);


//        if(!Portfolio.currentPortfolio.addTransaction(transEquity, transAcct, dateString )){
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error Dialog");
//            alert.setHeaderText("Transaction failed.");
//            alert.setContentText("You probably don't have enough money. Double check and try again.");
//
//            alert.showAndWait();
//        }else{
//            homeController.tabPane.getSelectionModel().select( homeController.homeTab );
//            buyNumShares.setText("");
//            buyLossField.setText("");
//            buyGainField.setText("");
//        }
        TransactionCommand transCommand = new TransactionCommand(currentPortfolio,transEquity, transAcct, dateString );
        if(!transCommand.canPerform()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Transaction failed.");
            alert.setContentText("You probably don't have enough money. Double check and try again.");

            alert.showAndWait();
        }
        else{
            transCommand.execute();
            careTaker.setUndoEmptyProperty();
            careTaker.setRedoEmptyProperty();
            homeController.tabPane.getSelectionModel().select( homeController.homeTab );
            buyNumShares.setText("");
            buyLossField.setText("");
            buyGainField.setText("");
        }
        updateTransactions();
        cashAccountController.updateCashAccounts();
        homeController.userEquityUpdate();
        homeController.updateEquityChart();
        homeController.updateAccountChart();
        homeController.updateStats();
    }

    /**
     * This function allows the user to sell an equity and records
     * this in the profile via a transaction of the cash account.
     * This function is repsonsible for throwing alerts if the info
     * is not correctly entered in the GUI.
     */
    public void sellEquity(){
        double sharesLost = Double.parseDouble(sellNumShares.getText());
        CashAccount copyAcct = depositing;
        Equity copyEquity = new Equity(selling.getEquityInfo(), selling.getEquityInfo().getValue() , getDateString(), sharesLost, true);


        double valueGained = sharesLost * selling.getEquityInfo().getValue();


        String dateString = getDateString();

        CashAccount transAcct = new CashAccount(valueGained,copyAcct.getName(),dateString);
        Equity transEquity = copyEquity;


//        if(!Portfolio.currentPortfolio.addTransaction( transAcct, copyEquity, dateString )){
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error Dialog");
//            alert.setHeaderText("Transaction failed.");
//            alert.setContentText("You probably don't have enough shares. Double check and try again.");
//            alert.showAndWait();
//        }else{
//            homeController.tabPane.getSelectionModel().select( homeController.homeTab );
//            sellNumShares.setText("");
//            sellLossField.setText("");
//            sellGainField.setText("");
//        }

        TransactionCommand transCommand = new TransactionCommand(currentPortfolio,transAcct, transEquity, dateString );

        if(!transCommand.canPerform()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Transaction failed.");
            alert.setContentText("You probably don't have enough money. Double check and try again.");

            alert.showAndWait();
        }
        else{
            transCommand.execute();
            careTaker.setUndoEmptyProperty();
            careTaker.setRedoEmptyProperty();
            homeController.tabPane.getSelectionModel().select( homeController.homeTab );
            buyNumShares.setText("");
            buyLossField.setText("");
            buyGainField.setText("");
        }

        updateTransactions();
        cashAccountController.updateCashAccounts();
        userEquityController.updateUserEquityData();
        homeController.updateEquityChart();
        homeController.updateAccountChart();
        homeController.updateStats();
    }

    /**
     * This function will change the content of the buy/sell tab whenever
     * one of the radio buttons are selected
     */
    public void buyOrSell(){

        boolean invisible = false;
        boolean visible = true;
        String gain = "Gain";
        String loss = "Loss";
        if ( buyRadio.isSelected() ){
            sellLossField.setVisible( invisible );
            sellGainField.setVisible( invisible );
            SellButton.setVisible( invisible );
            sellText.setVisible( invisible );
            sellNumShares.setVisible( invisible );
            sellChooseEqButton.setVisible( invisible );
            sellChooseAcctButton.setVisible( invisible );

            buyChooseAcctButton.setVisible( visible );
            buyChooseEqButton.setVisible( visible );
            buyButton.setVisible( visible );
            buyText.setVisible( visible );
            buyNumShares.setVisible( visible );
            buyLossField.setVisible( visible );
            buyGainField.setVisible( visible );

            lowerText.setText( gain );
            upperText.setText( loss );

        }
        else if( sellRadio.isSelected() ){

            buyButton.setVisible( invisible );
            buyText.setVisible( invisible );
            buyNumShares.setVisible( invisible );
            buyChooseEqButton.setVisible( invisible );
            buyChooseAcctButton.setVisible( invisible );
            buyLossField.setVisible( invisible );
            buyGainField.setVisible( invisible );

            sellChooseAcctButton.setVisible( visible );
            sellChooseEqButton.setVisible( visible);
            SellButton.setVisible( visible );
            sellText.setVisible( visible );
            sellNumShares.setVisible( visible );
            sellLossField.setVisible( visible );
            sellGainField.setVisible( visible );

            lowerText.setText( loss );
            upperText.setText( gain );
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

    /**
     * This function is responsible for updating the transactions
     * in the portfolio
     */
    public void updateTransactions(){
        transactionData.clear();
        transactionData.addAll(Portfolio.currentPortfolio.getTransactions());
        homeController.reapplyTableSortOrder(homeController.transTable);
    }
}
