package edu.escuelaing.arep.app.service;

import edu.escuelaing.arep.app.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private List<Product> productList;

    public ProductService() {
        this.productList = new ArrayList<>();
        Product product = new Product("1", "Orange", "Fruit", 1200);
        addProduct(product);
    }

    public void addProduct(Product product) {
        productList.add(product);
    }

    public List<Product> getAllProducts() {
        return productList;
    }

}