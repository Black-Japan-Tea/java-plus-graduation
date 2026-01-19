# Explore With Me — микросервисы

## Архитектура
- `gateway-server` — единая точка входа для клиентов (порт `8080`).
- `config-server` — централизованная конфигурация.
- `discovery-server` (Eureka) — обнаружение сервисов.
- Основные сервисы:
  - `events-service` — управление событиями, публикации, поиск и фильтрация, админская модерация.
  - `requests-service` — заявки на участие и контроль статусов (создание, отмена, подтверждение).
  - `admin-users-service` — администрирование пользователей.
  - `extra-service` — категории, подборки, комментарии и их модерация.
- `stats-server` — сбор и выдача статистики просмотров.

Все сервисы — Spring Boot приложения, регистрируются в Eureka и получают настройки из Config Server.  
Файлы конфигурации: `infra/config-server/src/main/resources/config/*.properties`.

## Задачи приложения
Explore With Me — сервис для публикации и поиска мероприятий с учётом правил доступа и модерации.  
Сценарии:
- пользователи создают события и управляют ими;
- другие пользователи находят события по фильтрам, просматривают детали и подают заявки на участие;
- администраторы управляют пользователями, категориями и подборками, а также модерацией событий и комментариев;
- система фиксирует просмотры и отдаёт статистику.

## Маршрутизация в Gateway
Все запросы идут через gateway:
- `/users/*/requests/**` → `requests-service`
- `/categories/**`, `/admin/categories/**`, `/compilations/**`, `/admin/compilations/**`,
  `/users/*/comments/**`, `/admin/comments/**` → `extra-service`
- `/admin/users/**` → `admin-users-service`
- `/users/*/events/**`, `/events/**`, `/admin/events/**` → `events-service`

## Внутренний API
Прямые межсервисные HTTP-вызовы (OpenFeign, Eureka):
- `events-service` → `requests-service`
  - `GET /internal/events/{eventId}/requests` — заявки по событию
  - `PATCH /internal/events/{eventId}/requests` — изменение статусов заявок
  - `GET /internal/events/{eventId}/requests/count?status=CONFIRMED` — количество заявок
- `events-service` → `stats-server`
  - `POST /hit` — отправка информации о просмотре (`EndpointHitDto`)
  - `GET /stats` — получение статистики просмотров (`ViewStatsDto`)

Остальные core-сервисы пока используют общую схему `main_database`; подключение к БД задаётся через Config Server.
