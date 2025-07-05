package com.jos.yourtaxistandcustomer;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.jos.yourtaxistandcustomer.util.NetworkHandler;
import com.jos.yourtaxistandcustomer.util.NetworkHandler.HTTP_METHOD;

public class SignUp extends AppCompatActivity implements
		android.view.View.OnClickListener {

	AlertDialog.Builder alert;
	private ProgressDialog dialog;

	EditText firstname, lastname, email, password, phone, phone2, add_line1,
			add_line2, add_line3, city, zip,coupon_code;

	Spinner state;
	TextView submit;
	String[] regionsArray;
	ArrayList<String> regions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_sign_up);

		regionsArray = getResources().getStringArray(R.array.state_arrays);
		regions = new ArrayList<String>(Arrays.asList(regionsArray));

		firstname = (EditText) findViewById(R.id.firstname);
		lastname = (EditText) findViewById(R.id.lstname);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.pass);
		phone = (EditText) findViewById(R.id.phone);
		phone2 = (EditText) findViewById(R.id.phone_alt);

		add_line1 = (EditText) findViewById(R.id.address);
		add_line2 = (EditText) findViewById(R.id.address_1);

		add_line3 = (EditText) findViewById(R.id.address_2);
		city = (EditText) findViewById(R.id.city);

		state = (Spinner) findViewById(R.id.state);
		zip = (EditText) findViewById(R.id.zip);
		
		coupon_code = (EditText) findViewById(R.id.coupon);

		submit = (TextView) findViewById(R.id.submit);
		submit.setOnClickListener(this);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, regions);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		state.setAdapter(dataAdapter);
	}

	public class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {// Build JSON string\\
			ContentValues urlArguments = new ContentValues(); 
			urlArguments.put("firstname", firstname
					.getText().toString().trim());
			urlArguments.put("lastname", lastname
					.getText().toString().trim());
			urlArguments.put("email", email.getText()
					.toString().trim());
			urlArguments.put("password", password
					.getText().toString().trim());
			urlArguments.put("phone", phone.getText()
					.toString().trim());
			urlArguments.put("phone2", phone2.getText()
					.toString().trim());
			urlArguments.put("add_line1", add_line1
					.getText().toString().trim());
			urlArguments.put("add_line2", add_line2
					.getText().toString().trim());
			urlArguments.put("add_line3", add_line3
					.getText().toString().trim());
			urlArguments.put("city", city.getText()
					.toString().trim());
			urlArguments.put("state", state
					.getSelectedItem().toString());
			urlArguments.put("zip", zip.getText()
					.toString().trim());
			urlArguments.put("coupon_code", coupon_code.getText()
					.toString().trim());

			return NetworkHandler.getStringFromURL(urls[0], urlArguments, HTTP_METHOD.POST);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {

			System.out.println("Result::::" + result);
			if (dialog.isShowing())
				dialog.dismiss();
			try {
				JSONObject res = new JSONObject(result);
				// {"email":"parthi.sp@yahoo.com","status_code":1,"status_message":"Sign up successful. Check your email and activate your account","user_id":7}

				if (res.getString("status_code").equals("1")) {
					alert = new AlertDialog.Builder(SignUp.this);
					alert.setTitle("Success!")

							.setMessage(
									"Sign up successful. Check your email and activate your accoun")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											startActivity(new Intent(
													SignUp.this,
													LoginActivity.class));
											SignUp.this.finish();

										}
									});

					AlertDialog build = alert.create();
					build.show();
				} else {
					alert = new AlertDialog.Builder(SignUp.this);
					alert.setTitle("Sorry!")

							.setMessage(res.getString("status_message"))
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
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (firstname.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(SignUp.this);
			alert.setTitle("Alert!")

					.setMessage("Firstname should not be empty")
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
		} else if (password.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(SignUp.this);
			alert.setTitle("Alert!")
					.setMessage("password should not be empty")
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
		} else if (phone.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(SignUp.this);
			alert.setTitle("Alert!")
					.setMessage("Phone should not be empty")
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
		} else if (email.getText().toString().length() == 0 ||  !android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
			alert = new AlertDialog.Builder(SignUp.this);
				alert.setTitle("Invalid E-Mail")
					.setMessage("Please check the E-mail id you have provided.")
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
		} else if (city.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(SignUp.this);
			alert.setTitle("Alert!")
					.setMessage("City should not be empty")
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
		} else {
			dialog = ProgressDialog.show(SignUp.this, "Signup request",
					"Processing your request. Please wait...", true);
			new HttpAsyncTask()
					.execute("http://yourtaxistand.com/customer/signup/app");
		}
	}

}
