package ua.realestate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.realestate.loader.LandlordLoader;
import ua.realestate.model.Landlord;
import ua.realestate.model.Property;
import ua.realestate.model.PropertyType;

import java.nio.file.Path;
import java.util.List;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LandlordLoader loader = new LandlordLoader();

        System.out.println("=== 1. Читання CSV-файлу з коректними та некоректними рядками ===");
        Path csvPath = Path.of("data", "landlords.csv");
        List<Landlord> landlords = loader.loadLandlords(csvPath);

        System.out.println("Успішно завантажено орендодавців: " + landlords.size());
        for (Landlord landlord : landlords) {
            System.out.println("  - " + landlord);
        }

        System.out.println();
        System.out.println("=== 2. Спроба прочитати відсутній файл (програма не зупиняється) ===");
        Path missingPath = Path.of("data", "non_existent_landlords.csv");
        List<Landlord> missingResult = loader.loadLandlords(missingPath);
        System.out.println("Результат читання відсутнього файлу (кількість): " + missingResult.size());
        System.out.println("Програма успішно продовжила виконання після спроби відкрити відсутній файл.");

        System.out.println();
        System.out.println("=== 3. Некоректне значення в сеттері змінюваного класу та його обробка ===");
        Landlord defaultLandlord = new Landlord("1234567890", "Іван Іванов", "+380501234567");
        Property property = new Property(
            "1234567890:01:001:0001",
            "вул. Шевченка, 10, Київ",
            defaultLandlord,
            PropertyType.APARTMENT,
            15000.0
        );
        System.out.println("Поточна місячна оренда: " + property.getMonthlyRent());

        try {
            System.out.println("Спроба встановити від'ємну орендну плату (-500.0)...");
            property.setMonthlyRent(-500.0);
        } catch (IllegalArgumentException e) {
            log.warn("Handled invalid setter invocation for monthlyRent: {}", e.getMessage(), e);
            System.out.println("Перехоплено очікуване виключення: " + e.getMessage());
        }
        System.out.println("Місячна оренда після обробки виключення залишилася: " + property.getMonthlyRent());

        System.out.println();
        System.out.println("=== 4. Демонстрація завершена ===");
    }
}
