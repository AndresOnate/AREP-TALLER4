package edu.escuelaing.arep.app.controllers;

import edu.escuelaing.arep.app.annotations.*;
import edu.escuelaing.arep.app.annotations.Component;
import edu.escuelaing.arep.app.model.Product;
import edu.escuelaing.arep.app.services.ProductService;

@Component
public class ProductController {

    private static ProductService productService = new ProductService();


    @GetMapping(value = "/products", produces = "application/json")
    public static String getAllProducts(){
        return productService.getAllProducts().toString();
    }

    @PostMapping(value = "/products", produces = "application/json")
    public static String saveProduct(@RequestBody String newProduct){
        Product product = new Product(newProduct);
        productService.addProduct(product);
        return product.toString();
    }

    @GetMapping(value = "/products/", produces = "application/json")
    public static String getProductsById(@PathVariable String id){
        return productService.getProductById(id).toString();
    }






}