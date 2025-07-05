package com.jos.yourtaxistandcustomer;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jos.yourtaxistandcustomer.util.NetworkHandler;
import com.jos.yourtaxistandcustomer.util.NetworkHandler.HTTP_METHOD;

public class HistoryDeail extends AppCompatActivity {

	AlertDialog.Builder alert;
	private ProgressDialog dialog;

	ListView list;
	ArrayList<String> offer_title = new ArrayList<String>();
	ArrayList<String> offer_desc = new ArrayList<String>();
	ArrayList<String> offer_valid = new ArrayList<String>();

	ArrayList<String> post_ = new ArrayList<String>();
	ArrayList<String> db_bid = new ArrayList<String>();

	String val1, val2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_deail);

		list = (ListView) findViewById(R.id.list);

		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				//val1 = post_.get(position).toString();
				val2 = db_bid.get(position).toString();

				dialog = ProgressDialog.show(HistoryDeail.this,
						"Accept request",
						"Processing your request. Please wait...", true);
				new HttpAsyncTask_()
						.execute("http://www.yourtaxistand.com/customer/update_bid_id/app");

			}
		});

		String id = getIntent().getStringExtra("id");

		dialog = ProgressDialog.show(this, "History detail request",
				"Processing your request. Please wait...", true);
		new HttpAsyncTask()
				.execute("http://www.yourtaxistand.com/customer/view_posted_requirements/"
						+ id + "/app");
	}

	
	public class HttpAsyncTask_ extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {// Build JSON string\\

			ContentValues urlArguments = new ContentValues();

			urlArguments.put("post_requirement_id",
					val1);
			urlArguments.put("bid_id", val2);

			return NetworkHandler.getStringFromURL(urls[0], urlArguments, HTTP_METHOD.POST);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {

			System.out.println("Result::::" + result);
			if (dialog.isShowing())
				dialog.dismiss();
			
		}
	}

	public class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			ContentValues urlArguments = new ContentValues();
			urlArguments.put("user_id", "1");
			return NetworkHandler.getStringFromURL(urls[0],urlArguments,HTTP_METHOD.POST);
		}
		@Override
		protected void onPostExecute(String result) {
			if (dialog.isShowing())
				dialog.dismiss();
			try {
				JSONObject res = new JSONObject(result);
				
				val1=res.getString("post_requirement_id");

				JSONArray arr = res.getJSONArray("bids_array");

				for (int i = 0; i < arr.length(); i++) {
					System.out.println("Driver ID: "
							+ arr.getJSONObject(i).getString("driver_id")
							+ " Vehicle name: "
							+ arr.getJSONObject(i).getString("vehicle_name"));

					offer_title.add("Driver ID: "
							+ arr.getJSONObject(i).getString("driver_id")
							+ " Vehicle name: "
							+ arr.getJSONObject(i).getString("vehicle_name"));

					offer_desc.add("Package fare: "
							+ arr.getJSONObject(i).getString("fixed_amount"));
					offer_valid.add("Additional charge per hour: "
							+ arr.getJSONObject(i).getString("additional_hour")
							+ " per km"
							+ arr.getJSONObject(i).getString("additional_km"));

					post_.add(arr.getJSONObject(i).getString(
							"post_requirement_id"));

					db_bid.add(arr.getJSONObject(i).getString("db_bidid"));
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ItemAdapter adapter = new ItemAdapter();
			list.setAdapter(adapter);

		}
	}

	public class ItemAdapter extends BaseAdapter {

		Context context;

		private class ViewHolder {
			public TextView text;
			public TextView text_desc;
			public TextView text_valid;

		}

		@Override
		public int getCount() {
			return offer_title.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			final ViewHolder holder;
			if (convertView == null) {
				view = getLayoutInflater().inflate(
						R.layout.item_list_image_rai, parent, false);
				holder = new ViewHolder();

				holder.text = (TextView) view.findViewById(R.id.text1);
				holder.text_desc = (TextView) view.findViewById(R.id.text2);
				holder.text_valid = (TextView) view.findViewById(R.id.text3);

				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}

			holder.text.setText(offer_title.get(position).toString());
			holder.text_desc.setText(offer_desc.get(position).toString());
			holder.text_valid.setText(offer_valid.get(position).toString());

			return view;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history_deail, menu);
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_history_deail,
					container, false);
			return rootView;

		}
	}

}
