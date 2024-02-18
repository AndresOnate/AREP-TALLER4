# MICROFRAMEWORKS WEB

En este taller se construyó un servidor web para soportar una funcionalidad similar a la de SparkJava, un microframework WEB que permite construir aplicaciones web de manera simple usando funciones lambda.

## Diseño de la aplicación

La aplicación está diseñada para cumplir con los requisitos especificados en el enunciado del taller y proporcionar una experiencia de usuario fluida y satisfactoria. A continuación, se describen los principales componentes y características de la aplicación:

- La clase `MySpark` permite el registro de servicios get y post usando funciones lambda, es responsable de manejar las solicitudes entrantes de los usuarios y coordinar las interacciones entre el cliente. Además, incluye métodos para configurar el directorio de los archivos estáticos y el tipo de respuesta del servicio, lo que permite servir contenido estático como archivos `HTML`, `CSS`, `JavaScript` e imágenes en los formatos `PNG` y `JPG`.
- La interfaz `Function` define un único método `handle`, que toma un objeto de tipo `Request` y `ResponseBuilder` para devolver una cadena de caracteres. Esta interfaz se utiliza en la clase `MySpark` para definir las funciones lambda que manejan las solicitudes entrantes de los usuarios. El método handle es responsable de procesar la solicitud y generar una respuesta adecuada.
- La clase `Request` representa una solicitud HTTP entrante y proporciona métodos para acceder a sus atributos, como la URI y el cuerpo de la solicitud. 
- La clase `APIController` realiza la conexión a OMDb API en el método `connectToMoviesAPI`, al cual se le pasa como argumento el título de la película. Si la película es encontrada, se retorna un String con los datos, de lo contrario, se establecen mecanismos para validar si la película no fue encontrada y mostrar al usuario el estado de la consulta. Utiliza una estructura de datos concurrente `ConcurrentHashMap` para almacenar en caché las consultas realizadas a la API. Esto mejora significativamente los tiempos de respuesta al evitar consultas repetidas para las mismas películas. 
- La clase `ResponseBuilder` ofrece métodos para construir las respuestas HTML que son enviadas a los usuarios. Proporciona métodos para generar respuestas HTTP con diferentes códigos de estado y tipos de contenido.
- Se agrega el directorio `resources` donde se almacenan los archivos que serán leídos por el servidor.
- Las clases `Product` y `ProductService`son agregadas para mostrar el comportamiento del servicio post.
- La clase `MyServices` contiene ejemplos que muestran cómo se desarrollarían las aplicaciones en su servidor.

## Guía de Inicio

Las siguientes instrucciones le permitirán descargar una copia y ejecutar la aplicación en su máquina local.

### Prerrequisitos

- Java versión 8 OpenJDK
- Maven
- Git

## Instalación 

1. Ubíquese sobre el directorio donde desea realizar la descarga y ejecute el siguiente comando:
   
     ``` git clone https://github.com/AndresOnate/AREP-TALLER3.git ```

2. Navegue al directorio del proyecto:
   
      ``` cd  AREP-TALLER3 ```

3. Ejecute el siguiente comando para compilar el código:

      ``` mvn compile ```

5.  Ejecute el siguiente comando para empaquetar el proyecto:
   
      ``` mvn package ``` 

6. Para iniciar el servidor, ejecute el siguiente comando:

    ``` java -cp target/LAB3_AREP-1.0-SNAPSHOT.jar edu.escuelaing.arep.app.MyServices ```

7. Verifique en la linea de comanos que se imprimió el mensaje **Listo para recibir ...**
   
![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/1060d327-357b-4a9f-8055-60a98065be70)


9. De igual forma, puede abrir el proyecto con un IDE y ejecutar el método main de la clase `MyServices`. En la imagen siguiente se muestra el proyecto con el IDE IntelliJ:

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/97f4d62b-d5e9-41aa-9b86-0eb87f01ce01)


## Construyendo aplicaciones

La clase `MySpark` permite el registro de servicios get y post usando funciones lambda. Cuando se recibe una solicitud GET o POST en la ruta especificada , el servidor ejecutará la función lambda proporcionada como segundo argumento. La función lambda toma un objeto `Request` como parámetro, que representa la solicitud entrante y un objeto `ResponseBuilder` que le permite modificar el tipo de la respuesta, por ejemplo, a "application/json".

Para registrar un servicio GET o POST, utilizamos los métodos get() y post() proporcionados por la clase `MySpark`. Estos métodos toman dos argumentos: la ruta del servicio y una función lambda que define el comportamiento del servicio. Dentro de la función lambda, puedes procesar la solicitud, realizar operaciones necesarias (como procesar datos, consultar APIs externas etc.) y generar la respuesta correspondiente.


Ejemplo de una función lambda en un servicio GET:

```
// Registro de un servicio GET en la ruta '/hi'
get("/hi", (req, res) -> {
          // Obtener la ruta de la URI de la solicitud
          String path = req.getUri().getPath();
          // Generar una respuesta con la ruta
          return "El query es:" + path;
      });
```

Ejemplo de una función lambda en un servicio POST:
```
post("/products", (req, res) -> {
    // Configurar el tipo de respuesta como JSON
    res.setResponseType("application/json");
    // Crear un nuevo producto a partir de los datos en el cuerpo de la solicitud
    Product product = new Product(req.getBody());
    // Agregar el producto a la lista de productos
    productService.addProduct(product);
    // Devolver el producto como una cadena JSON
    return product.toString();
});

```

Por defecto, la ruta de acceso a los recursos estáticos es `/public`, si se requiere, se puede cambiar con el método `setLocation` de la clase `MySpark`.
Una vez que se hayan definido los servicios, puede ejecutar el servidor (método `runServer`) para comenzar a manejar solicitudes HTTP:

```
public static void main(String[] args) throws Exception {
    // servicios post y get
    MySpark.getInstance().runServer(args);
}

```

Por defecto, los servicios estarán disponibles en la ruta `http://localhost:35000/`

## Probando la Aplicación.  

### Archivos Estáticos

Puede asignar una carpeta  que sirve archivos estáticos con el método `MySpark.setLocation`, si por ejemplo, el directorio es configurado en `/public`, Un archivo /public/css/style.css está disponible como `http://{host}:{port}/css/style.css`.

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/8462a39b-ce0f-4065-80aa-344bb6c03664)

Entrega archivos estáticos como páginas HTML, CSS, JS e imágenes:

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/cee5f6a9-9f3f-4675-a475-0046efcf4c99)

Si no se encuentra el archivo en el directorio especificado, se mostrará un mensaje de error:

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/cd653c10-ded2-4c5d-8d8c-6c2b7b2af4a0)


### GET

La clase `mySpark` ofrece el servicio get, se desarrollaron los siguientes ejemplos:

`/hi`retorna un mensaje que incluye la ruta del URI recibido en la solicitud:

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/a751ecff-e540-40b4-9fa7-fe0ea877e2f0)

`/users` interpreta los parámetros de la consulta del URI para mostrar un mensaje personalizado.

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/ec3df66f-5fe8-4912-95b6-cad027643909)

`/movies` realiza una solicitud a una API de películas utilizando el título proporcionado en los parámetros de la consulta del URI y devuelve la respuesta en formato JSON.

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/2f20b64b-a200-4c41-89a9-1d253abbeb48)

Este mismo servicio puede ser usado por clientes web para dar un mejor formato a la salida de la consulta:

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/7f6c0f71-4ef5-4b40-9187-f8f66c70a7cd)

### POST

La clase `mySpark` ofrece el servicio post. Se desarrolló el siguiente ejemplo:

Se implementó un servicio sencillo para enviar al servidor solicitudes POST para la creación de productos:

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/9ab564be-a570-4e01-b5e1-fe929bb34edc)

El servidor retorna el JSON del producto creado:

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/ab48e395-bd5b-4d37-8a7f-0c514f837090)

Podemos acceder a todos los productos al acceder al servicio get con ruta `/products`

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/733adf12-e5fb-467d-9fc5-5f3dfaed6dc9)


La demostración anterior se ejecutó sobre el sistema operativo Windows, a continuación, se ejecutará el servidor web en una máquina virtual con sistema operativo Kali Linux:

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/7b37a773-c45b-4226-b278-83108f7bcaf3)

Realizamos solicitudes para comprobar el correcto funcionamiento.

Archivos estáticos:

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/57bcd77a-e091-4e2f-b472-1dc55bded3aa)

Servicio GET:

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/f26f1624-ce4e-48b7-b2be-baaa95e70529)


## Ejecutando las Pruebas.  

A continuación se muestra cómo ejecutar las pruebas desde la línea de comandos y un IDE como IntelliJ.

1. Navegue al directorio del proyecto con la línea de comandos.
2. Ejecute el siguiente comando:
   
   ``` mvn test ```
3. Debe mostrarse en pantalla que las pruebas fueron exitosas.

   ![image](https://github.com/AndresOnate/AREP-TALLER2/assets/63562181/a14eef59-15ba-4f74-a8ea-c0749929d6d0)


4. Puede ejecutar las pruebas desde un IDE como IntelliJ:

![image](https://github.com/AndresOnate/AREP-TALLER3/assets/63562181/3f85f85b-4909-46ea-82c9-2a7da900650e)



## Construido Con. 

- Maven - Administrador de dependencias

## Versión

1.0.0

## Autores

- Andrés Camilo Oñate Quimbayo

