# Sistema de Gestión de Cola de Impresión con Kafka

Este proyecto implementa un sistema de gestión de impresión distribuido y eficiente utilizando **Java** y **Apache Kafka**.  
El sistema separa la recepción de documentos, su procesamiento (paginación) y su impresión física (simulada), garantizando escalabilidad y persistencia.

## 📋 Arquitectura del Sistema

El sistema sigue una arquitectura orientada a eventos con los siguientes componentes:

1.  **Productores (Empleados - `Enployees`)**: 
    - Generan documentos JSON con campos: `titulo`, `documento`, `tipo` (B/N o Color), y `sender`.
    - Envían los documentos originales al **Topic de Recepción** (`topic-recepcion`).
    
2.  **Consumidores/Procesadores**:
    - **Archivador (`Archivador`)**: Escucha el `topic-recepcion` y guarda cada documento JSON original en una carpeta específica del empleado (`archivados/{sender}/`). Funciona en paralelo al transformador.
    - **Transformador (`Trasformer`)**: 
        - Escucha el `topic-recepcion`.
        - Divide el contenido del documento en páginas de máximo 400 caracteres.
        - Envía cada página procesada al topic correspondiente según el tipo de impresión (`topic-color` o `topic-bn`).

3.  **Impresoras (Simuladas - `ImpresoraSimulada`)**:
    - **Impresoras Color** (2 hilos): Escuchan `topic-color`. Clustered en `grupo-impresoras-COLOR`.
    - **Impresoras B/N** (3 hilos): Escuchan `topic-bn`. Clustered en `grupo-impresoras-BLANCO_NEGRO`.
    - Simulan la impresión guardando archivos de texto en `impresiones/COLOR/` o `impresiones/BLANCO_NEGRO/`.

### Kafka Topics

| Nombre del Topic | Descripción |
| :--- | :--- |
| **`topic-recepcion`** | Entrada principal. Recibe documentos JSON crudos de los empleados. |
| **`topic-color`** | Cola de trabajo para impresiones a color (páginas transformadas). |
| **`topic-bn`** | Cola de trabajo para impresiones B/N (páginas transformadas). |

---

## 🚀 Puesta en Marcha (Entorno de Desarrollo Compilado)

### Prerrequisitos
- Tener instalado **Java 17** o superior.
- Tener instalado y configurado **Apache Kafka** y Zookeeper.

### 1. Iniciar Kafka
Asegúrate de tener Zookeeper y Kafka corriendo (comandos estándar desde la carpeta de instalación de Kafka):

**Terminal 1 (Zookeeper):**
```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
# En Windows:
# bin\windows\zookeeper-server-start.bat config\zookeeper.properties
```

**Terminal 2 (Kafka Server):**
```bash
bin/kafka-server-start.sh config/server.properties
# En Windows:
# bin\windows\kafka-server-start.bat config\server.properties
```

### 2. Crear los Topics
Es necesario crear los topics antes de arrancar la aplicación para evitar errores si la auto-creación está deshabilitada.

```bash
# Crear topic de recepción
bin/kafka-topics.sh --create --topic topic-recepcion --bootstrap-server localhost:9092

# Crear topic para Color
bin/kafka-topics.sh --create --topic topic-color --bootstrap-server localhost:9092

# Crear topic para B/N
bin/kafka-topics.sh --create --topic topic-bn --bootstrap-server localhost:9092
```

### 3. Ejecutar la Aplicación
El proyecto es un proyecto Maven estándar. Puedes importarlo en tu IDE favorito (Eclipse, IntelliJ) o ejecutarlo desde consola.

La clase principal es **`org.cuatrovientos.impresoras.Office`**.

Esta clase lanzará automáticamente:
- 1 hilo para el Archivador.
- 1 hilo para el Transformador.
- 3 hilos de impresora B/N.
- 2 hilos de impresora Color.
- Simulación de envío de documentos por parte de "Miguel" y "Ana".

---

## 🛠️ Guía para el Mantenedor

### Reiniciar el Sistema y Limpieza
Para reiniciar el sistema completamente y borrar todos los mensajes pendientes, sigue estos pasos:

1.  **Detener la aplicación Java**.
2.  **Borrar datos de Kafka (Opcional pero recomendado para limpieza total):**
    - Elimina los topics y vuélvelos a crear:
    ```bash
    bin/kafka-topics.sh --delete --topic topic-recepcion --bootstrap-server localhost:9092
    bin/kafka-topics.sh --delete --topic topic-color --bootstrap-server localhost:9092
    bin/kafka-topics.sh --delete --topic topic-bn --bootstrap-server localhost:9092
    ```
3.  **Limpiar archivos locales:**
    - Borra manualmente las carpetas `archivados/` e `impresiones/` que se generan en la raíz del proyecto para eliminar los archivos de ejecuciones anteriores.

### Solución de Problemas Comunes
- **Error "Broker may not be available"**: Verifica que Kafka está corriendo en `localhost:9092`.
- **Los consumidores no leen mensajes**: Verifica que `group.id` sea correcto y único si estás probando con múltiples instancias externas.
- **Error de Serialización**: El sistema espera JSON válido. Si envías mensajes manuales a `topic-recepcion`, asegúrate de cumplir el esquema del objeto `Document`.

---

## 📦 Estructura de Archivos Salientes
El sistema generará la siguiente estructura de carpetas durante su ejecución:

```
/archivados
    /Miguel Goyena
        /Acta Reunión.json
    /Ana Lopez
        /Cartel Publicitario.json
/impresiones
    /COLOR
        /170000123_imp1.txt  (Páginas del cartel)
    /BLANCO_NEGRO
        /170000456_imp3.txt  (Páginas del acta)
```
