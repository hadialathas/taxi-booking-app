package com.jos.yourtaxistandcustomer.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Set;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.util.Log;

public class NetworkHandler {
	// Enumerations for setting http url method
	public static enum HTTP_METHOD {
		GET, POST, PUT, SET
	};

	private static String TAG = "NetworkHandler";

	public static String getStringFromURL(String url, ContentValues arguments,
			HTTP_METHOD method) {
		URL requestURL;
		HttpURLConnection httpURLConnection;
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		StringBuilder contentStringBuilder = null;
		try {
			requestURL = new URL(url);
			// set request method from enumerations
			httpURLConnection = (HttpURLConnection) requestURL.openConnection();
			httpURLConnection.setRequestMethod(method.name());

			// Set request parameters
			if (arguments != null) {
				OutputStreamWriter writer = new OutputStreamWriter(
						httpURLConnection.getOutputStream());
				writer.write(encodeArguments(arguments));
				writer.close();
			}
			inputStreamReader = new InputStreamReader(
					httpURLConnection.getInputStream());
			bufferedReader = new BufferedReader(inputStreamReader);
			contentStringBuilder = new StringBuilder();
			String readLine;
			while ((readLine = bufferedReader.readLine()) != null) {
				contentStringBuilder.append(readLine);
			}

		} catch (MalformedURLException urlException) {
			Log.e(TAG, "Is the request url is valid?");
			urlException.printStackTrace();
		} catch (IOException ioException) {
			Log.e(TAG, "Cannot connect to server");
			ioException.printStackTrace();
		} finally {
			if (bufferedReader != null && inputStreamReader != null)
				try {
					bufferedReader.close();
					inputStreamReader.close();
				} catch (IOException e) {
					Log.e(TAG, "Error closing streams");
					e.printStackTrace();
				}
		}

		return contentStringBuilder.toString();

	}

	private static String encodeArguments(ContentValues argumentValues) {
		boolean isFirstArgument = true;
		StringBuilder argumentsString = new StringBuilder();
		for (Entry<String, Object> item : argumentValues.valueSet()) {
			String argumentSet = "";
			if (isFirstArgument) {
				isFirstArgument = false;
			} else {
				argumentSet = "&";
			}
			argumentSet += item.getKey() + "=" + item.getValue();
			argumentsString.append(argumentSet);
		}
		return argumentsString.toString();
	}
}
