package ua.realestate.model;

import ua.common.BaseEntity;
import ua.realestate.util.RealEstateUtils;

import java.util.Objects;

public class Landlord extends BaseEntity {

    private final String taxCode;
    private final String fullName;
    private final String phone;

    public Landlord(String taxCode, String fullName, String phone) {
        super();
        RealEstateUtils.validateNotBlank(taxCode, "taxCode");
        RealEstateUtils.validateNotBlank(fullName, "fullName");
        RealEstateUtils.validateNotBlank(phone, "phone");
        this.taxCode = taxCode.trim();
        this.fullName = fullName.trim();
        this.phone = phone.trim();
    }

    public String getTaxCode() {
        return taxCode;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhone() {
        return phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Landlord landlord = (Landlord) o;
        return Objects.equals(taxCode, landlord.taxCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taxCode);
    }

    @Override
    public String toString() {
        return "Landlord{" +
            "taxCode='" + taxCode + '\'' +
            ", fullName='" + fullName + '\'' +
            ", phone='" + phone + '\'' +
            ", createdAt=" + createdAt +
            '}';
    }
}
