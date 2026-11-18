package ua.realestate.model;

import ua.realestate.util.RealEstateUtils;

import java.time.LocalDate;
import java.util.Objects;

public class LeaseContract {

    private final String contractNumber;
    private final Property property;
    private final String tenantName;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double depositAmount;
    private ContractStatus status;

    private LeaseContract(String contractNumber, Property property, String tenantName,
                          LocalDate startDate, LocalDate endDate, double depositAmount, ContractStatus status) {
        RealEstateUtils.validateNotBlank(contractNumber, "contractNumber");
        RealEstateUtils.validateNotNull(property, "property");
        RealEstateUtils.validateNotBlank(tenantName, "tenantName");
        RealEstateUtils.validateNotNull(startDate, "startDate");
        RealEstateUtils.validateNotNull(endDate, "endDate");
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException(
                "endDate must be strictly after startDate (" + startDate + "), got: " + endDate
            );
        }
        if (depositAmount < property.getMonthlyRent()) {
            throw new IllegalArgumentException(
                "depositAmount must be >= property.monthlyRent (" + property.getMonthlyRent() + "), got: " + depositAmount
            );
        }
        this.contractNumber = contractNumber.trim();
        this.property = property;
        this.tenantName = tenantName.trim();
        this.startDate = startDate;
        this.endDate = endDate;
        this.depositAmount = depositAmount;
        setStatus(status);
    }

    public static LeaseContract of(String contractNumber, Property property, String tenantName,
                                   LocalDate startDate, LocalDate endDate, double depositAmount, ContractStatus status) {
        return new LeaseContract(contractNumber, property, tenantName, startDate, endDate, depositAmount, status);
    }

    public final void setStatus(ContractStatus status) {
        RealEstateUtils.validateNotNull(status, "status");
        this.status = status;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public Property getProperty() {
        return property;
    }

    public String getTenantName() {
        return tenantName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public double getDepositAmount() {
        return depositAmount;
    }

    public ContractStatus getStatus() {
        return status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LeaseContract that = (LeaseContract) o;
        return Objects.equals(contractNumber, that.contractNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contractNumber);
    }

    @Override
    public String toString() {
        return "LeaseContract{" +
            "contractNumber='" + contractNumber + '\'' +
            ", property=" + property.getCadastralNumber() +
            ", tenantName='" + tenantName + '\'' +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", depositAmount=" + depositAmount +
            ", status=" + status +
            '}';
    }
}
