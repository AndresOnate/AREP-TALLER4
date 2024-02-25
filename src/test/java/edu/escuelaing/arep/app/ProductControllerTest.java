package edu.escuelaing.arep.app;

import edu.escuelaing.arep.app.model.Product;
import org.junit.Test;
import static org.junit.Assert.*;

public class ProductControllerTest {

    @Test
    public void testProductConstructor() {
        Product product = new Product("1", "Product1", "Description1", 10.99);
        assertEquals("1", product.getId());
        assertEquals("Product1", product.getName());
        assertEquals("Description1", product.getDescription());
        assertEquals(10.99, product.getPrice(), 0.001);
    }

    @Test
    public void testProductJSONConstructor() {
        String JSONData = "id=2&name=Product2&description=Description2&price=20.99";
        Product product = new Product(JSONData);
        assertEquals("2", product.getId());
        assertEquals("Product2", product.getName());
        assertEquals("Description2", product.getDescription());
        assertEquals(20.99, product.getPrice(), 0.001);
    }

    @Test
    public void testToString() {
        Product product = new Product("3", "Product3", "Description3", 30.99);
        String expectedJSON = "{\"id\": \"3\",\"name\": \"Product3\",\"description\": \"Description3\",\"price\": 30.99}";
        assertEquals(expectedJSON, product.toString());
    }
}

