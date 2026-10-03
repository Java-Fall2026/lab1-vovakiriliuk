package ua.realestate.util;

import ua.realestate.model.LeaseContract;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RealEstateUtils {

    private RealEstateUtils() {
    }

    public static long contractMonths(LeaseContract contract) {
        ValidationHelper.requireNotNull(contract, "contract");
        LocalDate start = contract.getStartDate();
        LocalDate end = contract.getEndDate();
        return ChronoUnit.MONTHS.between(start, end);
    }

    public static double totalRent(LeaseContract contract) {
        ValidationHelper.requireNotNull(contract, "contract");
        long months = contractMonths(contract);
        return months * contract.getProperty().getMonthlyRent();
    }

    public static String normalize(String value) {
        return FormatHelper.normalize(value);
    }

    public static String formatMoney(double amount) {
        return FormatHelper.formatMoney(amount);
    }

    public static void validateNotBlank(String value, String fieldName) {
        ValidationHelper.requireNotBlank(value, fieldName);
    }

    public static void validateNotNull(Object value, String fieldName) {
        ValidationHelper.requireNotNull(value, fieldName);
    }

    public static void validatePositive(double value, String fieldName) {
        ValidationHelper.requirePositive(value, fieldName);
    }
}
