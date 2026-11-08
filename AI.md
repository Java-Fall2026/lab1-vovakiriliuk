# Робота з AI

## Якщо ви не користувалися AI

Показав AI усі класи домену (`Landlord`, `Property`, `LeaseContract`, `RentPayment`), утиліти (`ValidationHelper`, `FormatHelper`, `RealEstateUtils`) і `Main` і попросив оцінити якість коду.

**З чим у його відповіді погоджуюсь:**
AI правильно відзначив, що `final` на сеттерах (`setMonthlyRent`, `setStatus`) є обов'язковим, бо вони викликаються з конструктора, і що `getClass()` у `equals` є коректнішим за `instanceof` для звичайних класів.

**З чим не погоджуюсь і чому:**
AI запропонував видалити "невикористовувані" методи з `ValidationHelper` (`requireGreaterOrEqual`, `requireDateAfter`, `requireDateNotBefore`) і прибрати дублювання констант між `ValidationHelper` і класами моделі. Не погоджуюсь: `ValidationHelper` — це загальний контракт утилітного класу, а не мінімальний набір для поточних викликів; методи можуть знадобитися при розширенні домену. Дублювання ж констант є неминучим наслідком архітектурного рішення тримати `ValidationHelper` package-private — з пакету `model` його просто не видно.
