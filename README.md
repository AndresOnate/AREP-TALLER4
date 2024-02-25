# TALLER DE ARQUITECTURAS DE SERVIDORES DE APLICACIONES, META PROTOCOLOS DE OBJETOS, PATRÓN IOC, REFLEXIÓN

En este taller se construyó un servidor web  tipo apache para soportar una funcionalidad similar a la de Spring. El servidor debe provee un framework IoC para la construcción de aplicaciones web a partir de POJOS.

## Diseño de la aplicación

La aplicación está diseñada para cumplir con los requisitos especificados en el enunciado del taller y proporcionar una experiencia de usuario fluida y satisfactoria. A continuación, se describen los principales componentes y características de la aplicación:

Se definieron las siguientes anotaciones:

- `@Component`:  Se utiliza para marcar las clases como componentes que ofrecen servicios web.
- `@GetMapping`: Esta anotación se usa para mapear métodos a solicitudes HTTP GET. Cuando se aplica `@GetMapping` a un método dentro de un componente, se especifica la ruta o la URL relativa a la cual ese método manejará las solicitudes GET.
- `@PostMapping`: Esta anotación se usa para mapear métodos a solicitudes HTTP POST. Cuando se aplica `@PostMapping` a un método dentro de un componente, se especifica la ruta o la URL relativa a la cual ese método manejará las solicitudes POST.
- `@PathVariable`: Esta anotación se utiliza para mapear variables de la URL de una solicitud a parámetros en un método controlador.
- `@RequestParam`: Esta anotación se utiliza para vincular parámetros de solicitud HTTP a parámetros de método en controladores.
- `@RequestBody`: Esta anotación se utiliza para vincular un parámetro al cuerpo de la solicitud HTTP.

Se implementaron las siguientes clases:

- La clase `HTTPServer` consulta de forma recursiva  el directorio raíz  buscando clases con la anotación `@Component`, si la clase contiene la anotación, busca métodos con las anotaciones `@GetMapping` y `@PostMapping` para poder procesar las solicitudes asociadas a cada método. Esta clase también ofrece métodos para procesar las solicitudes de servicios estáticos como archivos `HTML` y `PNG` .
- La clase `HTTPServer` aún permite el registro de servicios get y post usando funciones lambda, como lo fue implementado en el taller anterior, es responsable de manejar las solicitudes entrantes de los usuarios y coordinar las interacciones entre el cliente. Además, incluye métodos para configurar el directorio de los archivos estáticos y el tipo de respuesta del servicio, lo que permite servir contenido estático como archivos HTML, CSS, JavaScript e imágenes en los formatos PNG y JPG.
- La interfaz `Function` define un único método `handle`, que toma un objeto de tipo `Request` y `ResponseBuilder` para devolver una cadena de caracteres. Esta interfaz se utiliza en la clase `MySpark` para definir las funciones lambda que manejan las solicitudes entrantes de los usuarios. El método handle es responsable de procesar la solicitud y generar una respuesta adecuada.
- La clase `Request` representa una solicitud HTTP entrante y proporciona métodos para acceder a sus atributos, como la URI y el cuerpo de la solicitud. 
- La clase `APIController` realiza la conexión a OMDb API en el método `connectToMoviesAPI`, al cual se le pasa como argumento el título de la película. Si la película es encontrada, se retorna un String con los datos, de lo contrario, se establecen mecanismos para validar si la película no fue encontrada y mostrar al usuario el estado de la consulta. Utiliza una estructura de datos concurrente `ConcurrentHashMap` para almacenar en caché las consultas realizadas a la API. Esto mejora significativamente los tiempos de respuesta al evitar consultas repetidas para las mismas películas. 
- La clase `ResponseBuilder` ofrece métodos para construir las respuestas HTML que son enviadas a los usuarios. Proporciona métodos para generar respuestas HTTP con diferentes códigos de estado y tipos de contenido.
- Se agrega el directorio `resources` donde se almacenan los archivos que serán leídos por el servidor.
- Las clases `Product` y `ProductService`son agregadas para mostrar el comportamiento del servicio post.
- La clase `MyServices` contiene ejemplos que muestran cómo se desarrollarían las aplicaciones en mySpark en su servidor.

## Guía de Inicio

Las siguientes instrucciones le permitirán descargar una copia y ejecutar la aplicación en su máquina local.

### Prerrequisitos

- Java versión 8 OpenJDK
- Maven
- Git

## Instalación 

1. Ubíquese sobre el directorio donde desea realizar la descarga y ejecute el siguiente comando:
   
     ``` git clone https://github.com/AndresOnate/AREP-TALLER4.git ```

2. Navegue al directorio del proyecto:
   
      ``` cd  AREP-TALLER4 ```

3. Ejecute el siguiente comando para compilar el código:

      ``` mvn compile ```

5.  Ejecute el siguiente comando para empaquetar el proyecto:
   
      ``` mvn package ``` 

6. Para iniciar el servidor, ejecute el siguiente comando:

    ``` java -cp target/LAB4_AREP-1.0-SNAPSHOT.jar edu.escuelaing.arep.app.HTTPServer ```

7. Verifique en la linea de comanos que se imprimió el mensaje **Listo para recibir ...**
   
![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/f048c372-cb97-436a-9613-ea7ef0f6268e)

9. De igual forma, puede abrir el proyecto con un IDE y ejecutar el método main de la clase `HTTPServer`. En la imagen siguiente se muestra el proyecto con el IDE IntelliJ:

![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/f5d1cc4e-5035-4e49-9f26-35735e538372)


## Construyendo aplicaciones

Las anotaciones  `GetMapping` y  `PostMapping` registran el valor con el cual el usuario puede acceder a los servicios con el atributo `value` produces, el atributo `produces` permite cambiar el tipo de retorno, por ejemplo `application/json`.

- `GetMapping`: La anotación @GetMapping se usa para mapear métodos de controlador a solicitudes HTTP GET. Se aplica sobre métodos de controlador y se especifica la ruta relativa del endpoint que manejará. Por ejemplo, al aplicar @GetMapping("/products") a un método getAllProducts(), este método manejará las solicitudes GET a la URL "/products". 

- `PostMapping`: La anotación @PostMapping se utiliza para mapear métodos de controlador a solicitudes HTTP POST. Al igual que @GetMapping, se aplica sobre métodos de controlador y se especifica la ruta relativa del endpoint que manejará. Por ejemplo, al aplicar @PostMapping("/products") a un método saveProduct(String newProduct), este método manejará las solicitudes POST a la URL "/products".
  
- `PathVariable`: En algunos casos, es necesario capturar variables de la URL dentro de los métodos del controlador. El valor de la variable de ruta se extraería de la URL y se pasaría al método como argumento. Por ahora, solo está implementado para una variable.

- `PathVariable`: Se utiliza para extraer los parámetros de una solicitud HTTP. Por ejemplo, una solicitud GET a "/movies?title=TheMatrix" proporcionaría "TheMatrix" como valor para el parámetro "title"

Ejemplo de un servicio GET:

```
    @GetMapping(value = "/movies", produces = "application/json")
    public static String getMovieInformation(@RequestParam String title) throws IOException {
        APIController apiMovies = new APIController();
        return  apiMovies.connectToMoviesAPI(title);
    }
```
En este ejemplo, se tiene un método de controlador que maneja las solicitudes HTTP GET a la ruta "/movies" y produce una respuesta en formato JSON. `@RequestParam`: Esta anotación se utiliza para extraer parámetros de la URL de la solicitud HTTP. En este caso, el parámetro title se espera que esté presente en la URL de la solicitud GET. Por ejemplo, si la URL de la solicitud GET es "/movies?title=Wish", el valor "Wish" se asignará al parámetro title.

```
    @GetMapping(value = "/products/", produces = "application/json")
    public static String getProductsById(@PathVariable String id){
        return productService.getProductById(id).toString();
    }
```
Este método maneja las solicitudes GET dirigidas a la ruta `/products/` seguida de un identificador único de producto, como por ejemplo `/products/1`. Captura este identificador único de producto de la URL como un parámetro de ruta utilizando la anotación `@PathVariable`.

Ejemplo de una función lambda en un servicio POST:
```
    @PostMapping(value = "/products", produces = "application/json")
    public static String saveProduct(@RequestBody String newProduct){
        Product product = new Product(newProduct);
        productService.addProduct(product);
        return product.toString();
    }

```

Este método maneja las solicitudes POST dirigidas a la ruta "/products". Cuando se recibe una solicitud, espera que el cuerpo de la misma contenga información sobre un nuevo producto en formato JSON. Utilizando esta información, crea un nuevo objeto de tipo Product y lo añade a la base de datos mediante el servicio productService. Posteriormente, devuelve los detalles del producto recién creado en formato JSON como respuesta al cliente.

Por defecto, los servicios estarán disponibles en la ruta `http://localhost:35000/`

## Probando la Aplicación.  

### Archivos Estáticos

Entrega archivos estáticos como páginas HTML, CSS, JS e imágenes:

![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/edafe78f-7adf-42ce-b584-58358aac5c2f)

![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/2f927234-091e-414f-987f-3ff82e148bcc)

![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/4d4f4459-de63-43ed-8b11-5244df7c821d)

Si no se encuentra el archivo en el directorio especificado, se mostrará un mensaje de error:

![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/eeca9a36-4104-48f8-8c9b-c80bbee05458)


### GET


`/hello`retorna un mensaje como el mostrado en el ejemplo dado en el enunciado:

![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/2309dabe-cfea-4dc4-bd77-4b817235a01f)

`/movies` realiza una solicitud a una API de películas utilizando el título proporcionado en los parámetros de la consulta del URI y devuelve la respuesta en formato JSON.

![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/10baec65-37f5-4dc5-aea7-e298b715dad2)

Este mismo servicio puede ser usado por clientes web para dar un mejor formato a la salida de la consulta:

![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/d78bff31-f5ee-4672-b6b1-3196870c9c2d)

`/products` muestra todos los productos registrado en el servicio.

![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/382f5414-7c9a-4ade-a293-a47671d9e8ed)



### POST

Se implementó un servicio sencillo para enviar al servidor solicitudes POST para la creación de productos:

![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/08876ab5-88ae-43bc-8118-c2b5a7f539aa)


El servidor retorna el JSON del producto creado.
Podemos acceder a todos los productos al acceder al servicio get con ruta `/products`

![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/98c3c489-eb46-4177-b1d4-ea683a46bce4)

Si se quiere acceder a un producto en específico se implementó un método que hace uso de la anotación `@PathVariable`:

![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/3e543062-3b27-423f-b182-5fd84ef57090)


## Ejecutando las Pruebas.  

A continuación se muestra cómo ejecutar las pruebas desde la línea de comandos y un IDE como IntelliJ.

1. Navegue al directorio del proyecto con la línea de comandos.
2. Ejecute el siguiente comando:
   
   ``` mvn test ```
3. Debe mostrarse en pantalla que las pruebas fueron exitosas.

![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/4c39b8e5-5738-43d7-acf7-5f8bc9f09bea)

4. Puede ejecutar las pruebas desde un IDE como IntelliJ:

![image](https://github.com/AndresOnate/AREP-TALLER4/assets/63562181/7d9aa6e5-4033-4b4b-b185-32eb20aee33d)

## Construido Con. 

- Maven - Administrador de dependencias

## Versión

1.0.0

## Autores

- Andrés Camilo Oñate Quimbayo

