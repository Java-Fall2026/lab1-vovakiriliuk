package ua.realestate;

import ua.realestate.exception.DuplicateIdentityException;
import ua.realestate.loader.LandlordLoader;
import ua.realestate.model.ContractStatus;
import ua.realestate.model.Landlord;
import ua.realestate.model.LeaseContract;
import ua.realestate.model.Property;
import ua.realestate.model.PropertyType;
import ua.realestate.repository.GenericRepository;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        System.out.println("=== 1. Ініціалізація трьох репозиторіїв різних типів ===");
        GenericRepository<Landlord> landlordRepo = new GenericRepository<>(
                "LandlordRepo",
                item -> item.taxCode()
        );
        GenericRepository<Property> propertyRepo = new GenericRepository<>(
                "PropertyRepo",
                item -> item.getCadastralNumber()
        );
        GenericRepository<LeaseContract> contractRepo = new GenericRepository<>(
                "ContractRepo",
                item -> item.getContractNumber()
        );
        System.out.println("Створено три репозиторії: " + landlordRepo.getName() + ", "
                + propertyRepo.getName() + ", " + contractRepo.getName());

        System.out.println("\n=== 2. Наповнення репозиторію з CSV-файла (включно з дублікатом) ===");
        LandlordLoader loader = new LandlordLoader();
        Path csvPath = Path.of("data", "landlords.csv");
        List<Landlord> loadedLandlords = loader.loadLandlords(csvPath);
        System.out.println("Завантажено рядків з CSV: " + loadedLandlords.size());

        for (Landlord landlord : loadedLandlords) {
            boolean added = landlordRepo.addIfAbsent(landlord);
            if (added) {
                System.out.println("  [+] Додано орендодавця: " + landlord.taxCode() + " (" + landlord.fullName() + ")");
            } else {
                System.out.println("  [!] Дублікат з CSV пропущено через addIfAbsent: " + landlord.taxCode());
            }
        }
        System.out.println("Усього унікальних орендодавців у репозиторії: " + landlordRepo.size());

        System.out.println("\n=== 3. Додавання дубліката через add vs addIfAbsent ===");
        Landlord duplicateCandidate = new Landlord("3456789012", "Іван Тестовий", "+380509999999");
        System.out.println("Спроба 1: додавання дубліката через addIfAbsent()...");
        boolean addIfAbsentResult = landlordRepo.addIfAbsent(duplicateCandidate);
        System.out.println("Результат addIfAbsent(): " + addIfAbsentResult + " (елемент не додано, виключення не кинуто)");

        System.out.println("Спроба 2: додавання того ж дубліката через add()...");
        try {
            landlordRepo.add(duplicateCandidate);
            System.out.println("Помилка: дублікат було додано!");
        } catch (DuplicateIdentityException e) {
            System.out.println("Перехоплено DuplicateIdentityException: " + e.getMessage());
        }

        System.out.println("\n=== 4. Наповнення та пошук у трьох типах репозиторіїв ===");
        Landlord landlord = landlordRepo.findByIdentity("3456789012").orElseThrow();

        Property property = new Property(
                "8000000000:66:101:0001",
                "вул. Хрещатик, 22, Київ",
                landlord,
                PropertyType.COMMERCIAL,
                45000.0
        );
        propertyRepo.add(property);

        LeaseContract contract = LeaseContract.of(
                "CNT-2026-001",
                property,
                "ТОВ Тест Орендар",
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 12, 31),
                45000.0,
                ContractStatus.ACTIVE
        );
        contractRepo.add(contract);

        System.out.println("\nПошук у LandlordRepo:");
        Optional<Landlord> foundLandlord = landlordRepo.findByIdentity("3456789012");
        System.out.println("  Знайдено за '3456789012': " + (foundLandlord.isPresent() ? foundLandlord.get().fullName() : "не знайдено"));
        Optional<Landlord> missingLandlord = landlordRepo.findByIdentity("0000000000");
        System.out.println("  Знайдено за '0000000000': " + (missingLandlord.isPresent() ? missingLandlord.get().fullName() : "не знайдено"));

        System.out.println("\nПошук у PropertyRepo:");
        Optional<Property> foundProperty = propertyRepo.findByIdentity("8000000000:66:101:0001");
        System.out.println("  Знайдено за '8000000000:66:101:0001': " + (foundProperty.isPresent() ? foundProperty.get().getAddress() : "не знайдено"));
        Optional<Property> missingProperty = propertyRepo.findByIdentity("9999999999:99:999:9999");
        System.out.println("  Знайдено за '9999999999:99:999:9999': " + (missingProperty.isPresent() ? missingProperty.get().getAddress() : "не знайдено"));

        System.out.println("\nПошук у ContractRepo:");
        Optional<LeaseContract> foundContract = contractRepo.findByIdentity("CNT-2026-001");
        System.out.println("  Знайдено за 'CNT-2026-001': " + (foundContract.isPresent() ? foundContract.get().getTenantName() : "не знайдено"));
        Optional<LeaseContract> missingContract = contractRepo.findByIdentity("CNT-NONEXISTENT");
        System.out.println("  Знайдено за 'CNT-NONEXISTENT': " + (missingContract.isPresent() ? missingContract.get().getTenantName() : "не знайдено"));

        System.out.println("\n=== 5. Видалення наявного та відсутнього елемента ===");
        System.out.println("Кількість об'єктів у PropertyRepo до видалення: " + propertyRepo.size());
        boolean removedExisting = propertyRepo.removeByIdentity("8000000000:66:101:0001");
        System.out.println("Видалення наявного елемента ('8000000000:66:101:0001'): " + removedExisting);
        System.out.println("Кількість об'єктів у PropertyRepo після видалення: " + propertyRepo.size());

        boolean removedAbsent = propertyRepo.removeByIdentity("8000000000:66:101:0001");
        System.out.println("Повторне видалення того ж (вже відсутнього) елемента: " + removedAbsent);

        System.out.println("\n=== Демонстрацію завершено успішно ===");
    }
}
