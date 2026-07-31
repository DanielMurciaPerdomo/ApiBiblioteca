# Biblioteca API

API REST para la gestión de préstamos de libros en una biblioteca. Desarrollada con Java 17, Spring Boot 3.5.4 y MySQL 8.

## Requisitos previos

- [Docker](https://docs.docker.com/get-docker/) y [Docker Compose](https://docs.docker.com/compose/install/)

## Instalación y ejecución

```bash
# 1. Clonar el repositorio
git clone <url-del-repo>
cd ApiBiblioteca

# 2. Copiar el archivo de variables de entorno
cp .env.example .env

# 3. Levantar la aplicación con Docker Compose
docker compose up -d --build
```

La API estará disponible en `http://localhost:8080/api`.

## Variables de entorno

| Variable | Descripción | Valor por defecto |
|---|---|---|
| `DB_NAME` | Nombre de la base de datos | `biblioteca` |
| `DB_USER` | Usuario de MySQL | `biblioteca_user` |
| `DB_PASSWORD` | Contraseña del usuario MySQL | `biblioteca_pass` |
| `DB_ROOT_PASSWORD` | Contraseña del usuario root MySQL | `root_pass_segura` |
| `DB_PORT` | Puerto de MySQL en el host | `3306` |
| `API_PORT` | Puerto de la API en el host | `8080` |
| `FRONTEND_URL` | URL del frontend para CORS | `http://localhost:5173` |

## Endpoints

Base path: `/api`

### Usuarios

| Método | Ruta | Descripción | Códigos |
|---|---|---|---|
| POST | `/api/usuarios` | Crear un nuevo usuario | 201, 400 |
| GET | `/api/usuarios` | Listar todos los usuarios | 200 |
| GET | `/api/usuarios/{id}` | Obtener usuario por ID | 200, 404 |
| PUT | `/api/usuarios/{id}` | Actualizar usuario | 200, 404 |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario | 204, 404 |

### Libros

| Método | Ruta | Descripción | Códigos |
|---|---|---|---|
| POST | `/api/libros` | Crear un libro con N ejemplares | 201, 400 |
| GET | `/api/libros` | Listar todos los libros | 200 |
| GET | `/api/libros/{id}` | Obtener libro por ID | 200, 404 |
| PUT | `/api/libros/{id}` | Actualizar libro | 200, 404 |
| DELETE | `/api/libros/{id}` | Eliminar libro | 204, 404 |
| GET | `/api/libros/{isbn}/ejemplares-disponibles` | Listar ejemplares disponibles por ISBN | 200, 404 |

### Préstamos

| Método | Ruta | Descripción | Códigos |
|---|---|---|---|
| POST | `/api/prestamos` | Registrar un préstamo | 201, 404, 409 |
| GET | `/api/prestamos` | Listar préstamos (filtros opcionales: `?usuarioId=X&libroId=Y`) | 200 |
| PUT | `/api/prestamos/{id}/devolver` | Devolver un libro | 200, 404 |

## Respaldo de la base de datos

Generar un dump de la base de datos:

```bash
# Generar un dump de la base de datos:
docker exec biblioteca_db mysqldump --no-tablespaces -u biblioteca_user -pbiblioteca_pass biblioteca > database/backup/biblioteca.dump
```

Restaurar un dump existente:

```bash
docker exec -i biblioteca_db mysql -u biblioteca_user -pbiblioteca_pass biblioteca < database/backup/biblioteca.dump
```
