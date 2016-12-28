package licuri.server.main;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainController {

    @FXML
    private Label lblStatusServer;

    @FXML
    private Button btnStartServer;

    @FXML
    private Button btnStopServer;

    private Main main;

    @FXML
    public void initialize() {
    }

    protected void updateStatusServer() {	

	btnStartServer.setDisable(main.serverRunning);
	btnStopServer.setDisable(!main.serverRunning);

	if (main.serverRunning) {

	    lblStatusServer.setText("Iniciado!!!");
	    
	    lblStatusServer.getStyleClass().add("green");
	    lblStatusServer.getStyleClass().remove("red");

	    System.out.println(lblStatusServer.getFont().getStyle());

	} else {

	    lblStatusServer.setText("Parado!!!");
	    lblStatusServer.getStyleClass().add("red");
	    lblStatusServer.getStyleClass().remove("green");
	}
    }

    @FXML
    private void handleStopServer() {
	main.stopServer();
    }

    @FXML
    private void handleStartServer() {
	main.initServer();
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(Main main) {
	this.main = main;

	updateStatusServer();
    }

}
