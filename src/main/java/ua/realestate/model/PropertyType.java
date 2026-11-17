package ua.realestate.model;

public enum PropertyType {
    APARTMENT(1.0),
    HOUSE(1.6),
    COMMERCIAL(2.5),
    GARAGE(0.5);

    private final double priceFactor;

    PropertyType(double priceFactor) {
        this.priceFactor = priceFactor;
    }

    public double getPriceFactor() {
        return priceFactor;
    }
}
