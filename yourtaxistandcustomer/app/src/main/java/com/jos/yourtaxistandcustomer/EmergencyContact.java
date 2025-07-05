package com.jos.yourtaxistandcustomer;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.jos.yourtaxistandcustomer.util.NetworkHandler;
import com.jos.yourtaxistandcustomer.util.NetworkHandler.HTTP_METHOD;

@SuppressLint("NewApi")
public class EmergencyContact extends Fragment implements OnClickListener {

	EditText number, name;
	Button post;
	AlertDialog.Builder alert;
	private ProgressDialog dialog;
	ConnectionDetector con;
	SessionManager session;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.emergency_contact, container,
				false);
		name = (EditText) rootView.findViewById(R.id.c_name);
		number = (EditText) rootView.findViewById(R.id.c_number);
		post = (Button) rootView.findViewById(R.id.post);
		post.setOnClickListener(this);
		session=new SessionManager(getActivity());
		con = new ConnectionDetector(getActivity());
		return rootView;
	}

	
	public class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			HashMap<String, String> user = session.getUserID();
			ContentValues urlArguments = new ContentValues();
			urlArguments.put("cust_id", user.get(SessionManager.KEY_ID));
			urlArguments.put("contact_number", number
					.getText().toString().trim());
			urlArguments.put("contact_name", name
					.getText().toString().trim());

			return NetworkHandler.getStringFromURL(urls[0], urlArguments, HTTP_METHOD.POST);
		}

		@Override
		protected void onPostExecute(String result) {
			if (dialog.isShowing())
				dialog.dismiss();
			try {
				JSONObject res = new JSONObject(result);

				if (res.getString("status_code").equals("0")) {
					alert = new AlertDialog.Builder(getActivity());
					alert.setTitle("Sorry!")

							.setMessage(
									"Emergency information has not send successfully")
							.setCancelable(false)
							.setPositiveButton("OK",
									null);

					AlertDialog build = alert.create();
					build.show();
				} else {

					alert = new AlertDialog.Builder(getActivity());
					alert.setTitle("Sucess!")

							.setMessage(
									"Emergency information send successfully")
							.setCancelable(false)
							.setPositiveButton("OK",null);

					AlertDialog build = alert.create();
					build.show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (!con.isConnectingToInternet()) {

			alert = new AlertDialog.Builder(getActivity());

			// set title
			alert.setTitle("Internet!");

			// set dialog message
			alert.setMessage("Please connect to WiFi or Mobile network")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									startActivity(new Intent(
											android.provider.Settings.ACTION_SETTINGS));
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alert.create();

			// show it
			alertDialog.show();

		} else if (number.getText().toString().length() <10) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Contact number should not be less than 10 numbers.")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							});

			AlertDialog build = alert.create();
			build.show();
		}
		else if (name.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Name should not be empty")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							});

			AlertDialog build = alert.create();
			build.show();
		}else {
			dialog = ProgressDialog.show(getActivity(),
					"Emergency contact request",
					"Processing your request. Please wait...", true);
			new HttpAsyncTask()
					.execute("http://www.yourtaxistand.com/myapps/emergency_contact");
		}
	
	}

}
