package billingrecord;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;

@SuppressWarnings("all")
public class RecordData {
    public static final String TABLE_RECORDS = "records";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ASSESSMENT_YEAR = "assessmentyear";
    public static final String COLUMN_PAYMENT_DATE = "paymentdate";
    public static final String COLUMN_PAYMENT_AMOUNT = "paymentamount";
    public static final String COLUMN_IT_GST = "it_gst";

    private static String OLD_NAME;
    private static String OLD_ASSESSMENT_YEAR;
    private static String OLD_TAX_TYPE;
    private static String OLD_PAYMENT_DATE;
    private static String OLD_PAYMENT_AMOUNT;
    private static String NEW_NAME;
    private static String NEW_ASSESSMENT_YEAR;
    private static String NEW_TAX_TYPE;
    private static String NEW_PAYMENT_DATE;
    private static String NEW_PAYMENT_AMOUNT;

    private final ObservableList<Record> records;
    private final ObservableList<Record> searchRecords;

    private PreparedStatement statement;
    private Connection connection;
    private ResultSet resultSet;

    private Record record = null;

    private final LogFile logFile = new LogFile();

    //constructor to initialize the array list of all content and search content
    public RecordData() {
        records = FXCollections.observableArrayList();
        searchRecords = FXCollections.observableArrayList();
    }

    public ObservableList<Record> getRecords() {
        return records;
    }

    public boolean open() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:records.db");
            statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + TABLE_RECORDS + "(" + COLUMN_NAME + ", " + COLUMN_ASSESSMENT_YEAR + ", " + COLUMN_IT_GST + "," + COLUMN_PAYMENT_DATE + ", " +
                    COLUMN_PAYMENT_AMOUNT + ")");
            //            statement.execute("CREATE TABLE IF NOT EXISTS " + TABLE_RECORDS + "(" + COLUMN_NAME + ", " + COLUMN_ASSESSMENT_YEAR + ", " + COLUMN_IT_GST + "," + COLUMN_PAYMENT_DATE + ", " +
            //                    COLUMN_PAYMENT_AMOUNT + ")");
            statement.execute();
            return true;
        } catch (SQLException e) {
            logFile.modifyLogFile("Unable to load database/connection " + e.getMessage());
            return false;
        }
    }

    public boolean close() {
        try {
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
            return true;
        } catch (SQLException e) {
            logFile.modifyLogFile("Couldn't close connection/database " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void loadAllRecords() {
        try {
            records.clear();
            statement = connection.prepareStatement("Select * from records");
            resultSet = statement.executeQuery();

            if (!resultSet.next()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Data Error");
                alert.setHeaderText("Oops!");
                alert.setContentText("No Data record Found in database");
                alert.showAndWait();
            } else {
                do {
                    record = new Record();
                    record.setClientName(resultSet.getString(1));
                    record.setAssessmentYear(resultSet.getString(2));
                    record.setTaxType(resultSet.getString(3));
                    record.setPaymentDate(resultSet.getString(4));
                    record.setPaymentAmount(resultSet.getString(5));
                    records.add(record);
                } while (resultSet.next());
            }
        } catch (SQLException e) {
            logFile.modifyLogFile("Exception in populating all records, " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void addRecord(Record record, String clientName, String assessmentYear, String taxType, String paymentDate, String paymentAmount) {

        //insert into records values("name", "assessmentyear", "taxtype", "paymentdate", "paymentamount")

        String add = "INSERT INTO " + TABLE_RECORDS + " VALUES" + "(\"" + clientName + "\",\"" + assessmentYear + "\"" + ",\"" + taxType + "\"" + ",\"" +
                paymentDate + "\"" + ",\"" + paymentAmount + "\")";
        records.add(record);

        try {
            statement = connection.prepareStatement(add);
            statement.executeUpdate();
            logFile.modifyLogFile("New record added successfully in database"); //writing insert statement into log file
        } catch (SQLException e) {
            logFile.modifyLogFile("Error adding new record in database : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void deleteRecord(Record record, String clientName, String assessmentYear, String taxType, String paymentDate, String paymentAmount) {

        //delete from records where name="name 4" and it_gst="IT.GST" and assessmentyear="2021"
        String delete = "DELETE FROM " + TABLE_RECORDS + " WHERE " + COLUMN_NAME + "=\"" + clientName + "\" AND " + COLUMN_ASSESSMENT_YEAR + "=\"" + assessmentYear + "\" AND " +
                COLUMN_IT_GST + "=\"" + taxType + "\" AND " + COLUMN_PAYMENT_DATE + "=\"" + paymentDate + "\" AND " + COLUMN_PAYMENT_AMOUNT + "=\"" + paymentAmount + "\"";
        records.remove(record);

        try {
            statement = connection.prepareStatement(delete);
            statement.executeUpdate();
            logFile.modifyLogFile("Data successfully deleted from database");
        } catch (SQLException e) {
            logFile.modifyLogFile("Error deleting record : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setPreviousEntries(String OLD_NAME, String OLD_ASSESSMENT_YEAR, String OLD_TAX_TYPE, String OLD_PAYMENT_DATE, String OLD_PAYMENT_AMOUNT) {
        this.OLD_NAME = OLD_NAME;
        this.OLD_ASSESSMENT_YEAR = OLD_ASSESSMENT_YEAR;
        this.OLD_TAX_TYPE = OLD_TAX_TYPE;
        this.OLD_PAYMENT_DATE = OLD_PAYMENT_DATE;
        this.OLD_PAYMENT_AMOUNT = OLD_PAYMENT_AMOUNT;
    }

    public void newEntries(String NEW_NAME, String NEW_ASSESSMENT_YEAR, String NEW_TAX_TYPE, String NEW_PAYMENT_DATE, String NEW_PAYMENT_AMOUNT) {
        this.NEW_NAME = NEW_NAME;
        this.NEW_ASSESSMENT_YEAR = NEW_ASSESSMENT_YEAR;
        this.NEW_TAX_TYPE = NEW_TAX_TYPE;
        this.NEW_PAYMENT_DATE = NEW_PAYMENT_DATE;
        this.NEW_PAYMENT_AMOUNT = NEW_PAYMENT_AMOUNT;
    }

    public void updateRecord(Record selectedRecord) {
        String updateRecord;
        selectedRecord.setClientName(NEW_NAME);
        selectedRecord.setAssessmentYear(NEW_ASSESSMENT_YEAR);
        selectedRecord.setTaxType(NEW_TAX_TYPE);
        selectedRecord.setPaymentDate(NEW_PAYMENT_DATE);
        selectedRecord.setPaymentAmount(NEW_PAYMENT_AMOUNT);

        //        update records set name="newName",assessmentyear="newAY",it_gst="newTaxType",paymentdate="ni batara",paymentamount="2312.18" WHERE
        //        name="Sample name" AND assessmentyear="2025" AND it_gst="Income Tax" AND paymentdate="12-8-2020" AND paymentamount="100.00"

        updateRecord = "UPDATE " + TABLE_RECORDS + " SET " + COLUMN_NAME + "=\"" + NEW_NAME + "\"," + COLUMN_ASSESSMENT_YEAR + "=\"" + NEW_ASSESSMENT_YEAR +
                "\"," + COLUMN_IT_GST + "=\"" + NEW_TAX_TYPE + "\"," + COLUMN_PAYMENT_DATE + "=\"" + NEW_PAYMENT_DATE + "\"," + COLUMN_PAYMENT_AMOUNT +
                "=\"" + NEW_PAYMENT_AMOUNT + "\" WHERE " +
                COLUMN_NAME + "=\"" + OLD_NAME + "\" AND " + COLUMN_ASSESSMENT_YEAR + "=\"" + OLD_ASSESSMENT_YEAR +
                "\" AND " + COLUMN_IT_GST + "=\"" + OLD_TAX_TYPE + "\" AND " + COLUMN_PAYMENT_DATE + "=\"" + OLD_PAYMENT_DATE + "\" AND " + COLUMN_PAYMENT_AMOUNT +
                "=\"" + OLD_PAYMENT_AMOUNT + "\"";

        try {
            statement = connection.prepareStatement(updateRecord);
            statement.executeUpdate();
            logFile.modifyLogFile("Data record successfully updated in the database");
        } catch (SQLException e) {
            logFile.modifyLogFile("Data updated in database failed : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ObservableList<Record> searchRecord(String sqlStatement) {
        try {
            searchRecords.clear();
            //            ResultSet resultSet = statement.executeQuery("Select * from records WHERE assessmentyear=\"2021\"");
            try {
                statement = connection.prepareStatement(sqlStatement);
                //                System.out.println("Statement : " + sqlStatement);
                resultSet = statement.executeQuery();
            } catch (Exception e) {
                logFile.modifyLogFile("Error in executing the search query : " + e.getMessage());
                e.printStackTrace();
            }

            if (!resultSet.next()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No record found");
                alert.setHeaderText(null);
                alert.setContentText("No Record found in database");
                alert.showAndWait();
            } else {
                do {
                    record = new Record();
                    record.setClientName(resultSet.getString(1));
                    record.setAssessmentYear(resultSet.getString(2));
                    record.setTaxType(resultSet.getString(3));
                    record.setPaymentDate(resultSet.getString(4));
                    record.setPaymentAmount(resultSet.getString(5));
                    searchRecords.add(record);
                } while (resultSet.next());

                return searchRecords;
            }
        } catch (SQLException e) {
            System.out.println("Exception in searching record");
            logFile.modifyLogFile("Exception in searching record : " + e.getMessage());
        }
        return null;
    }

    public void exportDataToExcel(Stage primaryStage) {
        try {
            statement = connection.prepareStatement("SELECT * FROM " + TABLE_RECORDS);
            ResultSet excelSet = statement.executeQuery();

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Payment Details");

            XSSFCellStyle style = workbook.createCellStyle();
            style.setBorderTop((short) 1);
            style.setBorderBottom((short) 1);
            style.setBorderRight((short) 1);
            style.setBorderLeft((short) 1);
            XSSFFont font = workbook.createFont();
            font.setFontHeightInPoints((short) 15);
            font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
            style.setFont(font);
            style.setAlignment(CellStyle.ALIGN_CENTER);

            XSSFCellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setBorderTop((short) 1);
            cellStyle.setBorderBottom((short) 1);
            cellStyle.setBorderRight((short) 1);
            cellStyle.setBorderLeft((short) 1);
            cellStyle.setAlignment(HorizontalAlignment.CENTER);

            XSSFRow header = sheet.createRow(0);

            Cell cellZero = header.createCell(0);
            cellZero.setCellValue("S.No");
            cellZero.setCellStyle(style);

            Cell cellOne = header.createCell(1);
            cellOne.setCellValue("Client Name");
            cellOne.setCellStyle(style);

            Cell cellTwo = header.createCell(2);
            cellTwo.setCellValue("Assessment Year");
            cellTwo.setCellStyle(style);

            Cell cellThree = header.createCell(3);
            cellThree.setCellValue("Tax Type");
            cellThree.setCellStyle(style);

            Cell cellFour = header.createCell(4);
            cellFour.setCellValue("Payment Date");
            cellFour.setCellStyle(style);

            Cell cellFive = header.createCell(5);
            cellFive.setCellValue("Payment Amount");
            cellFive.setCellStyle(style);

            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            sheet.autoSizeColumn(5);

            int index = 1;    //to create rows for each record
            int serial = 1; //to maintain serial number of all records

            while (excelSet.next()) {

                XSSFRow row = sheet.createRow(index++);

                Cell serialNumber = row.createCell(0);
                serialNumber.setCellValue(serial++);
                serialNumber.setCellStyle(cellStyle);

                Cell clientName = row.createCell(1);
                clientName.setCellValue(excelSet.getString(1));
                clientName.setCellStyle(cellStyle);

                Cell assessmentYear = row.createCell(2);
                assessmentYear.setCellValue(excelSet.getString(2));
                assessmentYear.setCellStyle(cellStyle);

                Cell taxType = row.createCell(3);
                taxType.setCellValue(excelSet.getString(3));
                taxType.setCellStyle(cellStyle);

                Cell paymentDate = row.createCell(4);
                paymentDate.setCellValue(excelSet.getString(4));
                paymentDate.setCellStyle(cellStyle);

                Cell paymentAmount = row.createCell(5);
                paymentAmount.setCellValue(Double.parseDouble(excelSet.getString(5)));
                paymentAmount.setCellStyle(cellStyle);
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel File", "*.xlsx"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + File.separator + "Desktop"));
            fileChooser.setInitialFileName("MyExcelFile");
            File excelFile = fileChooser.showSaveDialog(primaryStage);

            if (excelFile != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(excelFile.getAbsolutePath());
                workbook.write(fileOutputStream);
                fileOutputStream.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export Success");
                alert.setHeaderText(null);
                alert.setContentText("\nData successfully exported to Excel Sheet\n");
                alert.showAndWait();
                logFile.modifyLogFile("Data successfully exported to Excel");
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Export cancelled by user");
                alert.setHeaderText(null);
                alert.setContentText("\nExport to EXCEL cancelled\n");
                alert.showAndWait();
                logFile.modifyLogFile("Export cancelled");
            }

        } catch (SQLException | FileNotFoundException e) {
            logFile.modifyLogFile("Export to EXCEL Failed : " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            logFile.modifyLogFile("Error during writing data to excel file");
            e.printStackTrace();
        }
    }

}
