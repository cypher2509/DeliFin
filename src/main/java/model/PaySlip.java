package model;
import java.util.List;

public class PaySlip {
    private String firstName;
    private String lastName;
    private String driverId;
    private int weekNumber;
    private String invoiceNumber;
    private String from;
    private String to;
    private List<Transaction> transactions;
    private double gasOrBonus= 0;
    private double insurance = 0;
    private double deductions = 0;
    private double payableAmount;
    private int totalDeliveries;
    private double ytdEarnings;
    private int ytdDeliveries;

    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        this.weekNumber = weekNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public double getGasOrBonus() {
        return gasOrBonus;
    }

    public void setGasOrBonus(float gasOrBonus) {
        this.gasOrBonus = gasOrBonus;
    }

    public double getInsurance() {
        return insurance;
    }
    public void setInsurance(double insurance) {
        this.insurance = insurance;
    }

    public double getDeductions() {
        return deductions;
    }
    public void setDeductions(double deductions) {
        this.deductions = deductions;
    }

    public void setId(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getId() {
        return invoiceNumber;
    }

    public double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public int getTotalDeliveries() {
        return totalDeliveries;
    }
    public void setTotalDeliveries(int totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }

    public double getYtdEarnings() {
        return ytdEarnings;
    }
    public void setYtdEarnings(double ytdEarnings) {
        this.ytdEarnings = ytdEarnings;
    }

    public int getYtdDeliveries() {
        return ytdDeliveries;
    }
    public void setYtdDeliveries(int ytdDeliveries) {
        this.ytdDeliveries = ytdDeliveries;
    }
}
