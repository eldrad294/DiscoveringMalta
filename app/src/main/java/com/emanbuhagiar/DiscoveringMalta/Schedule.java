package com.emanbuhagiar.DiscoveringMalta;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;


public class Schedule extends ActionBarActivity implements MonthLoader.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener {

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    WeekView mWeekView;
    ArrayList<String> act = new ArrayList<String>();
    ArrayList<String> price = new ArrayList<String>();
    ArrayList<String> start = new ArrayList<String>();
    ArrayList<String> duration = new ArrayList<String>();
    String[] date;
    Long DateLong;
    List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
    int month;


    // double[] price,start;
    int bundleSize,day;

    Calendar cal = Calendar.getInstance();

    Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        ctx = this;
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        act = bundle.getStringArrayList("Name");
        price = bundle.getStringArrayList("Price");
        start = bundle.getStringArrayList("Start");
        duration = bundle.getStringArrayList("Duration");
        date = bundle.getStringArray("Date");
        DateLong = bundle.getLong("DateLong");

        bundleSize = bundle.size();
        month = Integer.parseInt(date[1]);

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        Date d = new Date(DateLong);
        cal.setTime(d);
        mWeekView.goToDate(cal);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);


    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, event.getName() + " €" + getPrice(event.getName()), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        setContentView(R.layout.activity_schedule);
//        setupDateTimeInterpreter(id == R.id.action_week_view);
//        switch (id){
//            case R.id.action_today:
//                mWeekView.goToToday();
//                return true;
//            case R.id.action_day_view:
//                if (mWeekViewType != TYPE_DAY_VIEW) {
//                    item.setChecked(!item.isChecked());
//                    mWeekViewType = TYPE_DAY_VIEW;
//                    mWeekView.setNumberOfVisibleDays(1);
//
//                    // Lets change some dimensions to best fit the view.
//                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
//                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
//                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
//                }
//                return true;
//            case R.id.action_three_day_view:
//                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
//                    item.setChecked(!item.isChecked());
//                    mWeekViewType = TYPE_THREE_DAY_VIEW;
//                    mWeekView.setNumberOfVisibleDays(3);
//
//                    // Lets change some dimensions to best fit the view.
//                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
//                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
//                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
//                }
//                return true;
//            case R.id.action_week_view:
//                if (mWeekViewType != TYPE_WEEK_VIEW) {
//                    item.setChecked(!item.isChecked());
//                    mWeekViewType = TYPE_WEEK_VIEW;
//                    mWeekView.setNumberOfVisibleDays(7);
//
//                    // Lets change some dimensions to best fit the view.
//                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
//                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
//                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
//                }
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.ENGLISH);
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" d/MM", Locale.ENGLISH);

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        mWeekView.clearFocus();
        //Calendar startTime = Calendar.getInstance();
        int startHr, DurationHr,nextHr = 0;
        double startMin, DurationMin;
        day = Integer.parseInt(date[2]);
        Random rand = new Random();

        Date d = new Date(DateLong);
        int test = Integer.parseInt(date[1]);
        //newMonth = newMonth - 1;
        if (month == newMonth) {

            for (int i = 0; i < start.size(); i++) {
                int r = rand.nextInt(255);
                int g = rand.nextInt(255);
                int b = rand.nextInt(255);
                int randomColor = Color.rgb(r, g, b);

                startHr = Integer.parseInt((start.get(i)).split("\\.")[0]);
                startMin = Double.parseDouble("0." + (start.get(i)).split("\\.")[1]) * 60;

                if (i < start.size() - 1) {
                    if (start.get(i + 1) != null)
                        nextHr = Integer.parseInt((start.get(i + 1)).split("\\.")[0]);
                    else
                        nextHr = 0;
                }
                DurationHr = Integer.parseInt(duration.get(i)) / 60;
                String temp = String.valueOf(Double.parseDouble(duration.get(i)) / 60);//.split("\\.").toString();
                Double tempD = Double.parseDouble("0." + temp.split("\\.")[1]) * 60;
                DurationMin = tempD;

                Double curr = Double.parseDouble(start.get(i));
                Double next = 0.0;

                if (i < start.size() - 1)
                    next = Double.parseDouble(start.get(i + 1));

                if (nextHr != 0 && next < curr && next != 0) {
                    day = day + 1;
                }

                if (day == 32)
                    month = 1;

                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, startHr);
                startTime.set(Calendar.MINUTE, (int) startMin);
                if (newMonth == 1)
                    startTime.set(Calendar.MONTH, 0);
                else
                    startTime.set(Calendar.MONTH, month - 1);

                startTime.set(Calendar.DAY_OF_MONTH, day);
                startTime.set(Calendar.YEAR, Integer.parseInt(date[0]));
                Calendar endTime = (Calendar) startTime.clone();
                endTime.add(Calendar.HOUR, DurationHr);
                endTime.add(Calendar.MINUTE, (int) DurationMin);
                if(newMonth == 1)
                    endTime.set(Calendar.MONTH, 0);
                else
                    endTime.set(Calendar.MONTH, month - 1);
                WeekViewEvent event = new WeekViewEvent(1, act.get(i), startTime, endTime);
                event.setColor((randomColor));
                events.add(event);
            }
        }
        return events;
    }

    private String getEventTitle(Calendar time) {

        for (int i=0; i<act.size(); i++)
            return String.format(act.get(i), time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE)
                , time.get(Calendar.DAY_OF_MONTH), time.get(Calendar.MONTH)+1);
        return null;
    }

    private String getPrice(String event)
    {
        NumberFormat formatter = new DecimalFormat("#0.00");
        int index = act.indexOf(event);
        return (formatter.format(Double.parseDouble(price.get(index))));
    }
}
