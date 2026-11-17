package ua.realestate.model;

import ua.realestate.util.RealEstateUtils;

public record Landlord(String taxCode, String fullName, String phone) {

    public Landlord {
        RealEstateUtils.validateNotBlank(taxCode, "taxCode");
        RealEstateUtils.validateNotBlank(fullName, "fullName");
        RealEstateUtils.validateNotBlank(phone, "phone");
        taxCode = taxCode.trim();
        fullName = fullName.trim();
        phone = phone.trim();
    }
}
