package ua.realestate;

import ua.realestate.model.ContractStatus;
import ua.realestate.model.Landlord;
import ua.realestate.model.LeaseContract;
import ua.realestate.model.Property;
import ua.realestate.model.PropertyType;
import ua.realestate.model.RentPayment;
import ua.realestate.util.RealEstateUtils;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== 1. Створення record-об'єкта та звернення до його компонентів ===");
        Landlord landlord = new Landlord("3456789012", "Іванов Іван Іванович", "+380501234567");
        System.out.println("taxCode(): " + landlord.taxCode());
        System.out.println("fullName(): " + landlord.fullName());
        System.out.println("phone(): " + landlord.phone());

        System.out.println();
        System.out.println("=== 2. Робота з enum (values, власне поле, valueOf у try/catch) ===");
        System.out.println("Перебір усіх значень PropertyType.values():");
        for (PropertyType type : PropertyType.values()) {
            System.out.println("  " + type.name() + " -> коефіцієнт ціни: " + type.getPriceFactor());
        }

        PropertyType commercial = PropertyType.COMMERCIAL;
        System.out.println("Доступ до власного поля константи COMMERCIAL: " + commercial.getPriceFactor());

        try {
            PropertyType.valueOf("INVALID_TYPE");
        } catch (IllegalArgumentException e) {
            System.out.println("Перехоплено виняток для невідомого рядка у valueOf: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== 3. Робота switch-виразів ===");
        for (PropertyType type : PropertyType.values()) {
            System.out.println("Switch 1 (PropertyType): " + type + " -> " + RealEstateUtils.getPropertyTypeDescription(type));
        }

        for (ContractStatus status : ContractStatus.values()) {
            System.out.println("Switch 2 (ContractStatus): " + status + " -> " + RealEstateUtils.getAvailableAction(status));
        }

        System.out.println();
        System.out.println("=== 4. Створення record з невалідними даними (компактний конструктор) ===");
        try {
            new Landlord("   ", "Петренко Петро", "+380501112233");
        } catch (IllegalArgumentException e) {
            System.out.println("Перехоплено виняток для порожнього taxCode: " + e.getMessage());
        }

        Property property = new Property(
            "1234567890:01:001:0001",
            "вул. Шевченка, 10, Київ",
            landlord,
            PropertyType.APARTMENT,
            15000.0
        );

        LeaseContract contract = LeaseContract.of(
            "CNT-2026-01",
            property,
            "Петренко Петро",
            LocalDate.of(2026, 1, 1),
            LocalDate.of(2027, 1, 1),
            15000.0,
            ContractStatus.ACTIVE
        );

        try {
            RentPayment.of(contract, -500.0, LocalDate.of(2026, 2, 1));
        } catch (IllegalArgumentException e) {
            System.out.println("Перехоплено виняток для негативної суми платежу: " + e.getMessage());
        }

        try {
            RentPayment.of(contract, 15000.0, LocalDate.of(2025, 12, 1));
        } catch (IllegalArgumentException e) {
            System.out.println("Перехоплено виняток для paymentDate ранішої за startDate: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== 5. Порівняння record-об'єктів (equals та hashCode без власного коду) ===");
        Landlord sameLandlord = new Landlord("3456789012", "Іванов Іван Іванович", "+380501234567");
        Landlord otherLandlord = new Landlord("9876543210", "Сидоренко Василь", "+380679876543");

        System.out.println("landlord == sameLandlord: " + (landlord == sameLandlord));
        System.out.println("landlord.equals(sameLandlord): " + landlord.equals(sameLandlord));
        System.out.println("landlord.hashCode() == sameLandlord.hashCode(): " + (landlord.hashCode() == sameLandlord.hashCode()));
        System.out.println("landlord.equals(otherLandlord): " + landlord.equals(otherLandlord));

        RentPayment payment1 = RentPayment.of(contract, 15000.0, LocalDate.of(2026, 2, 1));
        RentPayment payment2 = RentPayment.of(contract, 15000.0, LocalDate.of(2026, 2, 1));
        System.out.println("payment1.equals(payment2): " + payment1.equals(payment2));
        System.out.println("payment1.hashCode() == payment2.hashCode(): " + (payment1.hashCode() == payment2.hashCode()));

        System.out.println();
        System.out.println("=== 6. Вивід record через згенерований toString() ===");
        System.out.println("Landlord toString(): " + landlord);
        System.out.println("RentPayment toString(): " + payment1);

        System.out.println();
        System.out.println("=== 7. Класи з мутабельним станом та розрахунки ===");
        System.out.println("Початкова орендна плата Property: " + property.getMonthlyRent());
        property.setMonthlyRent(18000.0);
        System.out.println("Оновлена орендна плата: " + property.getMonthlyRent());

        System.out.println("Поточний статус договору: " + contract.getStatus());
        contract.setStatus(ContractStatus.EXPIRED);
        System.out.println("Оновлений статус договору: " + contract.getStatus());
        System.out.println("Дія для нового статусу: " + RealEstateUtils.getAvailableAction(contract.getStatus()));

        System.out.println("Тривалість договору (місяців): " + RealEstateUtils.contractMonths(contract));
        System.out.println("Загальна сума оренди: " + RealEstateUtils.formatMoney(RealEstateUtils.totalRent(contract)));
    }
}
