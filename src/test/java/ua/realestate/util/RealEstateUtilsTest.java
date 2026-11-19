package ua.realestate.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.realestate.model.ContractStatus;
import ua.realestate.model.Landlord;
import ua.realestate.model.LeaseContract;
import ua.realestate.model.Property;
import ua.realestate.model.PropertyType;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RealEstateUtilsTest {

    private LeaseContract contract;

    @BeforeEach
    void setUp() {
        Landlord landlord = new Landlord("1234567890", "Test Landlord", "+380501112233");
        Property property = new Property("123:45:67", "Test Address", landlord, PropertyType.APARTMENT, 10000.0);
        contract = LeaseContract.of(
            "CNT-001",
            property,
            "Test Tenant",
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2026, 7, 1),
            10000.0,
            ContractStatus.ACTIVE
        );
    }

    @Test
    @DisplayName("Should correctly calculate contract duration in months")
    void shouldCalculateContractMonthsCorrectly() {
        long months = RealEstateUtils.contractMonths(contract);
        assertEquals(6, months);
    }

    @Test
    @DisplayName("Should correctly calculate total rent amount for the contract")
    void shouldCalculateTotalRentCorrectly() {
        double total = RealEstateUtils.totalRent(contract);
        assertEquals(60000.0, total);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when calculating months for null contract")
    void shouldThrowExceptionWhenContractIsNull() {
        assertThrows(IllegalArgumentException.class, () -> RealEstateUtils.contractMonths(null));
    }
}
