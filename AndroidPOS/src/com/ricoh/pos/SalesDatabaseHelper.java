package com.ricoh.pos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ricoh.pos.data.WomanShopSalesDef;

public class SalesDatabaseHelper extends SQLiteOpenHelper {

	public SalesDatabaseHelper(Context context) {
		super(context, "sales_dummy.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("Create Table sales_dummy (" + "_id Integer Primary Key, "
				+ WomanShopSalesDef.PRODUCT_CODE.name() + " Text, "
				+ WomanShopSalesDef.PRODUCT_CATEGORY.name() + " Text, "
				+ WomanShopSalesDef.ITEM_CATEGORY.name() + " Text, "
				+ WomanShopSalesDef.QTY.name() + " Integer, "
				+ WomanShopSalesDef.SALE_PRICE.name() + " Real, "
				+ WomanShopSalesDef.TOTAL_SALE_PRICE.name() + " Real, "
				+ WomanShopSalesDef.DISCOUNT.name() + " Real, "
				+ WomanShopSalesDef.DATE.name() + " Text)");
		Log.d("debug", "Sales database onCreate");
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
	}

}