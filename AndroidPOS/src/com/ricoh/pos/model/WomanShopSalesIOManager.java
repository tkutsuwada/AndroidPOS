package com.ricoh.pos.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.ricoh.pos.data.Order;
import com.ricoh.pos.data.SingleSalesRecord;
import com.ricoh.pos.data.WomanShopFormatter;
import com.ricoh.pos.data.WomanShopSalesDef;

public class WomanShopSalesIOManager implements IOManager {

	private static WomanShopSalesIOManager instance = null;
	private SQLiteDatabase salesDatabase;
	private static String DATABASE_NAME = "sales_dummy";
	private ArrayList<SingleSalesRecord> salesRecords;
	private static String csvStorageFolder = "/AndroidPOS";

	private WomanShopSalesIOManager() {
		this.salesRecords = new ArrayList<SingleSalesRecord>();
	}
	
	public static WomanShopSalesIOManager getInstance(){
		if (instance == null) {
			instance = new WomanShopSalesIOManager();
		}
		return instance;
	}
	
	public static void resetInstance(){
		Log.d("debug", "Reset Instance:" + "WomanShopSalesIOManager");
		instance = null;
	}

	@Override
	public BufferedReader importCSVfromAssets(AssetManager assetManager) {
		BufferedReader bufferReader = null;
		try {
			// TODO: Should import & export data
			bufferReader = new BufferedReader(new InputStreamReader(assetManager.open(DATABASE_NAME
					+ ".csv")));
		} catch (IOException e) {
			Log.d("debug", "" + e + "");
		}
		return bufferReader;
	}

	@Override
	public void insertRecords(BufferedReader bufferReader) {

		ContentValues contentValue = new ContentValues();
		try {
			readFieldName(bufferReader);

			String record;
			while ((record = bufferReader.readLine()) != null) {
				String[] fieldValues = record.split(",");
				Log.d("debug", "Product Code" + fieldValues[0]);

				int i = 0;
				for (WomanShopSalesDef field : WomanShopSalesDef.values()) {
					contentValue.put(field.name(), fieldValues[i++]);
				}

				salesDatabase.insertWithOnConflict(DATABASE_NAME, null, contentValue,
						SQLiteDatabase.CONFLICT_REPLACE);
			}
		} catch (IOException e) {
			Log.d("debug", "" + e + "");
		}
	}

	@Override
	public String[] searchAlldata() {
		Cursor cursor = null;

		try {
			cursor = salesDatabase.query(
					DATABASE_NAME,
					new String[] { WomanShopSalesDef.PRODUCT_CODE.name(),
							WomanShopSalesDef.PRODUCT_CATEGORY.name(),
							WomanShopSalesDef.ITEM_CATEGORY.name(),
							WomanShopSalesDef.QTY.name(),
							WomanShopSalesDef.SALE_PRICE.name(),
							WomanShopSalesDef.TOTAL_SALE_PRICE.name(),
							WomanShopSalesDef.DISCOUNT.name(),
							WomanShopSalesDef.DATE.name() }, 
							null, null, null, null, null);
			String[] results = new String[cursor.getCount()];
			Log.d("debug", "count:" + cursor.getCount());
			for (int i = 0; i < cursor.getCount(); i++) {
				results[i] = readCursor(cursor);
			}
			return results;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	// TODO: Temporary function
	@Override
	public String searchByCode(String code) {
		Cursor cursor = null;
		try {
			cursor = salesDatabase.query(
					DATABASE_NAME, 
					new String[] { WomanShopSalesDef.PRODUCT_CODE.name(),
							WomanShopSalesDef.PRODUCT_CATEGORY.name(),
							WomanShopSalesDef.ITEM_CATEGORY.name(),
							WomanShopSalesDef.QTY.name(),
							WomanShopSalesDef.SALE_PRICE.name(),
							WomanShopSalesDef.TOTAL_SALE_PRICE.name(),
							WomanShopSalesDef.DISCOUNT.name(),
							WomanShopSalesDef.DATE.name() }, 
							WomanShopSalesDef.PRODUCT_CODE.name() + " = ?", 
							new String[] { code }, null, null, null);
			return readCursor(cursor);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	public void saveSalesRecord(SingleSalesRecord record){
		salesRecords.add(record);
		
		ArrayList<Order> orders = record.getAllOrders();
		for (Order order : orders) {
			String salesRecord = order.getProductCode() + "," + order.getProductCategory() + ","
					+ order.getProductName() + "," + order.getNumberOfOrder() + ","
					+ order.getProductPrice() + "," + order.getTotalAmount() + ","
					+ record.getDiscountValue() + "," + WomanShopFormatter.formatDate(record.getSalesDate());
			insertSingleRecord(salesRecord);
		}
	}
	
	public void deleteSingleSalesRecordRelatedTo(String date) {
		salesDatabase.delete(DATABASE_NAME, WomanShopSalesDef.DATE.name() + "='" + date + "'", null);
	}
	
	// TODO: Add this function to interface
	public void setDatabase(SQLiteDatabase database) {
		if (salesDatabase == null) {
			Log.d("debug", "Database set:" + DATABASE_NAME);
			salesDatabase = database;
		} else if (!salesDatabase.isOpen()) {
			Log.d("debug", "Database not open:" + DATABASE_NAME);
		}
	}
	
	// TODO: Add this function to interface
	public void closeDatabase() {
		if (salesDatabase != null) {
			Log.d("debug", "Database closed:" + DATABASE_NAME);
			salesDatabase.close();
		}
	}
	
	public void exportCSV(Context context) {
		Cursor cursor = null;

		try {
			cursor = salesDatabase.query(
					DATABASE_NAME,
					new String[] { WomanShopSalesDef.PRODUCT_CODE.name(),
							WomanShopSalesDef.PRODUCT_CATEGORY.name(),
							WomanShopSalesDef.ITEM_CATEGORY.name(),
							WomanShopSalesDef.QTY.name(),
							WomanShopSalesDef.SALE_PRICE.name(),
							WomanShopSalesDef.TOTAL_SALE_PRICE.name(),
							WomanShopSalesDef.DISCOUNT.name(),
							WomanShopSalesDef.DATE.name() }, 
							null, null, null, null, null);
			String[] results = new String[cursor.getCount()];
			Log.d("debug", "sales count:" + cursor.getCount());
			for (int i = 0; i < cursor.getCount(); i++) {
				results[i] = readCursor(cursor);
			}
			writeSalesData(results, context);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	public ArrayList<SingleSalesRecord> getSalesRecords(){
		return salesRecords;
	}

	private void readFieldName(BufferedReader bufferReader) throws IOException {
		String record = bufferReader.readLine();

		// TODO: Show field name for debug
		String[] fieldNames = record.split(",");
		for (String fieldName : fieldNames) {
			Log.d("debug", fieldName);
		}
	}

	private String readCursor(Cursor cursor) {
		String result = "";

		int indexProductCode = cursor.getColumnIndex(WomanShopSalesDef.PRODUCT_CODE.name());
		int indexProductCategory = cursor.getColumnIndex(WomanShopSalesDef.PRODUCT_CATEGORY.name());
		int indexItemCategory = cursor.getColumnIndex(WomanShopSalesDef.ITEM_CATEGORY.name());
		int indexQTY = cursor.getColumnIndex(WomanShopSalesDef.QTY.name());
		int indexSalePrice = cursor.getColumnIndex(WomanShopSalesDef.SALE_PRICE.name());
		int indexTotalSalePrice = cursor.getColumnIndex(WomanShopSalesDef.TOTAL_SALE_PRICE.name());
		int indexDiscount = cursor.getColumnIndex(WomanShopSalesDef.DISCOUNT.name());
		int indexDate = cursor.getColumnIndex(WomanShopSalesDef.DATE.name());

		if (cursor.moveToNext()) {
			String productCode = cursor.getString(indexProductCode);
			String productCategory = cursor.getString(indexProductCategory);
			String itemCategory = cursor.getString(indexItemCategory);
			int qty = cursor.getInt(indexQTY);
			double salePrice = cursor.getDouble(indexSalePrice);
			double totalSalePrice = cursor.getDouble(indexTotalSalePrice);
			double discount = cursor.getDouble(indexDiscount);
			String date = cursor.getString(indexDate);

			result += productCode + "," + productCategory + "," + itemCategory + "," + qty + ","
					+ salePrice + "," + totalSalePrice + "," + discount + "," + date + "\n";
		}
		return result;
	}
	
	private void insertSingleRecord(String record) {
		
		ContentValues contentValue = new ContentValues();

		String[] fieldValues = record.split(",");
		Log.d("debug", "Product Code" + fieldValues[0]);

		int i = 0;
		for (WomanShopSalesDef field : WomanShopSalesDef.values()) {
			contentValue.put(field.name(), fieldValues[i++]);
		}

		salesDatabase.insertWithOnConflict(DATABASE_NAME, null, contentValue,
				SQLiteDatabase.CONFLICT_REPLACE);

	}
	
	private void writeSalesData(String[] salesData, Context context) {
		String csvStoragePath = getCSVStoragePath();
		File csvStorage = new File(csvStoragePath);
		if (!csvStorage.exists()) {
			Log.d("debug", "make directory:" + csvStoragePath);
			csvStorage.mkdir();
		}
		File salesDataCSV = new File(csvStoragePath + "/sales_dummy.csv");
		FileWriter filewriter = null;
		try {
			filewriter = new FileWriter(salesDataCSV);
			for (String singleSalesData : salesData) {
				Log.d("debug", "write csv:" + singleSalesData);
				filewriter.write(singleSalesData);
			}
		} catch (IOException e) {
			System.out.println(e);
		} finally {
			if (filewriter != null) {
				try {
					filewriter.close();
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		}
	}

	private String getCSVStoragePath() {
		File exterlStorage = Environment.getExternalStorageDirectory();
		Log.d("debug", "Environment External:" + exterlStorage.getAbsolutePath());
		return exterlStorage.getAbsolutePath() + csvStorageFolder;
	}
}
