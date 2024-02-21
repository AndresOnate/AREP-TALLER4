package edu.escuelaing.arep.app;


import edu.escuelaing.arep.app.annotations.Component;
import edu.escuelaing.arep.app.annotations.GetMapping;
import edu.escuelaing.arep.app.annotations.PostMapping;
import edu.escuelaing.arep.app.annotations.RequestBody;
import edu.escuelaing.arep.app.controller.APIController;
import edu.escuelaing.arep.app.controller.MovieAPI;
import edu.escuelaing.arep.app.model.Request;
import edu.escuelaing.arep.app.model.ResponseBuilder;
import edu.escuelaing.arep.app.service.Function;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The `HttpServer` class represents a simple HTTP server that listens on port 35000 and handles incoming HTTP requests.
 * It utilizes a basic API for retrieving movie information and responds with HTML content based on the request URI.
 *
 * It supports requests related to movie information and provides a default HTML response.
 */
public class HTTPServer
{
    /**
     * Represents the API controller for fetching movie information.
     */
    private static MovieAPI myMoviesAPI = new APIController();
    private static HTTPServer _instance = new HTTPServer();
    private static String location = "public";

        private static HashMap<String, Method> getServices = new HashMap<String, Method>();
    private static HashMap<String, Method> postServices = new HashMap<String, Method>();

    private static String classesPath = "target/classes/edu/escuelaing/arep/app/controller/";

    private HTTPServer(){}

    public static HTTPServer getInstance(){
        return _instance;
    }


    /**
     * The main method that serves as the entry point for the HTTP server.
     * This method sets up a server socket on port 35000 and continuously listens for incoming client connections.
     * When a client connection is accepted, it spawns a new thread to handle the client request.
     * @param args The command line arguments passed to the program.
     * @throws Exception If an error occurs during the execution of the server.
     */
    public static void main(String[] args)  throws Exception
    {
        List<Class<?>> classes = getClasses(classesPath);
        for (Class<?> classInPath : classes) {
            if (classInPath.isAnnotationPresent(Component.class)) {
                loadComponent(classInPath);
            }
        }
        System.out.println(getServices.keySet());

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        Boolean running = true;
        while (running) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
                handleClientRequest(clientSocket);
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        }
        serverSocket.close();
    }

    /**
     * Handles incoming client requests on the server.
     * Reads client requests, processes the request, and sends a response back to the client.
     * @param clientSocket The client socket through which communication with the server occurs.
     * @throws IOException If an I/O error occurs while reading from or writing to the client socket.
     * @throws URISyntaxException If an error occurs while parsing the URI of the request.
     */
    private static void handleClientRequest(Socket clientSocket) throws IOException, URISyntaxException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        clientSocket.getInputStream()));
        String inputLine, outputLine = null;
        String requestBody = "";
        boolean firstLine = true;
        String httpMethod = "";
        String uriStr = "";

        Request request = new Request();
        while ((inputLine = in.readLine()) != null) {
            if(firstLine){
                httpMethod = inputLine.split(" ")[0];
                uriStr = inputLine.split(" ")[1];
                firstLine = false;
            }

            if(httpMethod.equals("POST")){
                int contentLength = 0;
                while ((inputLine = in.readLine()) != null && !inputLine.isEmpty()) {
                    if (inputLine.startsWith("Content-Length:")) {
                        contentLength = Integer.parseInt(inputLine.substring(16));
                        System.out.println(contentLength);
                    }
                }

                if (contentLength > 0) {
                    char[] bodyBuffer = new char[contentLength];
                    in.read(bodyBuffer, 0, contentLength);
                    requestBody = new String(bodyBuffer);
                    request.setBody(requestBody);
                    request.setHTTPVerb("POST");

                }
            }
            System.out.println("Received: " + inputLine);
            if (!in.ready()) {
                break;
            }
        }

        URI requestURI = new URI(uriStr);
        request.setUri(requestURI);
        outputLine =  ResponseBuilder.httpError(requestURI);
        String path = requestURI.getPath();

        try {
            if(requestURI.getPath().contains(".")){
                if(uriStr.contains("png") || uriStr.contains("jpg") || uriStr.contains("ico")){
                    handleImageRequest(requestURI, clientSocket.getOutputStream());
                }else{
                    outputLine = httpResponseFile(requestURI);
                }
            }else if(httpMethod.equals("GET")){
                if(getServices.containsKey(path)){
                    Method webService = getServices.get(path);
                    String contentType= webService.getAnnotation(GetMapping.class).produces();
                    outputLine = ResponseBuilder.httpOkServiceCall("GET", contentType);
                    outputLine = outputLine + webService.invoke(null);
                }
            } else if(httpMethod.equals("POST")) {
                if (postServices.containsKey(path)) {
                    Method webService = postServices.get(path);
                    Parameter[] parameters = webService.getParameters();
                    String contentType= webService.getAnnotation(PostMapping.class).produces();
                    outputLine = ResponseBuilder.httpOkServiceCall("POST", contentType);
                    if(parameters[0].isAnnotationPresent(RequestBody.class)){
                        outputLine = outputLine + webService.invoke(null, requestBody);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        out.println(outputLine);
        out.println();
        out.close();
        in.close();
        clientSocket.close();
    }

    /**
     * Generates an HTTP response for a requested file.
     * This method reads the content of the requested file from the server's filesystem,
     * constructs an HTTP response header with the appropriate content type, and returns
     * the HTTP response as a string.
     * @param requestedURI The URI of the requested file.
     * @return The HTTP response string containing the content of the requested file.
     * @throws IOException If an I/O error occurs while reading the file or constructing the response.
     */
    public static String httpResponseFile(URI requestedURI) throws IOException{
        String path = requestedURI.getPath();
        if (!path.contains(".")) {
            path = "/index.html";
        }
        Path file = Paths.get("target/classes/" + location + path);
        String extension = path.substring(path.lastIndexOf('.') + 1);
        String contentType = ResponseBuilder.getContentType(extension);
        String outputLine = ResponseBuilder.httpOkHeader(contentType);
        Charset charset = Charset.forName("UTF-8");
        BufferedReader reader = Files.newBufferedReader(file, charset);
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            outputLine = outputLine + line;
        }
        return  outputLine;
    }

    /**
     * Handles an image request by reading the requested image file from the server's filesystem
     * and writing it to the provided output stream along with an HTTP response header.
     * @param requestedURI The URI of the requested image file.
     * @param out The output stream to which the image content and HTTP response header will be written.
     * @throws IOException If an I/O error occurs while reading the image file or writing to the output stream.
     */
    public static void handleImageRequest(URI requestedURI, OutputStream out) throws IOException {
        String path = requestedURI.getPath();
        if(path.contains("favicon.ico")){
            path = "/img/mySpark.png";
        }
        String extension = path.substring(path.lastIndexOf('.') + 1);
        String contentType = ResponseBuilder.getContentType(extension);
        String outputLine = ResponseBuilder.httpOkHeader(contentType);
        Path file = Paths.get("target/classes/" + location + path);
        byte[] fileArray;
        fileArray = Files.readAllBytes(file);
        out.write(outputLine.getBytes());
        out.write(fileArray, 0, fileArray.length);
        out.close();
    }

    public static void setLocation(String newLocation){
        location = newLocation;
    }

    private String callService(Function service, Request request) throws IOException {
        String output = "";
        try {
            output = service.handle(request, new ResponseBuilder());
            System.out.println("==== Function Response: " + output + " =====");
        }catch (IOException e){
            e.printStackTrace();
            return ResponseBuilder.httpError(request.getUri());
        }
        return ResponseBuilder.httpOkServiceCall(request) + output;
    }

    public static Map<String, String> getParamsFromURI(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                if (keyValue.length == 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
        }
        return params;
    }

    public static List<Class<?>> getClasses(String directory) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        File folder = new File(directory);
        String[] files = folder.list();
        for (String file : files) {
            if (file.endsWith(".class")) {
                String className = file.substring(0, file.length() - 6);
                className = "edu.escuelaing.arep.app.controller." + className;
                System.out.println(className);
                Class<?> classInPath = loadClass(className, directory);
                if (classInPath != null) {
                    classes.add(classInPath);
                }
            }
        }
        return classes;
    }

    public static Class<?> loadClass(String className, String directory) throws ClassNotFoundException {
        try {
            File classFile = new File(directory);
            URL url = classFile.toURI().toURL();
            URL[] urls = new URL[]{url};
            ClassLoader classLoader = new URLClassLoader(urls);
            return classLoader.loadClass(className);
        } catch (Exception e) {
            return null;
        }
    }

    private static void loadComponent(Class<?> classInPath){
        for(Method method: classInPath.getMethods()){
            if (method.isAnnotationPresent(GetMapping.class)) {
                String service = method.getAnnotation(GetMapping.class).value();
                getServices.put(service, method);
            }
            if (method.isAnnotationPresent(PostMapping.class)) {
                String service = method.getAnnotation(PostMapping.class).value();
                postServices.put(service, method);
            }
        }
    }

}
