package fpts.controllers;

import fpts.FPTSRunner;
import fpts.command.AddCashAccountCommand;
import fpts.command.Command;
import fpts.command.RemoveCashAccountCommand;
import fpts.command.UndoRedoCaretaker;
import fpts.data.CashAccount;
import fpts.data.Portfolio;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Controller for the Cash Account Tab
 *
 * @author Philip Bedward
 */
public class CashAccountController {

    @FXML
    TableView<CashAccount> cashAcctTable;

    @FXML
    TableColumn<CashAccount, String> acctName, acctDate;
    @FXML
    TableColumn<CashAccount, Number> acctBalance;

    @FXML
    TextField acctBalanceField, acctNameField;

    @FXML
    Button removeButton, createButton, selectAcctBttn;

    private FPTSRunner application;

    private HomeController hc;
    private BuySellController buySellController;

    // a list of cash account data whose changes will be observable to the user.
    public ObservableList<CashAccount> cashAcctData = FXCollections.observableArrayList();

    private UndoRedoCaretaker caretaker = UndoRedoCaretaker.getCaretakerInstance();

    /**
     * A setter for home controller.
     * @param hc The home controller.
     */
    public void setMainController(HomeController hc){
        this.hc = hc;
    }

    /**
     * A setter for buy sell controller.
     * @param bsc The buy sell controller.
     */
    public void setBuySellController(BuySellController bsc){this.buySellController = bsc;}

    /**
     * Automatically called by javaFX.
     */
    @FXML
    public void initialize(){
        initCashTable();
    }

    /**
     * Helper method to prepopulate the cash account table upon load
     */
    public void initCashTable() {

        cashAcctData.addAll(Portfolio.currentPortfolio.getCashAccounts());
        cashAcctTable.setItems(cashAcctData);


        acctBalance.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CashAccount, Number>, ObservableValue<Number>>() {
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<CashAccount, Number> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return p.getValue().balanceProperty();
            }
        });

        acctDate.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CashAccount, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CashAccount, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return p.getValue().dateProperty();
            }
        });

        acctName.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CashAccount, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<CashAccount, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return p.getValue().nameProperty();
            }

        });

        cashAcctTable.setOnMouseClicked( new EventHandler< MouseEvent>() {
            public void handle(MouseEvent me) {
                if(cashAcctTable.getSelectionModel().getSelectedCells().size() != 0) {
                    removeButton.setDisable(false);
                    selectAcctBttn.setDisable(false);
                    createButton.setDisable(true);
                }

            }
        });

        acctBalanceField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                removeButton.setDisable(true);
                createButton.setDisable(false);
                selectAcctBttn.setDisable(true);
            }
        });

        acctNameField.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                removeButton.setDisable(true);
                createButton.setDisable(false);
                selectAcctBttn.setDisable(true);
            }
        });



    }


    /**
     * A function that updates all the cash accounts and clears
     * data on the current portfolio
     */
    public void updateCashAccounts(){
        cashAcctData.clear();
        cashAcctData.addAll(Portfolio.currentPortfolio.getCashAccounts());
        reapplyTableSortOrder(cashAcctTable);
    }

    /**
     * Event handler that creates a new account after
     * The 'create' button has been clicked
     */
    public void createAccount(){

        String dateString = getDateString();

        double init = Double.parseDouble(acctBalanceField.getText());
        String name = acctNameField.getText();
        //Portfolio.currentPortfolio.importCashAccount( init , name,dateString );

        Command createCashAcct = new AddCashAccountCommand(Portfolio.currentPortfolio,init,name,dateString);
        createCashAcct.execute();
        caretaker.setUndoEmptyProperty();
        caretaker.setRedoEmptyProperty();

        updateCashAccounts();

        clearFields();
        hc.updateAccountChart();
        hc.updateStats();
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
     * Helper function to clear all fields after a new account has been made
     */
    private void clearFields(){

        acctBalanceField.clear();
        acctNameField.clear();


    }

    /**
     * Event Handler for the removal of cash accounts.
     * Called when the remove Button is clicked on the
     * Cash Account tab.
     */
    public void removeAcct(){
        boolean enable = true;
        boolean disable = false;
        TablePosition pos = cashAcctTable.getSelectionModel().getSelectedCells().get(0);
        int row = pos.getRow();
        CashAccount selectedAcct = cashAcctTable.getItems().get(row);

        Command removeCashAcct = new RemoveCashAccountCommand(Portfolio.currentPortfolio, selectedAcct);

        removeCashAcct.execute();
        caretaker.setUndoEmptyProperty();
        caretaker.setRedoEmptyProperty();

        updateCashAccounts();

        clearFields();
        hc.updateAccountChart();
        hc.updateStats();
    }

    /**
     * This function allows the user to select an account
     * based on the cash accounts in the table.
     */
    public void selectAccount(){

        TablePosition pos = cashAcctTable.getSelectionModel().getSelectedCells().get(0);
        int row = pos.getRow();

        // Item here is the table view type:
        CashAccount selectedAcct = cashAcctTable.getItems().get(row);

        /*
        TableColumn col = pos.getTableColumn();

        // this gives the value in the selected cell:
        String data = (String) col.getCellObservableValue(item).getValue();
        */
        if(buySellController.returnTo == buySellController.BUYING){
            hc.tabPane.getSelectionModel().select( hc.buySellTab );
            selectAcctBttn.setVisible(false);
            buySellController.buyLossField.setText(selectedAcct.getName());
            buySellController.withdrawing = selectedAcct;
        }else if(buySellController.returnTo == buySellController.SELLING){
            hc.tabPane.getSelectionModel().select( hc.buySellTab );
            selectAcctBttn.setVisible(false);
            buySellController.sellGainField.setText(selectedAcct.getName());
            buySellController.depositing = selectedAcct;
        }


    }

    /**
     * Re sorts the table based on the new content after an update.
     * @param table - the table that needs to be re-sorted
     */
    public void reapplyTableSortOrder(TableView table) {
        ArrayList<TableColumn<CashAccount, ?>> sortOrder = new ArrayList<TableColumn<CashAccount, ?>>(table.getSortOrder());
        table.getSortOrder().clear();
        table.getSortOrder().addAll(sortOrder);
    }

}
