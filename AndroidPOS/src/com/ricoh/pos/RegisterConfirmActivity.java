package com.ricoh.pos;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.ricoh.pos.data.SingleSalesRecord;
import com.ricoh.pos.model.RegisterManager;
import com.ricoh.pos.model.SalesRecordManager;

public class RegisterConfirmActivity extends FragmentActivity
implements RegisterConfirmFragment.OnButtonClickListener{
	
	private SalesDatabaseHelper salesDatabaseHelper;
	private static SQLiteDatabase salesDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_confirm);
		if (findViewById(R.id.order_list_container) != null) {
			// add OrderListFragment
			OrderListFragment fragment = new OrderListFragment();
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.order_list_container, fragment).commit();

			// add RegisterConfirmFragment
			RegisterConfirmFragment registerConfirmFragment = new RegisterConfirmFragment();
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.register_confirm_container, registerConfirmFragment).commit();
		}
		
		salesDatabaseHelper = new SalesDatabaseHelper(this);
		salesDatabase = salesDatabaseHelper.getWritableDatabase();
	}

	@Override
	public void onOkClicked() {
		// Save this sales record
		SingleSalesRecord record = RegisterManager.getInstance().getSingleSalesRecord();
		SalesRecordManager.getInstance().storeSingleSalesRecord(salesDatabase, record);
		
		// Clear this record
		RegisterManager.getInstance().clearAllOrders();
		
		// Go to the CategoryListActivity
		Intent intent = new Intent(this, CategoryListActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	@Override
	public void onCancelClicked() {
		RegisterManager.getInstance().updateDiscountValue(0);
		finish();
	}

	@Override
	public void onPriceDownClicked() {
		showPriceDownDialog();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		salesDatabase.close();
		salesDatabaseHelper.close();
		Log.d("debug", "Exit RegisterConfirmActivity onDestroy");
	}

	private void showPriceDownDialog()
	{
		PriceDownDialog dialog = new PriceDownDialog();
		dialog.show(this);
	}
}
