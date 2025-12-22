# Pet Project Frontend

React + TypeScript frontend для системы управления питомцами.

## Технологии

- **React 19** - UI библиотека
- **TypeScript** - типизация
- **Vite** - сборщик и dev сервер
- **Tailwind CSS** - стилизация
- **React Router** - маршрутизация
- **Axios** - HTTP клиент

## Запуск в режиме разработки

```bash
npm install
npm run dev
```

Приложение будет доступно на http://localhost:5173

## Сборка для продакшена

```bash
npm run build
```

Собранные файлы будут в папке `dist/`

## Структура проекта

```
frontend/
├── src/
│   ├── components/     # React компоненты
│   │   ├── OwnerForm.tsx
│   │   ├── OwnerList.tsx
│   │   ├── PetForm.tsx
│   │   └── PetList.tsx
│   ├── pages/          # Страницы приложения
│   │   ├── Home.tsx
│   │   ├── Owners.tsx
│   │   └── Pets.tsx
│   ├── services/        # API сервисы
│   │   └── api.ts
│   ├── App.tsx         # Главный компонент
│   └── main.tsx        # Точка входа
├── Dockerfile          # Docker образ
└── package.json
```

## Переменные окружения

Создайте файл `.env`:

```
VITE_API_URL=http://localhost:8081/api
```

## Запуск через Docker

```bash
docker-compose up frontend
```

Или весь стек:

```bash
docker-compose up
```

## Функциональность

- ✅ Управление владельцами (CRUD)
- ✅ Управление питомцами (CRUD)
- ✅ Фильтрация питомцев по типу и владельцу
- ✅ Современный UI с Tailwind CSS
- ✅ Адаптивный дизайн
