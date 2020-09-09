package com.example.githubtest;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.githubtest.SQL.BTConnection;
import com.example.githubtest.SQL.DBAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.listener.ViewportChangeListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

public class ChartActivity extends AppCompatActivity {


    static Toast toast;
    Date startDate = new Date();
    Date endDate = new Date();
    PlaceholderFragment placeholderFragment;

    public static String formatTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MM-dd", Locale.CHINESE);
        return format.format(date);
    }

    void setDateOnClickListener(View DatePicker, final TextView DateText, final Calendar DateCalendar, final Date date) {
        date.setTime(DateCalendar.getTime().getTime());
        DatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(ChartActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                                DateCalendar.set(Calendar.YEAR, year);
                                DateCalendar.set(Calendar.MONTH, month);
                                DateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                date.setTime(DateCalendar.getTime().getTime());
                                DateText.setText(formatTime(date));
                                placeholderFragment.updateDate(startDate, endDate);
                            }
                        },
                        DateCalendar.get(Calendar.YEAR),
                        DateCalendar.get(Calendar.MONTH),
                        DateCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.show();
            }
        });
    }

    void initDateTime(TextView startDateText, Calendar startDateCalendar, TextView endDateText, Calendar endDateCalendar) {
        startDateText.setText(formatTime(startDateCalendar.getTime()));
        endDateText.setText(formatTime(endDateCalendar.getTime()));
    }



    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.charts);

        TextView startDateText = findViewById(R.id.startDate);
        Calendar startDateCalendar = Calendar.getInstance();
        startDateCalendar.setTimeInMillis(startDateCalendar.getTime().getTime() - DateUtils.DAY_IN_MILLIS * 6);
        setDateOnClickListener(startDateText, startDateText, startDateCalendar, startDate);
        TextView endDateText = findViewById(R.id.endDate);
        Calendar endDateCalendar = Calendar.getInstance();
        setDateOnClickListener(endDateText, endDateText, endDateCalendar, endDate);

        initDateTime(startDateText, startDateCalendar, endDateText, endDateCalendar);

        DBAdapter dbAdapter = new DBAdapter(this);
        dbAdapter.open();

//        dbAdapter.insertBTConnection(new BTConnection(BTConnection.strToDate("2020-09-02 11:22:33"),"02:00:00:00:00:00"));
//        dbAdapter.insertBTConnection(new BTConnection(BTConnection.strToDate("2020-09-03 11:22:33"),"02:00:00:00:00:00"));
//        dbAdapter.insertBTConnection(new BTConnection(BTConnection.strToDate("2020-09-05 11:22:33"),"02:00:00:00:00:00"));


        placeholderFragment = new PlaceholderFragment(
                findViewById(R.id.lineChartView), findViewById(R.id.columnChartView),
                startDate, endDate, dbAdapter, this);

        placeholderFragment.updateDate(startDate, endDate);
    }

}

 class PlaceholderFragment {
    private LineChartView chartTop;
    private ColumnChartView chartBottom;
    private Context context;
    private LineChartData lineData;
    private ColumnChartData columnData;
    private Date startDate, endDate;
    private DBAdapter dbAdapter;

    public PlaceholderFragment(View lineChartView, View columnChartView, Date StartDate, Date EndDate, DBAdapter dbAdapter, Context context) {
        chartTop = (LineChartView) lineChartView;
        chartBottom = (ColumnChartView) columnChartView;
        startDate = StartDate;
        endDate = EndDate;
        this.dbAdapter = dbAdapter;
        this.context = context;
        generateInitialLineData();
        generateColumnData(initDateByDay(EndDate));
    }

    public void updateDate(Date StartDate, Date EndDate) {
        startDate = StartDate;
        endDate = EndDate;
        generateInitialLineData();
        generateLineData();
    }




    /**
     * Generates initial data for line chart. At the begining all Y values are equals 0. That will change when user
     * will select value on column chart.
     */

    String getDay(long time) {
        Date date = new Date(time);
        return new SimpleDateFormat("MM-dd", Locale.CHINESE).format(date);
    }

     private void generateColumnData(Date date) {
         int numColumns = 12;

         Log.v("Column",BTConnection.DateToString(date));

         List<AxisValue> axisValues = new ArrayList<AxisValue>();
         List<Column> columns = new ArrayList<Column>();
         List<SubcolumnValue> values;
         for (int i = 0; i < numColumns; ++i) {

             values = new ArrayList<SubcolumnValue>();

             values.add(new SubcolumnValue(queryByDateTime(new Date(date.getTime()+i*2*60*60*1000)), Color.rgb(243, 156, 18)));


             axisValues.add(new AxisValue(i).setLabel(2*i+"~"+2*(i+1)));

             columns.add(new Column(values).setHasLabelsOnlyForSelected(true));
         }

         columnData = new ColumnChartData(columns);

         columnData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
         columnData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(2));

         chartBottom.setColumnChartData(columnData);

         // Set value touch listener that will trigger changes for chartTop.
//         chartBottom.setOnValueTouchListener(new ValueColumnTouchListener(chartBottom));

         // Set selection mode to keep selected month column highlighted.
         chartBottom.setValueSelectionEnabled(true);

         chartBottom.setZoomType(ZoomType.HORIZONTAL);

     }


    private void generateInitialLineData() {
        long Start = startDate.getTime() / DateUtils.DAY_IN_MILLIS * DateUtils.DAY_IN_MILLIS, End = endDate.getTime() / DateUtils.DAY_IN_MILLIS * DateUtils.DAY_IN_MILLIS;
        int size = (int) ((End - Start) / DateUtils.DAY_IN_MILLIS);
        Start = startDate.getTime();
        End = Start + size * DateUtils.DAY_IN_MILLIS;


        List<AxisValue> axisValues = new ArrayList<AxisValue>();
        List<PointValue> values = new ArrayList<PointValue>();
//            for (int i = 0; i < numValues; ++i) {
//                values.add(new PointValue(i, 0));
//                axisValues.add(new AxisValue(i).setLabel(days[i]));
//            }
        for (int i = 0; ; ++i) {
            values.add(new PointValue(i, 0));
            axisValues.add(new AxisValue(i).setLabel(getDay(Start)));
            Start += DateUtils.DAY_IN_MILLIS;
            if (Start > End) break;
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_GREEN).setCubic(true);

        List<Line> lines = new ArrayList<Line>();
        line.setHasPoints(size <= 7);
        line.setFilled(true);
        lines.add(line);

        lineData = new LineChartData(lines);
        lineData.setAxisXBottom(new Axis(axisValues).setHasLines(true));
        lineData.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(3));

        chartTop.setLineChartData(lineData);

        // For build-up animation you have to disable viewport recalculation.
        chartTop.setViewportCalculationEnabled(false);
        // And set initial max viewport and current viewport- remember to set viewports after data.
        Viewport v = new Viewport(-0.1f, 24, (float) size + 0.1f, 0);
//            Viewport v = new Viewport(chartTop.getMaximumViewport());
        chartTop.setMaximumViewport(v);
        chartTop.setCurrentViewport(v);
        chartTop.setZoomType(ZoomType.HORIZONTAL);
        chartTop.setViewportChangeListener(new LineChartViewportChangeListener(chartTop, 7));
    }

     private int queryByDate(Date date) {
        BTConnection[] res = dbAdapter.queryBTConnectionByDate(new java.sql.Date(date.getTime()), new java.sql.Date(date.getTime()));
        if (res == null) return 0;
         Log.v("length",""+res.length);
         return res.length;
     }

     private int queryByDateTime(Date date) {
         BTConnection[] res = dbAdapter.queryBTConnectionByDate2(new java.sql.Date(date.getTime()), new java.sql.Date(date.getTime()+2*60*60*1000));
         if (res == null) return 0;
         Log.v("length",""+res.length);
         return res.length;
     }

    private void generateLineData() {
        long Start = startDate.getTime();
        // Cancel last animation if not finished.
        chartTop.cancelDataAnimation();
        // Modify data targets
        Line line = lineData.getLines().get(0);// For this example there is always only one line.

        for (int i = 0; i < line.getValues().size(); i++) {

            line.getValues().get(i).setTarget(line.getValues().get(i).getX(), queryByDate(new Date(Start)));
            Start += DateUtils.DAY_IN_MILLIS;
//                } else {
//                    line.getValues().get(i).setTarget(line.getValues().get(i).getX(), 0);
//                }
        }
        // Start new data animation with 300ms duration;
        chartTop.startDataAnimation(300);
        chartTop.setOnValueTouchListener(new ValueLineTouchListener(chartTop));
    }


//     private class ValueColumnTouchListener implements ColumnChartOnValueSelectListener {
//         ColumnChartView columnChartView;
//
//         public ValueColumnTouchListener(ColumnChartView columnChartView) {
//             this.columnChartView = columnChartView;
//         }
//
//         @Override
//         public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
//             generateLineData();
//
//             long Start = startDate.getTime() / DateUtils.DAY_IN_MILLIS * DateUtils.DAY_IN_MILLIS, End = endDate.getTime() / DateUtils.DAY_IN_MILLIS * DateUtils.DAY_IN_MILLIS;
//             int size = (int) ((End - Start) / DateUtils.DAY_IN_MILLIS) + 1;
//             if (value.getValue() * 60 > 120) {
//                 if (ChartView.toast == null) {
//                     toast = Toast.makeText(context, "在选定的" + size + "天内，共花费了约" + (float) (Math.round(value.getValue() * 10)) / 10 + "小时在" + getTags()[columnIndex].name + "上", Toast.LENGTH_SHORT);
//                 } else {
//                     toast.setText("在选定的" + size + "天内，共花费了约" + (float) (Math.round(value.getValue() * 10)) / 10 + "小时在" + getTags()[columnIndex].name + "上");
//                     toast.setDuration(Toast.LENGTH_SHORT);
//                 }
//                 toast.show();
//             } else {
//                 if (ChartView.toast == null) {
//                     toast = Toast.makeText(context, "在选定的" + size + "天内，共花费了" + (int) (value.getValue() * 60) + "分钟在" + getTags()[columnIndex].name + "上", Toast.LENGTH_SHORT);
//                 } else {
//                     toast.setText("在选定的" + size + "天内，共花费了" + (int) (value.getValue() * 60) + "分钟在" + getTags()[columnIndex].name + "上");
//                     toast.setDuration(Toast.LENGTH_SHORT);
//                 }
//                 toast.show();
//             }
//         }
//
//         @Override
//         public void onValueDeselected() {
//             generateLineData(ChartUtils.COLOR_GREEN, 0, 0);
//         }
//     }

     public static Date initDateByDay(Date date){
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(date);
         calendar.set(Calendar.HOUR_OF_DAY, 0);
         calendar.set(Calendar.MINUTE, 0);
         calendar.set(Calendar.SECOND, 0);
         return calendar.getTime();
     }

     private class ValueLineTouchListener implements LineChartOnValueSelectListener {
         LineChartView lineChartView;
         String tag;

         public ValueLineTouchListener(LineChartView lineChartView) {
             this.lineChartView = lineChartView;
         }


         @Override
         public void onValueSelected(int lineIndex, int pointIndex, PointValue pointValue) {

             long Start = startDate.getTime();
             long Now = (Start + DateUtils.DAY_IN_MILLIS * pointIndex);
             String date = getDay(Now);
             generateColumnData(new Date(initDateByDay(startDate).getTime()+DateUtils.DAY_IN_MILLIS * pointIndex));
                 if (ChartActivity.toast == null) {
                     ChartActivity.toast = Toast.makeText(context, "在" + date + "，共扫描到" + (int) pointValue.getY()  + "条蓝牙连接记录" , Toast.LENGTH_SHORT);
                 } else {
                     ChartActivity.toast.setText("在" + date + "，共扫描到" +  (int) pointValue.getY()  + "条蓝牙连接记录");
                     ChartActivity.toast.setDuration(Toast.LENGTH_SHORT);
                 }
                 ChartActivity.toast.show();
         }

         @Override
         public void onValueDeselected() {
         }
     }

    private class LineChartViewportChangeListener implements ViewportChangeListener {
        LineChartView lineChartView;
        float changeSize;

        public LineChartViewportChangeListener(LineChartView lineChartView, float changeSize) {
            this.lineChartView = lineChartView;
            this.changeSize = changeSize;
        }


        @Override
        public void onViewportChanged(Viewport viewport) {
            if (viewport.right - viewport.left <= changeSize) {
                lineChartView.getLineChartData().getLines().get(0).setHasPoints(true);
            } else {
                lineChartView.getLineChartData().getLines().get(0).setHasPoints(false);
            }
        }
    }
}