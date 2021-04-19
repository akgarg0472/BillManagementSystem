package billingrecord;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

@SuppressWarnings("all")
public class LoginForm {

    private Label userName, userPassword;
    private TextField userNameField;
    private PasswordField userPasswordField;
    private Button loginButton;
    private Alert loginFail;
    private Alert loginSuccess;

    private RecordData recordData;
    private Controller controller = new Controller();

    protected void loginForm(Stage primaryStage) {
        Stage secondaryStage = new Stage();
        GridPane gridpane = new GridPane();
        Scene scene = new Scene(gridpane, 400, 200);

        userName = new Label("Enter Username");
        userPassword = new Label("Enter Password");
        userNameField = new TextField();
        userPasswordField = new PasswordField();
        loginButton = new Button("Login");
        loginFail = new Alert(Alert.AlertType.ERROR);
        loginSuccess = new Alert(Alert.AlertType.INFORMATION);

        userName.setTranslateX(40);
        userName.setTranslateY(30);

        userPassword.setTranslateX(40);
        userPassword.setTranslateY(80);

        userNameField.setTranslateX(175);
        userNameField.setTranslateY(30);
        userNameField.setPromptText("Enter Username");

        userPasswordField.setTranslateX(175);
        userPasswordField.setTranslateY(80);
        userPasswordField.setPromptText("Enter Password");

        loginButton.setTranslateX(gridpane.getWidth() / 2 - gridpane.getWidth() / 15);
        loginButton.setTranslateY(145);

        loginSuccess.setTitle("Success");
        loginSuccess.setHeaderText("Login Successful");
        loginSuccess.setContentText("Press Ctrl+Alt+S to show all records");
        loginSuccess.initOwner(primaryStage.getScene().getWindow());

        loginFail.setTitle("Oops!!");
        loginFail.setHeaderText("");
        loginFail.setContentText("Invalid username/password\nCheck your credentials and try again");

        userNameField.setTooltip(new Tooltip("Enter username"));
        userPasswordField.setTooltip(new Tooltip("Enter password"));
        loginButton.setTooltip(new Tooltip("Login"));

        userPasswordField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    if (userNameField.getText().equals("admin") && userPasswordField.getText().equals("admin")) {
                        secondaryStage.close();
                        primaryStage.show();
                        loginSuccess.showAndWait();
                    } else {
                        loginFail.show();
                        userNameField.setText("");
                        userPasswordField.setText("");
                        userNameField.requestFocus();
                    }
                }
            }
        });

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (userNameField.getText().equals("admin") && userPasswordField.getText().equals("admin")) {
                    secondaryStage.close();
                    primaryStage.show();
                    loginSuccess.show();
                } else {
                    loginFail.show();
                    userNameField.setText("");
                    userPasswordField.setText("");
                    userNameField.requestFocus();
                }
            }
        });

        loginButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    if (userNameField.getText().equals("admin") && userPasswordField.getText().equals("admin")) {
                        secondaryStage.close();
                        primaryStage.show();
                        loginSuccess.show();
                    } else {
                        loginFail.show();
                        userNameField.setText("");
                        userPasswordField.setText("");
                        userNameField.requestFocus();
                    }
                }
            }
        });

        secondaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                recordData = new RecordData();
                if (recordData.close()) {
                    LogFile logFile = new LogFile();
                    logFile.modifyLogFile("All resources closed and program terminates successfully");
                } else {
                    LogFile logFile = new LogFile();
                    logFile.modifyLogFile("Error closing all resources and program terminated with some errors");
                }
                primaryStage.close();
            }
        });

        gridpane.getChildren().addAll(userName, userPassword, userNameField, userPasswordField, loginButton);
        secondaryStage.setTitle("Enter Login Credentials");
        secondaryStage.setScene(scene);
        secondaryStage.initModality(Modality.WINDOW_MODAL);
        secondaryStage.initOwner(primaryStage);
        secondaryStage.setResizable(false);
        secondaryStage.show();
    }
}
