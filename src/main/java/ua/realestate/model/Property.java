package ua.realestate.model;

import ua.common.BaseEntity;
import ua.realestate.util.RealEstateUtils;

import java.util.Objects;
import java.util.Set;

public class Property extends BaseEntity {

    private static final Set<String> ALLOWED_TYPES = Set.of("APARTMENT", "HOUSE", "COMMERCIAL", "GARAGE");

    private final String cadastralNumber;
    private final String address;
    private final Landlord landlord;
    private final String type;
    private double monthlyRent;

    public Property(String cadastralNumber, String address, Landlord landlord, String type, double monthlyRent) {
        super();
        RealEstateUtils.validateNotBlank(cadastralNumber, "cadastralNumber");
        RealEstateUtils.validateNotBlank(address, "address");
        RealEstateUtils.validateNotNull(landlord, "landlord");
        this.cadastralNumber = cadastralNumber.trim();
        this.address = address.trim();
        this.landlord = landlord;
        this.type = validateType(type);
        setMonthlyRent(monthlyRent);
    }

    private static String validateType(String type) {
        RealEstateUtils.validateNotBlank(type, "type");
        String normalized = RealEstateUtils.normalize(type);
        if (!ALLOWED_TYPES.contains(normalized)) {
            throw new IllegalArgumentException(
                "type must be one of " + ALLOWED_TYPES + ", got: " + type
            );
        }
        return normalized;
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

    public String getType() {
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
            ", landlord=" + landlord.getTaxCode() +
            ", type='" + type + '\'' +
            ", monthlyRent=" + monthlyRent +
            ", createdAt=" + createdAt +
            '}';
    }
}
