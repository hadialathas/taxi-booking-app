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
public class Referral extends Fragment implements OnClickListener {

	EditText mail;
	Button post;
	AlertDialog.Builder alert;
	private ProgressDialog dialog;
	ConnectionDetector con;
	SessionManager session;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.referral, container, false);
		mail = (EditText) rootView.findViewById(R.id.c_mail);
		post = (Button) rootView.findViewById(R.id.post);
		post.setOnClickListener(this);
		session=new SessionManager(getActivity());
		con = new ConnectionDetector(getActivity());
		return rootView;
	}

	
	public class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {// Build JSON string\\

			HashMap<String, String> user = session.getUserID();
			ContentValues urlArguments = new ContentValues();
			urlArguments.put("cust_id",user.get(SessionManager.KEY_ID));
			urlArguments.put("email", mail.getText()
					.toString().trim());
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

							.setMessage("Referral information has not send successfully")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub

										}
									});

					AlertDialog build = alert.create();
					build.show();
				} else {

					alert = new AlertDialog.Builder(getActivity());
					alert.setTitle("Sucess!")

							.setMessage("Referral information send successfully")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub

										}
									});

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
									// if this button is clicked, close
									// current activity
									startActivity(new Intent(
											android.provider.Settings.ACTION_SETTINGS));
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alert.create();

			// show it
			alertDialog.show();

		} else if (mail.getText().toString().length() == 0  || ! android.util.Patterns.EMAIL_ADDRESS.matcher(mail.getText().toString()).matches()) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Invalid E-Mail")

					.setMessage("Please check the E-Mail you have provided.")
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
		}  else {
			dialog = ProgressDialog.show(getActivity(),
					"Referral request",
					"Processing your request. Please wait...", true);
			new HttpAsyncTask()
					.execute("http://www.yourtaxistand.com/myapps/refer_friend_byemail");
		}
	
	}

}
