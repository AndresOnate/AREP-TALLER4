package edu.escuelaing.arep.app.services;

import edu.escuelaing.arep.app.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private List<Product> productList;

    /**
     * Constructs a new ProductService with an empty product list.
     */
    public ProductService() {
        this.productList = new ArrayList<>();
        Product product = new Product("1", "Orange", "Fruit", 1200);
        addProduct(product);
    }

    /**
     * Adds a product to the product list.
     * @param product The product to add.
     */
    public void addProduct(Product product) {
        productList.add(product);
    }

    /**
     * Retrieves all products from the product list.
     * @return A list containing all products.
     */
    public List<Product> getAllProducts() {
        return productList;
    }

    /**
     * Retrieves a product from the product list by its ID.
     * @param id The ID of the product to retrieve.
     * @return The product with the specified ID, or null if no such product exists.
     */
    public Product getProductById(String id){
        for(Product product: productList){
            if(product.getId().equals(id)){
                return product;
            }
        }
        return null;
    }

}