package com.ricoh.pos.model;

import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

import com.ricoh.pos.data.Product;
import com.ricoh.pos.data.WomanShopContent;
import com.ricoh.pos.data.WomanShopDataDef;

public class ProductsManager {

	private HashMap<String, ArrayList<Product>> productsMap;
	private static ProductsManager instance;

	private ProductsManager() {
		this.productsMap = new HashMap<String, ArrayList<Product>>();
	}

	public static ProductsManager getInstance() {
		if (instance == null) {
			instance = new ProductsManager();
		}
		return instance;
	}

	public void updateProducts(String[] results) {
		for (String result : results) {
			String delims = "[:]+";
			String[] fieldData = result.split(delims);
			Product product = new Product(fieldData[WomanShopDataDef.PRODUCT_CODE.ordinal()],
					fieldData[WomanShopDataDef.PRODUCT_CATEGORY.ordinal()],
					fieldData[WomanShopDataDef.ITEM_CATEGORY.ordinal()]);

			Log.d("debug", fieldData[WomanShopDataDef.PRODUCT_CATEGORY.ordinal()] + ":"
					+ fieldData[WomanShopDataDef.ITEM_CATEGORY.ordinal()]);

			product.setOriginalCost(Double
					.parseDouble(fieldData[WomanShopDataDef.COST_TO_ENTREPRENEUR.ordinal()]));
			product.setPrice(Double.parseDouble(fieldData[WomanShopDataDef.SALE_PRICE.ordinal()]));
			product.setStock(Integer.parseInt(fieldData[WomanShopDataDef.QTY.ordinal()]));
			product.setProductImagePath(fieldData[WomanShopDataDef.PRODUCT_CODE.ordinal()]);
			addNewProductInCategory(fieldData[WomanShopDataDef.PRODUCT_CATEGORY.ordinal()], product);
		}
	}

	public void addNewProductInCategory(String category, Product product) {

		if (category == null || category.length() == 0) {
			throw new IllegalArgumentException("Invalid category");
		}

		if (product == null) {
			throw new IllegalArgumentException("Passing product object is null");
		}

		if (this.productsMap.containsKey(category)) {
			ArrayList<Product> produtcsInCategory = productsMap.get(category);
			for (Product registeredProduct : produtcsInCategory) {
				if (registeredProduct.equals(product)) {
					// TODO: Should update data
					return;
				}
			}
			produtcsInCategory.add(product);
		} else {
			ArrayList<Product> productsInCategory = new ArrayList<Product>();
			productsInCategory.add(product);
			productsMap.put(category, productsInCategory);
		}
	}

	public Product getProductByName(String category, String productName) {
		if (productName == null || productName.length() == 0) {
			throw new IllegalArgumentException("Invalid product name");
		}
		if (category == null || category.length() == 0) {
			throw new IllegalArgumentException("Invalid category name");
		}

		if (!productsMap.containsKey(category)) {
			throw new IllegalArgumentException("Passing category does not exist: " + category);
		}

		ArrayList<Product> productList = productsMap.get(category);
		for (Product product : productList) {
			if (product.getName().equals(productName)) {
				// Successfully found
				return product;
			}
		}
		// Not Found
		return null;
	}

	public Product getProductFromCode(String category, String productCode) {
		if (category == null || category.length() == 0) {
			throw new IllegalArgumentException("Invalid category name");
		}
		if (productCode == null || productCode.length() == 0) {
			throw new IllegalArgumentException("Invalid productCode name");
		}

		if (!productsMap.containsKey(category)) {
			throw new IllegalArgumentException("Passing category does not exist: " + category);
		}
		
		ArrayList<Product> productList = productsMap.get(category);
		for (Product product : productList) {
			if (product.getCode().equals(productCode)) {
				// Successfully found
				return product;
			}
		}
		// Not Found
		return null;
	}

	public ArrayList<Product> getProductsInCategory(String category) {

		if (category == null || category.length() == 0) {
			throw new IllegalArgumentException("Invalid category name");
		}

		if (category.equals(WomanShopContent.CATEGORY_ALL)) {
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

	public int getNumberOfProductsInCategory(String category) {

		if (category == null || category.length() == 0) {
			throw new IllegalArgumentException("Invalid category name");
		}

		if (!productsMap.containsKey(category)) {
			throw new IllegalArgumentException("Passing category does not exist: " + category);
		} else {
			return productsMap.get(category).size();
		}
	}

	public String[] getAllCategoryName() {
		int categoryCount = getCategoryCount();
		if (categoryCount <= 0) {
			throw new NegativeArraySizeException("Category does not exist: " + categoryCount);
		}

		String[] results = new String[getCategoryCount()];
		int i = 0;
		for (HashMap.Entry<String, ArrayList<Product>> keyValue : productsMap.entrySet()) {
			results[i++] = keyValue.getKey();
		}
		return results;
	}

	public int getCategoryCount() {
		return productsMap.entrySet().size();
	}
}
