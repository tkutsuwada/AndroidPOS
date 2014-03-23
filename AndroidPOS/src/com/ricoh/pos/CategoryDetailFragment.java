package com.ricoh.pos;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ricoh.pos.data.Order;
import com.ricoh.pos.data.Product;
import com.ricoh.pos.model.ProductsManager;
import com.ricoh.pos.model.RegisterManager;

/**
 * A fragment representing a single Category detail screen. This fragment is
 * either contained in a {@link CategoryListActivity} in two-pane mode (on
 * tablets) or a {@link CategoryDetailActivity} on handsets.
 */
public class CategoryDetailFragment extends ListFragment {

	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	private RegisterManager registerManager;

	private String category;
	private ArrayList<Product> productList;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public CategoryDetailFragment() {
		this.registerManager = RegisterManager.getInstance();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		this.category = bundle.getString(CategoryDetailFragment.ARG_ITEM_ID);

		productList = ProductsManager.getInstance().getProductsInCategory(category);
		setListAdapter(new ListAdapter(getActivity()));
		
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
	}

	public class ListAdapter extends BaseAdapter {
		// private Context contextInAdapter;
		private LayoutInflater inflater;

		public ListAdapter(Context context) {
			// contextInAdapter = context;
			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return productList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.row, null);
			}

			Product product = productList.get(position);

			setImageView(convertView,product);
			setProductInformationView(convertView,product);
			setNumberOfOrderView(convertView, product);
			
			return convertView;
		}
		
		private void setImageView(View convertView, Product product){
			ImageView imageView = (ImageView) convertView.findViewById(R.id.photo);
			imageView.setLayoutParams(new LinearLayout.LayoutParams(120, 120));
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageView.setImageResource(getResourceID(product.getProductImagePath()));
		}
		
		private void setProductInformationView(View convertView, Product product){
			TextView textView = (TextView) convertView.findViewById(R.id.filename);
			textView.setPadding(10, 0, 0, 0);
			String productName = product.getName();
			if (productName == null || productName.length() == 0) {
				throw new NullPointerException("Product name is not valid");
			}
			textView.setText(productName);

			TextView priceView = (TextView) convertView.findViewById(R.id.price);
			priceView.setPadding(10, 0, 0, 0);
			priceView.setText(getString(R.string.price_label) + " " + String.valueOf(product.getPrice()) + getString(R.string.currency_india));
			
			TextView originalCostView = (TextView) convertView.findViewById(R.id.original_cost);
			originalCostView.setPadding(10, 0, 0, 0);
			originalCostView.setText( getString(R.string.original_cost_label) + " " + String.valueOf(product.getOriginalCost()) + getString(R.string.currency_india));
			
		}

		private void setNumberOfOrderView(View convertView, Product product){
			
			ProductEditText numberOfSalesText = (ProductEditText) convertView
					.findViewById(R.id.numberOfSales);
			numberOfSalesText.setProduct(product);
			numberOfSalesText.setInputType(InputType.TYPE_CLASS_NUMBER);
			numberOfSalesText.setImeOptions(EditorInfo.IME_ACTION_DONE);
			numberOfSalesText.addTextChangedListener(new NumberOfSalesWatcher(numberOfSalesText));
			
			ProductButton plusBtn = (ProductButton) convertView.findViewById(R.id.plusButton);
			plusBtn.setProduct(product);
			plusBtn.setProductEditText(numberOfSalesText);
			plusBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ProductButton button = (ProductButton) v;
					Product product = button.getProduct();
					registerManager.plusNumberOfOrder(product);
					int numberOfOrder = registerManager.getNumberOfOrder(product);
					ProductEditText editText = button.getProductEditText();
					editText.setText(String.valueOf(numberOfOrder));
				}
			});
			
			ProductButton minusBtn = (ProductButton) convertView.findViewById(R.id.minusButton);
			minusBtn.setProduct(product);
			minusBtn.setProductEditText(numberOfSalesText);
			minusBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ProductButton button = (ProductButton) v;
					Product product = button.getProduct();
					registerManager.minusNumberOfOrder(product);
					int numberOfOrder = registerManager.getNumberOfOrder(product);
					ProductEditText editText = button.getProductEditText();
					if (numberOfOrder == 0) {
						editText.getEditableText().clear();
					} else {
						editText.setText(String.valueOf(numberOfOrder));
					}
				}
			});
			
			Order order = registerManager.findOrderOfTheProduct(product);
			if (order == null || order.getNumberOfOrder() == 0) {
				numberOfSalesText.getEditableText().clear();
			} else {
				 int numberOfSales = order.getNumberOfOrder();
				 numberOfSalesText.setText(String.valueOf(numberOfSales));
			}
		}
		

		private int getResourceID(String fileName) {
			int resID = getResources().getIdentifier(fileName, "drawable", "com.ricoh.pos");
			return resID;
		}
	}

	public class NumberOfSalesWatcher implements TextWatcher {

		private ProductEditText productEditView;

		public NumberOfSalesWatcher(ProductEditText view) {
			this.productEditView = view;
		}

		@Override
		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {

			Product product = productEditView.getProduct();
			if (s.length() == 0) {
				registerManager.updateOrder(product, 0);
			} else {
				registerManager.updateOrder(product, Integer.parseInt(s.toString()));
			}
		}

	}

}
