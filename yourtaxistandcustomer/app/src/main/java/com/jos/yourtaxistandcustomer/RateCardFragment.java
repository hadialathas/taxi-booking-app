package com.jos.yourtaxistandcustomer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.net.NetworkRequest;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.jos.yourtaxistandcustomer.adapters.RateListAdapter;
import com.jos.yourtaxistandcustomer.util.APIUtil;
import com.jos.yourtaxistandcustomer.util.NetworkHandler;

import org.json.JSONArray;

import java.util.Arrays;

@SuppressLint("NewApi")
public class RateCardFragment extends Fragment {
    private static String RATE_CARD_API_URL = "http://www.yourtaxistand.com/customer/view_rates/app";
    private ListView rateListView;
    private ProgressDialog progressDialog;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return  inflater.inflate(R.layout.fragment_rate_card,container,false);
	}

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new RatesTask().execute(RATE_CARD_API_URL);
        rateListView =(ListView) view.findViewById(R.id.lv_rates);
    }

    private class RatesTask extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(RateCardFragment.this.getContext(),"Lodaing Rates..",null,true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            return   NetworkHandler.getStringFromURL(params[0],null, NetworkHandler.HTTP_METHOD.GET);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray rateCardsArray = new JSONArray(s);
                rateListView.setAdapter(new RateListAdapter(getActivity(),R.layout.row_rate_list,rateCardsArray));
                if(progressDialog.isShowing()){
                    progressDialog.hide();
                    }
            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }
}
