# 🏪 Franquicias API

API RESTful reactiva para la gestión de franquicias, sucursales y productos, construida con **Spring Boot WebFlux**, arquitectura hexagonal y persistencia en **MongoDB Atlas**.

---

## 📌 Descripción del proyecto

Esta API permite:

- Crear franquicias y agregar sucursales.
- Agregar, eliminar y actualizar productos en las sucursales.
- Consultar el producto con mayor stock por sucursal de una franquicia.

El proyecto fue desarrollado siguiendo principios de arquitectura limpia para separar la lógica de negocio del acceso a datos.

---

## ⚙️ Tecnologías principales

- ☕ Java 17  
- 🌐 Spring Boot 3 + WebFlux  
- 🍃 MongoDB Atlas (base de datos en la nube)  
- 🧩 Arquitectura hexagonal (puertos y adaptadores)  
- 📋 SLF4J para logging  
- 🧪 JUnit 5 + Mockito para pruebas unitarias  
- 🔍 WebTestClient para pruebas de controladores  
- 🐳 Docker + Spring Boot Buildpacks  

---

## 📫 Endpoints principales

| Método | Endpoint                                                                 | Descripción                                         |
|--------|--------------------------------------------------------------------------|-----------------------------------------------------|
| POST   | `/api/franchises`                                                       | Crear una nueva franquicia                          |
| POST   | `/api/franchises/{franchiseId}/branches`                                | Agregar una sucursal a una franquicia              |
| POST   | `/api/franchises/{franchiseId}/branches/{branchId}/products`            | Agregar un producto a una sucursal                 |
| DELETE | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}`| Eliminar un producto de una sucursal               |
| PATCH  | `/api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock` | Actualizar el stock de un producto         |
| GET    | `/api/franchises/{franchiseId}/max-stock`                               | Obtener el producto con mayor stock por sucursal   |

---

## 🚀 Cómo usar y desplegar la aplicación localmente

### 1️⃣ Clonar el repositorio

```bash
git clone https://github.com/GinaRM/franquicias-api.git
cd franquicias-api
```

---

### 2️⃣ Configurar MongoDB Atlas

Crea un archivo `application-local.properties` en `src/main/resources/` (ya está en `.gitignore` para proteger tus credenciales).

Agrega tu conexión de MongoDB:

```properties
spring.data.mongodb.uri=mongodb+srv://${MONGO_USER}:${MONGO_PASS}@cluster1.umaehwe.mongodb.net/franquicias?retryWrites=true&w=majority&appName=Cluster1
```

Define tus variables de entorno localmente:

**En Windows:**

```bash
setx MONGO_USER "tu_usuario"
setx MONGO_PASS "tu_password"
```

**En macOS/Linux:**

```bash
export MONGO_USER="tu_usuario"
export MONGO_PASS="tu_password"
```

Asegúrate de que tu IP esté permitida en la configuración de red de tu clúster de MongoDB Atlas.

---

### 3️⃣ Construir y ejecutar localmente

```bash
./mvnw clean install
./mvnw spring-boot:run
```

La API quedará disponible en [http://localhost:8080](http://localhost:8080).

---

### 🐳 Despliegue con Docker

Este proyecto usa Spring Boot Buildpacks para empaquetar la aplicación como contenedor, sin necesidad de un `Dockerfile`.

#### 🏗️ Construir la imagen Docker

```bash
./mvnw spring-boot:build-image -Dspring-boot.build-image.imageName=franquicias-api
```

Esto genera una imagen llamada `franquicias-api`.

#### ▶️ Ejecutar el contenedor

```bash
docker run -e MONGO_USER=tu_usuario -e MONGO_PASS=tu_password -p 8080:8080 franquicias-api
```

La API quedará accesible en [http://localhost:8080](http://localhost:8080).

---

## ✅ Ejecutar pruebas unitarias

Para correr todas las pruebas del proyecto:

```bash
./mvnw test
```

Las pruebas unitarias cubren todos los servicios y controladores principales con escenarios de éxito y error.

---

## 📖 Consideraciones de diseño

- El proyecto está estructurado siguiendo arquitectura hexagonal, separando la lógica de negocio (dominio) de la infraestructura (adaptadores, repositorios y base de datos).
- Se utilizan operadores reactivos (`map`, `flatMap`, `switchIfEmpty`) junto con señales (`doOnNext`, `doOnError`, `doOnSuccess`) para implementar un flujo reactivo no bloqueante.
- Se aplican logs en puntos clave para trazabilidad: creación y eliminación de franquicias, sucursales y productos.
- La API incluye validación de duplicados: no permite nombres repetidos para franquicias, sucursales ni productos dentro de la misma sucursal.
