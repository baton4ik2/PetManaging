# Testcontainers в проекте

Проект использует **Testcontainers** для интеграционного тестирования с реальной PostgreSQL базой данных.

## Что такое Testcontainers?

Testcontainers - это Java библиотека, которая позволяет запускать Docker контейнеры во время выполнения тестов. Это дает возможность тестировать приложение на той же базе данных, что используется в продакшене.

## Преимущества

✅ **Реалистичное тестирование** - используется реальная PostgreSQL, а не H2  
✅ **Проверка специфичных функций** - тестируются PostgreSQL-специфичные возможности  
✅ **Валидация миграций** - можно проверить работу Liquibase миграций  
✅ **Изоляция** - каждый тест работает с чистой базой данных  
✅ **Автоматизация** - контейнеры создаются и удаляются автоматически

## Как это работает

### Базовый класс

Все тесты, использующие Testcontainers, наследуются от `AbstractTestcontainersTest`:

```java
@DataJpaTest
class UserRepositoryTest extends AbstractTestcontainersTest {
    // тесты используют PostgreSQL контейнер
}
```

### Конфигурация

`AbstractTestcontainersTest` автоматически:
1. Создает PostgreSQL контейнер (postgres:15-alpine)
2. Настраивает Spring через `@DynamicPropertySource`
3. Переиспользует контейнер между тестами для ускорения

### Переиспользование контейнера

Контейнер настроен с `withReuse(true)`, что означает:
- Контейнер не удаляется после каждого теста
- Переиспользуется для всех тестов в рамках одного запуска
- Значительно ускоряет выполнение тестов

## Требования

### Docker

Для работы Testcontainers требуется:
- **Docker Desktop** (Windows/Mac) или **Docker Engine** (Linux)
- Docker должен быть запущен перед запуском тестов

### Проверка Docker

```bash
docker ps
```

Если команда работает, Docker готов к использованию.

## Первый запуск

При первом запуске тестов:
1. Testcontainers автоматически загрузит образ `postgres:15-alpine` из Docker Hub
2. Это может занять несколько минут (только первый раз)
3. Последующие запуски будут быстрее

## Запуск тестов

### Все тесты
```bash
mvn test
```

### Конкретный тест
```bash
mvn test -Dtest=UserRepositoryTest
```

### Без Docker (если нужно)

Если Docker недоступен, можно временно использовать H2, изменив тесты:
- Убрать `extends AbstractTestcontainersTest`
- Добавить `@ActiveProfiles("test")` (со старым H2 конфигом)

## Производительность

### С Testcontainers
- Первый запуск: ~30-60 секунд (загрузка образа)
- Последующие запуски: ~10-20 секунд
- Переиспользование контейнера ускоряет тесты

### Оптимизация

Для еще большего ускорения можно:
1. Использовать локальный образ PostgreSQL
2. Настроить Docker volume для персистентности
3. Использовать Testcontainers в режиме "reuse" (уже настроено)

## Отладка

### Просмотр логов контейнера

Testcontainers логирует информацию о создании и работе контейнеров:
```
[testcontainers] Creating container for image: postgres:15-alpine
[testcontainers] Container postgres:15-alpine is starting
```

### Проверка контейнера

После запуска тестов можно проверить контейнер:
```bash
docker ps -a | grep postgres
```

### Очистка

Если нужно удалить все контейнеры Testcontainers:
```bash
docker rm -f $(docker ps -aq --filter "label=org.testcontainers=true")
```

## Структура

```
src/test/java/ru/akbirov/petproject/
├── config/
│   └── AbstractTestcontainersTest.java  # Базовый класс
├── repository/
│   ├── UserRepositoryTest.java          # Использует Testcontainers
│   ├── OwnerRepositoryTest.java         # Использует Testcontainers
│   └── PetRepositoryTest.java          # Использует Testcontainers
└── integration/
    └── ApiIntegrationTest.java         # Использует Testcontainers
```

## Дополнительные ресурсы

- [Testcontainers Documentation](https://www.testcontainers.org/)
- [PostgreSQL Module](https://www.testcontainers.org/modules/databases/postgres/)
- [Spring Boot Integration](https://www.testcontainers.org/modules/spring_boot/)

