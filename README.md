# Лабораторна робота №1 — варіант 8

Основи Java та модифікатори доступу.

![build](https://github.com/Java-Fall2026/lab1-vovakiriliuk/actions/workflows/build.yml/badge.svg)

- Умова: https://github.com/Java-Univer-2025/Java-Univer-2025.github.io/blob/main/lab-01/README.md
- Домени та поля: https://github.com/Java-Univer-2025/Java-Univer-2025.github.io/blob/main/lab-01/tasks_2025_domains.md

## Структура

```
src/main/java/ua/common/BaseEntity.java        готовий клас, не змінюйте
src/main/java/ua/realestate/model/             сутності домену
src/main/java/ua/realestate/util/              ValidationHelper, FormatHelper, RealEstateUtils
src/main/java/ua/realestate/Main.java          демонстраційний клас
```

## Як зібрати і запустити

```
gradlew.bat build
gradlew.bat run
```

## Чекліст здачі

- [x] усі сутності варіанта успадковують `BaseEntity` і викликають `super()`
- [x] поля `private`, сеттери тільки там, де в профілі стоїть ✎
- [x] два класи створюються через `of(...)`, конструктор у них `private` (LeaseContract, RentPayment)
- [x] валідація в конструкторах і сеттерах, повідомлення показує очікуване й отримане
- [x] `ValidationHelper` і `FormatHelper` — package-private, `RealEstateUtils` — public
- [x] два обчислювані методи з профілю (`contractMonths`, `totalRent`)
- [x] `equals`, `hashCode`, `toString` у кожній сутності
- [x] `Main` демонструє всі сценарії, зокрема невдалі
- [x] бейдж зелений

## Частина про AI

Я попросив AI протестувати мій код, знайти проблеми та очистити його від зайвого.

**Що AI зробив неправильно або не точно:**

1. Спочатку класи моделі імпортували `ValidationHelper` напряму — але він package-private у пакеті `util`, тому класи з `model` (іншого пакета) не мають до нього доступу. AI сам виявив і виправив цю проблему, видаливши прямі імпорти та дублювавши потрібні перевірки інлайн.
2. `ALLOWED_TYPES` у `Property` і `ALLOWED_STATUSES` у `LeaseContract` спочатку посилалися на публічні константи з `ValidationHelper` — що теж порушує інкапсуляцію пакета. AI виправив це, замінивши на локальні `Set.of(...)`.

## Здача

Код у цьому репозиторії + захист 5 хв. Без захисту робота не зарахована.
