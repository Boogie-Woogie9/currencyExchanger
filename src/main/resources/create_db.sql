-- Создание таблицы валют
CREATE TABLE IF NOT EXISTS Currencies (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    Code TEXT NOT NULL UNIQUE,
    FullName TEXT NOT NULL,
    Sign TEXT NOT NULL
);

-- Создание таблицы обменных курсов
CREATE TABLE IF NOT EXISTS ExchangeRates (
    ID INTEGER PRIMARY KEY AUTOINCREMENT,
    BaseCurrencyId INTEGER NOT NULL,
    TargetCurrencyId INTEGER NOT NULL,
    Rate DECIMAL(10,6) NOT NULL,
    FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies(ID),
    FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies(ID),
    UNIQUE (BaseCurrencyId, TargetCurrencyId)
);

-- Наполнение таблицы валют начальными данными
INSERT INTO Currencies (Code, FullName, Sign) VALUES
    ('USD', 'United States dollar', '$'),
    ('EUR', 'Euro', '€'),
    ('RUB', 'Russian Ruble', '₽'),
    ('AUD', 'Australian Dollar', 'A$');

-- Наполнение таблицы курсов обмена
INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate) VALUES
    (1, 2, 0.93),
    (2, 1, 1.07),
    (1, 3, 96.5),
    (1, 4, 1.52);
