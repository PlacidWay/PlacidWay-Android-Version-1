package com.placidway.inc.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.placidway.inc.R;
import com.placidway.inc.appInfo.PlacidWay;
import com.placidway.inc.modal.Treatment;
import com.placidway.inc.webcalls.WebCalls;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;

public class MedicalCenterActivity extends Activity {

    Activity act;
    ArrayList<String> array_treatment;
    ArrayList<String> array_country;
    ArrayList<String> array_city;

    TextView tv_treatment;
    TextView tv_country;
    TextView tv_city;

    boolean clicked = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_center);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        act = this;

        tv_treatment = (TextView) findViewById(R.id.tv_treatment);
        tv_country = (TextView) findViewById(R.id.tv_country);
        tv_city = (TextView) findViewById(R.id.tv_city_mc);

        if (!PlacidWay.getInstance().is_internet_available())
        {
            showAlert("No Internet Connection");
        }
        else {
            //Create instance for AsyncCallWS
            AsyncCallGetTreatment task = new AsyncCallGetTreatment(act);
            //Call execute
            task.execute();
            if (!PlacidWay.getInstance().is_internet_available()) {
                showAlert("No Internet Connection");
            } else{
                AsyncCallGetCountry asyncCallGetCountry = new AsyncCallGetCountry(act);
                asyncCallGetCountry.execute();
            }
        }
    }
    private class AsyncCallGetTreatment extends AsyncTask<String, Void, ArrayList>
    {
        ProgressDialog pd;

        Activity act;
        ArrayList response;
        public AsyncCallGetTreatment(Activity act) {
            this.act = act;
            pd = ProgressDialog.show(act, "", "Please Wait");
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
        @Override
        protected ArrayList doInBackground(String... params)
        {
            response = WebCalls.wcGetAllTreatment(act);
            return response;
        }

        @Override
        protected void onPostExecute(ArrayList result)
        {
            if (pd != null && pd.isShowing())
                pd.dismiss();
            super.onPostExecute(result);
            array_treatment = result;
        }

    }
    private class AsyncCallGetCountry extends AsyncTask<String, Void, ArrayList>
    {
        ProgressDialog pd;

        Activity act;
        ArrayList response;
        public AsyncCallGetCountry(Activity act) {
            this.act = act;
            pd = ProgressDialog.show(act, "", "Please Wait");
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
        @Override
        protected ArrayList doInBackground(String... params)
        {
            response = WebCalls.wcGetAllCountry(act);
            return response;
        }

        @Override
        protected void onPostExecute(ArrayList result)
        {
            if (pd != null && pd.isShowing())
                pd.dismiss();
            super.onPostExecute(result);
            array_country = result;

        }

    }

    private class AsyncCallGetCity extends AsyncTask<String, Void, ArrayList>
    {
        ProgressDialog pd;

        Activity act;
        ArrayList response;
        String country;
        public AsyncCallGetCity(Activity act,String country) {
            this.act = act;
            pd = ProgressDialog.show(act, "", "Please Wait");
            this.country=country;
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
        @Override
        protected ArrayList doInBackground(String... params)
        {
            response = WebCalls.wcGetCityByCountry(act, country);
            return response;
        }

        @Override
        protected void onPostExecute(ArrayList result)
        {
            if (pd != null && pd.isShowing())
                pd.dismiss();
            super.onPostExecute(result);
            array_city = result;

        }

    }


    public void popupTreatment(View v)
    {
        if (array_treatment != null)
        {
            if (!clicked) {
                clicked = true;
                final String[] treatment = array_treatment.toArray(new String[array_treatment.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(
                        this, android.R.style.Theme_DeviceDefault_Light_Dialog));

                builder.setTitle("Choose Treatment");
                builder.setItems(treatment, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        tv_treatment.setText(treatment[item]);
                        //Toast.makeText(getApplicationContext(), country[item], Toast.LENGTH_SHORT).show();
                        clicked = false;
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
        else
        {
            if (!clicked) {
                clicked = true;
                if (!PlacidWay.getInstance().is_internet_available()) {
                    showAlert("No Internet Connection");
                    clicked = false;
                } else {
                    clicked = false;
                    AsyncCallGetTreatment task = new AsyncCallGetTreatment(act);
                    //Call execute
                    task.execute();

                    AsyncCallGetCountry asyncCallGetCountry = new AsyncCallGetCountry(act);
                    asyncCallGetCountry.execute();
                }
            }
        }

    }
    public void popupCountryListMC(View v)
    {
        if (array_country != null) {
            if (!clicked) {
                clicked = true;
                final String[] countries = array_country.toArray(new String[array_country.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(
                        this, android.R.style.Theme_DeviceDefault_Light_Dialog));

                builder.setTitle("Choose Country");
                builder.setItems(countries, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        tv_country.setText(countries[item]);
                        tv_city.setText("Choose City");
                        clicked = false;
                        //Toast.makeText(getApplicationContext(), country[item], Toast.LENGTH_SHORT).show();
                        if (!PlacidWay.getInstance().is_internet_available()) {
                            showAlert("No Internet Connection");
                        } else {
                            AsyncCallGetCity asyncCallGetCountry = new AsyncCallGetCity(act, countries[item]);
                            asyncCallGetCountry.execute();
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
        else
        {
            if (!clicked) {
                clicked = true;
                if (!PlacidWay.getInstance().is_internet_available()) {
                    showAlert("No Internet Connection");
                    clicked = false;
                } else {
                    clicked = false;
                    AsyncCallGetCountry asyncCallGetCountry = new AsyncCallGetCountry(act);
                    asyncCallGetCountry.execute();
                }
            }
        }
    }
    public void popupCity(View v)
    {
        if (!clicked) {
            clicked = true;
            if (tv_country.getText().toString().equals("Choose Country")) {
                showAlert("Choose Country First.");
                clicked = false;
            } else {
                final String[] city = array_city.toArray(new String[array_city.size()]);

                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(
                        this, android.R.style.Theme_DeviceDefault_Light_Dialog));

                builder.setTitle("Choose City");
                builder.setItems(city, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        tv_city.setText(city[item]);
                        clicked = false;
                        //Toast.makeText(getApplicationContext(), country[item], Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }

    }
    private void showAlert(final String msg)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(
                this,android.R.style.Theme_DeviceDefault_Light_Dialog));

        builder.setTitle("Alert");
        builder.setMessage(msg);

        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                //stuff you want the button to do

                dialog.dismiss();
                if(msg.equals("Form Submit Successfully."))
                {
                    finish();
                }
            }
        });
        final AlertDialog alert = builder.create();

        alert.show();
    }
    public void gotoSearch(View v)
    {
        if (!PlacidWay.getInstance().is_internet_available())
        {
            showAlert("No Internet Connection' message");
        }
        else {
            AsyncCallSearchList asyncCallSearchList = new AsyncCallSearchList(act, tv_treatment.getText().toString(), tv_country.getText().toString(), tv_city.getText().toString());
            asyncCallSearchList.execute();
        }
    }
    public void goMainHome(View v)
    {
        Intent intent = new Intent(act,MainActivity.class);
        startActivity(intent);
    }
    public void goBack(View v)
    {
        finish();
    }

    private class AsyncCallSearchList extends AsyncTask<String, Void, ArrayList>
    {
        ProgressDialog pd;

        Activity act;
        ArrayList response;

        String treatment;
        String country;
        String city;
        public AsyncCallSearchList(Activity act,String treatment,String country,String city) {
            this.act = act;
            pd = ProgressDialog.show(act, "", "Please Wait");
            if (treatment.equals("Choose Treatment"))
                this.treatment = "";
            else
                this.treatment = treatment;
            if (country.equals("Choose Country"))
                this.country = "";
            else
                this.country = country;
            if (city.equals("Choose City"))
                this.city = "";
            else
                this.city = city;
        }
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }
        @Override
        protected ArrayList doInBackground(String... params)
        {
            response = WebCalls.wcGetSearchResult(act, treatment, country, city);
            return response;
        }

        @Override
        protected void onPostExecute(ArrayList result)
        {
            if (pd != null && pd.isShowing())
                pd.dismiss();
            super.onPostExecute(result);
            PlacidWay.getInstance().setAl_medInfo(result);

            Intent intent = new Intent(act,SearchResultActivity.class);
            startActivity(intent);
        }

    }

}
