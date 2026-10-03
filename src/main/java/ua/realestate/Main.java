package ua.realestate;

import ua.realestate.model.Landlord;
import ua.realestate.model.LeaseContract;
import ua.realestate.model.Property;
import ua.realestate.model.RentPayment;
import ua.realestate.util.RealEstateUtils;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {

        System.out.println("=== 1. Створення об'єктів конструктором і фабричним методом ===");

        Landlord landlordByConstructor = new Landlord("3456789012", "Іванов Іван Іванович", "+380501234567");
        System.out.println("Landlord (конструктор): " + landlordByConstructor);

        Property property = new Property("1234567890:01:001:0001", "вул. Шевченка, 10, Київ",
            landlordByConstructor, "apartment", 15000.0);
        System.out.println("Property (конструктор, type нормалізовано): " + property);

        LeaseContract contractByFactory = LeaseContract.of(
            "CNT-2026-01", property, "Петренко Петро",
            LocalDate.of(2026, 1, 1), LocalDate.of(2027, 1, 1),
            15000.0, "active"
        );
        System.out.println("LeaseContract (фабричний метод, status нормалізовано): " + contractByFactory);

        RentPayment paymentByFactory = RentPayment.of(contractByFactory, 15000.0, LocalDate.of(2026, 2, 1));
        System.out.println("RentPayment (фабричний метод): " + paymentByFactory);

        System.out.println();
        System.out.println("=== 2. Нормалізація ===");
        System.out.println("\"  apartment  \" -> normalize -> " + RealEstateUtils.normalize("  apartment  "));
        System.out.println("\"  active  \" -> normalize -> " + RealEstateUtils.normalize("  active  "));

        System.out.println();
        System.out.println("=== 3. Спрацювання винятків на невалідних даних ===");

        try {
            new Landlord("", "Іванов", "+380501234567");
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка порожнього taxCode: " + e.getMessage());
        }

        try {
            new Property("1234567890:01:001:0002", "вул. Франка, 5", landlordByConstructor, "OFFICE", -500.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка неправильного type: " + e.getMessage());
        }

        try {
            LeaseContract.of("CNT-2026-02", property, "Сидоренко Олег",
                LocalDate.of(2026, 6, 1), LocalDate.of(2026, 1, 1),
                15000.0, "ACTIVE");
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка endDate <= startDate: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== 4. Валідація у сеттері ===");

        System.out.println("Поточна monthlyRent: " + property.getMonthlyRent());
        property.setMonthlyRent(20000.0);
        System.out.println("Після setMonthlyRent(20000.0): " + property.getMonthlyRent());

        try {
            property.setMonthlyRent(-1.0);
        } catch (IllegalArgumentException e) {
            System.out.println("Помилка негативної monthlyRent у сеттері: " + e.getMessage());
        }

        System.out.println();
        System.out.println("=== 5. == vs equals та hashCode ===");

        Landlord l1 = new Landlord("3456789012", "Іванов Іван Іванович", "+380501234567");
        Landlord l2 = new Landlord("3456789012", "Іванов Іван Іванович", "+380501234567");
        Landlord l3 = new Landlord("9999999999", "Інший Власник", "+380997654321");

        System.out.println("l1 == l2 : " + (l1 == l2));
        System.out.println("l1.equals(l2) : " + l1.equals(l2));
        System.out.println("l1.hashCode() == l2.hashCode() : " + (l1.hashCode() == l2.hashCode()));
        System.out.println("l1.equals(l3) : " + l1.equals(l3));

        RentPayment p1 = RentPayment.of(contractByFactory, 15000.0, LocalDate.of(2026, 2, 1));
        RentPayment p2 = RentPayment.of(contractByFactory, 15000.0, LocalDate.of(2026, 2, 1));
        RentPayment p3 = RentPayment.of(contractByFactory, 15000.0, LocalDate.of(2026, 3, 1));

        System.out.println("p1 == p2 : " + (p1 == p2));
        System.out.println("p1.equals(p2) : " + p1.equals(p2));
        System.out.println("p1.equals(p3) : " + p1.equals(p3));

        System.out.println();
        System.out.println("=== 6. Обчислювані методи ===");

        LeaseContract longContract = LeaseContract.of(
            "CNT-2026-02", property, "Коваль Марія",
            LocalDate.of(2026, 1, 1), LocalDate.of(2028, 7, 1),
            20000.0, "ACTIVE"
        );

        long months = RealEstateUtils.contractMonths(longContract);
        double total = RealEstateUtils.totalRent(longContract);
        System.out.println("contractMonths: " + months + " місяців");
        System.out.println("totalRent: " + RealEstateUtils.formatMoney(total));

        System.out.println();
        System.out.println("=== 7. toString() усіх сутностей ===");
        System.out.println(landlordByConstructor);
        System.out.println(property);
        System.out.println(contractByFactory);
        System.out.println(paymentByFactory);

        System.out.println();
        System.out.println("=== 8. Доступ до protected createdAt з іншого пакета ===");
        System.out.println("landlord.getCreatedAt(): " + landlordByConstructor.getCreatedAt());

        // Рядок нижче не компілюється, бо ValidationHelper — package-private і недоступний з ua.realestate:
        // ua.realestate.util.ValidationHelper.requireNotBlank("x", "field");
        // Рядок нижче не компілюється, бо конструктор LeaseContract — private:
        // new LeaseContract("CNT-X", property, "Тест", LocalDate.now(), LocalDate.now().plusYears(1), 20000.0, "ACTIVE");
    }
}
