package com.ricoh.pos;

import java.io.BufferedReader;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.ricoh.pos.model.ProductsManager;
import com.ricoh.pos.model.WomanShopIOManager;
import com.ricoh.pos.model.WomanShopSalesIOManager;

public class DataSyncTask extends AsyncTask<String, Void, AsyncTaskResult<String>> {
	final String TAG = "DataSyncTask";
	DataSyncTaskCallback callback;
	Context context;
	ProgressDialog progressDialog;
	WomanShopIOManager womanShopIOManager;
	ProductsManager productsManager;

	public DataSyncTask(Context context, DataSyncTaskCallback callback,
			WomanShopIOManager womanShopIOManager) {
		this.callback = callback;
		this.context = context;
		this.womanShopIOManager = womanShopIOManager;
		this.productsManager = ProductsManager.getInstance();
	}

	@Override
	protected void onPreExecute() {
		Log.d(TAG, "onPreExecute");
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage(context.getString(R.string.dialog_data_syncro_message));
			progressDialog.show();
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setCancelable(false);
		}
	}

	@Override
	protected AsyncTaskResult<String> doInBackground(String... params) {
		Log.d(TAG, "doInBackground");
		try {
			Log.d("debug", "SyncButton click");

			//AssetManager assetManager = context.getResources().getAssets();
			//BufferedReader bufferReader = womanShopIOManager.importCSVfromAssets(assetManager);
			BufferedReader bufferReader = womanShopIOManager.importCSVfromSD();
			if (bufferReader == null) {
				Log.d("debug", "File not found");
				return AsyncTaskResult.createErrorResult(R.string.sd_import_error);
			}
			womanShopIOManager.insertRecords(bufferReader);

			String[] results = womanShopIOManager.searchAlldata();
			for (String result : results) {
				Log.d("debug", result);
			}
			productsManager.updateProducts(results);
			WomanShopSalesIOManager.getInstance().exportCSV(context);
		} catch (Exception e) {
			// TODO: Should separate exception(Import, Export, at least)
			e.printStackTrace();
			return AsyncTaskResult.createErrorResult(R.string.sd_import_error);
		}
		// The argument is null because nothing to notify on success
		return AsyncTaskResult.createNormalResult(null);
	}

	@Override
	protected void onPostExecute(AsyncTaskResult<String> result) {
		Log.d(TAG, "onPostExecute");
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
			
			if (result.isError()) {
				callback.onFailedSyncData(result.getResourceId());
			} else {
				callback.onSuccessSyncData();
			}
		}
	}
}