package billingrecord;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Optional;

@SuppressWarnings("all")
public class Main extends Application {

    private RecordData recordData = new RecordData();
    private LoginForm loginForm = new LoginForm();
    private LogFile logFile = new LogFile();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        logFile.modifyLogFile("Program started");

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Payment Management System");
        Scene scene = new Scene(root, 1024, 550);
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(700);
        Controller controller = new Controller();
        loginForm.loginForm(primaryStage);

        scene.getStylesheets().addAll(Main.class.getResource("CSS.css").toExternalForm());

        controller.getStage(primaryStage);

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm Exit");
                alert.setHeaderText(null);
                alert.setContentText("Are you sure want to exit application?");

                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.OK) {
                    RecordData recordData = new RecordData();
                    if (recordData.close()) {
                        logFile.modifyLogFile("All resources closed and program terminates successfully");
                    } else {
                        logFile.modifyLogFile("Error closing all resources and program terminated with some errors");
                    }
                } else {
                    event.consume();
                }
            }
        });

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.F11) {
                    controller.showHelp();
                }
            }
        });
    }
}
