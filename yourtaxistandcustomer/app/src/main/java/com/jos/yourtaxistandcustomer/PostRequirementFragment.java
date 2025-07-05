package com.jos.yourtaxistandcustomer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.jos.yourtaxistandcustomer.util.NetworkHandler;
import com.jos.yourtaxistandcustomer.util.NetworkHandler.HTTP_METHOD;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class PostRequirementFragment extends Fragment implements
		OnClickListener, OnItemSelectedListener {

	final String[] items = { "Tamil", "Telungu", "Malayalam", "Kannada",
			"Hindi", "English" };
	final String[] items_add = { "Carrier Required", "Pregnant Lady Passenger",
			"Senior Citizen" };
	AlertDialog.Builder alert;
	private ProgressDialog dialog;
	String choice, db_id;
	static final int DATE_DIALOG_ID = 999;
	static final int TIME_DIALOG_ID = 1000;
	static final int DATE_DIALOG_ID1 = 1001;

	private int hour;
	private int minute;
	private int year, cyear;
	private int month, cmonth;
	private int day, cday;
	Spinner city, trip, prefvech, packge, persons;
	ArrayAdapter<String> cityadapter, tripadapter;
	ArrayList<String> citylist, triplist, pkglist;
	ArrayList<Integer> personslist = new ArrayList<Integer>();
	ArrayList<String> vechist = new ArrayList<String>();
	ArrayList<String> packlist = new ArrayList<String>();
	AutoCompleteTextView pickup, drop;
	PlacesTask placesTask;
	ParserTask parserTask;

	EditText date, dateto, time, pickuppointadd1, pickuppointadd2,
			pickuppointadd3, pickuppointland, destinationadd1, destinationadd2,
			destinationadd3, destinationland, lang, additional, about;

	RadioGroup tripradio;
	TextView t, todatetext,packageTitleTextView;
	protected boolean[] _selections = new boolean[items.length];
	protected boolean[] _selections1 = new boolean[items_add.length];
	Set<String> additionalInfoSet = new HashSet<String>();
	Set<String> knownLanguagesSet = new HashSet<String>();

	// Buttons for next and previous
	Button page0_next, page1_previous, page1_next, page2_previous, page2_next,
			page3_previous, page3_next;

	RelativeLayout page0, page1, page2, page3;
	SessionManager session;

	public PostRequirementFragment() {
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_post_requirement,
				container, false);

		session = new SessionManager(getActivity());
		// Load date to page0
		for (Integer i = 0; i <= 61; i++) {
			personslist.add(i);
		}

		vechist.add("Select vechicle");
		vechist.add("SUV");
		vechist.add("Hatchback");
		vechist.add("Sedan");

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

		final Calendar c = Calendar.getInstance();
		year = c.get(Calendar.YEAR);
		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
		String[] regionsArray = getResources().getStringArray(
				R.array.city_arrays);
		citylist = new ArrayList<String>(Arrays.asList(regionsArray));

		String[] regionsArray1 = getResources().getStringArray(
				R.array.trip_arrays);
		triplist = new ArrayList<String>(Arrays.asList(regionsArray1));

		t = (TextView) rootView.findViewById(R.id.t);
		todatetext = (TextView) rootView.findViewById(R.id.todatetext);
		city = (Spinner) rootView.findViewById(R.id.cty);
		trip = (Spinner) rootView.findViewById(R.id.trip);
		prefvech = (Spinner) rootView.findViewById(R.id.veh);
		packge = (Spinner) rootView.findViewById(R.id.pack);
		persons = (Spinner) rootView.findViewById(R.id.persons);
		tripradio = (RadioGroup) rootView.findViewById(R.id.myRadioGroup);
		packageTitleTextView=(TextView)rootView.findViewById(R.id.tv_pack_title);
		packge.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				if (prefvech.getSelectedItemPosition() == 1) {
					db_id = String.valueOf(packge.getSelectedItemPosition() + 1);
					Toast.makeText(
							getActivity(),
							String.valueOf(packge.getSelectedItemPosition() + 1),
							Toast.LENGTH_SHORT).show();
				} else if (prefvech.getSelectedItemPosition() == 2) {

					Toast.makeText(
							getActivity(),
							String.valueOf(packge.getSelectedItemPosition() + 13),
							Toast.LENGTH_SHORT).show();
					db_id = String.valueOf(packge.getSelectedItemPosition() + 13);

				} else if (prefvech.getSelectedItemPosition() == 3) {
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

		trip.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (position == 0) {
					tripradio.setVisibility(View.GONE);
					t.setVisibility(View.GONE);
					todatetext.setVisibility(View.GONE);
					dateto.setVisibility(View.GONE);
					todatetext.setVisibility(View.GONE);
					packge.setVisibility(View.VISIBLE);
					packageTitleTextView.setVisibility(View.VISIBLE);
				} else if (position == 1) {
					tripradio.setVisibility(View.VISIBLE);
					t.setVisibility(View.VISIBLE);
					packge.setVisibility(View.GONE);
					packageTitleTextView.setVisibility(View.GONE);
				} else {
					tripradio.setVisibility(View.GONE);
					t.setVisibility(View.GONE);
					todatetext.setVisibility(View.VISIBLE);
					dateto.setVisibility(View.VISIBLE);
					choice = "";
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
		trip.setSelection(0);

		tripradio.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// find which radio button is selected
				if (checkedId == R.id.sound) {
					choice = "Oneway";
					dateto.setVisibility(View.GONE);
                    todatetext.setVisibility(View.GONE);
				} else if (checkedId == R.id.vibration) {
					choice = "Round trip";
					dateto.setVisibility(View.VISIBLE);
                    todatetext.setVisibility(View.VISIBLE);
				} else {
					choice = "";
				}
			}

		});
		page0 = (RelativeLayout) rootView.findViewById(R.id.page0);
		page1 = (RelativeLayout) rootView.findViewById(R.id.page1);
		page2 = (RelativeLayout) rootView.findViewById(R.id.page2);
		page3 = (RelativeLayout) rootView.findViewById(R.id.page3);

		page0_next = (Button) rootView.findViewById(R.id.page0_next);
		page1_next = (Button) rootView.findViewById(R.id.page1_next);
		page2_next = (Button) rootView.findViewById(R.id.page2_next);
		page3_next = (Button) rootView.findViewById(R.id.page3_next);
		page1_previous = (Button) rootView.findViewById(R.id.page1_previous);
		page2_previous = (Button) rootView.findViewById(R.id.page2_previous);
		page3_previous = (Button) rootView.findViewById(R.id.page3_previous);
		// submit = (TextView) rootView.findviewById(R.id.submit);
		date = (EditText) rootView.findViewById(R.id.date);

		dateto = (EditText) rootView.findViewById(R.id.todate);
		time = (EditText) rootView.findViewById(R.id.time);

		pickuppointadd1 = (EditText) rootView.findViewById(R.id.pikup_home);
		pickuppointadd2 = (EditText) rootView.findViewById(R.id.pikup_street);
		pickuppointadd3 = (EditText) rootView.findViewById(R.id.pikup_area);
		pickuppointland = (EditText) rootView.findViewById(R.id.pikup_landmark);

		destinationadd1 = (EditText) rootView.findViewById(R.id.dest_home);
		destinationadd2 = (EditText) rootView.findViewById(R.id.dest_street);
		destinationadd3 = (EditText) rootView.findViewById(R.id.dest_area);
		destinationland = (EditText) rootView.findViewById(R.id.dest_landmark);
		lang = (EditText) rootView.findViewById(R.id.lang);
		additional = (EditText) rootView.findViewById(R.id.additional);
		about = (EditText) rootView.findViewById(R.id.about);
		lang.setOnClickListener(this);
		additional.setOnClickListener(this);

		date.setOnClickListener(new OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DatePickerDialog newFragment = new DatePickerDialog(
						getActivity(), datePickerListener, year, month, day);
				newFragment.show();
			}
		});

		dateto.setOnClickListener(new OnClickListener() {

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
		ArrayAdapter<String> cityadapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, citylist);
		cityadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		city.setAdapter(cityadapter);

		tripadapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, triplist);
		tripadapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		trip.setAdapter(tripadapter);

		ArrayAdapter<Integer> peradapeter = new ArrayAdapter<Integer>(
				getActivity(), android.R.layout.simple_spinner_item,
				personslist);
		peradapeter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		persons.setAdapter(peradapeter);

		ArrayAdapter<String> vechadapeter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, vechist);
		vechadapeter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		prefvech.setAdapter(vechadapeter);

		ArrayAdapter<String> packadapeter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_spinner_item, packlist);
		packadapeter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		packge.setAdapter(packadapeter);

		pickup = (AutoCompleteTextView) rootView.findViewById(R.id.pickup);
		pickup.setThreshold(1);

		pickup.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
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

		drop = (AutoCompleteTextView) rootView.findViewById(R.id.drop);
		drop.setThreshold(1);

		drop.addTextChangedListener(new TextWatcher() {

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
		// Onclick for next
		page0_next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

					page0.setVisibility(View.GONE);
					page1.setVisibility(View.VISIBLE);
			}
		});
		page1_next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (valPage1()) {
					page1.setVisibility(View.GONE);
					page2.setVisibility(View.VISIBLE);
				}

			}
		});
		page2_next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (valPage2()) {
					page2.setVisibility(View.GONE);
					page3.setVisibility(View.VISIBLE);
				}
			}
		});
		page3_next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog = ProgressDialog.show(getActivity(),
						"Requirement request",
						"Processing your request. Please wait...", true);
				new HttpAsyncTask()
						.execute("http://www.yourtaxistand.com/customer/requirements/app");
			}
		});
		// Onclick for previous

		page3_previous.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				page3.setVisibility(View.GONE);
				page2.setVisibility(View.VISIBLE);

			}
		});
		page2_previous.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				page2.setVisibility(View.GONE);
				page1.setVisibility(View.VISIBLE);

			}
		});
		page1_previous.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				page1.setVisibility(View.GONE);
				page0.setVisibility(View.VISIBLE);

			}
		});
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
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			hour = selectedHour;
			minute = selectedMinute;

			// set current time into textview
			time.setText(new StringBuilder().append(pad(hour)).append(":")
					.append(pad(minute)));

			// set current time into timepicker

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
		public void onDateSet(DatePicker view, int selectedYear,
				int selectedMonth, int selectedDay) {
			year = selectedYear;
			month = selectedMonth;
			day = selectedDay;

			// set selected date into textview
			date.setText(new StringBuilder().append(day).append("/")
					.append(month + 1).append("/").append(year).append(" "));

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

			// set selected date into textview
			dateto.setText(new StringBuilder().append(day).append("/")
					.append(month + 1).append("/").append(year).append(" "));

			SimpleDateFormat format = new SimpleDateFormat("dd/mm/yyyy");
			try {
				Date dates = format.parse(dateto.getText().toString());
				Date datee = format.parse(date.getText().toString());

				System.out.println(getDaysDifference(datee, dates));
				if (getDaysDifference(datee, dates) < 1) {

					dateto.setText("");

					Toast.makeText(getActivity(),
							"End date should be greater than start date",
							Toast.LENGTH_SHORT).show();
				} else {
					// set selected date itnto textview
					dateto.setText(new StringBuilder().append(month + 1)
							.append("/").append(day).append("/").append(year)
							.append(" "));

				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

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

	// Fetches all places from GooglePlaces AutoComplete Web Service
	private class PlacesTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... place) {
			// For storing data from web service
			String data = " ";

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

			String[] from = new String[] { "description" };
			int[] to = new int[] { android.R.id.text1 };

			// Creating a SimpleAdapter for the AutoCompleteTextView
			SimpleAdapter adapter = new SimpleAdapter(getActivity(), result,
					android.R.layout.simple_list_item_1, from, to);

			// Setting the adapter
			drop.setAdapter(adapter);
			pickup.setAdapter(adapter);
		}
	}

	public static int getDaysDifference(Date fromDate, Date toDate) {
		if (fromDate == null || toDate == null)
			return 0;

		return (int) ((toDate.getTime() - fromDate.getTime()) / (1000 * 60 * 60 * 24));
	}

	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getActivity().getMenuInflater().inflate(R.menu.post_requirement, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.history) {

			Intent i = new Intent(getActivity(), History.class);
			startActivity(i);
			return true;
		}

		else {
			Intent i = new Intent(getActivity(), Credit.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v == page3_next) {

			if (about.getText().toString().length() == 0) {
				alert = new AlertDialog.Builder(getActivity());
				alert.setTitle("Alert!")

						.setMessage("Please enter destination landmark")
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

			}
		} else if (v == date) {
			getActivity().showDialog(DATE_DIALOG_ID);
		} else if (v == dateto) {
			getActivity().showDialog(DATE_DIALOG_ID1);
		} else if (v == time) {
			getActivity().showDialog(TIME_DIALOG_ID);
		} else if (v == lang) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setTitle("Select language")
					.setMultiChoiceItems(items, _selections,
							new DialogInterface.OnMultiChoiceClickListener() {
								public void onClick(DialogInterface dialog, int item, boolean isChecked) {
									if(isChecked){
										knownLanguagesSet.add(items[item]);
									}else{
										knownLanguagesSet.remove(items[item]);
									}
									_selections[item]=isChecked;
									lang.setText(knownLanguagesSet.toString()
											.replace("[", "").replace("]", ""));
								}
							})
					.setPositiveButton("OK", null)
					.create();

			AlertDialog dialog = builder.create();
			dialog.show();
		} else if (v == additional) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setTitle("Select language")
					.setMultiChoiceItems(items_add, _selections1,
							new DialogInterface.OnMultiChoiceClickListener() {
								public void onClick(DialogInterface dialog,
										int item, boolean isChecked) {
									if(isChecked){
										additionalInfoSet.add(items_add[item]);
									}else{
										additionalInfoSet.remove(items_add[item]);
									}
									_selections1[item]=isChecked;
								}
							})
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									additional.setText(additionalInfoSet.toString()
											.replace("[", "").replace("]", ""));
								}
							});

			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}


	public class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {// Build JSON string\\

			HashMap<String, String> user = session.getUserID();
			ContentValues urlArguments = new ContentValues();
			urlArguments.put("user_id", user
					.get(SessionManager.KEY_ID));
			urlArguments.put("city_id", "1");
			urlArguments.put("trip", choice);
			urlArguments.put("trip_type", String
							.valueOf(trip.getSelectedItemPosition()).toString()
							.trim());
			urlArguments.put("pickup_point", pickup
					.getText().toString().trim());
			urlArguments.put("pickup_add_line1",
					pickuppointadd1.getText().toString().trim());
			urlArguments.put("pickup_add_line2",
					pickuppointadd2.getText().toString().trim());
			urlArguments.put("pickup_add_line3",
					pickuppointadd3.getText().toString().trim());
			urlArguments.put("landmark",
					pickuppointadd3.getText().toString().trim());

			urlArguments.put("destination", drop
					.getText().toString().trim());
			urlArguments.put("drop_add_line1",
					destinationadd1.getText().toString().trim());
			urlArguments.put("drop_add_line2",
					destinationadd2.getText().toString().trim());
			urlArguments.put("drop_add_line3",
					destinationadd3.getText().toString().trim());
			urlArguments.put("drop_landmark",
					destinationland.getText().toString().trim());

			urlArguments.put("date_from", date
					.getText().toString().trim());
			urlArguments.put("date_to", dateto
					.getText().toString().trim());
			urlArguments.put("time_from_hrs", time
					.getText().toString().trim());

			urlArguments.put("vehicle", String
					.valueOf(prefvech.getSelectedItemPosition()).toString()
					.trim());
			urlArguments.put("package", packge
					.getSelectedItem().toString().trim());
			urlArguments.put("no_persons", persons
					.getSelectedItem().toString().trim());

			urlArguments.put("language_known[]", knownLanguagesSet
					.toString().replace("[", "").replace("]", ""));
			urlArguments.put("add_info[]", additionalInfoSet
					.toString().replace("[", "").replace("]", ""));
			urlArguments.put("description", about
					.getText().toString().trim());

			return NetworkHandler.getStringFromURL(urls[0], urlArguments, HTTP_METHOD.POST);
		}

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

							.setMessage("Requirement not posted successfully")
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

							.setMessage("Requirement posted successfully")
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
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}


	public boolean valPage1() {
		if (pickup.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please enter pickup point address")
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
			return false;
		} else if (pickuppointadd1.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please enter pickup address line 1")
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
			return false;
		} else if (pickuppointadd2.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please enter pickup address line 2")
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
			return false;
		} else if (pickuppointadd3.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please enter pickup address line 3")
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
			return false;
		} else if (pickuppointland.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please enter pick up  landmark")
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
			return false;

		} else if (drop.getText().toString().length() == 0) {
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
			return false;
		}

		// else if (destinationadd1.getText().toString().length() == 0) {
		// alert = new AlertDialog.Builder(getActivity());
		// alert.setTitle("Alert!")
		//
		// .setMessage("Please enter destination address line 1")
		// .setCancelable(false)
		// .setPositiveButton("OK",
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		//
		// AlertDialog build = alert.create();
		// build.show();
		// return false;
		// } else if (destinationadd2.getText().toString().length() == 0) {
		// alert = new AlertDialog.Builder(getActivity());
		// alert.setTitle("Alert!")
		//
		// .setMessage("Please enter destination address line 2")
		// .setCancelable(false)
		// .setPositiveButton("OK",
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		//
		// AlertDialog build = alert.create();
		// build.show();
		// return false;
		// }
		// else if (destinationadd3.getText().toString().length() == 0) {
		// alert = new AlertDialog.Builder(getActivity());
		// alert.setTitle("Alert!")
		//
		// .setMessage("Please enter destination address line 3")
		// .setCancelable(false)
		// .setPositiveButton("OK",
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		//
		// AlertDialog build = alert.create();
		// build.show();
		// return false;
		// }
		// else if (destinationland.getText().toString().length() == 0) {
		// alert = new AlertDialog.Builder(getActivity());
		// alert.setTitle("Alert!")
		//
		// .setMessage("Please enter destination landmark")
		// .setCancelable(false)
		// .setPositiveButton("OK",
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog,
		// int which) {
		// // TODO Auto-generated method stub
		//
		// }
		// });
		//
		// AlertDialog build = alert.create();
		// build.show();
		// return false;
		// }
		else {
			return true;
		}
	}

	public boolean valPage2() {
		if (date.getText().toString().length() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please enter date")
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
			return false;
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
			return false;
		} else if (prefvech.getSelectedItemPosition() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please select preferred vechicle")
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
			return false;
		} else if (persons.getSelectedItemPosition() == 0) {
			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please select number of persons")
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
			return false;
		} else if (packge.getSelectedItemPosition() == -1) {

			alert = new AlertDialog.Builder(getActivity());
			alert.setTitle("Alert!")

					.setMessage("Please select package")
					.setCancelable(false)
					.setPositiveButton("OK",
							null);
			AlertDialog build = alert.create();
			build.show();
			return false;

		} else {
			return true;
		}
	}

}
