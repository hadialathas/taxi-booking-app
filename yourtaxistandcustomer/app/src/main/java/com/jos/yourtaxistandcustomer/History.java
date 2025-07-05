package com.jos.yourtaxistandcustomer;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

public class History extends ActionBarActivity {

	AlertDialog.Builder alert;
	private ProgressDialog dialog;

	ListView list;
	ArrayList<String> offer_title = new ArrayList<String>();
	ArrayList<String> offer_desc = new ArrayList<String>();
	ArrayList<String> offer_valid = new ArrayList<String>();
	ArrayList<String> id_ = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_history);

		list = (ListView) findViewById(R.id.list);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent i=new Intent(History.this, HistoryDeail.class);
				i.putExtra("id",History.this.id_.get(position).toString());
				startActivity(i);
				
			}
		});

		dialog = ProgressDialog.show(History.this, "Login request",
				"Processing your request. Please wait...", true);
		new HttpAsyncTask()
				.execute("http://www.yourtaxistand.com/customer/posted_requirements/app");

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

	public class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {// Build JSON string\\
			ContentValues urlArguments = new ContentValues();
			urlArguments.put("user_id", "1");
			return NetworkHandler.getStringFromURL(urls[0], urlArguments, HTTP_METHOD.POST);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {

			System.out.println("Result::::" + result);
			if (dialog.isShowing())
				dialog.dismiss();
			try {
				JSONArray res = new JSONArray(result);

				for (int i = 0; i < res.length(); i++) {
					System.out.println(res.getJSONObject(i).getString(
							"pickup_point"));
					offer_title.add(res.getJSONObject(i).getString(
							"pickup_point")
							+ " to "
							+ res.getJSONObject(i).getString("destination"));
					offer_desc.add(res.getJSONObject(i).getString("date_from"));
					offer_valid
							.add(res.getJSONObject(i).getString("bidstatus"));
					id_
					.add(res.getJSONObject(i).getString("id"));
				}
				ItemAdapter adapter=new ItemAdapter();
				list.setAdapter(adapter);
				 

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.history, menu);
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

}
