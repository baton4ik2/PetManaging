# Тесты для Pet Project

Этот каталог содержит полный набор тестов для бэкенда приложения Pet Project.

## Структура тестов

### Unit тесты сервисов
- `service/AuthServiceTest.java` - тесты для сервиса аутентификации
- `service/UserServiceTest.java` - тесты для сервиса управления пользователями
- `service/OwnerServiceTest.java` - тесты для сервиса управления владельцами
- `service/PetServiceTest.java` - тесты для сервиса управления питомцами
- `service/StatisticsServiceTest.java` - тесты для сервиса статистики

### Unit тесты контроллеров
- `controller/AuthControllerTest.java` - тесты REST API для аутентификации
- `controller/UserControllerTest.java` - тесты REST API для пользователей

### Тесты репозиториев (Data JPA тесты)
- `repository/UserRepositoryTest.java` - тесты для UserRepository
- `repository/OwnerRepositoryTest.java` - тесты для OwnerRepository
- `repository/PetRepositoryTest.java` - тесты для PetRepository

### Тесты безопасности
- `security/UserDetailsServiceImplTest.java` - тесты для UserDetailsService

### Тесты обработки исключений
- `controller/GlobalExceptionHandlerTest.java` - тесты для глобального обработчика исключений

### Integration тесты
- `integration/ApiIntegrationTest.java` - полные интеграционные тесты для всего API

## Запуск тестов

### Все тесты
```bash
mvn test
```

### Конкретный класс тестов
```bash
mvn test -Dtest=AuthServiceTest
```

### Все тесты в пакете
```bash
mvn test -Dtest=ru.akbirov.petproject.service.*
```

## Конфигурация

Тесты используют **Testcontainers** с PostgreSQL для максимально реалистичного тестирования.
Это позволяет тестировать на той же базе данных, что используется в продакшене.

### Testcontainers

- Используется PostgreSQL 15 Alpine образ
- Контейнер переиспользуется между тестами для ускорения (`withReuse(true)`)
- Автоматически настраивается через `AbstractTestcontainersTest`
- Конфигурация находится в `src/test/resources/application-test.yaml`

### Преимущества Testcontainers

✅ Тестирование на реальной PostgreSQL (как в продакшене)  
✅ Проверка специфичных для PostgreSQL функций  
✅ Валидация миграций Liquibase  
✅ Более надежные интеграционные тесты

## Покрытие тестами

Тесты покрывают:
- ✅ Все методы сервисов (успешные сценарии и исключения)
- ✅ Все REST endpoints контроллеров
- ✅ Все методы репозиториев
- ✅ Обработку исключений
- ✅ Безопасность и авторизацию
- ✅ Интеграционные сценарии полного цикла работы API

## Технологии

- JUnit 5
- Mockito
- Spring Boot Test
- Spring Data JPA Test
- **Testcontainers** (PostgreSQL для интеграционных тестов)
- MockMvc (для тестирования REST API)

## Требования

Для запуска тестов с Testcontainers требуется:
- Docker Desktop (или Docker Engine)
- Доступ к Docker Hub для загрузки образа PostgreSQL

При первом запуске тестов Testcontainers автоматически загрузит образ PostgreSQL.

