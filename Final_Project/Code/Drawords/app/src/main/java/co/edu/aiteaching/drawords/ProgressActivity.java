package co.edu.aiteaching.drawords;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

public class ProgressActivity extends AppCompatActivity {

    private String userID;
    private RadarChart chart;


    private HashMap<String,String> hm_images = new HashMap<String, String>();
    private HashMap<String,String> hm_names = new HashMap<String, String>();
    private HashMap<String,ArrayList<String>> hm_cat = new HashMap<String, ArrayList<String>>();

    private ArrayList<String> categories;
    private ArrayList<RadarEntry> gScores;
    private ArrayList<RadarEntry> bScores;
    private int maxV = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);


        userID = getIntent().getStringExtra("iduser");

        categories = new ArrayList<String>();
        gScores = new ArrayList<RadarEntry>();
        bScores = new ArrayList<RadarEntry>();


        chart = (RadarChart) findViewById(R.id.radarChart);
        chart.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        chart.getDescription().setEnabled(false);

        chart.setWebLineWidth(1f);
        chart.setWebColor(Color.LTGRAY);
        chart.setWebLineWidthInner(1f);
        chart.setWebColorInner(Color.LTGRAY);
        chart.setWebAlpha(100);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new RadarMarkerView(this, R.layout.radar_makerview);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart
        getData();




    }
    private void getData(){
        FirebaseDatabase.getInstance().getReference().child("scores").child(userID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot


                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                            if (dsp.getKey().startsWith("G")) {
                                categories.add(dsp.getKey().substring(1));
                                gScores.add(new RadarEntry((Long) dsp.getValue()));
                            } else {
                                bScores.add(new RadarEntry((Long) dsp.getValue()));

                            }
                            if ( (Long)dsp.getValue() > maxV){
                                maxV = ((Long) dsp.getValue()).intValue();
                            }
                        }
                        setData();
                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }
    private void setData() {

        RadarDataSet set1 = new RadarDataSet(gScores, "Good");
        set1.setColor(Color.rgb(121, 162, 175));
        set1.setFillColor(Color.rgb(121, 162, 175));

        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);

        RadarDataSet set2 = new RadarDataSet(bScores, "Bad");
        set2.setColor(Color.rgb(203, 110, 129));
        set2.setFillColor(Color.rgb(203, 110, 129));

        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set1);
        sets.add(set2);

        RadarData data = new RadarData(sets);

        data.setValueTextSize(15f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        chart.setData(data);
        chart.invalidate();

        configure();
    }

    private void configure(){
        chart.animateXY(2000, 2000, Easing.EaseInOutQuad);

        XAxis xAxis = chart.getXAxis();

        xAxis.setTextSize(15f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new ValueFormatter() {

            //private final String[] mActivities = new String[]{"Burger", "Steak", "Salad", "Pasta", "Pizza"};


            @Override
            public String getFormattedValue(float value) {
                return categories.get((int) value % categories.size());
            }
        });
        xAxis.setTextColor(Color.WHITE);

        YAxis yAxis = chart.getYAxis();

        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(15f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(maxV);
        yAxis.setDrawLabels(false);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);

        l.setXEntrySpace(5f);
        l.setYEntrySpace(5f);
        l.setTextColor(Color.WHITE);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}

