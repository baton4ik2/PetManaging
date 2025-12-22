# Запуск приложения с Docker

Этот проект поддерживает запуск через Docker и Docker Compose.

## Требования

- Docker Desktop (или Docker Engine + Docker Compose)
- Минимум 2GB свободной RAM

## Быстрый старт

### 1. Запуск всего стека (приложение + PostgreSQL)

```bash
docker-compose up -d
```

Эта команда:
- Соберет Docker образ приложения
- Запустит PostgreSQL в контейнере
- Запустит Spring Boot приложение
- Настроит сеть между контейнерами

### 2. Проверка статуса

```bash
docker-compose ps
```

### 3. Просмотр логов

```bash
# Логи приложения
docker-compose logs -f app

# Логи PostgreSQL
docker-compose logs -f postgres

# Все логи
docker-compose logs -f
```

### 4. Остановка

```bash
docker-compose down
```

Для удаления данных базы данных:
```bash
docker-compose down -v
```

## Доступ к приложению

После запуска приложение будет доступно по адресам:

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **API Owners**: http://localhost:8081/api/owners
- **API Pets**: http://localhost:8081/api/pets

## Доступ к базе данных

PostgreSQL доступен на:
- **Host**: localhost
- **Port**: 5432
- **Database**: pet_db
- **Username**: postgres
- **Password**: postgres

### Подключение через psql

```bash
docker-compose exec postgres psql -U postgres -d pet_db
```

### Подключение из внешнего клиента

Используйте настройки:
- Host: `localhost`
- Port: `5432`
- Database: `pet_db`
- Username: `postgres`
- Password: `postgres`

## Пересборка образа

Если вы изменили код, пересоберите образ:

```bash
docker-compose build
docker-compose up -d
```

Или одной командой:
```bash
docker-compose up -d --build
```

## Переменные окружения

Вы можете настроить приложение через переменные окружения в `docker-compose.yml`:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/pet_db
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: postgres
  SPRING_JPA_HIBERNATE_DDL_AUTO: none
  SHOW_SQL: "true"
```

## Отладка

### Вход в контейнер приложения

```bash
docker-compose exec app sh
```

### Вход в контейнер PostgreSQL

```bash
docker-compose exec postgres sh
```

### Просмотр логов приложения

```bash
docker-compose logs -f app | grep ERROR
```

## Полезные команды

```bash
# Перезапуск приложения
docker-compose restart app

# Остановка только приложения (БД продолжит работать)
docker-compose stop app

# Запуск только приложения
docker-compose start app

# Удаление всех контейнеров и volumes
docker-compose down -v

# Просмотр использования ресурсов
docker stats
```

## Структура Docker

- **Dockerfile**: Многостадийная сборка (Maven build + JRE runtime)
- **docker-compose.yml**: Оркестрация приложения и PostgreSQL
- **.dockerignore**: Исключения при сборке образа

## Решение проблем

### Порт уже занят

Если порт 8081 или 5432 занят, измените их в `docker-compose.yml`:

```yaml
ports:
  - "8082:8081"  # Внешний:Внутренний
```

### Приложение не подключается к БД

Убедитесь, что:
1. PostgreSQL контейнер запущен: `docker-compose ps`
2. Healthcheck прошел: `docker-compose logs postgres`
3. Используется правильный hostname: `postgres` (не `localhost`)

### Очистка Docker

```bash
# Удалить все неиспользуемые образы
docker image prune -a

# Удалить все неиспользуемые volumes
docker volume prune

# Полная очистка (осторожно!)
docker system prune -a --volumes
```

