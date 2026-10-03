package ua.realestate.model;

import ua.common.BaseEntity;
import ua.realestate.util.RealEstateUtils;

import java.time.LocalDate;
import java.util.Objects;

public class RentPayment extends BaseEntity {

    private final LeaseContract contract;
    private final double amount;
    private final LocalDate paymentDate;

    private RentPayment(LeaseContract contract, double amount, LocalDate paymentDate) {
        super();
        RealEstateUtils.validateNotNull(contract, "contract");
        RealEstateUtils.validatePositive(amount, "amount");
        RealEstateUtils.validateNotNull(paymentDate, "paymentDate");
        if (paymentDate.isBefore(contract.getStartDate())) {
            throw new IllegalArgumentException(
                "paymentDate must not be before contract.startDate (" + contract.getStartDate() + "), got: " + paymentDate
            );
        }
        this.contract = contract;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

    public static RentPayment of(LeaseContract contract, double amount, LocalDate paymentDate) {
        return new RentPayment(contract, amount, paymentDate);
    }

    public LeaseContract getContract() {
        return contract;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RentPayment that = (RentPayment) o;
        return Objects.equals(contract, that.contract)
            && Objects.equals(paymentDate, that.paymentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(contract, paymentDate);
    }

    @Override
    public String toString() {
        return "RentPayment{" +
            "contract=" + contract.getContractNumber() +
            ", amount=" + amount +
            ", paymentDate=" + paymentDate +
            ", createdAt=" + createdAt +
            '}';
    }
}
