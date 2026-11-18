# Banco Chepito

Aplicación de escritorio desarrollada en Java para simular operaciones bancarias básicas. La interfaz está construida con Swing y formularios de NetBeans; los datos se guardan localmente en una base SQLite (`banco.db`) mediante JDBC.

El sistema permite crear usuarios e iniciar sesión, consultar el saldo de una cuenta, enviar dinero a otro usuario registrado, consultar el historial de movimientos y cargar saldo de la cuenta a una tarjeta asociada.

## 1. Tecnologías

- Java 17.
- Swing para las ventanas y formularios.
- Maven para la compilación y administración de dependencias.
- SQLite mediante `sqlite-jdbc` 3.51.3.0.
- AbsoluteLayout de NetBeans para la disposición de algunos componentes gráficos.

## 2. Estructura del proyecto

```text
Proyecto-Progra/
├── pom.xml
├── sqlite3.exe
└── src/main/java/com/mycompany/proyecto_progra/
    ├── Proyecto_Progra.java      -> punto de entrada; prepara la BD y abre el login
    ├── DataBaseManager.java      -> conexión SQLite, creación de tablas y datos iniciales
    ├── Usuario.java              -> modelo de usuario
    ├── UsuarioDAO.java           -> login, registro, consulta y actualización de saldo
    ├── Tarjeta.java              -> modelo de tarjeta
    ├── TarjetaDAO.java           -> creación, consulta y carga de saldo de tarjeta
    ├── TransferenciaDAO.java     -> registro de transferencias e historial
    ├── login.java / login.form   -> inicio de sesión y registro
    ├── pantalla_bancaria.java    -> menú y saldo de la cuenta
    ├── Historial.java / .form    -> historial y formulario para transferir
    ├── Tarjetas.java / .form     -> información de tarjeta y carga de saldo
    ├── Error.java / .form        -> aviso de credenciales incorrectas
    └── imagenes/candado.png      -> recurso gráfico
```

Los archivos `.form` permiten abrir y editar las pantallas con el diseñador de formularios de NetBeans. El código generado por el diseñador está dentro de las secciones marcadas como `Generated Code`.

## 3. Preparación y ejecución

Se necesita un JDK 17 o superior y Maven instalado (o Maven integrado en el IDE).

### Desde NetBeans

1. Abra NetBeans y elija **File → Open Project…**.
2. Seleccione la carpeta `Proyecto-Progra`.
3. Espere a que Maven descargue las dependencias definidas en `pom.xml`.
4. Ejecute `com.mycompany.proyecto_progra.Proyecto_Progra` o use **Run Project**.

### Desde una terminal

Desde la carpeta del proyecto:

```bash
mvn clean package
mvn exec:java -Dexec.mainClass="com.mycompany.proyecto_progra.Proyecto_Progra"
```

El `pom.xml` no configura el plugin `exec-maven-plugin`, por lo que, si el segundo comando no está disponible en la instalación de Maven, compile con `mvn clean package` y ejecute desde NetBeans, o lance la clase principal con el classpath que incluya las dependencias de Maven.

Al iniciar, `DataBaseManager` crea las tablas si todavía no existen. SQLite crea `banco.db` en el directorio de trabajo desde el cual se ejecuta la aplicación. No se requiere instalar ni configurar un servidor de base de datos.

## 4. Datos y modelo de almacenamiento

La conexión está configurada en `DataBaseManager` con la URL `jdbc:sqlite:banco.db`. Se crean estas tablas:

- **usuarios**: identificador, nombre de usuario único, contraseña y saldo de la cuenta.
- **transferencias**: usuario asociado al movimiento, origen, destino, monto, fecha y descripción opcional.
- **tarjetas**: usuario asociado, número único de tarjeta y saldo.

En el primer inicio se insertan, si no existen, los usuarios de demostración siguientes y se les crea una tarjeta:

| Usuario | Contraseña | Saldo inicial de cuenta |
|---|---|---:|
| Erick | `123` | Q5,000.00 |
| Jefferson | `321` | Q3,000.00 |

Las nuevas cuentas registradas comienzan con Q100.00 y reciben una tarjeta con saldo Q0.00. El número de tarjeta se genera aleatoriamente en grupos de cuatro dígitos. La base de datos conserva los datos entre ejecuciones.

## 5. Funcionalidades

### Acceso y registro

- El login valida usuario y contraseña contra SQLite.
- El registro solicita nombre de usuario y contraseña, exige al menos tres caracteres y evita nombres de usuario duplicados.
- Después del registro se crea la tarjeta asociada al usuario.

### Cuenta bancaria

La pantalla principal muestra el nombre del usuario y el saldo de su cuenta. Desde el menú se puede abrir el historial, acceder a las tarjetas o cerrar sesión.

### Transferencias e historial

En la ventana **Historial** se puede enviar dinero indicando usuario destinatario, monto en quetzales y una descripción opcional. La aplicación valida que el destino y el monto estén presentes, que el monto sea numérico y positivo, que haya fondos suficientes y que el usuario no se transfiera a sí mismo. El destinatario debe existir.

Cuando se completa una transferencia, se actualizan los saldos del emisor y del destinatario y se guardan movimientos para ambas partes. La ventana muestra los movimientos más recientes primero e identifica cada uno como enviado o recibido, con monto, fecha y descripción.

### Tarjeta

La pantalla **Tarjetas** presenta el número y saldo de la tarjeta. Permite trasladar un monto positivo desde el saldo de la cuenta bancaria a la tarjeta, siempre que la cuenta tenga fondos suficientes. La operación actualiza ambos saldos dentro de una transacción de base de datos.

## 6. Organización del código

El proyecto mantiene la interfaz, los modelos y el acceso a datos en clases separadas dentro del mismo paquete. `Proyecto_Progra` inicia la base y abre el login; las clases `UsuarioDAO`, `TransferenciaDAO` y `TarjetaDAO` realizan las consultas y actualizaciones SQLite que utilizan las pantallas Swing.

## 7. Alcance y notas

- Es una aplicación académica de simulación bancaria con almacenamiento local; no se conecta a un banco real ni a una red de pagos.
- La base de datos es un archivo local y no requiere PostgreSQL ni otro servidor.
- El código guarda las contraseñas como texto en la tabla `usuarios`; no se implementa hash de contraseñas.
- Las tablas se crean automáticamente, pero no se configura explícitamente `PRAGMA foreign_keys = ON` para cada conexión SQLite.
- `sqlite3.exe` está incluido en la carpeta del proyecto; la aplicación Java utiliza el driver JDBC declarado en Maven.
