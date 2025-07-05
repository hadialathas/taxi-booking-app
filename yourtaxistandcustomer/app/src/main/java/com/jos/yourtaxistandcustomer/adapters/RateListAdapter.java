package com.jos.yourtaxistandcustomer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jos.yourtaxistandcustomer.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by vigneshwaran on 28/01/16.
 */
public class RateListAdapter extends BaseAdapter {
    private JSONArray dataItems;
    public RateListAdapter(Context context, int resId, JSONArray dataItems){
            super();
        this.dataItems =dataItems;
    }

    @Override
    public int getCount() {
      return this.dataItems.length();
    }

    @Override
    public long getItemId(int position) {
        return 0;

    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            viewHolder= new ViewHolder();
            convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rate_list,parent,false);
            viewHolder.cityTextView = (TextView) convertView.findViewById(R.id.tv_trip_city);
            viewHolder.packageRateTextView = (TextView) convertView.findViewById(R.id.tv_trip_package_rate);
            viewHolder.driverBataTextView =(TextView) convertView.findViewById(R.id.tv_trip_driver_beta);
            viewHolder.rateAdditionalKmTextView = (TextView) convertView.findViewById(R.id.tv_trip_rate_additional_km);
            viewHolder.rateAdditionalHourTextView = (TextView) convertView.findViewById(R.id.tv_trip_rate_additional_hour);
            viewHolder.packageDistanceTextView = (TextView) convertView.findViewById(R.id.tv_trip_package_distance);
            viewHolder.packageHoursTextView = (TextView) convertView.findViewById(R.id.tv_trip_package_hours);
            viewHolder.tripTypeTextView =(TextView) convertView.findViewById(R.id.tv_trip_type);
            viewHolder.vehicleTypeTextView = (TextView) convertView.findViewById(R.id.tv_trip_vehicle_type);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        JSONObject rateObject=null;
        try {
            rateObject = dataItems.getJSONObject(position);
            if(rateObject!=null) {
                viewHolder.cityTextView.setText("City: "+rateObject.getString("city"));
                viewHolder.vehicleTypeTextView.setText("Vehicle Type: "+rateObject.getString("vehicle_type"));
                viewHolder.tripTypeTextView.setText("Trip Type: "+rateObject.getString("trip_type"));
                viewHolder.packageDistanceTextView.setText("Package Distance: "+rateObject.getString("package_distance"));
                viewHolder.packageHoursTextView.setText("Package Hours: "+rateObject.getString("package_hours"));
                viewHolder.packageRateTextView.setText("Package Rate: "+rateObject.getString("package_rate"));
                viewHolder.rateAdditionalKmTextView.setText("Addition Per Hour Rs:"+rateObject
                .getString("rate_additional_km"));
                viewHolder.rateAdditionalHourTextView.setText("Additionala Rs per Km: "+rateObject.getString("rate_additional_hour"));
                viewHolder.driverBataTextView.setText("Driver bata: "+rateObject.getString("driver_bata"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return convertView ;
    }

    private static class ViewHolder {
        private TextView cityTextView;
        private TextView tripTypeTextView;
        private TextView vehicleTypeTextView;
        private TextView packageHoursTextView;
        private TextView packageDistanceTextView;
        private TextView packageRateTextView;
        private TextView rateAdditionalKmTextView;
        private TextView rateAdditionalHourTextView;
        private TextView driverBataTextView;
    }
}
