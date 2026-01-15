package model;

import java.util.HashMap;
import java.util.Map;

// Represents a product model (item) sold in the store.
// Tracks the price and the stock quantity available in each outlet.
public class Model {
    // Attributes
    private String modelId;
    private String modelPrice;
    private Map<String, Integer> stockInOutlet = new HashMap<>();

    public Model(String modelId, String modelPrice) {
        this.modelId = modelId;
        this.modelPrice = modelPrice;
    }

    // Add stock for an outlet
    public void setStock(String outletCode, int quantity) {
        stockInOutlet.put(outletCode, quantity);
    }

    // Get stock for an outlet
    public int getStock(String outletCode) {
        return stockInOutlet.getOrDefault(outletCode, 0);
    }


    // Get all stock data
    public Map<String, Integer> getAllStock() {
        return stockInOutlet;
    }

    public String getModelId() {
        return this.modelId;
    }

    public String getModelPrice() {
        return this.modelPrice;
    }
}
