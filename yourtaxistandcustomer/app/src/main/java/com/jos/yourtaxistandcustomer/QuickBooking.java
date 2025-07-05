package com.jos.yourtaxistandcustomer;

import info.androidhive.slidingmenu.model.MyLocation;
import info.androidhive.slidingmenu.model.MyLocation.LocationResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jos.yourtaxistandcustomer.util.NetworkHandler;
import com.jos.yourtaxistandcustomer.util.NetworkHandler.HTTP_METHOD;

@SuppressLint("NewApi")
public class QuickBooking extends Fragment implements OnClickListener {
	EditText from_date, to_date, time, mobile_number;
	String address_;
	String db_id;
	TextView text_to_date, text_trip, text_pack;
	CustomAutoCompleteTextView from, to_;
	PlacesTask placesTask;
	ParserTask parserTask;
	Spinner veh_type, trip_type, passenger, packge;
	ArrayList<String> personslist = new ArrayList<String>();
	ArrayList<String> vechist = new ArrayList<String>();
	ArrayList<String> triplist = new ArrayList<String>();
	ArrayList<String> packlist = new ArrayList<String>();
	ArrayList<String> packlist_1 = new ArrayList<String>();
	ArrayList<String> packlist_2 = new ArrayList<String>();
	ArrayList<String> packlist_3 = new ArrayList<String>();

	AlertDialog.Builder alert;
	private ProgressDialog dialog;
	Button post;
	static final int DATE_DIALOG_ID = 999;
	static final int TIME_DIALOG_ID = 1000;
	static final int DATE_DIALOG_ID1 = 1001;
	RadioGroup tripradio;

	private int hour;
	private int minute;
	private int year, cyear;
	private int month, cmonth;
	private int day, cday;

	String choice;
	SessionManager session;
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.quickbooking, container,
				false);
		session = new SessionManager(getActivity());
		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);

		personslist.add(("-Select number of persons-"));
		for (Integer i = 1; i <= 60; i++) {
			personslist.add(String.valueOf(i));
		}

		vechist.add("-Select vechicle-");
		vechist.add("SUV");
		vechist.add("Hatchback");
		vechist.add("Sedan");

		triplist.add("-Select trip type-");
		triplist.add("Local");
		triplist.add("Outstation");

		packlist.add("1hr/10km");
		packlist.add("2hr/20km");
		packlist.add("3hr/30km");
		packlist.add("4hr/40km");
		packlist.add("5hr/50km");
		packlist.add("6hr/60km");
		packlist.add("7hr/70km");
		packlist.add("8hr/80km");
		packlist.add("9hr/90km");
		packlist.add("10hr/100km");
		packlist.add("11hr/110km");
		packlist.add("12hr/120km");

		tripradio = (RadioGroup) rootView.findViewById(R.id.myRadioGroup);

		tripradio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// find which radio button is selected
				if (checkedId == R.id.sound) {
					choice = "Oneway";
					text_to_date.setVisibility(View.GONE);
					to_date.setVisibility(View.GONE);
				} else if (checkedId == R.id.vibration) {
					choice = "Round trip";
					text_to_date.setVisibility(View.VISIBLE);
					to_date.setVisibility(View.VISIBLE);
				} else {
					choice = "";
				}
			}
		});

		text_trip = (TextView) rootView.findViewById(R.id.text_trip);
		text_to_date = (TextView) rootView.findViewById(R.id.text_t_date);
		text_pack = (TextView) rootView.findViewById(R.id.text_pack);

		from_date = (EditText) rootView.findViewById(R.id.f_date);
		to_date = (EditText) rootView.findViewById(R.id.t_date);
		time = (EditText) rootView.findViewById(R.id.time);

		from = (CustomAutoCompleteTextView) rootView.findViewById(R.id.from);
		mobile_number = (EditText) rootView.findViewById(R.id.number);
		post = (Button) rootView.findViewById(R.id.post_quick);
		post.setOnClickListener(this);

		trip_type = (Spinner) rootView.findViewById(R.id.t_type);
		veh_type = (Spinner) rootView.findViewById(R.id.v_type);
		passenger = (Spinner) rootView.findViewById(R.id.pass);
		packge = (Spinner) rootView.findViewById(R.id.pack);

		packge.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				if (veh_type.getSelectedItemPosition() == 1) {
					db_id = String.valueOf(packge.getSelectedItemPosition() + 1);
					Toast.makeText(
							getActivity(),
							String.valueOf(packge.getSelectedItemPosition() + 1),
							Toast.LENGTH_SHORT).show();
				} else if (veh_type.getSelectedItemPosition() == 2) {

					Toast.makeText(
							getActivity(),
							String.valueOf(packge.getSelectedItemPosition() + 13),
							Toast.LENGTH_SHORT).show();
					db_id = String.valueOf(packge.getSelectedItemPosition() + 13);

				} else if (veh_type.getSelectedItemPosition() == 3) {
					Toast.makeText(
							getActivity(),
							String.valueOf(packge.getSelectedItemPosition() + 25),
							Toast.LENGTH_SHORT).show();
					db_id = String.valueOf(packge.getSelectedItemPosition() + 25);
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		trip_type.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 1) {
					tripradio.setVisibility(View.GONE);
					text_trip.setVisibility(View.GONE);

					text_pack.setVisibility(View.VISIBLE);
					packge.setVisibility(View.VISIBLE);

				} else if (position == 2) {
					tripradio.setVisibility(View.VISIBLE);
					text_trip.setVisibility(View.VISIBLE);

					text_pack.setVisibility(View.GONE);
					packge.setVisibility(View.GONE);
				} else {
					tripradio.setVisibility(View.GONE);
					text_trip.setVisibility(View.GONE);

					choice = "";

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		from_date.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatePickerDialog newFragment = new DatePickerDialog(
						getActivity(), datePickerListener, year, month, day);
				newFragment.show();
			}
		});

		to_date.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatePickerDialog newFragment = new DatePickerDialog(
						getActivity(), datePickerListener1, year, month, day);
				newFragment.show();
			}
		});

		time.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TimePickerDialog newFragment = new TimePickerDialog(
						getActivity(), timePickerListener, hour, minute, false);
				newFragment.show();
			}
		});

		ArrayAdapter<String> peradapeter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item,
				personslist);
		peradapeter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		passenger.setAdapter(peradapeter);

		ArrayAdapter<String> vechadapeter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, vechist);
		vechadapeter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		veh_type.setAdapter(vechadapeter);

		ArrayAdapter<String> packadapeter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, triplist);
		packadapeter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		trip_type.setAdapter(packadapeter);

		ArrayAdapter<String> packagedapeter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, packlist);
		packagedapeter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		packge.setAdapter(packagedapeter);

		to_ = (CustomAutoCompleteTextView) rootView.findViewById(R.id.to);
		to_.setThreshold(1);
		from.setThreshold(1);
		from.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				placesTask = new PlacesTask();
				placesTask.execute(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		to_.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				placesTask = new PlacesTask();
				placesTask.execute(s.toString());
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});

		LocationResult locationResult = new LocationResult() {
			@Override
			public void gotLocation(Location location) {
				// Got the location!
				if (location != null) {
					getAddress(location.getLatitude(), location.getLongitude());
				}

			}
		};
		MyLocation myLocation = new MyLocation();
		myLocation.getLocation(getActivity(), locationResult);
		return rootView;
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			// set date picker as current date
			return new DatePickerDialog(getActivity(), datePickerListener,
					year, month, day);

		case DATE_DIALOG_ID1:
			// set date picker as current date
			return new DatePickerDialog(getActivity(), datePickerListener1,
					year, month, day);

		case TIME_DIALOG_ID:
			// set time picker as current time
			return new TimePickerDialog(getActivity(), timePickerListener,
					hour, minute, false);

		}
		return null;
	}

	private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
		@SuppressLint("SimpleDateFormat")
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {

			hour = selectedHour;
			minute = selectedMinute;

			if (from_date.getText().toString().length() == 0) {

				Toast.makeText(getActivity(), "Please enter from date",
						Toast.LENGTH_SHORT).show();
			} else {

				Calendar datetime = Calendar.getInstance();

				String fdate = dateFormat.format(datetime.getTime());
				Date convertedDate = new Date();
				Date fromDate = new Date();
				try {

					convertedDate = dateFormat.parse(from_date.getText()
							.toString());
					fromDate = dateFormat.parse(fdate);

					if (getDaysDifference(fromDate, convertedDate) == 0) {

						Date now = new Date();

						String current_time = sdf.format(now);
						System.out.println("my time:::" + current_time);

						DateFormat format = new SimpleDateFormat("HH:mm",
								Locale.US);
						format.setTimeZone(TimeZone.getTimeZone("UTC"));
						Date start = format.parse(current_time);
						Date end = format.parse(new StringBuilder()
								.append(pad(hour)).append(":")
								.append(pad(minute)).toString());

						long difference = end.getTime() - start.getTime();

						long hours = TimeUnit.MILLISECONDS.toHours(difference);
						long minutes = TimeUnit.MILLISECONDS
								.toMinutes(difference) % 60;

						System.out.println("Hours: " + hours);
						System.out.println("Minutes: " + minutes);

						if (hours < 0) {
							Toast.makeText(getActivity(),
									"Time should be greater than current time",
									Toast.LENGTH_SHORT).show();
							time.setText("");
						}
						if (minutes < 0) {
							Toast.makeText(getActivity(),
									"Time should be greater than current time",
									Toast.LENGTH_SHORT).show();
							time.setText("");

						} else if (hours == 0 && minutes <= 30) {
							Toast.makeText(
									getActivity(),
									"You are choosing current time or within current time plus half-an_hour.So fourty minutes will be added to your selected time",
									Toast.LENGTH_SHORT).show();

							Calendar d = Calendar.getInstance();
							d.add(Calendar.MINUTE, 40);

							time.setText(sdf.format(d.getTime()));

						} else {
							time.setText(new StringBuilder().append(pad(hour))
									.append(":").append(pad(minute)));

						}

					} else {
						time.setText(new StringBuilder().append(pad(hour))
								.append(":").append(pad(minute)));

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	};

	private static String pad(int c) {
		if (c >= 10)
			return String.valueOf(c);
		else
			return "0" + String.valueOf(c);
	}

	private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		@SuppressLint("SimpleDateFormat")
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			Calendar datetime = Calendar.getInstance();
			from_date.setText(new StringBuilder().append(day).append("/")
					.append(month + 1).append("/").append(year).append(" "));

			String fdate = dateFormat.format(datetime.getTime());
			Date convertedDate = new Date();
			Date fromDate = new Date();
			try {
				convertedDate = dateFormat
						.parse(from_date.getText().toString());
				fromDate = dateFormat.parse(fdate);

				if (getDaysDifference(fromDate, convertedDate) < 0) {

					Toast.makeText(getActivity(),
							"From date should be future date",
							Toast.LENGTH_SHORT).show();
					from_date.setText("");
				} else {
					from_date.setText(new StringBuilder().append(day)
							.append("/").append(month + 1).append("/")
							.append(year).append(" "));
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// set selected date into textview

		}
	};

	private DatePickerDialog.OnDateSetListener datePickerListener1 = new DatePickerDialog.OnDateSetListener() {

		// when dialog box is closed, below method will be called.
		@SuppressLint("SimpleDateFormat")
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {

			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			to_date.setText(new StringBuilder().append(day).append("/")
					.append(month + 1).append("/").append(year).append(" "));

			SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");
			try {
				Date dates = format.parse(to_date.getText().toString());
				Date datee = format.parse(from_date.getText().toString());

				System.out.println(getDaysDifference(datee, dates));
				if (getDaysDifference(datee, dates) < 1) {

					to_date.setText("");

					Toast.makeText(getActivity(),
							"End date should be greater than start date",
							Toast.LENGTH_SHORT).show();
				} else {
					// set selected date itnto textview
					// set selected date into textview
					to_date.setText(new StringBuilder().append(day).append("/")
							.append(month + 1).append("/").append(year)
							.append(" "));

				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	public static int getDaysDifference(Date fromDate, Date toDate) {
		if (fromDate == null || toDate == null)
			return 0;

		return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
	}

	public List<Address> getAddress(double latitude, double longitude) {
		try {
			Geocoder geocoder;
			List<Address> addresses;
			geocoder = new Geocoder(getActivity());
			if (latitude != 0 || longitude != 0) {
				addresses = geocoder.getFromLocation(latitude, longitude, 1);
				String address = addresses.get(0).getAddressLine(0);
				String city = addresses.get(0).getAddressLine(1);
				String country = addresses.get(0).getAddressLine(2);
				Log.d("TAG", "address = " + address + ", city =" + city
						+ ", country = " + country);

				address_ = address + "," + city + "," + country;
				from.setText(address_);
				return addresses;
			} else {
				Toast.makeText(getActivity(),
						"latitude and longitude are null", Toast.LENGTH_LONG)
						.show();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Fetches all places from GooglePlaces AutoComplete Web Service
	private class PlacesTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {
			// For storing data from web service
			String data = "";

			// Obtain browser key from https://code.google.com/apis/console
			String key = "YOUR-API-KEY";

			String input = "";

			try {
				input = "input=" + URLEncoder.encode(place[0], "utf-8");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}

			// place type to be searched
			String types = "types=geocode";

			// Sensor enabled
			String sensor = "sensor=false";

			// Building the parameters to the web service
			String parameters = input + "&" + types + "&" + sensor + "&" + key;

			// Output format
			String output = "json";

			// Building the url to the web service
			String url = "https://maps.googleapis.com/maps/api/place/autocomplete/"
					+ output + "?" + parameters;

			try {
				// Fetching the data from web service in background
				data = downloadUrl(url);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			// Creating ParserTask
			parserTask = new ParserTask();

			// Starting Parsing the JSON string returned by Web Service
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;

			PlaceJSONParser placeJsonParser = new PlaceJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				// Getting the parsed data as a List construct
				places = placeJsonParser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		@Override
		protected void onPostExecute(List<HashMap<String, String>> result) {

			String[] fromString = new String[] { "description" };
			int[] to = new int[] { android.R.id.text1 };

			// Creating a SimpleAdapter for the AutoCompleteTextView
			SimpleAdapter adapter = new SimpleAdapter(getActivity(), result,
					android.R.layout.simple_list_item_1, fromString, to);

			// Setting the adapter
			from.setAdapter(adapter);
			to_.setAdapter(adapter);
		}
	}

	private String downloadUrl(String strUrl) throws IOException {
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try {
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(
					iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}

	@Override
	public void onClick(View v) {

		if (trip_type.getSelectedItemPosition() == 2) {
			if (veh_type.getSelectedItemPosition() == 1) {
				db_id = "37";

			} else if (veh_type.getSelectedItemPosition() == 2) {
				db_id = "38";
			} else {
				db_id = "39";
			}
		} else if (trip_type.getSelectedItemPosition() == 1) {

			if (veh_type.getSelectedItemPosition() == 1) {
				db_id = String.valueOf(packge.getSelectedItemPosition() + 1);
			} else if (veh_type.getSelectedItemPosition() == 2) {
				db_id = String.valueOf(packge.getSelectedItemPosition() + 13);

			} else if (veh_type.getSelectedItemPosition() == 3) {
				db_id = String.valueOf(packge.getSelectedItemPosition() + 25);
			}

		}

		Toast.makeText(getActivity(), db_id, Toast.LENGTH_SHORT).show();
		// TODO Auto-generated method stub
		if (from.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please enter starting place")
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

		} else if (to_.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please enter destination place")
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

		} else if (mobile_number.getText().toString().length() < 10) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Mobile number should be at least 10 digits.")
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

		} else if (trip_type.getSelectedItemPosition() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please select trip type")
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

		} else if (from_date.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please enter from date")
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

		} else if (time.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please enter time")
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

		} else if (veh_type.getSelectedItemPosition() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please select trip type")
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

		} else if (passenger.getSelectedItemPosition() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please select trip type")
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

			dialog = ProgressDialog.show(getActivity(),
					"Quick booking request",
					"Processing your request. Please wait...", true);
			new HttpAsyncTask()
					.execute("http://www.yourtaxistand.com/customer/requirements/app");
		}
	}

	public class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {// Build JSON string\\
			HashMap<String, String> user = session.getUserID();




			if (trip_type.getSelectedItemPosition() == 2)

			{
				if (veh_type.getSelectedItemPosition() == 1) {
					db_id = "37";
				} else if (veh_type.getSelectedItemPosition() == 2) {
					db_id = "38";

				} else if (veh_type.getSelectedItemPosition() == 3) {
					db_id = "39";
				}
			}

			else {
				if (veh_type.getSelectedItemPosition() == 1) {
					db_id = String
							.valueOf(packge.getSelectedItemPosition() + 1);
				} else if (veh_type.getSelectedItemPosition() == 2) {
					db_id = String
							.valueOf(packge.getSelectedItemPosition() + 13);

				} else if (veh_type.getSelectedItemPosition() == 3) {
					db_id = String
							.valueOf(packge.getSelectedItemPosition() + 25);
				}
			}
			ContentValues urlArguments = new ContentValues();
			urlArguments.put("user_id", user.get(SessionManager.KEY_ID));
			urlArguments.put("city_id", "1");
			urlArguments.put("trip", choice);
			urlArguments.put("trip_type",
					String.valueOf(trip_type.getSelectedItemPosition())
							.toString().trim());
			urlArguments.put("pickup_point", from.getText().toString().trim());
			urlArguments.put("phone_number", mobile_number.getText().toString()
					.trim());

			urlArguments.put("destination", to_.getText().toString().trim());

			urlArguments
					.put("date_from", from_date.getText().toString().trim());
			urlArguments.put("date_to", to_date.getText().toString().trim());
			urlArguments.put("time_from_hrs", time.getText().toString().trim());
			urlArguments.put("time_from_min", time.getText().toString().trim());

			urlArguments.put("vehicle",
					String.valueOf(veh_type.getSelectedItemPosition())
							.toString().trim());

			urlArguments.put("no_persons", passenger.getSelectedItem()
					.toString().trim());
			urlArguments.put("package", db_id);
			urlArguments.put("is_quick_booking", "1");
			return NetworkHandler.getStringFromURL(urls[0], urlArguments,
					HTTP_METHOD.POST);
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
					alert = new AlertDialog.Builder(getActivity());
					alert.setTitle("Alert!")

							.setMessage("Invalid username or password")
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
					Toast.makeText(getActivity(),
							"Requirement has been posted successfully",
							Toast.LENGTH_LONG).show();
					clearFields();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void clearFields() {
		from_date.setText("");
		to_date.setText("");
		time.setText("");
		from.setText("");
		mobile_number.setText("");
		trip_type.setSelection(0);
		veh_type.setSelection(0);
		passenger.setSelection(0);
		packge.setSelection(0);
	}


}
