# Frontend - Pet Management System

React + TypeScript фронтенд для системы управления питомцами.

## Быстрый старт

### Локальная разработка

1. Установите зависимости:
```bash
cd frontend
npm install
```

2. Запустите dev сервер:
```bash
npm run dev
```

Приложение будет доступно на http://localhost:5173

3. Убедитесь, что backend запущен на http://localhost:8081

### Через Docker

```bash
# Из корня проекта
docker-compose up frontend
```

Или весь стек:
```bash
docker-compose up
```

## Структура

```
frontend/
├── src/
│   ├── components/      # Переиспользуемые компоненты
│   │   ├── OwnerForm.tsx    # Форма создания/редактирования владельца
│   │   ├── OwnerList.tsx    # Список владельцев
│   │   ├── PetForm.tsx      # Форма создания/редактирования питомца
│   │   └── PetList.tsx      # Список питомцев
│   ├── pages/           # Страницы приложения
│   │   ├── Home.tsx         # Главная страница
│   │   ├── Owners.tsx       # Страница управления владельцами
│   │   └── Pets.tsx         # Страница управления питомцами
│   ├── services/        # API сервисы
│   │   └── api.ts           # Axios клиент и типы
│   ├── App.tsx          # Главный компонент с роутингом
│   └── main.tsx         # Точка входа
├── Dockerfile           # Docker образ (nginx)
├── nginx.conf          # Конфигурация nginx
└── package.json
```

## Функциональность

### Управление владельцами
- ✅ Просмотр списка владельцев
- ✅ Создание нового владельца
- ✅ Редактирование владельца
- ✅ Удаление владельца
- ✅ Просмотр питомцев владельца

### Управление питомцами
- ✅ Просмотр списка питомцев
- ✅ Создание нового питомца
- ✅ Редактирование питомца
- ✅ Удаление питомца
- ✅ Фильтрация по типу питомца
- ✅ Фильтрация по владельцу

## API Интеграция

Все API запросы идут через `src/services/api.ts`:

```typescript
import { ownerService, petService } from './services/api';

// Получить всех владельцев
const owners = await ownerService.getAll();

// Создать владельца
await ownerService.create({
  firstName: 'Иван',
  lastName: 'Иванов',
  email: 'ivan@example.com',
  phone: '+7-999-123-45-67',
  address: 'Москва'
});
```

## Переменные окружения

Создайте `.env` файл в папке `frontend`:

```env
VITE_API_URL=http://localhost:8081/api
```

В продакшене (Docker) используется nginx proxy, поэтому URL будет `/api`.

## Сборка

```bash
npm run build
```

Собранные файлы будут в `dist/` и готовы для деплоя.

## Стилизация

Используется Tailwind CSS. Все стили инлайн через классы:

```tsx
<button className="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700">
  Click me
</button>
```

## Роутинг

React Router настроен в `App.tsx`:

- `/` - Главная страница
- `/owners` - Управление владельцами
- `/pets` - Управление питомцами

