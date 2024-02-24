package edu.escuelaing.arep.app.controller;

import edu.escuelaing.arep.app.annotations.*;
import edu.escuelaing.arep.app.annotations.Component;
import edu.escuelaing.arep.app.model.Product;
import edu.escuelaing.arep.app.service.ProductService;

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


    @GetMapping(value = "/products/{id}", produces = "application/json")
    public static String getProductsById(@RequestParam String id){
        return productService.getProductById(id).toString();
    }






}
