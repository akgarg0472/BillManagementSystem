package billingrecord;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("all")
public class RecordController {

    @FXML
    private TextField clientNameField;
    @FXML
    private ComboBox assessmentYearField;
    @FXML
    private ComboBox taxTypeField;
    @FXML
    private DatePicker paymentDateField;
    @FXML
    private TextField paymentAmountField;

    private String OLD_NAME;
    private String OLD_ASSESSMENT_YEAR;
    private String OLD_TAX_TYPE;
    private String OLD_PAYMENT_DATE;
    private String OLD_PAYMENT_AMOUNT;

    private String NEW_NAME;
    private String NEW_ASSESSMENT_YEAR;
    private String NEW_TAX_TYPE;
    protected static String NEW_PAYMENT_DATE;
    private String NEW_PAYMENT_AMOUNT;

    private RecordData recordData = new RecordData();

    private LogFile logFile = new LogFile();

    public void initialize() {
        String[] taxTypesArray = {"Income Tax", "GST", "Other"};
        taxTypeField.setItems(FXCollections.observableArrayList(taxTypesArray));
        taxTypeField.getSelectionModel().selectFirst();

        clientNameField.setTooltip(new Tooltip("Enter Client Name"));
        assessmentYearField.setTooltip(new Tooltip("Enter Assessment Year"));
        taxTypeField.setTooltip(new Tooltip("Select Tax Type"));
        paymentDateField.setTooltip(new Tooltip("Enter Date in dd/MM/yyyy format"));
        paymentDateField.setTooltip(new Tooltip("Enter Payment Amount"));

        String[] assessmentYearList = {"2016-17", "2017-18", "2018-19", "2019-20", "2020-21", "2021-22"};
        assessmentYearField.setItems(FXCollections.observableArrayList(assessmentYearList));
        assessmentYearField.getSelectionModel().selectFirst();

        Platform.runLater(() -> clientNameField.requestFocus());
    }

    public Record getNewRecord() {
        String clientName, assessmentYear = "", taxType, paymentAmount, paymentDate;

        if (clientNameField.getText().equals("")) {
            clientName = "null";
        } else {
            clientName = clientNameField.getText();
        }


        assessmentYear = assessmentYearField.getSelectionModel().getSelectedItem().toString();

        taxType = taxTypeField.getSelectionModel().getSelectedItem().toString();


        if (paymentDateField.getValue() == null) {
            paymentDate = null;
        } else {
            paymentDate = paymentDateField.getValue().toString();
        }

        if (paymentAmountField.getText().equals("")) {
            paymentAmount = "0.0";
        } else {
            paymentAmount = paymentAmountField.getText();
        }

        Record newRecord = new Record(clientName, assessmentYear, taxType, paymentDate, paymentAmount);
        return newRecord;
    }

    public void editRecord(Record record) {
        clientNameField.setText(OLD_NAME);

        if (OLD_ASSESSMENT_YEAR.equals("2016-17")) {
            assessmentYearField.getSelectionModel().select(0);
        } else if (OLD_ASSESSMENT_YEAR.equals("2017-18")) {
            assessmentYearField.getSelectionModel().select(1);
        } else if (OLD_ASSESSMENT_YEAR.equals("2018-19")) {
            assessmentYearField.getSelectionModel().select(2);
        } else if (OLD_ASSESSMENT_YEAR.equals("2019-20")) {
            assessmentYearField.getSelectionModel().select(3);
        } else if (OLD_ASSESSMENT_YEAR.equals("2020-21")) {
            assessmentYearField.getSelectionModel().select(4);
        } else if (OLD_ASSESSMENT_YEAR.equals("2021-22")) {
            assessmentYearField.getSelectionModel().select(5);
        }

        if (OLD_TAX_TYPE.equals("Income Tax")) {
            taxTypeField.getSelectionModel().selectFirst();
        } else if (OLD_TAX_TYPE.equals("GST")) {
            taxTypeField.getSelectionModel().select(1);
        } else if (OLD_TAX_TYPE.equals("Other")) {
            taxTypeField.getSelectionModel().selectLast();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(OLD_PAYMENT_DATE, formatter);
        paymentDateField.setValue(localDate);

        paymentAmountField.setText(OLD_PAYMENT_AMOUNT);
    }

    public void setPreviousEntries(String OLD_NAME, String OLD_ASSESSMENT_YEAR, String OLD_TAX_TYPE, String OLD_PAYMENT_DATE, String OLD_PAYMENT_AMOUNT) {
        this.OLD_NAME = OLD_NAME;
        this.OLD_ASSESSMENT_YEAR = OLD_ASSESSMENT_YEAR;
        this.OLD_TAX_TYPE = OLD_TAX_TYPE;


        this.OLD_PAYMENT_DATE = OLD_PAYMENT_DATE;

        this.OLD_PAYMENT_AMOUNT = OLD_PAYMENT_AMOUNT;
        recordData.setPreviousEntries(OLD_NAME, OLD_ASSESSMENT_YEAR, OLD_TAX_TYPE, OLD_PAYMENT_DATE, OLD_PAYMENT_AMOUNT);
    }

    public void updateRecord() {
        this.NEW_NAME = clientNameField.getText();
        this.NEW_ASSESSMENT_YEAR = assessmentYearField.getSelectionModel().getSelectedItem().toString();
        this.NEW_TAX_TYPE = taxTypeField.getSelectionModel().getSelectedItem().toString();

        if (paymentDateField.getValue() != null) {
            this.NEW_PAYMENT_DATE = paymentDateField.getValue().toString();
        } else {
            this.NEW_PAYMENT_DATE = null;
        }

        this.NEW_PAYMENT_AMOUNT = paymentAmountField.getText();

        recordData.newEntries(NEW_NAME, NEW_ASSESSMENT_YEAR, NEW_TAX_TYPE, NEW_PAYMENT_DATE, NEW_PAYMENT_AMOUNT);
    }
}
