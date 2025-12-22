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

**Примечание:** Для работы с приложением необходимо сначала зарегистрироваться или войти через фронтенд. Все API эндпоинты (кроме `/api/auth/**`) требуют JWT токен.

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

### 2. Регистрация и вход

1. Откройте http://localhost:3000 в браузере
2. Нажмите "Register" для создания нового аккаунта
3. Заполните форму регистрации (username, email, password)
4. После регистрации вы автоматически войдете в систему

Или используйте API напрямую:
```bash
# Регистрация
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","email":"test@example.com","password":"password123"}'

# Вход (сохраните токен из ответа)
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

### 3. Проверка API (требует токен)
```bash
# Замените YOUR_JWT_TOKEN на токен из шага 2
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8081/api/statistics
```

Должна вернуться статистика с тестовыми данными.

### 4. Откройте браузер
Перейдите на http://localhost:3000 - вы увидите страницу входа. После регистрации/входа откроется главная страница приложения.

## Тестовые запросы

> **⚠️ Важно:** Все запросы ниже требуют JWT токен. Сначала получите токен через `/api/auth/login` или `/api/auth/register`.

### Получить токен (если еще не получили):
```bash
# Замените на свои данные
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}' \
  | grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo "Token: $TOKEN"
```

### Создать владельца:
```bash
curl -X POST http://localhost:8081/api/owners \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
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
  -H "Authorization: Bearer $TOKEN" \
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
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/api/owners
```

### Получить всех питомцев:
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/api/pets
```

### Поиск питомцев:
```bash
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8081/api/pets?search=Барсик"
```

### Получить статистику:
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/api/statistics
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
