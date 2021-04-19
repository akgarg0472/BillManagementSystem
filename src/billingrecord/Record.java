package billingrecord;

import javafx.beans.property.SimpleStringProperty;

@SuppressWarnings("all")
public class Record {
    //    name, ay, payment date, amount, phone no
    private SimpleStringProperty clientName = new SimpleStringProperty();
    private SimpleStringProperty assessmentYear = new SimpleStringProperty();
    private SimpleStringProperty paymentDate = new SimpleStringProperty();
    private SimpleStringProperty paymentAmount = new SimpleStringProperty();
    private SimpleStringProperty taxType = new SimpleStringProperty();

    public Record() {
    }

    public Record(String clientName, String assessmentYear, String taxType, String paymentDate,
                  String paymentAmount) {
        this.clientName.set(clientName);
        this.assessmentYear.set(assessmentYear);
        this.taxType.set(taxType);
        this.paymentDate.set(paymentDate);
        this.paymentAmount.set(paymentAmount);
    }

    public String getClientName() {
        return clientName.get();
    }

    public SimpleStringProperty clientNameProperty() {
        return clientName;
    }

    public String getAssessmentYear() {
        return assessmentYear.get();
    }

    public SimpleStringProperty assessmentYearProperty() {
        return assessmentYear;
    }

    public String getPaymentDate() {
        return paymentDate.get();
    }

    public SimpleStringProperty paymentDateProperty() {
        return paymentDate;
    }

    public String getPaymentAmount() {
        return paymentAmount.get();
    }

    public SimpleStringProperty paymentAmountProperty() {
        return paymentAmount;
    }

    public String getTaxType() {
        return taxType.get();
    }

    public SimpleStringProperty taxTypeProperty() {
        return taxType;
    }

    public void setClientName(String clientName) {
        this.clientName.set(clientName);
    }

    public void setAssessmentYear(String assessmentYear) {
        this.assessmentYear.set(assessmentYear);
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate.set(paymentDate);
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount.set(paymentAmount);
    }

    public void setTaxType(String taxType) {
        this.taxType.set(taxType);
    }

}
