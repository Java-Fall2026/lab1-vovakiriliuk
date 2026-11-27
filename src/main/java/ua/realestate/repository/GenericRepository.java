package ua.realestate.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.realestate.exception.DuplicateIdentityException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GenericRepository<T> {

    private static final Logger log = LoggerFactory.getLogger(GenericRepository.class);

    private final String name;
    private final IdentityExtractor<T> extractor;
    private final Map<String, T> storage;

    public GenericRepository(String name, IdentityExtractor<T> extractor) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Repository name cannot be null or blank");
        }
        if (extractor == null) {
            throw new IllegalArgumentException("IdentityExtractor cannot be null");
        }
        this.name = name.trim();
        this.extractor = extractor;
        this.storage = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    public void add(T item) {
        if (item == null) {
            log.error("Cannot add null item to repository '{}'", name);
            throw new IllegalArgumentException("Item cannot be null in repository '" + name + "'");
        }
        String identity = extractor.identityOf(item);
        if (identity == null || identity.isBlank()) {
            log.error("Item identity cannot be null or blank in repository '{}'", name);
            throw new IllegalArgumentException("Item identity cannot be null or blank in repository '" + name + "'");
        }
        if (storage.containsKey(identity)) {
            log.error("Duplicate identity '{}' in repository '{}'", identity, name);
            throw new DuplicateIdentityException("Duplicate identity '" + identity + "' in repository '" + name + "'");
        }
        storage.put(identity, item);
        log.info("Added item with identity '{}' to repository '{}'", identity, name);
    }

    public boolean addIfAbsent(T item) {
        if (item == null) {
            log.error("Cannot add null item to repository '{}'", name);
            throw new IllegalArgumentException("Item cannot be null in repository '" + name + "'");
        }
        String identity = extractor.identityOf(item);
        if (identity == null || identity.isBlank()) {
            log.error("Item identity cannot be null or blank in repository '{}'", name);
            throw new IllegalArgumentException("Item identity cannot be null or blank in repository '" + name + "'");
        }
        if (storage.containsKey(identity)) {
            log.warn("Rejected duplicate item with identity '{}' in repository '{}'", identity, name);
            return false;
        }
        storage.put(identity, item);
        log.info("Added item with identity '{}' to repository '{}'", identity, name);
        return true;
    }

    public void addAll(Collection<? extends T> items) {
        if (items == null) {
            log.error("Items collection cannot be null in repository '{}'", name);
            throw new IllegalArgumentException("Items collection cannot be null in repository '" + name + "'");
        }
        for (T item : items) {
            add(item);
        }
    }

    public boolean removeByIdentity(String identity) {
        if (identity == null || identity.isBlank() || !storage.containsKey(identity)) {
            log.warn("Cannot remove: item with identity '{}' not found in repository '{}'", identity, name);
            return false;
        }
        storage.remove(identity);
        log.info("Removed item with identity '{}' from repository '{}'", identity, name);
        return true;
    }

    public Optional<T> findByIdentity(String identity) {
        if (identity == null || identity.isBlank()) {
            log.warn("Cannot find item with null or blank identity in repository '{}'", name);
            return Optional.empty();
        }
        T item = storage.get(identity);
        if (item == null) {
            log.warn("Item with identity '{}' not found in repository '{}'", identity, name);
            return Optional.empty();
        }
        log.info("Found item with identity '{}' in repository '{}'", identity, name);
        return Optional.of(item);
    }

    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }

    public int size() {
        return storage.size();
    }
}
