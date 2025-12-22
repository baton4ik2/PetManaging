# Быстрый старт

## Запуск через Docker (Рекомендуется) 🐳

Самый простой способ запустить приложение:

```bash
docker-compose up -d
```

Это запустит весь стек:
- PostgreSQL (порт 5433)
- Spring Boot API (порт 8081)
- React Frontend (порт 3000)

**Доступ к приложению:**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8081/api
- Swagger UI: http://localhost:8081/swagger-ui.html

**Остановка:**
```bash
docker-compose down
```

## Проверка работы

### 1. Проверка контейнеров
```bash
docker-compose ps
```

Все сервисы должны быть в статусе "Up".

### 2. Проверка API
```bash
curl http://localhost:8081/api/statistics
```

Должна вернуться статистика с тестовыми данными.

### 3. Откройте браузер
Перейдите на http://localhost:3000 - вы увидите главную страницу приложения.

## Тестовые запросы

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

### Поиск питомцев:
```bash
curl "http://localhost:8081/api/pets?search=Барсик"
```

### Получить статистику:
```bash
curl http://localhost:8081/api/statistics
```

## Локальная разработка (без Docker)

Если нужно запустить приложение локально:

### Backend

1. Установите PostgreSQL локально
2. Создайте базу данных:
```sql
CREATE DATABASE pet_db;
```

3. Обновите `src/main/resources/application.yaml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/pet_db
    username: postgres
    password: postgres
```

4. Запустите приложение:
```bash
mvn clean spring-boot:run
```

### Frontend

1. Перейдите в папку frontend:
```bash
cd frontend
```

2. Установите зависимости:
```bash
npm install
```

3. Запустите dev сервер:
```bash
npm run dev
```

Frontend будет доступен на http://localhost:5173

## Полезные команды

### Docker

```bash
# Просмотр логов
docker-compose logs -f

# Пересборка образов
docker-compose build

# Остановка и удаление данных
docker-compose down -v

# Перезапуск сервиса
docker-compose restart app
```

### Maven

```bash
# Компиляция
mvn clean compile

# Запуск приложения
mvn spring-boot:run

# Сборка JAR
mvn clean package
```

## Решение проблем

### Порт занят
Если порт 8081, 3000 или 5433 занят, измените порты в `docker-compose.yml`.

### База данных не запускается
Проверьте логи:
```bash
docker-compose logs postgres
```

### Приложение не подключается к БД
Убедитесь, что PostgreSQL контейнер запущен:
```bash
docker-compose ps
```

Подробнее: [DOCKER.md](DOCKER.md)
