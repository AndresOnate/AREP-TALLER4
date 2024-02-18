package edu.escuelaing.arep.app.model;

public class Product {
    private String id;
    private String name;
    private String description;
    private double price;

    // Constructor
    public Product(String id, String name, String description, double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Product(String JSONData) {
        System.out.println("JSONDATA ->" + JSONData);
        String[] parts = JSONData.split("&");
        for (String part : parts) {
            String[] keyValue = part.split("=");
            String key = keyValue[0];
            String value = keyValue[1];
            switch (key) {
                case "name":
                    this.name = value;
                    break;
                case "description":
                    this.description = value;
                    break;
                case "price":
                    this.price = Double.parseDouble(value);
                    break;
                default:
                    break;
            }
        }
    }
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "{"
                + "\"name\": \"" + name + "\","
                + "\"description\": \"" + description + "\","
                + "\"price\": " + price
                + "}";
    }



}
