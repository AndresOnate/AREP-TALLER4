package edu.escuelaing.arep.app.model;


import java.net.URI;

/**
 * The `HTMLBuilder` class provides methods for generating HTML responses
 * that can be sent as part of HTTP responses in the context of handling movie information.
 */
public class ResponseBuilder {

    private static String responseType = "text/html";

    /**
     * Generates an HTTP response for a 400 Not Found
     *
     * @param title The title of the movie that was not found.
     * @return An HTTP response string for a 400 Not Found error.
     */
    public static String httpMovieError(String title) {
        String outputLine = "HTTP/1.1 400 Not Found\r\n"
                + "Content-Type:application/json\r\n"
                + "\r\n"
                + "{ \"Not Found\": \"" + title + " movie not found\"}";
        return outputLine;
    }

    /**
     * Gets the content type header value based on the file extension.
     * @param extension The file extension for which to determine the content type.
     * @return The content type header value corresponding to the file extension.
     */
    public static  String getContentType(String extension){
        String contentType;
        switch (extension.toLowerCase()) {
            case "js":
                contentType = "text/javascript\r\n";
                break;
            case "html":
                contentType = "text/html\r\n";
                break;
            case "css":
                contentType = "text/css\r\n";
                break;
            case "png":
                contentType = "image/png\r\n";
                break;
            case "jpg":
                contentType = "image/jpg\r\n";
                break;
            default:
                contentType = "text/plain\r\n";
                break;
        }
        return  contentType;
    }

    /**
     * Generates an HTTP response header with a status of 200 OK and the specified content type.
     * @return The HTTP response header string with the status and content type.
     */
    public static String httpOkServiceCall(Request request){
        String returnCode = "200 OK";
        if(request.getHTTPVerb().equals("POST")){
            returnCode = "201 Created";
        }
        String output = "HTTP/1.1 " +  returnCode + "\r\n"
                + "Content-Type:" + responseType + "\r\n"
                + "\r\n";
        if(responseType != "text/html"){
            responseType = "text/html";
        }
        return output;
    }

    public static String httpOkHeader(String contentType){
        return   "HTTP/1.1 200 OK\r\n"
                + "Content-Type:" + contentType
                + "\r\n";
    }

    /**
     * Generates an HTTP response for the case where a requested resource is not found (status code 400).
     * @return The HTTP response string with the status code 400 and content type set to application/json.
     */
    public static String httpError(URI requestURI) {
        String outputLine = "HTTP/1.1 400 Not Found\r\n"
                + "Content-Type:text/html\r\n"
                + "\r\n"
                + "<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>aonateSpark</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "<h1>Error: " + requestURI.getPath() + " not found"+ "</h1>" +
                "    </body>\n" +
                "</html>";;
        return outputLine;
    }

    /**
     * Generates an HTTP response for successful movie data retrieval with the provided JSON string.
     *
     * @param movieJSON The JSON string containing information about the movie.
     * @return An HTTP response string for successful movie data retrieval.
     */
    public static String httpMovieData(String movieJSON){
        String outputLine = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: application/json\r\n"
                + "\r\n"
                + movieJSON;
        return outputLine;
    }

    public void setResponseType(String newType){
        responseType = newType;
    }
}
