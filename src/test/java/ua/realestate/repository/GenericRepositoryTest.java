package ua.realestate.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.realestate.exception.DuplicateIdentityException;
import ua.realestate.model.Landlord;
import ua.realestate.model.Property;
import ua.realestate.model.PropertyType;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GenericRepositoryTest {

    private GenericRepository<Landlord> repository;
    private Landlord landlord1;
    private Landlord landlord2;

    @BeforeEach
    void setUp() {
        repository = new GenericRepository<>("TestLandlordRepo", Landlord::taxCode);
        landlord1 = new Landlord("1111111111", "John Doe", "+380501112233");
        landlord2 = new Landlord("2222222222", "Jane Smith", "+380504445566");
    }

    @Test
    @DisplayName("Should find added element by its identity")
    void shouldFindAddedItemByIdentity() {
        repository.add(landlord1);

        Optional<Landlord> found = repository.findByIdentity("1111111111");

        assertTrue(found.isPresent());
        assertEquals(landlord1, found.get());
    }

    @Test
    @DisplayName("Should return empty Optional when identity is absent")
    void shouldReturnEmptyOptionalWhenIdentityNotFound() {
        Optional<Landlord> found = repository.findByIdentity("non-existent-tax-code");

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should throw DuplicateIdentityException containing identity and repo name on repeated add")
    void shouldThrowDuplicateIdentityExceptionOnRepeatedAdd() {
        repository.add(landlord1);

        DuplicateIdentityException exception = assertThrows(
                DuplicateIdentityException.class,
                () -> repository.add(new Landlord("1111111111", "Different Name", "+380507778899"))
        );

        assertTrue(exception.getMessage().contains("1111111111"));
        assertTrue(exception.getMessage().contains("TestLandlordRepo"));
        assertEquals(1, repository.size());
    }

    @Test
    @DisplayName("Should return false and not change size when addIfAbsent encounters duplicate")
    void shouldReturnFalseAndKeepSizeUnchangedWhenAddingDuplicateViaAddIfAbsent() {
        repository.add(landlord1);
        int initialSize = repository.size();

        boolean added = repository.addIfAbsent(new Landlord("1111111111", "Another Name", "+380500000000"));

        assertFalse(added);
        assertEquals(initialSize, repository.size());
    }

    @Test
    @DisplayName("Should remove existing element and return false for absent element")
    void shouldRemoveExistingItemAndReturnFalseForAbsentItem() {
        repository.add(landlord1);
        assertEquals(1, repository.size());

        boolean removedExisting = repository.removeByIdentity("1111111111");
        assertTrue(removedExisting);
        assertEquals(0, repository.size());
        assertTrue(repository.findByIdentity("1111111111").isEmpty());

        boolean removedAbsent = repository.removeByIdentity("1111111111");
        assertFalse(removedAbsent);
        assertEquals(0, repository.size());
    }

    @Test
    @DisplayName("Should not affect repository state when attempting to modify findAll result")
    void shouldNotAffectRepositoryStateWhenModifyingFindAllResult() {
        repository.add(landlord1);
        repository.add(landlord2);
        assertEquals(2, repository.size());

        List<Landlord> list = repository.findAll();
        list.clear();

        assertEquals(2, repository.size());
        assertEquals(2, repository.findAll().size());
        assertTrue(repository.findByIdentity("1111111111").isPresent());
    }

    @Test
    @DisplayName("Should work correctly with multiple different domain types (Record and Mutable class)")
    void shouldWorkWithMultipleDifferentDomainTypes() {
        GenericRepository<Landlord> landlordRepo = new GenericRepository<>("Landlords", Landlord::taxCode);
        landlordRepo.add(landlord1);
        assertEquals(1, landlordRepo.size());
        assertEquals(landlord1, landlordRepo.findByIdentity("1111111111").orElseThrow());

        GenericRepository<Property> propertyRepo = new GenericRepository<>(
                "Properties",
                Property::getCadastralNumber
        );
        Property property = new Property(
                "8000000000:01:001:0001",
                "Khreshchatyk 1",
                landlord1,
                PropertyType.COMMERCIAL,
                25000.0
        );
        propertyRepo.add(property);
        assertEquals(1, propertyRepo.size());
        assertEquals(property, propertyRepo.findByIdentity("8000000000:01:001:0001").orElseThrow());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when adding null item or empty identity")
    void shouldThrowIllegalArgumentExceptionWhenAddingNullOrEmptyIdentity() {
        assertThrows(IllegalArgumentException.class, () -> repository.add(null));
        assertThrows(IllegalArgumentException.class, () -> repository.addIfAbsent(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> repository.add(new Landlord("   ", "Name", "+380501112233"))
        );
    }

    @Test
    @DisplayName("Should add all items from collection")
    void shouldAddAllItemsSuccessfully() {
        repository.addAll(List.of(landlord1, landlord2));
        assertEquals(2, repository.size());
    }
}
