package ua.realestate.util;

import java.time.LocalDate;
import java.util.Set;

class ValidationHelper {

    static final Set<String> ALLOWED_PROPERTY_TYPES = Set.of("APARTMENT", "HOUSE", "COMMERCIAL", "GARAGE");
    static final Set<String> ALLOWED_CONTRACT_STATUSES = Set.of("ACTIVE", "TERMINATED", "EXPIRED");

    private ValidationHelper() {
    }

    static void requireNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                fieldName + " must not be blank, got: " + value
            );
        }
    }

    static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(
                fieldName + " must not be null"
            );
        }
    }

    static void requirePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(
                fieldName + " must be > 0, got: " + value
            );
        }
    }

    static void requireGreaterOrEqual(double value, double min, String fieldName) {
        if (value < min) {
            throw new IllegalArgumentException(
                fieldName + " must be >= " + min + ", got: " + value
            );
        }
    }

    static void requireDateAfter(LocalDate value, LocalDate reference, String fieldName, String referenceName) {
        requireNotNull(value, fieldName);
        if (!value.isAfter(reference)) {
            throw new IllegalArgumentException(
                fieldName + " must be strictly after " + referenceName + " (" + reference + "), got: " + value
            );
        }
    }

    static void requireDateNotBefore(LocalDate value, LocalDate reference, String fieldName, String referenceName) {
        requireNotNull(value, fieldName);
        if (value.isBefore(reference)) {
            throw new IllegalArgumentException(
                fieldName + " must not be before " + referenceName + " (" + reference + "), got: " + value
            );
        }
    }

    static void requireInAllowedSet(String value, Set<String> allowed, String fieldName) {
        if (!allowed.contains(value)) {
            throw new IllegalArgumentException(
                fieldName + " must be one of " + allowed + ", got: " + value
            );
        }
    }
}
