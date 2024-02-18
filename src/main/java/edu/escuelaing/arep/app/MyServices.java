package edu.escuelaing.arep.app;

import edu.escuelaing.arep.app.controller.APIController;
import edu.escuelaing.arep.app.model.Product;
import edu.escuelaing.arep.app.model.ResponseBuilder;
import edu.escuelaing.arep.app.service.ProductService;

import java.net.URI;
import java.util.Map;

import static edu.escuelaing.arep.app.MySpark.get;
import static edu.escuelaing.arep.app.MySpark.post;

public class MyServices {

    public static void main(String[] args) throws Exception {

        // root is 'src/main/resources', so put files in 'src/main/resources/public
        MySpark.setLocation("/public");

        ProductService productService = new ProductService();

        get("/hi", (req, res) -> { return "El query es:" + req.getUri().getPath();});

        get("/users", (req, res) -> {
            Map<String, String> params = MySpark.getParamsFromURI(req.getUri().getQuery());
            String name = params.get("name");
            String topic = params.get("topic");
            return "<h1>Hola " + name + ", estas buscando sobre " + topic + "</h1>" ;});

        get("/movies", (req, res) -> {
            res.setResponseType("application/json");
            URI requestURI = req.getUri();
            String title = requestURI.getQuery().split("=")[1].toLowerCase();
            APIController apiMovies = new APIController();
            return  apiMovies.connectToMoviesAPI(title);
        });

        post("/products", (req, res) -> {
            res.setResponseType("application/json");
            Product product = new Product(req.getBody());
            productService.addProduct(product);
            return product.toString();
        });

        get("/products", (req, res) -> {
            res.setResponseType("application/json");
            return productService.getAllProducts().toString();
        });

        MySpark.getInstance().runServer(args);
    }
}