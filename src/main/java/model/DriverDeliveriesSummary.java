package model;

import java.util.List;

public class DriverDeliveriesSummary {
    private List<DriverDeliveries> summaries;
    private int totalDeliveries;
    private double totalAmountPaid;

    public DriverDeliveriesSummary(List<DriverDeliveries> summaries, int totalDeliveries, double totalAmountPaid) {
        this.summaries = summaries;
        this.totalDeliveries = totalDeliveries;
        this.totalAmountPaid = totalAmountPaid;
    }

    public List<DriverDeliveries> getSummaries() {
        return summaries;
    }

    public void setSummaries(List<DriverDeliveries> summaries) {
        this.summaries = summaries;
    }

    public int getTotalDeliveries() {
        return totalDeliveries;
    }

    public void setTotalDeliveries(int totalDeliveries) {
        this.totalDeliveries = totalDeliveries;
    }

    public double getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(double totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }
}
