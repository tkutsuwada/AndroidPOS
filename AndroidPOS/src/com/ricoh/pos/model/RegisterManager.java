package com.ricoh.pos.model;

import java.util.ArrayList;
import java.util.Date;

import android.util.Log;

import com.ricoh.pos.data.Order;
import com.ricoh.pos.data.OrderUpdateInfo;
import com.ricoh.pos.data.Product;
import com.ricoh.pos.data.SingleSalesRecord;

public class RegisterManager {
	
	private static RegisterManager instance;
	
	private ArrayList<Order> orderList;
	
	private ArrayList<UpdateOrderListener> listeners;
	
	private double discountValue;
	
	private String userAttribute;
	
	private RegisterManager(){
		orderList = new ArrayList<Order>();
		listeners = new ArrayList<UpdateOrderListener>();
	}
	
	public static RegisterManager getInstance(){
		if (instance == null) {
			instance = new RegisterManager();
		}
		
		return instance;
	}
	
	public void updateOrder(Product product, int num){
		Order orderOfTheProduct = findOrderOfTheProduct(product);
		
		if (num == 0) {
			if (orderOfTheProduct == null) {
				return;
			} else {
				orderList.remove(orderOfTheProduct);
			}
		} else if (orderOfTheProduct == null) {
			Order newOrder = new Order(product,num);
			orderList.add(newOrder);
		} else {
			orderOfTheProduct.setNumberOfOrder(num);
		}
		
		notifyUpdateOrder();
	}
	
	public void plusNumberOfOrder(Product product){
		Order orderOfTheProduct = findOrderOfTheProduct(product);
		
		if (orderOfTheProduct == null) {
			int numberOfFirstOrder = 1;
			Order newOrder = new Order(product,numberOfFirstOrder);
			orderList.add(newOrder);
		} else {
			orderOfTheProduct.plusNumberOfOrder();
		}
		
		notifyUpdateOrder();
	}
	
	public void minusNumberOfOrder(Product product) {
		Order orderOfTheProduct = findOrderOfTheProduct(product);
		
		if (orderOfTheProduct == null) {
			Order newOrder = new Order(product,0);
			orderList.add(newOrder);
		}else{
			orderOfTheProduct.minusNumberOfOrder();
			
			// 注文が0になったら削除
			if (orderOfTheProduct.getNumberOfOrder() == 0) {
				orderList.remove(orderOfTheProduct);
			}
		}
		
		notifyUpdateOrder();
	}
	
	private void notifyUpdateOrder(){
		
		if (listeners == null || listeners.isEmpty()) {
			throw new IllegalStateException("UpdateOrderListener is not resgistered");
		}
		
		for (UpdateOrderListener listener : listeners) {
			if (listener == null) {
				throw new IllegalStateException("UpdateOrderListener to register is null");
			}
			listener.notifyUpdateOrder(new OrderUpdateInfo(getOriginalTotalAmount()
					, discountValue
					, getTotalAmountAfterDiscount()));
		}
	}
	
	public double getOriginalTotalAmount(){
		double totalAmount = 0;
		for (Order order: orderList) {
			totalAmount += order.getTotalAmount();
		}
		return totalAmount;
	}
	
	public double getTotalAmountAfterDiscount(){
		return getOriginalTotalAmount() - discountValue;
	}
	
	public int getTotalNumberOfOrder() {
		int totalNumber = 0;
		for (Order order: orderList) {
			totalNumber += order.getNumberOfOrder();
		}
		return totalNumber;
	}
	
	public void clearAllOrders(){
		orderList = new ArrayList<Order>();
		discountValue = 0;
		userAttribute = null;
	}
	
	public Order findOrderOfTheProduct(Product product){
		for (Order order : orderList) {
			if ((order.getProductCategory().equals(product.getCategory()) && order.getProductCode().equals(product.getCode()))) {
				return order;
			}
		}
		// Not Found
		return null;
	}
	
	public int getNumberOfOrder(Product product) {
		Order order = findOrderOfTheProduct(product);
		if (order == null) {
			return 0;
		} else {
			return order.getNumberOfOrder();	
		}
	}
	
	public void setUpdateOrderListener(UpdateOrderListener listener){
		listeners.add(listener);
	}
	
	public void removeUpdateOrderListener(UpdateOrderListener listener){
		listeners.remove(listener);
	}
	
	public void clearUpdateOrderListener(){
		listeners.clear();
	}
	
	public void updateDiscountValue(double discountValue)
	{
		if (discountValue >= getOriginalTotalAmount()) {
			throw new IllegalArgumentException("discountValues is larger than totalAmount");
		}
		this.discountValue = discountValue;
		notifyUpdateOrder();
	}
	
	public void setUserAttribute(String attribute){
		if (attribute == null || attribute.length() == 0) {
			throw new IllegalArgumentException("User attribute is illegal");
		}
		this.userAttribute = attribute;
		Log.d("Attribute:", " " + userAttribute + " ");
	}
	
	public SingleSalesRecord getSingleSalesRecord(){
		SingleSalesRecord record = new SingleSalesRecord(new Date());
		record.setOrders(orderList);
		record.setDiscountValue(discountValue);
		record.setUserAttribute(userAttribute);
		return record;
	}
	
}
