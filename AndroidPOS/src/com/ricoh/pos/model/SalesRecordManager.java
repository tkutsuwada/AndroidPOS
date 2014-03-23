package com.ricoh.pos.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.ricoh.pos.data.SingleSalesRecord;

public class SalesRecordManager {
	
	private static SalesRecordManager instance;
	public static final String SALES_DATE_KEY = "SalesDate";
	
	private SalesRecordManager(){
	}
	
	public static SalesRecordManager getInstance(){
		if (instance == null) {
			instance = new SalesRecordManager();
		}
		return instance;
	}
	
	public void storeSingleSalesRecord(SQLiteDatabase database, SingleSalesRecord record){
		if (record == null) {
			throw new IllegalArgumentException("The passing record is null");
		}
		
		WomanShopSalesIOManager.getInstance().saveSalesRecord(record);
		
		//TODO: for debug
		String[] results = WomanShopSalesIOManager.getInstance().searchAlldata();
		for (String result : results) {
			Log.d("debug", "Sales:" + result);
		}
	}
	
	public ArrayList<SingleSalesRecord> restoreSingleSalesRecordsOfTheDay(Date date){
		ArrayList<SingleSalesRecord> allSalesRecords = WomanShopSalesIOManager.getInstance().getSalesRecords();
		ArrayList<SingleSalesRecord> salesRecordsOfTheDay = new ArrayList<SingleSalesRecord>();
		
		for (SingleSalesRecord record : allSalesRecords) {
			if ( areSameDay(date,record.getSalesDate()) ) {
				salesRecordsOfTheDay.add(record);
			}
		}
		return salesRecordsOfTheDay;
	}
	
	public SingleSalesRecord getSingleSalesRecord(Date date){
		ArrayList<SingleSalesRecord> allSalesRecords = WomanShopSalesIOManager.getInstance().getSalesRecords();
		
		for (SingleSalesRecord record : allSalesRecords) {
			if (areSameSecond(record.getSalesDate(), date)) {
				return record;
			}
		}
		
		throw new IllegalStateException("Single sales record of the date is not found");
	}
	
	public double getOneDayTotalSales(Date date){
		ArrayList<SingleSalesRecord> salesRecords = restoreSingleSalesRecordsOfTheDay(date);
		double totalSales = 0;
		for (SingleSalesRecord record : salesRecords) {
			totalSales += record.getTotalSales();
		}
		return totalSales;
	}
	
	public double getOneDayTotalRevenue(Date date){
		ArrayList<SingleSalesRecord> salesRecords = restoreSingleSalesRecordsOfTheDay(date);
		double totalRevenue = 0;
		for (SingleSalesRecord record : salesRecords) {
			totalRevenue += record.getTotalRevenue();
		}
		return totalRevenue;
	}
	
	private boolean areSameDay(Date date1, Date date2){
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		
		if ( cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
			 cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
			 cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
			) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean areSameSecond(Date date1, Date date2){
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		
		if ( cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
			 cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
			 cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH) &&
			 cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY) &&
			 cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE) &&
			 cal1.get(Calendar.SECOND) == cal2.get(Calendar.SECOND)
			) {
			return true;
		} else {
			return false;
		}
	}
	
}
