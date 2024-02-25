package edu.escuelaing.arep.app;


import edu.escuelaing.arep.app.annotations.*;
import edu.escuelaing.arep.app.controllers.APIController;
import edu.escuelaing.arep.app.model.MovieAPI;
import edu.escuelaing.arep.app.model.Request;
import edu.escuelaing.arep.app.model.ResponseBuilder;
import edu.escuelaing.arep.app.services.Function;

import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
    private static HashMap<String, Function> mySparkGetServices = new HashMap<String, Function>();
    private static HashMap<String, Function> mySparkPostServices = new HashMap<String, Function>();


    private static String classesPath = "target\\classes\\edu\\escuelaing\\arep\\app\\";

    private HTTPServer(){}

    /**
     * Retrieves the singleton instance of the HTTPServer class.
     * @return The singleton instance of the HTTPServer class.
     */
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
        System.out.println(classes);
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
    public static void handleClientRequest(Socket clientSocket) throws IOException, URISyntaxException {
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
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
                if (uriStr.startsWith("/myspark") && mySparkGetServices.containsKey(path.replace("/myspark", ""))) {
                    String uriSpark = path.replace("/myspark", "");
                    outputLine = callService(mySparkGetServices.get(uriSpark), request);
                } else{
                    System.out.println("====Spring===");
                    if(path.split("/").length > 2){
                        int index = path.indexOf("/", 1);
                        path = path.substring(0,index+1);
                    }
                    if(getServices.containsKey(path)){
                        System.out.println(path);
                        outputLine = handleGETRequest(path, requestURI);
                    }
                }
            } else if(httpMethod.equals("POST")) {
                if (uriStr.startsWith("/myspark") && mySparkPostServices.containsKey(path.replace("/myspark", ""))) {
                    String uriSpark = path.replace("/myspark", "");
                    outputLine = callService(mySparkPostServices.get(uriSpark), request);
                } else if (postServices.containsKey(path)) {
                    outputLine = handlePOSTRequest(path, requestBody);
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
     * Handles a GET request by invoking the appropriate web service method based on the provided path and request URI.
     * Constructs an HTTP response with the content type obtained from the web service annotation.
     * If the web service method expects parameters, extracts them from the request URI and invokes the method accordingly.
     *
     * @param path       The path extracted from the request URI.
     * @param requestURI The URI of the incoming GET request.
     * @return The HTTP response constructed based on the outcome of invoking the web service method.
     */
    private static String handleGETRequest(String path, URI requestURI) {
        String outputLine =  "";
        Method webService = getServices.get(path);
        String contentType = webService.getAnnotation(GetMapping.class).produces();
        outputLine = ResponseBuilder.httpOkServiceCall("GET", contentType);
        Parameter[] parameters = webService.getParameters();
        try {
            if(parameters.length != 0) {
                String query = "";
                if(parameters[0].isAnnotationPresent(PathVariable.class)){
                    System.out.println("PathVariable");
                    query = requestURI.getPath().split("/")[2];
                }else if(parameters[0].isAnnotationPresent(RequestParam.class)) {
                    System.out.println("RequestParam");
                    query = requestURI.getQuery().split("=")[1];
                }
                System.out.println("Query:" + query);
                outputLine += webService.invoke(null, query);
            }else {
                outputLine += webService.invoke(null);
            }
        } catch (Exception e) {
            outputLine = ResponseBuilder.httpError(requestURI);
            e.printStackTrace();
        }
        return outputLine;
    }

    /**
     * Handles a POST request by invoking the appropriate web service method based on the provided path and request body.
     * Constructs an HTTP response with the content type obtained from the web service annotation.
     * If the web service method expects a request body parameter, it invokes the method with the provided request body.
     *
     * @param path        The path extracted from the request URI.
     * @param requestBody The body of the incoming POST request.
     * @return The HTTP response constructed based on the outcome of invoking the web service method.
     */
    private static String handlePOSTRequest(String path, String requestBody) {
        String outputLine = "";
        Method webService = postServices.get(path);
        Parameter[] parameters = webService.getParameters();
        String contentType = webService.getAnnotation(PostMapping.class).produces();
        outputLine = ResponseBuilder.httpOkServiceCall("POST", contentType);
        if (parameters[0].isAnnotationPresent(RequestBody.class)) {
            try {
                outputLine += webService.invoke(null, requestBody);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return outputLine;
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

    /**
     * Sets the location where files are served from by the HTTP server.
     * @param newLocation The new location for serving files.
     */
    public static void setLocation(String newLocation){
        location = newLocation;
    }
    /**
     * Calls a service function to handle the incoming request.
     * @param service The function to handle the request.
     * @param request The incoming request.
     * @return The HTTP response generated by the service.
     * @throws IOException If an I/O error occurs during the processing of the request.
     */
    private static String callService(Function service, Request request) throws IOException {
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

    /**
     * Extracts key-value pairs from a query string in the URI.
     * @param queryString The query string from the URI.
     * @return A map containing the extracted key-value pairs.
     */
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

    /**
     * Recursively scans the specified directory for class files and loads them into memory.
     * @param directory The directory to scan for class files.
     * @return A list containing the loaded classes.
     * @throws IOException            If an I/O error occurs while scanning the directory.
     * @throws ClassNotFoundException If a class cannot be found or loaded.
     */
    public static List<Class<?>> getClasses(String directory) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        File folder = new File(directory);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(getClasses(file.getPath() + '\\'));
                } else if (file.getName().endsWith(".class")) {
                    String className = file.getName().substring(0, file.getName().length() - 6);
                    className = directory.substring(directory.indexOf("classes") + 8).replace(File.separator, ".")  + className;
                    Class<?> classInPath = loadClass(className, directory);
                    if (classInPath != null && classInPath.isAnnotationPresent(Component.class)) {
                        classes.add(classInPath);
                    }
                }
            }
        }
        return classes;
    }


    /**
     * Loads a class with the specified name from the given directory.
     * @param className The fully qualified name of the class to load.
     * @param directory The directory from which to load the class file.
     * @return The loaded class, or null if the class cannot be loaded.
     * @throws ClassNotFoundException If the class cannot be found or loaded.
     */
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

    /**
     * Loads component from the specified class.
     * @param classInPath The class from which to load component annotations.
     */
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

    /**
     * Registers a GET service with the specified path and function.
     * @param path The path of the GET service.
     * @param svc The function representing the GET service.
     * @throws Exception If an error occurs while registering the service.
     */
    public static void  get(String path, Function svc) throws Exception {
        mySparkGetServices.put(path,svc);
    }

    /**
     * Registers a POST service with the specified path and function.
     * @param path The path of the POST service.
     * @param svc The function representing the POST service.
     * @throws Exception If an error occurs while registering the service.
     */
    public static void  post(String path, Function svc) throws Exception {
        mySparkPostServices.put(path,svc);
    }

}

