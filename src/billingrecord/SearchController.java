package billingrecord;

import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

@SuppressWarnings("all")
public class SearchController {

    @FXML
    private ComboBox searchComboBox;
    @FXML
    private TextField searchTextField;

    private static final String TABLE_RECORDS = "records";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_ASSESSMENT_YEAR = "assessmentyear";
    private static final String COLUMN_PAYMENT_DATE = "paymentdate";
    private static final String COLUMN_PAYMENT_AMOUNT = "paymentamount";
    private static final String COLUMN_IT_GST = "it_gst";

    private Alert searchErrorAlert;
    static String sqlCommand;

    public void initialize() {
        String[] searchCriteria = {"--- Please Choose an Option ---", "Name", "Assessment Year", "Tax Type", "Payment Date", "Payment Amount"};
        searchComboBox.setItems(FXCollections.observableArrayList(searchCriteria));
        searchComboBox.getSelectionModel().selectFirst();
        searchErrorAlert = new Alert(Alert.AlertType.ERROR);
        searchTextField.setTooltip(new Tooltip("Enter Search Keywords"));

        searchTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    if (searchComboBox.getSelectionModel().getSelectedItem() == "--- Please Choose an Option ---" || searchTextField.getText().equals("")) {
                        sqlCommand = null;
                    } else if (searchComboBox.getSelectionModel().getSelectedItem() == "Name") {
                        sqlCommand = "SELECT * FROM " + TABLE_RECORDS + " WHERE LOWER(" + COLUMN_NAME + ") LIKE LOWER(\'%" + searchTextField.getText() + "%\')";
                    } else if (searchComboBox.getSelectionModel().getSelectedItem() == "Assessment Year") {
                        sqlCommand = "SELECT * FROM " + TABLE_RECORDS + " WHERE LOWER(" + COLUMN_ASSESSMENT_YEAR + ") LIKE LOWER(\'%" + searchTextField.getText() + "%\')";
                    } else if (searchComboBox.getSelectionModel().getSelectedItem() == "Tax Type") {
                        sqlCommand = "SELECT * FROM " + TABLE_RECORDS + " WHERE LOWER(" + COLUMN_IT_GST + ") LIKE LOWER(\'%" + searchTextField.getText() + "%\')";
                    } else if (searchComboBox.getSelectionModel().getSelectedItem() == "Payment Date") {
                        sqlCommand = "SELECT * FROM " + TABLE_RECORDS + " WHERE LOWER(" + COLUMN_PAYMENT_DATE + ") LIKE LOWER(\'%" + searchTextField.getText() + "%\')";
                    } else if (searchComboBox.getSelectionModel().getSelectedItem() == "Payment Amount") {
                        sqlCommand = "SELECT * FROM " + TABLE_RECORDS + " WHERE LOWER(" + COLUMN_PAYMENT_AMOUNT + ") LIKE LOWER(\'%" + searchTextField.getText() + "%\')";
                    }
                }
            }
        });

        searchTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (searchComboBox.getSelectionModel().getSelectedItem() == "--- Please Choose an Option ---") {
                    searchErrorAlert.setTitle("Error Searching");
                    searchErrorAlert.setHeaderText(null);
                    searchErrorAlert.setContentText("Please Choose Search Criteria");
                    searchErrorAlert.showAndWait();
                } else if (searchComboBox.getSelectionModel().getSelectedItem() == "Name") {
                    sqlCommand = "SELECT * FROM " + TABLE_RECORDS + " WHERE LOWER(" + COLUMN_NAME + ") LIKE LOWER(\'%" + searchTextField.getText() + "%\')";
                } else if (searchComboBox.getSelectionModel().getSelectedItem() == "Assessment Year") {
                    sqlCommand = "SELECT * FROM " + TABLE_RECORDS + " WHERE LOWER(" + COLUMN_ASSESSMENT_YEAR + ") LIKE LOWER(\'%" + searchTextField.getText() + "%\')";
                } else if (searchComboBox.getSelectionModel().getSelectedItem() == "Tax Type") {
                    sqlCommand = "SELECT * FROM " + TABLE_RECORDS + " WHERE LOWER(" + COLUMN_IT_GST + ") LIKE LOWER(\'%" + searchTextField.getText() + "%\')";
                } else if (searchComboBox.getSelectionModel().getSelectedItem() == "Payment Date") {
                    sqlCommand = "SELECT * FROM " + TABLE_RECORDS + " WHERE LOWER(" + COLUMN_PAYMENT_DATE + ") LIKE LOWER(\'%" + searchTextField.getText() + "%\')";
                } else if (searchComboBox.getSelectionModel().getSelectedItem() == "Payment Amount") {
                    sqlCommand = "SELECT * FROM " + TABLE_RECORDS + " WHERE LOWER(" + COLUMN_PAYMENT_AMOUNT + ") LIKE LOWER(\'%" + searchTextField.getText() + "%\')";
                }
            }
        });
    }
}




