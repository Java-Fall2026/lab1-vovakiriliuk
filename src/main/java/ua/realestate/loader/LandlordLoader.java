package ua.realestate.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.realestate.exception.InvalidDataException;
import ua.realestate.model.Landlord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LandlordLoader {

    private static final Logger log = LoggerFactory.getLogger(LandlordLoader.class);

    public Landlord parseLine(String line, int lineNumber) throws InvalidDataException {
        if (line == null || line.isBlank()) {
            throw new InvalidDataException("Line " + lineNumber + ": line is blank");
        }
        try {
            String[] parts = line.split(",", -1);
            if (parts.length > 3) {
                throw new IllegalArgumentException("expected 3 columns but found " + parts.length);
            }
            return new Landlord(parts[0], parts[1], parts[2]);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            throw new InvalidDataException("Line " + lineNumber + ": " + e.getMessage(), e);
        }
    }

    public List<Landlord> readFromReader(BufferedReader reader) throws IOException {
        List<Landlord> landlords = new ArrayList<>();
        String line;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (lineNumber == 1) {
                continue;
            }
            if (line.isBlank()) {
                continue;
            }
            try {
                Landlord landlord = parseLine(line, lineNumber);
                landlords.add(landlord);
            } catch (InvalidDataException e) {
                log.warn("Skipping invalid CSV line {}: {}", lineNumber, e.getMessage(), e);
            }
        }
        return landlords;
    }

    public List<Landlord> loadLandlords(Path path) {
        log.info("Starting to read landlords from file: {}", path);
        List<Landlord> landlords = new ArrayList<>();
        if (path == null) {
            log.error("Path must not be null");
            return landlords;
        }
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            landlords = readFromReader(reader);
            log.info("Successfully loaded {} valid landlords from {}", landlords.size(), path);
        } catch (NoSuchFileException e) {
            log.error("File not found: {}", path, e);
        } catch (IOException e) {
            log.error("I/O error while reading file: {}", path, e);
        } finally {
            log.info("Finished processing file: {} (loaded {} records)", path, landlords.size());
        }
        return landlords;
    }
}
