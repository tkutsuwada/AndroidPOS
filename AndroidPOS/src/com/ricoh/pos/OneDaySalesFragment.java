package com.ricoh.pos;

import java.text.NumberFormat;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ricoh.pos.model.SalesCalenderManager;
import com.ricoh.pos.model.SalesRecordManager;

public class OneDaySalesFragment extends Fragment {

	@Override  
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_oneday_sales, container, false);
		
		Date date = SalesCalenderManager.getInstance().getSelectedSalesDate();

		TextView oneDaySalesView = (TextView) v.findViewById(R.id.oneDaySales);
		double  oneDaySales = SalesRecordManager.getInstance().getOneDayTotalSales(date);

		TextView oneDayRevenueView = (TextView) v.findViewById(R.id.oneDayRevenue);
		double  oneDayRevenue = SalesRecordManager.getInstance().getOneDayTotalRevenue(date);

		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(2);
		
		oneDaySalesView.setText(format.format(oneDaySales) + getString(R.string.currency_india));
		oneDayRevenueView.setText(format.format(oneDayRevenue) + getString(R.string.currency_india));
		
		return v;
	}

	
}
