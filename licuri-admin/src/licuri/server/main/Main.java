package licuri.server.main;

import java.io.IOException;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.springframework.beans.BeansException;

import com.dlnapps.controller.DlnaHttpServer;
import com.dlnapps.dao.Dao;
import com.dlnapps.dms.server.LicuriMovieServer;
import com.dlnapps.main.MainApplication;
import com.dlnapps.util.DlnaServerRunner;

public class Main extends Application {

    private BorderPane rootLayout;

    MainController controller;

    protected boolean serverRunning = false;

    private LicuriMovieServer licuriMovieServer;

    @Override
    public void start(Stage primaryStage) {
	try {

	    FXMLLoader loader = new FXMLLoader();

	    loader.setLocation(getClass().getResource("RootLayout.fxml"));

	    rootLayout = loader.load();

	    Scene scene = new Scene(rootLayout);
	    primaryStage.setScene(scene);
	    primaryStage.show();

	    primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		@Override
		public void handle(WindowEvent event) {
		    stopServer();
		    System.exit(1);
		}
	    });

	    showPersonOverview();

	    initServer();

	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public void stopServer() {
	try {
	    
	    MainApplication.getInstance().getApplicationContext().getBean(Dao.class).shutdown();
	    DlnaServerRunner.stopServer();
	    serverRunning = false;
	    controller.updateStatusServer();
	    licuriMovieServer.getUpnpService().shutdown();

	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public void initServer() {

	licuriMovieServer = new LicuriMovieServer();
	Thread serverThread = new Thread(licuriMovieServer);
	serverThread.setDaemon(false);
	serverThread.start();

	DlnaServerRunner.run(DlnaHttpServer.class);
	
	MainApplication.getInstance();

	serverRunning = true;

	controller.updateStatusServer();
	

    }

    public static void main(String[] args) throws BeansException, SQLException {

	launch(args);

    }

    public void showPersonOverview() {
	try {
	    // Load person overview.
	    FXMLLoader loader = new FXMLLoader();
	    loader.setLocation(getClass().getResource("main.fxml"));
	    AnchorPane personOverview = (AnchorPane) loader.load();

	    // Set person overview into the center of root layout.
	    rootLayout.setCenter(personOverview);

	    // Give the controller access to the main app.
	    controller = loader.getController();
	    controller.setMainApp(this);

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

}
