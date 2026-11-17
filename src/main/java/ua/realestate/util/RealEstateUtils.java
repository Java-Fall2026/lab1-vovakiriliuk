package ua.realestate.util;

import ua.realestate.model.ContractStatus;
import ua.realestate.model.LeaseContract;
import ua.realestate.model.PropertyType;

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

    public static String getPropertyTypeDescription(PropertyType type) {
        ValidationHelper.requireNotNull(type, "type");
        return switch (type) {
            case APARTMENT -> "Житлова квартира";
            case HOUSE -> "Приватний житловий будинок";
            case COMMERCIAL -> "Комерційне нежитлове приміщення";
            case GARAGE -> "Гаражний бокс або автомісце";
        };
    }

    public static String getAvailableAction(ContractStatus status) {
        ValidationHelper.requireNotNull(status, "status");
        return switch (status) {
            case ACTIVE -> "Платежі дозволено, договір обслуговується";
            case TERMINATED -> "Договір розірвано, будь-які операції заборонено";
            case EXPIRED -> "Термін дії вичерпано, очікується переукладання або закриття";
        };
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
