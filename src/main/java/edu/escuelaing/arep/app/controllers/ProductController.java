package edu.escuelaing.arep.app.controllers;

import edu.escuelaing.arep.app.annotations.*;
import edu.escuelaing.arep.app.annotations.Component;
import edu.escuelaing.arep.app.model.Product;
import edu.escuelaing.arep.app.services.ProductService;

@Component
public class ProductController {
    private static ProductService productService = new ProductService();

    /**
     * This method performs a GET request to retrieve all products
     * and returns their JSON representation.
     * @return The JSON representation of all products.
     */
    @GetMapping(value = "/products", produces = "application/json")
    public static String getAllProducts(){
        return productService.getAllProducts().toString();
    }

    /**
     * This method saves a new product by parsing the JSON representation
     * of the product received in the request body and returns the JSON representation
     * of the saved product.
     *
     * @param newProduct The JSON representation of the new product to be saved.
     * @return The JSON representation of the saved product.
     */
    @PostMapping(value = "/products", produces = "application/json")
    public static String saveProduct(@RequestBody String newProduct){
        Product product = new Product(newProduct);
        productService.addProduct(product);
        return product.toString();
    }

    /**
     * This method retrieves a product  by its unique identifier (ID).
     * It takes the ID as a path variable,
     * and returns the JSON representation of the product.
     *
     * @param id The unique identifier of the product to be retrieved.
     * @return The JSON representation of the retrieved product.
     */
    @GetMapping(value = "/products/", produces = "application/json")
    public static String getProductsById(@PathVariable String id){
        return productService.getProductById(id).toString();
    }
}
