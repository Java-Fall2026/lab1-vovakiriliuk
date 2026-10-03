package ua.realestate.util;

class FormatHelper {

    private FormatHelper() {
    }

    static String normalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toUpperCase();
    }

    static String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        String trimmed = value.trim();
        return Character.toUpperCase(trimmed.charAt(0)) + trimmed.substring(1).toLowerCase();
    }

    static String formatMoney(double amount) {
        return String.format("%.2f UAH", amount);
    }
}
