package ua.realestate.model;

import ua.realestate.util.RealEstateUtils;

import java.time.LocalDate;

public record RentPayment(LeaseContract contract, double amount, LocalDate paymentDate) {

    public RentPayment {
        RealEstateUtils.validateNotNull(contract, "contract");
        RealEstateUtils.validatePositive(amount, "amount");
        RealEstateUtils.validateNotNull(paymentDate, "paymentDate");
        if (paymentDate.isBefore(contract.getStartDate())) {
            throw new IllegalArgumentException(
                "paymentDate must not be before contract.startDate (" + contract.getStartDate() + "), got: " + paymentDate
            );
        }
    }

    public static RentPayment of(LeaseContract contract, double amount, LocalDate paymentDate) {
        return new RentPayment(contract, amount, paymentDate);
    }
}
