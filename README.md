# POS-GOA-RESTAURANT
> [!CAUTION]
> Esta versión no es para su uso en producción.
> Si usas lammp asegurate de realizar la comprobación de seguridad `sudo /opt/lampp/lampp security`
## Prerrequisitos

Antes de instalar y ejecutar el sistema, asegúrate de tener los siguientes requisitos previos instalados:

1. **Java Development Kit (JDK)**: Versión 17 o superior.
   - Puedes descargarlo desde [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) o [OpenJDK](https://openjdk.org/).


2. **MySQL Server**: Para la base de datos del sistema.
   - Asegúrate de tener una base de datos configurada con el archivo SQL proporcionado (`restaurant_goa.sql`).

3. **IDE recomendado**: [NetBeans](https://netbeans.apache.org/) o cualquier IDE compatible con proyectos Maven y Java.

## Instalación

Sigue estos pasos para instalar y ejecutar el sistema:

1. **Clona el repositorio**:

   ```bash
   git clone git@github.com:JordyPirata/POS-GOA-RESTAURANT.git
   cd POS-GOA-RESTAURANT
   ```

2. **Configura la base de datos**:

    Importa el archivo restaurant_goa.sql en tu servidor MySQL.
    Actualiza las credenciales de conexión a la base de datos en el archivo de configuración correspondiente (`/src/main/java/Modelo/Conexion.java`).
