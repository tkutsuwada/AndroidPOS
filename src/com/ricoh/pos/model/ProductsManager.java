package com.ricoh.pos.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.ricoh.pos.data.Product;

public class ProductsManager {
	
	private HashMap<String,ArrayList<Product>> productsMap;
	
	private static ProductsManager instance;
	
	private ProductsManager(){
		this.productsMap = new HashMap<String,ArrayList<Product>>();
		
		// TODO: import product data from DataBase
		createDummyProducts();
	}
	
	private void createDummyProducts(){
		
		// Food
		String foodCategory = "Food";
		for (int i=0; i<10; i++) {
			Product product = new Product(foodCategory, "food" + i);
			product.setProductImagePath("sample0" + (21 + i));
			addNewProductInCategory(foodCategory, product);
		}

		// Health
		String healthCategory = "Health";
		for (int i=0; i<7 ; i++) {
			Product product = new Product(healthCategory, "health" + i);
			product.setProductImagePath("sample0" + (31 + i));
			addNewProductInCategory(healthCategory, product);
		}
	}

	public static ProductsManager getInstance() {
		
		if (instance == null){
			instance = new ProductsManager();
		}
		
		return instance;
	}
	
	public void addNewProductInCategory(String category, Product product){
		
		if (category == null || category.length() == 0) {
			throw new IllegalArgumentException("Invalid category");
		}
		
		if (product == null) {
			throw new IllegalArgumentException("Passing product object is null");
		}
		
		if (this.productsMap.containsKey(category)){
			ArrayList<Product> produtcsInCategory = productsMap.get(category);
			for (Product registeredProduct : produtcsInCategory) {
				if(registeredProduct.getName().equals(product.getName())){
					throw new IllegalArgumentException("The passing product has already been registered");
				}
			}
			produtcsInCategory.add(product);
		} else {
			ArrayList<Product> productsInCategory = new ArrayList<Product>();
			productsInCategory.add(product);
			productsMap.put(category, productsInCategory);
		}
	}
	
	public Product getProductByName(String category, String productName){
		
		if (productName == null || productName.length() == 0) {
			throw new IllegalArgumentException("Invalid product name");
		}
		
		if(category == null || category.length() == 0){
			throw new IllegalArgumentException("Invalid category name");
		}
		
		if (!productsMap.containsKey(category)) {
			throw new IllegalArgumentException("Passing category does not exist: " + category);
		}
		
		ArrayList<Product> productList = productsMap.get(category);
		for (Product product : productList){
			if (product.getName().equals(productName)) {
				// Successfully found
				return product;
			}
		}
		
		// Not Found
		return null;
	}
	
	public Product getProductFromId(String category, int productId){
		
		if(category == null || category.length() == 0){
			throw new IllegalArgumentException("Invalid category name");
		}
		
		if (!productsMap.containsKey(category)) {
			throw new IllegalArgumentException("Passing category does not exist: " + category);
		} else {
			return productsMap.get(category).get(productId);
		}
	}
	
	public ArrayList<Product> getProductsInCategory(String category) {
		
		if(category == null || category.length() == 0){
			throw new IllegalArgumentException("Invalid category name");
		}
		
		if (category.equals("ALL")) {
			ArrayList<Product> allProducts = new ArrayList<Product>();
			for (String key : productsMap.keySet()) {
				allProducts.addAll(productsMap.get(key));
			}
			return allProducts;
		}
		
		if (!productsMap.containsKey(category)) {
			throw new IllegalArgumentException("Passing category does not exist: " + category);
		} else {
			return productsMap.get(category);
		}
	}
	
	public int getNumberOfProductsInCategory(String category){
		
		if(category == null || category.length() == 0){
			throw new IllegalArgumentException("Invalid category name");
		}
		
		if (!productsMap.containsKey(category)) {
			throw new IllegalArgumentException("Passing category does not exist: " + category);
		} else {
			return productsMap.get(category).size();
		}
	}

}
