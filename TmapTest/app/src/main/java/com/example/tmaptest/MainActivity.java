package com.example.tmaptest;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

public class MainActivity extends AppCompatActivity {
    TMapView tMapView;
    TMapPoint tMapPointStart = new TMapPoint(37.582191, 127.001915); // 혜화역
    TMapPoint tMapPointEnd = new TMapPoint(37.500628, 127.036392); // 역삼역
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //지도 부분
        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.tmap);
        tMapView = new TMapView(this);
        tMapView.setSKTMapApiKey("8f94f9d2-b48c-4f64-86d3-a6dbc31e8cfb");
        linearLayoutTmap.addView( tMapView );
        tMapView.setCenterPoint(127.001691,37.540263,  true);
        tMapView.setZoomLevel(12);


        //경로 부분
        TMapPolyLine polyLine = new TMapPolyLine();
        PathAsync pathAsync = new PathAsync();
        pathAsync.execute(polyLine);
    }

    class PathAsync extends AsyncTask<TMapPolyLine, Void, TMapPolyLine> {
        @Override
        protected TMapPolyLine doInBackground(TMapPolyLine... tMapPolyLines) {
            TMapPolyLine tMapPolyLine = tMapPolyLines[0];
            try {
                tMapPolyLine = new TMapData().findPathDataWithType(TMapData.TMapPathType.CAR_PATH, tMapPointStart, tMapPointEnd);
                tMapPolyLine.setLineColor(Color.BLUE);
                tMapPolyLine.setLineWidth(3);


            }catch(Exception e) {
                e.printStackTrace();
                Log.e("error",e.getMessage());
            }
            return tMapPolyLine;
        }

        @Override
        protected void onPostExecute(TMapPolyLine tMapPolyLine) {
            super.onPostExecute(tMapPolyLine);
            tMapView.addTMapPolyLine("Line1", tMapPolyLine);
        }
    }
}
