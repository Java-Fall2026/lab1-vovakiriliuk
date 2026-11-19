package ua.realestate.loader;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.realestate.exception.InvalidDataException;
import ua.realestate.model.Landlord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LandlordLoaderTest {

    private LandlordLoader loader;

    @BeforeEach
    void setUp() {
        loader = new LandlordLoader();
    }

    @Test
    @DisplayName("Should successfully parse valid CSV line")
    void shouldParseValidCsvLineSuccessfully() throws InvalidDataException {
        String line = "3456789012,John Doe,+380501234567";

        Landlord landlord = loader.parseLine(line, 2);

        assertNotNull(landlord);
        assertEquals("3456789012", landlord.taxCode());
        assertEquals("John Doe", landlord.fullName());
        assertEquals("+380501234567", landlord.phone());
    }

    @Test
    @DisplayName("Should throw InvalidDataException with line number and cause when columns are missing")
    void shouldThrowInvalidDataExceptionWhenColumnsMissing() {
        String line = "3456789012,John Doe";

        InvalidDataException exception = assertThrows(
            InvalidDataException.class,
            () -> loader.parseLine(line, 5)
        );

        assertTrue(exception.getMessage().contains("Line 5"));
        assertNotNull(exception.getCause());
        assertInstanceOf(ArrayIndexOutOfBoundsException.class, exception.getCause());
    }

    @Test
    @DisplayName("Should throw InvalidDataException when taxCode is blank")
    void shouldThrowInvalidDataExceptionWhenTaxCodeBlank() {
        String line = "   ,John Doe,+380501234567";

        InvalidDataException exception = assertThrows(
            InvalidDataException.class,
            () -> loader.parseLine(line, 7)
        );

        assertTrue(exception.getMessage().contains("Line 7"));
        assertNotNull(exception.getCause());
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }

    @Test
    @DisplayName("Should throw InvalidDataException when phone is blank")
    void shouldThrowInvalidDataExceptionWhenPhoneBlank() {
        String line = "3456789012,John Doe,   ";

        InvalidDataException exception = assertThrows(
            InvalidDataException.class,
            () -> loader.parseLine(line, 9)
        );

        assertTrue(exception.getMessage().contains("Line 9"));
        assertNotNull(exception.getCause());
        assertInstanceOf(IllegalArgumentException.class, exception.getCause());
    }

    @Test
    @DisplayName("Should skip invalid lines and parse valid lines from Reader")
    void shouldSkipInvalidLinesAndKeepValidWhenReadingFromReader() throws IOException {
        String csvContent = """
            taxCode,fullName,phone
            1111111111,First Landlord,+380501111111
            invalid_line_missing_columns
            2222222222, ,+380502222222
            3333333333,Third Landlord,+380503333333
            """;

        List<Landlord> landlords;
        try (BufferedReader reader = new BufferedReader(new StringReader(csvContent))) {
            landlords = loader.readFromReader(reader);
        }

        assertEquals(2, landlords.size());
        assertEquals("1111111111", landlords.get(0).taxCode());
        assertEquals("3333333333", landlords.get(1).taxCode());
    }

    @Test
    @DisplayName("Should safely return empty list when file does not exist")
    void shouldReturnEmptyListWhenFileNotFound() {
        Path nonExistent = Path.of("non_existent_dir", "missing_file.csv");

        List<Landlord> result = loader.loadLandlords(nonExistent);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
