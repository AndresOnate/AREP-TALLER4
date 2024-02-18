package edu.escuelaing.arep.app.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;


/**
 * The `APIController` class is responsible for handling HTTP requests related to movie information.
 * It interacts with the Movies API to retrieve data and provides responses
 * @Autor Andrés Oñate.
 */
public class APIController implements MovieAPI {
    /**
     * The user agent string to be used in HTTP requests to the Movies API.
     */
    private static final String USER_AGENT = "Mozilla/5.0";
    /**
     * The base URL for making requests to the Movies API.
     */
    private static final String GET_URL = "https://www.omdbapi.com/?apikey=4e3f8718&t=";

    /**
     * A cache to store already retrieved movie data, reducing the number of requests to the Movies API.
     */
    private static ConcurrentHashMap<String,String> cache = new ConcurrentHashMap<>();
    public APIController(){}

    /**
     * Connects to the Movies API to retrieve information about a given movie.
     *
     * This method initiates a connection to the Movies API OMDb using a GET request with the specified movie title as a parameter.
     * It checks if the movie information is already present in a cache, and if found, retrieves it directly from the cache.
     * If the information is not in the cache, it performs a GET request to the Movies API and processes the response.
     *
     * @param movie The title of the movie to search for.
     * @return A JSON string containing information about the movie if found, or null if the movie is not found.
     * @throws IOException If an I/O error occurs while connecting to the API.
     */
    public  String connectToMoviesAPI(String movie) throws IOException {
        String movieJSON = "{\"Response\":\"False\",\"Error\":\"Movie not found!\"}";
        if(cache.containsKey(movie)){
            System.out.println(movie + " se encuentra en Caché");
            movieJSON = cache.get(movie);
        }else{
            URL obj = new URL(GET_URL + movie);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            //The following invocation perform the connection implicitly before getting the code
            int responseCode = connection.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                // print result
                System.out.println(response.toString());
                if(!response.toString().equals("{\"Response\":\"False\",\"Error\":\"Movie not found!\"}")){
                    cache.put(movie, response.toString());
                    movieJSON = response.toString();
                }
            } else {
                System.out.println("GET request not worked");
            }
            System.out.println("GET DONE");
        }
        return movieJSON;
    }

    /**
     * Returns the cache used to store responses from movie API queries.
     * The cache is a ConcurrentHashMap data structure that stores key-value pairs,
     * where the key is the movie title and the value is the corresponding JSON response
     * obtained from the movie API.
     *
     * @return The cache used to store responses from movie API queries.
     */
    public static ConcurrentHashMap<String, String> getCache() {
        return cache;
    }
}
