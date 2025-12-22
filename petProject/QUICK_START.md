# Быстрый старт

## 1. Создание базы данных

```sql
CREATE DATABASE pet_db;
```

## 2. Настройка подключения

Отредактируйте `src/main/resources/application.yaml` или установите переменные окружения:

```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/pet_db
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=postgres
```

## 3. Запуск приложения

```bash
mvn clean spring-boot:run
```

## 4. Проверка работы

- Приложение запустится на порту **8081**
- Swagger UI: http://localhost:8081/swagger-ui.html
- API: http://localhost:8081/api/owners

## 5. Тестовые запросы

### Создать владельца:
```bash
curl -X POST http://localhost:8081/api/owners \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Иван",
    "lastName": "Иванов",
    "email": "ivan@example.com",
    "phone": "+7-999-123-45-67",
    "address": "Москва, ул. Примерная, д. 1"
  }'
```

### Создать питомца:
```bash
curl -X POST http://localhost:8081/api/pets \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Барсик",
    "type": "CAT",
    "breed": "Персидская",
    "dateOfBirth": "2020-05-15",
    "color": "Белый",
    "description": "Дружелюбный кот",
    "ownerId": 1
  }'
```

### Получить всех владельцев:
```bash
curl http://localhost:8081/api/owners
```

### Получить всех питомцев:
```bash
curl http://localhost:8081/api/pets
```

