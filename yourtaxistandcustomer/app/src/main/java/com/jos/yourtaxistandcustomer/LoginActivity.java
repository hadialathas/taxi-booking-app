package com.jos.yourtaxistandcustomer;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jos.yourtaxistandcustomer.util.NetworkHandler;
import com.jos.yourtaxistandcustomer.util.NetworkHandler.HTTP_METHOD;

@SuppressLint("NewApi")
public class LoginActivity extends AppCompatActivity implements OnClickListener {
	 EditText image;
	ImageButton post, signup_bt, forget;
	EditText username_edit, password_edit;

	AlertDialog.Builder alert;
	private ProgressDialog dialog;
	ConnectionDetector con;
	SessionManager session;
	private String userName,password;

	public static boolean islogin = false;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		con = new ConnectionDetector(this);
		session = new SessionManager(getApplicationContext());

		post = (ImageButton) findViewById(R.id.signin);
		signup_bt = (ImageButton) findViewById(R.id.signup);
		forget = (ImageButton) findViewById(R.id.forgotpass);

		username_edit = (EditText) findViewById(R.id.username);
		password_edit = (EditText) findViewById(R.id.password);

		post.setOnClickListener(this);
		signup_bt.setOnClickListener(this);
		forget.setOnClickListener(this);

	}

	

	public class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			ContentValues arguments = new ContentValues();
			arguments.put("email", userName);
			arguments.put("password",password);

			return NetworkHandler.getStringFromURL(urls[0], arguments, HTTP_METHOD.POST);
		}

		@Override
		protected void onPostExecute(String result) {

			System.out.println("Result::::" + result);
			if (dialog.isShowing())
				dialog.dismiss();
			try {
				JSONObject res = new JSONObject(result);

				if (res.getString("status_code").equals("0")) {
					alert = new AlertDialog.Builder(LoginActivity.this);
					alert.setTitle("Sorry!")

							.setMessage("Invalid username or password")
							.setCancelable(false)
							.setPositiveButton("OK",null);
					AlertDialog build = alert.create();
					build.show();
				} else {
					session.userID(res.getString("user_id"), true);
					Intent i = new Intent(LoginActivity.this,
							com.jos.yourtaxistandcustomer.MainActivity.class);
					startActivity(i);
					finish();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public class HttpAsyncTask_Foget extends AsyncTask<String, Void, String> {
		private String emailForForgotPassword;
		@Override
		protected void onPreExecute() {
			emailForForgotPassword =image
					.getText().toString().trim();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... urls) {
			ContentValues arguments = new ContentValues();
			arguments.put("email",emailForForgotPassword );

			return NetworkHandler.getStringFromURL(urls[0], arguments, HTTP_METHOD.POST);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {

			System.out.println("Result::::" + result);
			if (dialog.isShowing())
				dialog.dismiss();
			try {
				JSONObject res = new JSONObject(result);

				if (res.getString("status_code").equals("0")) {
					alert = new AlertDialog.Builder(LoginActivity.this);
					alert.setTitle("Sorry!")

							.setMessage("Invalid username")
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
					alert = new AlertDialog.Builder(LoginActivity.this);
					alert.setTitle("Sorry!")

							.setMessage("Password changed sucessfully")
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
		getMenuInflater().inflate(R.menu.login, menu);
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

		if (v == post) {
			if (!con.isConnectingToInternet()) {

				alert = new AlertDialog.Builder(LoginActivity.this);

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

			} else if (username_edit.getText().toString().length() == 0) {
				alert = new AlertDialog.Builder(LoginActivity.this);
				alert.setTitle("Alert!")

						.setMessage("Username should not be empty")
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
			} else if (password_edit.getText().toString().length() == 0) {
				alert = new AlertDialog.Builder(LoginActivity.this);
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
			} else {
				dialog = ProgressDialog.show(LoginActivity.this,
						"Login request",
						"Processing your request. Please wait...", true);
				userName = username_edit.getText().toString();
				password = password_edit.getText().toString();
				new HttpAsyncTask()
						.execute("http://yourtaxistand.com/customer/loginaction/app");
			}
		}

		else if (v == signup_bt) {
			Intent i = new Intent(LoginActivity.this, SignUp.class);
			startActivity(i);
		} else if (v == forget) {

			final Dialog dialog_window = new Dialog(LoginActivity.this);
			// Include dialog.xml file
			dialog_window.setContentView(R.layout.password_dialog);
			// Set dialog title
			dialog_window.setTitle("Forgot Password");

			// set values for custom dialog components - text, image and button

			 image = (EditText) dialog_window
					.findViewById(R.id.EditText_Pwd1);

			dialog_window.show();

			TextView declineButton = (TextView) dialog_window
					.findViewById(R.id.submit);
			// if decline button is clicked, close the custom dialog
			declineButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// Close dialog
					if (image.getText().toString().length() == 0) {

						alert = new AlertDialog.Builder(LoginActivity.this);
						alert.setTitle("Alert!")
								.setMessage("User id should not be empty")
								.setCancelable(false)
								.setPositiveButton("OK",
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												// TODO Auto-generated method
												// stub

											}
										});

						AlertDialog build = alert.create();
						build.show();
					} else {
						dialog = ProgressDialog
								.show(LoginActivity.this,
										"Login request",
										"Processing your request. Please wait...",
										true);
						new HttpAsyncTask_Foget()
								.execute("http://yourtaxistand.com/customer/forgotpass_action/app");
					}
				}
			});

		}
	}

	
}
