package ua.realestate.model;

import ua.realestate.util.RealEstateUtils;

import java.util.Objects;

public class Property {

    private final String cadastralNumber;
    private final String address;
    private final Landlord landlord;
    private final PropertyType type;
    private double monthlyRent;

    public Property(String cadastralNumber, String address, Landlord landlord, PropertyType type, double monthlyRent) {
        RealEstateUtils.validateNotBlank(cadastralNumber, "cadastralNumber");
        RealEstateUtils.validateNotBlank(address, "address");
        RealEstateUtils.validateNotNull(landlord, "landlord");
        RealEstateUtils.validateNotNull(type, "type");
        this.cadastralNumber = cadastralNumber.trim();
        this.address = address.trim();
        this.landlord = landlord;
        this.type = type;
        setMonthlyRent(monthlyRent);
    }

    public final void setMonthlyRent(double monthlyRent) {
        RealEstateUtils.validatePositive(monthlyRent, "monthlyRent");
        this.monthlyRent = monthlyRent;
    }

    public String getCadastralNumber() {
        return cadastralNumber;
    }

    public String getAddress() {
        return address;
    }

    public Landlord getLandlord() {
        return landlord;
    }

    public PropertyType getType() {
        return type;
    }

    public double getMonthlyRent() {
        return monthlyRent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return Objects.equals(cadastralNumber, property.cadastralNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cadastralNumber);
    }

    @Override
    public String toString() {
        return "Property{" +
            "cadastralNumber='" + cadastralNumber + '\'' +
            ", address='" + address + '\'' +
            ", landlord=" + landlord.taxCode() +
            ", type=" + type +
            ", monthlyRent=" + monthlyRent +
            '}';
    }
}
