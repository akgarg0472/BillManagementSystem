package billingrecord;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.util.Optional;

@SuppressWarnings("all")
public class Controller {

    private static Stage primaryStage;
    @FXML
    private BorderPane mainPanel;
    @FXML
    private TableView<Record> recordsTable;
    @FXML
    private ContextMenu contextMenu;
    @FXML
    private MenuItem add, show, exit, edit, delete, search;
    private RecordData data;
    private LogFile logFile;

    public void initialize() {
        contextMenu = new ContextMenu();
        logFile = new LogFile();
        MenuItem editContextMenu = new MenuItem("Edit");
        MenuItem deleteContextMenu = new MenuItem("Delete");
        contextMenu.getItems().addAll(editContextMenu, deleteContextMenu);
        data = new RecordData();

        data.open();

        add.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        show.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN, KeyCombination.ALT_DOWN));
        edit.setAccelerator(new KeyCodeCombination(KeyCode.E, KeyCombination.CONTROL_DOWN));
        delete.setAccelerator(new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN));
        search.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        exit.setAccelerator(new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN));

        editContextMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                editRecord();
            }
        });

        deleteContextMenu.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteRecord();
            }
        });

        recordsTable.setRowFactory(new Callback<TableView<Record>, TableRow<Record>>() {
            @Override
            public TableRow<Record> call(TableView<Record> param) {

                TableRow<Record> record = new TableRow<Record>();

                record.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        final int index = record.getIndex();
                        if (recordsTable.getSelectionModel().getSelectedIndex() == index) {
                            record.setContextMenu(contextMenu);
                        }
                    }
                });

                record.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        if (event.getButton() == MouseButton.PRIMARY) {
                            if (event.getClickCount() == 2 && !record.isEmpty()) {
                                Record selectedRecord = recordsTable.getSelectionModel().getSelectedItem();
                                String clientName;

                                if (selectedRecord.getClientName().length() > 30) {
                                    clientName = selectedRecord.getClientName().substring(0, 25) + "....";
                                } else {
                                    clientName = selectedRecord.getClientName();
                                }

                                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                alert.setTitle(clientName);
                                alert.setHeaderText(null);
                                alert.setContentText("Name : " + clientName +
                                        "\n\nAssessment Year : " + selectedRecord.getAssessmentYear() +
                                        "\n\nTax Type : " + selectedRecord.getTaxType() +
                                        "\n\nPayment Date : " + selectedRecord.getPaymentDate() +
                                        "\n\nPayment Amount : " + selectedRecord.getPaymentAmount() + " ₹");
                                alert.showAndWait();
                            } else if (record.isEmpty()) {
                                recordsTable.getSelectionModel().clearSelection();
                            }
                        }
                    }
                });

                return record;
            }
        });

        mainPanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.DELETE) {
                    if (recordsTable.getSelectionModel().getSelectedItem() != null) {
                        deleteRecord();
                    }
                }
            }
        });
    }

    protected void getStage(Stage stage) {
        primaryStage = stage;
    }


    @FXML
    public void addRecord() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPanel.getScene().getWindow());
        dialog.setTitle("New Payment Record");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("recordDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            logFile.modifyLogFile("Error loading addRecord dialog : " + e.getMessage());
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            RecordController recordController = fxmlLoader.getController();
            Record newRecord = recordController.getNewRecord();

            if (newRecord.getPaymentDate() == null || newRecord.getAssessmentYear() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Date Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter appropriate details and try again");
                alert.showAndWait();
            } else {
                data.addRecord(newRecord, newRecord.getClientName(), newRecord.getAssessmentYear(), newRecord.getTaxType(), newRecord.getPaymentDate(), newRecord.getPaymentAmount());
            }
        }

        showAllRecord();
    }


    @FXML
    public void editRecord() {
        Record selectedRecord = recordsTable.getSelectionModel().getSelectedItem();
        if (selectedRecord == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("No Record Selected");
            alert.setContentText("Please select a record to Edit");
            alert.showAndWait();
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPanel.getScene().getWindow());
        dialog.setTitle("Edit Record");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("recordDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            logFile.modifyLogFile("Error loading editRecord dialog : " + e.getMessage());
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        RecordController recordController = fxmlLoader.getController();
        recordController.setPreviousEntries(selectedRecord.getClientName(), selectedRecord.getAssessmentYear(), selectedRecord.getTaxType(), selectedRecord.getPaymentDate(), selectedRecord.getPaymentAmount());

        recordController.editRecord(selectedRecord);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            recordController.updateRecord();
            if (RecordController.NEW_PAYMENT_DATE == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Date Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter appropriate date and try again");
                alert.showAndWait();
            } else {
                data.updateRecord(selectedRecord);
            }
        }
    }


    @FXML
    public void deleteRecord() {
        Record selectedRecord = recordsTable.getSelectionModel().getSelectedItem();

        if (selectedRecord == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error!");
            alert.setHeaderText("No Record Selected");
            alert.setContentText("Please select a Record to Delete");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Record");
        alert.setHeaderText("Are you sure want to delete this record");
        alert.setContentText(selectedRecord.getClientName() + " -> " + selectedRecord.getTaxType());

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);
            alert1.setTitle("Confirm?");
            alert1.setHeaderText("Are you Sure?");
            alert1.setContentText("This action can't be reversed once done\n ");
            Optional<ButtonType> confirm = alert1.showAndWait();

            if (confirm.isPresent() && confirm.get() == ButtonType.OK) {
                data.deleteRecord(selectedRecord, selectedRecord.getClientName(), selectedRecord.getAssessmentYear(),
                        selectedRecord.getTaxType(), selectedRecord.getPaymentDate(), selectedRecord.getPaymentAmount());
            }
        }
    }


    @FXML
    public void searchRecord() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPanel.getScene().getWindow());
        dialog.setTitle("Search Record");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("searchDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            logFile.modifyLogFile("Error loading searchRecord dialog : " + e.getMessage());
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            if (SearchController.sqlCommand == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Searching");
                alert.setHeaderText(null);
                alert.setContentText("Invalid or Incorrect input(s)");
                alert.showAndWait();
                return;
            } else {
                recordsTable.setItems(data.searchRecord(SearchController.sqlCommand));
            }
        }
    }


    @FXML
    public void showAllRecord() {
        data.loadAllRecords();
        recordsTable.setItems(data.getRecords());
    }

    @FXML
    public void exitApplication() {
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
            primaryStage.close();
        }
    }

    @FXML
    public void exportToExcel() {
        data.exportDataToExcel(primaryStage);
    }

    @FXML
    public void showHelp() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Help");
        alert.setHeaderText(null);
        alert.setContentText("\nHappy to Bring some helpful Shortcuts\n\n" +
                "1. Show All Records :\t\tCtrl+Alt+S\n" +
                "2. Add New Record :\t\tCtrl+N\n" +
                "3. Edit Record :\t\t\tCtrl+E\n" +
                "4. Delete Record :\t\t\tCtrl+D\n" +
                "5. Search Record : \t\t\tCtrl+S\n" +
                "6. Show Help Menu :\t\tF11\n" +
                "7. Exit Application :\t\t\tAlt+F4\n\n");
        alert.showAndWait();
    }


    @FXML
    public void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Developer");
        alert.setHeaderText(null);
        alert.setContentText("------ Payment Management System ------\n\t\tBuild version 1.0.5\n\nThanks for using this software\n\nDeveloped by Akhilesh Garg. " +
                "This software is designed to maintain all payments records in\neasy and simpler way.\n\n" +
                "Report any bug to akgarg0472@gmail.com\n\n" + "Copyright© 2019-2020");
        alert.showAndWait();
    }


    @FXML
    public void availableSoon() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Under Development...");
        alert.setHeaderText(null);
        alert.setContentText("This feature will be available soon");
        alert.showAndWait();
    }


    @FXML
    public void showChangelogs() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Changelogs");
        alert.setHeaderText(null);
        alert.setContentText("\n--Current version Changelogs--\n\n" +
                "1. Added option to export data to excel spreadsheet\n" +
                "2. Fixed application closing issue bug\n" +
                "3. Change Assessment Year field to Dropdown selection\n" +
                "4. Improved shortcut keys support\n" +
                "5. Added option to delete record using Delete key\n" +
                "6. Fixed bug for date not updating while updating a record\n" +
                "7. Added logs support to trace the errors and other stuff\n" +
                "8. Bug Fixes and Performance Improvement\n" +
                "9. Under the hood optimisations and code cleanup\n\n");
        alert.showAndWait();
    }
}
