package ua.realestate.repository;

@FunctionalInterface
public interface IdentityExtractor<T> {
    String identityOf(T item);
}
