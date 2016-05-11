package com.emanbuhagiar.DiscoveringMalta;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.fourmob.datetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoggedInActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final String DATEPICKER_TAG = "datepicker";

    Context ctx;
    String dateToServer = "";
    List<CheckBox> checkBoxList = new ArrayList<>();

    ArrayList<String> Price = new ArrayList<String>();
    ArrayList<String> startTime = new ArrayList<String>();
    ArrayList<String> Name = new ArrayList<String>();
    ArrayList<String> Duration = new ArrayList<String>();
    String[] Date;
    long dateLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        final Calendar calendar = Calendar.getInstance();
        final LinearLayout layout = (LinearLayout) findViewById(R.id.checkboxLayout);
        final DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        ctx = this;
        AQuery aq = new AQuery(ctx);
        AQUtility.setDebug(true);

        findViewById(R.id.dateButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.setYearRange(2015, 2026);
                datePickerDialog.show(getSupportFragmentManager(), DATEPICKER_TAG);
            }
        });

        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this);
            }
        }

        aq.ajax("http://discovermaltaapi.azurewebsites.net/api/Types", JSONArray.class, new AjaxCallback<JSONArray>() {
            @Override
            public void callback(String url, JSONArray json, AjaxStatus status) {
                if (json != null) {
                    for (int i = 0; i < json.length(); i++) {
                        CheckBox checkBox = new CheckBox(ctx);
                        try {
                            JSONObject obj = json.getJSONObject(i);
//                            activities.add(obj.get("Description").toString());
                            checkBox.setText(obj.get("Description").toString());
                            checkBox.setHint(obj.get("Id").toString());
                            checkBoxList.add(checkBox);
                            layout.addView(checkBox);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        Button btn = (Button) findViewById(R.id.sendButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String startDate;
                int days;
                double budget;
//                int[] activityTypesId;

                TextView StartDate = (TextView) findViewById(R.id.startDate);
                TextView NumDays = (TextView) findViewById(R.id.numDays);
                TextView Budget = (TextView) findViewById(R.id.budget);

                startDate = StartDate.getText().toString();
                days = Integer.parseInt(NumDays.getText().toString());
                budget = Double.parseDouble(Budget.getText().toString());

                Map<String, Object> params = new HashMap<>();
                params.put("startDate", startDate + "T00:00:00.000Z");
                if (days == 0) {
                    Toast.makeText(LoggedInActivity.this, "The number of days cannot be 0!", Toast.LENGTH_LONG).show();
                }
                else {
                    params.put("days", days);
                }
                if (budget == 0) {
                    Toast.makeText(LoggedInActivity.this, "The budget cannot be €0!", Toast.LENGTH_LONG).show();
                }
                else {
                    params.put("budget", budget);
                }
                for (CheckBox c: checkBoxList) {
                    if (c.isChecked()) {
                        params.put("activityTypeIds", c.getHint());
                    }
                }

                AQuery aq = new AQuery(ctx);
                AQUtility.setDebug(true);

                AjaxCallback<JSONArray> cb = new AjaxCallback<JSONArray>() {
                    @Override
                    public void callback(String url, JSONArray json, AjaxStatus status) {
                        if (status.getCode() == 200) {
                            if (json != null) {
                                //successful ajax call, show status code and json content
                                for (int i = 0; i < json.length(); i++) {
                                    try {
                                        JSONObject obj = json.getJSONObject(i);
                                        //startTime.add(Double.parseDouble(obj.get("start").toString()));
                                        Name.add(obj.get("Name").toString());
                                        Price.add(obj.get("Price").toString());
                                        startTime.add(obj.get("start").toString());
                                        Duration.add(obj.get("Duration").toString());
                                        //Price.add(Double.parseDouble(obj.get("Price").toString()));

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                Log.d("test", status.getCode() + ":" + json.toString());
                                Intent k = new Intent(ctx, Schedule.class);
                                Bundle extras = new Bundle();
                                extras.putStringArrayList("Start", startTime);
                                extras.putStringArrayList("Name", Name);
                                extras.putStringArrayList("Price", Price);
                                extras.putStringArrayList("Duration", Duration);

                                TextView StartDate = (TextView) findViewById(R.id.startDate);
                                String whole = StartDate.getText().toString();
                                String[] dateParts = {whole.substring(0, 4), whole.substring(5, 7), whole.substring(8, 10)};
                                extras.putStringArray("Date", dateParts);
                                extras.putLong("DateLong", dateLong);
                                k.putExtras(extras);
                                LoggedInActivity.this.startActivity(k);
                                //ctx.startActivity(k);
                            }
                        } else {
                            //ajax error, show error code
                            Log.d("test", "Error:" + status.getCode() + " " + status.getMessage() + " " + status.getError() + " er: " + status);
                        }
                    }
                };

                cb.url("http://discovermaltaapi.azurewebsites.net/api/Schedule");
                cb.params(params).type(JSONArray.class); //no this for get
                aq.ajax(cb);
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        TextView StartDate = (TextView) findViewById(R.id.startDate);
        String newMonth = String.format("%02d", month + 1);
        String newDay = String.format("%02d", day);
        StartDate.setText(year + "-" + newMonth + "-" + newDay);
        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.YEAR, year);
        temp.set(Calendar.MONTH, month);
        temp.set(Calendar.DAY_OF_MONTH, day);
        dateLong = temp.getTimeInMillis();
    }
}
