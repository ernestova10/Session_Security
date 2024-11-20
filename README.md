
# Enunciado del ejercicio: Creación de una API REST con gestión de usuarios, roles, autenticación y sesiones

## Descripción del ejercicio

El objetivo de este ejercicio es desarrollar una API REST en **Spring Boot** para gestionar usuarios, productos y sesiones. Implementarás autenticación manual, un sistema de roles con privilegios diferenciados, y el manejo de contraseñas hasheadas y sesiones con tokens cifrados.

---

## Estructura de las entidades

Se trabajará con las siguientes tres entidades:

1. **Usuario**:
    - `id` (Long): Identificador único del usuario.
    - `nombre` (String): Nombre del usuario (debe ser único).
    - `password` (String): Contraseña del usuario (debe estar hasheada).
    - `rol` (String): Rol del usuario, que puede ser `USER` o `ADMIN`.

2. **Producto**:
    - `id` (Long): Identificador único del producto.
    - `nombre` (String): Nombre del producto.
    - `stock` (int): Cantidad disponible del producto.
    - `precio` (boolean): Indicador de si el producto tiene un precio válido o no.

3. **Sesión**:
    - `id` (Long): Identificador único de la sesión.
    - `token` (String): Token generado mediante el cifrado.
    - `expirationDate` (LocalDateTime): Fecha de expiración de la sesión (2 minutos desde su creación).
    - `usuario` (Usuario): Relación con la entidad `Usuario`.

---

## Requisitos funcionales

1. **Gestión de Usuarios**:
    - Los usuarios deben estar almacenados en la base de datos con los atributos mencionados.
    - Las contraseñas deben estar **hasheadas** utilizando un algoritmo seguro como `BCrypt`.
    - Cada usuario debe tener un rol:
        - `USER`: Solo puede acceder al endpoint para obtener un producto.
        - `ADMIN`: Puede acceder a los endpoints para obtener y crear productos.

2. **Gestión de Productos**:
    - Los productos estarán almacenados en la base de datos con los atributos indicados.
    - Implementa los siguientes endpoints:
        - `GET /productos/{id}`: Devuelve la información de un producto según su ID.
            - **Accesible por:** `USER` y `ADMIN`.
            - **Salida esperada**: JSON con el nombre, stock y si el producto tiene precio válido.
        - `POST /productos`: Permite insertar un nuevo producto.
            - **Accesible por:** `ADMIN` únicamente.
            - **Entrada esperada**: JSON con `nombre`, `stock` y `precio`.

3. **Autenticación mediante sesiones**:
    - Los usuarios deben autenticarse mediante el endpoint `POST /login`, enviando su nombre y contraseña.
    - Si las credenciales son válidas:
        - Se genera una **sesión** y se almacena en la base de datos.
        - El token de la sesión debe generarse cifrando el string `nombre_usuario:clave_secreta` utilizando **AES**.
        - La tabla de sesiones debe almacenar:
            - `id`: Identificador único de la sesión.
            - `token`: Token cifrado.
            - `expirationDate`: Fecha de expiración, con una validez de **2 minutos**.
            - `usuario`: Relación con el usuario correspondiente.

4. **Validación de Sesiones**:
    - Los endpoints de productos deben validar que:
        - El token proporcionado en los headers HTTP es válido.
        - La sesión no ha expirado (verificar `expirationDate`).
    - Si la sesión no es válida o ha expirado, se debe devolver un error con código **401 Unauthorized**.

---

## Cifrado del Token (AES)

Se incluye un ejemplo de cómo generar el token utilizando **AES**:

```java
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESCipherUtil {
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "claveSuperSecreta"; // 16 caracteres

    public static String encrypt(String data) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(SECRET_KEY.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] decryptedData = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedData);
    }
}
```

En este ejemplo:
- **Token generado**: `nombre_usuario:claveSuperSecreta`, cifrado con AES.
- **Clave simétrica**: Definida en las propiedades de la aplicación (`application.properties`).

---

## Endpoints requeridos

1. **Autenticación**:
    - `POST /login`: Permite al usuario autenticarse.
        - **Entrada**: JSON con `nombre` y `password`.
        - **Salida**: Token de la sesión si las credenciales son válidas.

2. **Gestión de Productos**:
    - `GET /productos/{id}`: Devuelve la información de un producto.
        - **Entrada**: ID del producto.
        - **Salida**: JSON con `nombre`, `stock`, y `precio`.
    - `POST /productos`: Permite insertar un nuevo producto.
        - **Entrada**: JSON con `nombre`, `stock`, y `precio`.

---

## Criterios de evaluación

1. **Funcionalidad**:
    - La API debe cumplir con todos los requisitos especificados.
2. **Seguridad**:
    - Las contraseñas y los tokens deben manejarse de forma segura.
    - Las sesiones deben ser validadas correctamente.
3. **Organización**:
    - Estructura limpia y modular del proyecto.
    - Código bien documentado.
4. **Pruebas**:
    - Incluye pruebas unitarias para los servicios y pruebas de integración para los endpoints.

---

**¡Manos a la obra!**
