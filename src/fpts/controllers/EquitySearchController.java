package fpts.controllers;

import fpts.FPTSRunner;
import fpts.command.AddEquityCommand;
import fpts.data.Equity;
import fpts.data.EquityInfo;
import fpts.iterator.Iterator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A helper class to assist the Home controller with search functionality
 *
 * @author Philip Bedward
 */
public class EquitySearchController {

    @FXML
    TableView<EquityInfo> equityTable;

    @FXML
    TableColumn<EquityInfo, String> Ticker, Name;
    @FXML
    TableColumn<EquityInfo, Number> Value;
    @FXML
    TableColumn<EquityInfo, String> Market;

    @FXML
    // The fields necessary for searching
    TextField tickerField, equityField, indexField;

    @FXML
    Button eqButton;

    @FXML
    ProgressBar progressBar;

    @FXML
    Pane searchPane;

    Stage stage;

    private ObservableList<EquityInfo> equityData = FXCollections.observableArrayList();

    private FPTSRunner application;

    private HomeController homeController;
    private BuySellController buySellController;
    private WatchlistController watchListController;
    private AddEquityController addEquityController;

    public int returnTo;

    /**
     * A setter for the main app.
     * @param app The main app.
     */
    public void setMainApp(FPTSRunner app){this.application = app;}

    /**
     * A setter for the main stage.
     * @param stage The main stage.
     */
    public void setMainStage(Stage stage){
    	this.stage = stage;
    	this.stage.getIcons().add(new Image(this.getClass().getResourceAsStream( "fptsIcon.png" )));
    	this.stage.setTitle("Equity Selection");
    }

    /**
     * A setter for the controllers.
     * @param homeC The home controller.
     * @param buySellC The buy sell controller.
     * @param watchC The watchlist controller.
     */
    public void setControllers(HomeController homeC, BuySellController buySellC,
                               WatchlistController watchC){
        homeController = homeC;
        buySellController = buySellC;
        watchListController = watchC;
    }

    /**
     * A setter for an add equity controller.
     * @param addEquityC The add equity controller.
     */
    public void setAddEquityController(AddEquityController addEquityC){
        addEquityController = addEquityC;
    }

    /**
     * A method to update the values in the table.
     */
    public void updateEquities() {
        equityData.clear();
        String equityName, tickerSymbol, marketIndex;
        equityName = equityField.getText();
        tickerSymbol = tickerField.getText();
        marketIndex = indexField.getText();

        ArrayList<EquityInfo> filteredEquities = EquityInfo.getMatchingInfo(equityName, tickerSymbol, marketIndex);
        equityData.addAll(filteredEquities);

        reapplyTableSortOrder();


    }

    /**
     * A getter for a list of the equity metadata in its observable form.
     * @return The equity metadata in the table.
     */
    public ObservableList<EquityInfo> getEquityData() {
        return equityData;
    }

    /**
     * A getter for the equity metadata table.
     * @return A table filled with equity metadata.
     */
    public TableView<EquityInfo> getEquityTable() {
        return equityTable;
    }

    /**
     * Re sorts the table.
     */
    private void reapplyTableSortOrder() {
        ArrayList<TableColumn<EquityInfo, ?>> sortOrder = new ArrayList<>(equityTable.getSortOrder());
        equityTable.getSortOrder().clear();
        equityTable.getSortOrder().addAll(sortOrder);
    }

    /**
     * Automatically called by javaFX when rendered for the first time.
     */
    public void initialize() {
        // add the equity info objects to the equity data
        // list to make it observable then add this observable list to
        // the table
    	ArrayList<EquityInfo> equities = new ArrayList<EquityInfo>();
    	Iterator<EquityInfo> itr = EquityInfo.getEquityInfoIterator();
    	while(itr.hasNext()){
    		equities.add(itr.next());
    	}
    	

    	
        equityData.addAll(equities);
        equityTable.setItems(equityData);
        progressBar.setVisible(false);

        // adds all the cell data to the table in the proper columns
        //Ticker.setCellValueFactory(cellData -> cellData.getValue().tickerProperty());
        //Name.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        // Market.setCellValueFactory(cellData -> cellData.getValue().marketProperty());
        //Value.setCellValueFactory(cellData -> cellData.getValue().valueProperty());

        //The following 4 methods are a replacement for the above 4, which don't work on Ryan's computer or lab machines

        Ticker.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<EquityInfo, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<EquityInfo, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return p.getValue().tickerProperty();
            }
        });

        Name.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<EquityInfo, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<EquityInfo, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return p.getValue().nameProperty();
            }
        });

        Market.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<EquityInfo, String>, ObservableValue<String>>() {
            public ObservableValue<String> call(TableColumn.CellDataFeatures<EquityInfo, String> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return p.getValue().marketProperty();
            }
        });

        Value.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<EquityInfo, Number>, ObservableValue<Number>>() {
            public ObservableValue<Number> call(TableColumn.CellDataFeatures<EquityInfo, Number> p) {
                // p.getValue() returns the Person instance for a particular TableView row
                return p.getValue().valueProperty();
            }
        });

        /**
         * These three method calls will allow the equity tables to be updated in real time
         * without the need for a search button
         */
        tickerField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateEquities();
            }
        });

        indexField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateEquities();
            }
        });

        equityField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                updateEquities();
            }


        });

        EquityInfo.percentageLoadedProperty.addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number oldValue, Number newValue) {
				double newVal = newValue.doubleValue();
				if(newVal == 1){
					eqButton.setDisable(false);
					updateProgress(newValue.doubleValue());
					progressBar.setVisible(false);
					updateEquities();
				}else if(newVal == 0){
					eqButton.setDisable(true);
					updateProgress(newValue.doubleValue());
				}else if(newVal > 0 && newVal < 1){
					if(!eqButton.isDisabled()){
						eqButton.setDisable(true);
					}
					if(!progressBar.isVisible()){
						progressBar.setVisible(true);
					}
					updateEquities();
					updateProgress(newValue.doubleValue());
				}else{
					//idk
				}
				
			}
        	
        });

    }

    /**
     * A method to select an equity on the table.
     */
    public void selectEquity(){

        TablePosition pos = equityTable.getSelectionModel().getSelectedCells().get(0);
        int row = pos.getRow();

        // Item here is the table view type:
        EquityInfo selectedEquity = equityTable.getItems().get(row);



        // set the text of the field
        if(returnTo == buySellController.BUYING){
            homeController.tabPane.getSelectionModel().select( homeController.buySellTab );
            eqButton.setVisible(false);
            buySellController.buyGainField.setText(selectedEquity.getName());
            buySellController.buying = selectedEquity;
        }else if(returnTo == buySellController.SELLING){
            homeController.tabPane.getSelectionModel().select( homeController.buySellTab );
            eqButton.setVisible(false);
            buySellController.sellLossField.setText(selectedEquity.getName());
            buySellController.selling = new Equity(selectedEquity, 0, getDateString(), 0, true);
        }else if(returnTo == addEquityController.ADD){
            homeController.tabPane.getSelectionModel().select( homeController.addTab );
            eqButton.setVisible(false);
            addEquityController.equityDisplayField.setText(selectedEquity.getName());
            addEquityController.adding = selectedEquity;
        }else if (returnTo == watchListController.WATCH)
            homeController.tabPane.getSelectionModel().select( homeController.watchTab );
            eqButton.setVisible(false);
            watchListController.addWatchField.setText(selectedEquity.getName());
            watchListController.adding = selectedEquity;
        stage.close();
        eqButton.setVisible(false);
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
     * Helper method to update the current percentage
     * of the progress bar
     * @param percentage - the current percentage of equites loaded
     */
    public void updateProgress(double percentage){
        progressBar.setProgress(percentage);
    }
}
