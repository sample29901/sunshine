package com.example.android.sunshine;
//API key  98bd9ea6b58ec679fd7281e900797081
//http://api.openweathermap.org/data/2.5/forecast/
//daily?q=London&mode=json&units=metric&cnt=7&lang=zh_cn&appid=98bd9ea6b58ec679fd7281e900797081
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.sunshine.sync.SunshineSyncAdapter;


public class MainActivity extends AppCompatActivity implements ForecastFragment.Callback{

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;
    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocation = Utility.getPreferredLocation(this);

        setContentView(R.layout.activity_main);
        if (findViewById(R.id.weather_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            //操作栏和今天item之间没有阴影
            getSupportActionBar().setElevation(0f);
        }
        ForecastFragment ff = (ForecastFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_forecast);
        ff.setUseTodayLayout(!mTwoPane);

        SunshineSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation( this );
        // update the location in our second pane using the fragment manager
        if (location != null && !location.equals(mLocation)) {
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager()
                    .findFragmentById(R.id.fragment_forecast);
            if ( null != ff ) {
                ff.onLocationChanged();
            }
            DetailFragment df = (DetailFragment)getSupportFragmentManager()
                    .findFragmentByTag(DETAILFRAGMENT_TAG);
            if(df != null){
                df.onLocationChanged(location);
            }
            mLocation = location;
        }
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        if (mTwoPane){
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, dateUri);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(
                    R.id.weather_detail_container,fragment, DETAILFRAGMENT_TAG)
                    .commit();


        }else{
            Intent intent = new Intent(this,DetailActivity.class).setData(dateUri);
            startActivity(intent);
        }

    }


    //long 转 普通时间函数测试
    /*public static String getStringFromLong(long millis)
    {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date dt = new Date(millis);
        return sdf.format(dt);
    }

    public static String getDate(long millis)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return String.format("%1$d年%2$d月%3$d日", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+ 1,cal.get(Calendar.DAY_OF_MONTH));
    }*/


    //获取天气按钮点击事件
    /*public void catchWeather(View view){
        ForecastFragment.FetchWeatherTask weather = forecastFragment.new FetchWeatherTask();
        String position;
        EditText editext = (EditText)findViewById(R.id.city);
        position = editext.getText().toString();
        weather.execute(position);
    }*/

}