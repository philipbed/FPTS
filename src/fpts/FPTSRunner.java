package fpts;
import fpts.controllers.LoginController;
import fpts.data.EquityInfo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * This is a class that is the main application runner.
 * This class will be ran when trying to execute the program contents.
 */
public class FPTSRunner extends Application{

	// current views of the the application gui
	private Stage primaryStage;
	private Pane rootLayout;

	// controller that will handle the initial views
	private LoginController controller;

	/**
	 * sets the stage and creates the initial views
	 * @param primaryStage - The Primary Stage
     */
	@Override
	public void start(Stage primaryStage){
		try {

			primaryStage.setTitle("FPTS");
			primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream( "fptsIcon.png" ))); 
			this.primaryStage = primaryStage;

			initRootLayout();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Initializes the layout views. Login views is the first users should see.
	 */
	public void initRootLayout() {
		try {

			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(FPTSRunner.class.getResource("views/login.fxml"));
			rootLayout = loader.load();

			//get the controller associated with the login views
			controller = loader.getController();

			//call this function to give the controller a reference to the app
			controller.setMainApp(this);

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return - The current primary stage
     */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * This function sets the primary stage of the GUI
	 * @param stage
     */
	public void setPrimaryStage(Stage stage){
		this.primaryStage = stage;
	}

	/**
	 * Called when the an event handler in a controller needs to switch views
	 * Useful for not opening new windows.
	 * @param newScene - new Scene to be switched in by the controller.
     */
		public void setAndShow(Scene newScene){
			this.primaryStage.setScene(newScene);
			this.primaryStage.show();

		}

	/**
	 * Cleanly stops the application on exit
	 */
		@Override
		public void stop() {
			System.exit(0);
		}

	/**
	 * All actions performed in this method are executed before anything visual is done
	 */
	public void init() {
		//This must stay here.
		EquityInfo.instantiateEquities();
		EquityInfo.startUpdateThread(5);
	}

	/**
	 * This function is the main driving function of this class.
	 * This launches the program.
	 * @param args Command line arguments.
     */
	public static void main(String[] args) {
		if(args.length == 2 && args[0].equals("-delete")){
			try {
				Files.delete(FileSystems.getDefault().getPath("portfolios", args[1] + ".pfo"));
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
		launch(args);
	}

}
