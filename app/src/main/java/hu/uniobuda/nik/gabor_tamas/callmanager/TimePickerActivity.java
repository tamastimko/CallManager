package hu.uniobuda.nik.gabor_tamas.callmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Tram on 2015.04.25..
 */
public class TimePickerActivity extends ActionBarActivity {
    TimePicker pickertime;
    TextView info;
    Intent resultIntent;
    int Ohour, Ominute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_picker);

        //pickerdate = (DatePicker) findViewById(R.id.pickerdate);
        pickertime = (TimePicker) findViewById(R.id.pickertime);
        info = (TextView) findViewById(R.id.info);


        Calendar now = Calendar.getInstance();

        pickertime.setCurrentHour(now.get(Calendar.HOUR_OF_DAY));
        pickertime.setCurrentMinute(now.get(Calendar.MINUTE));

        Ohour = now.get(Calendar.HOUR_OF_DAY);
        Ominute = now.get(Calendar.MINUTE);

        pickertime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Ohour = hourOfDay;
                Ominute = minute;
                if(Ominute < 10)
                {
                    info.setText(hourOfDay + ":0" + minute);
                }
                else
                {
                    info.setText(hourOfDay + ":" + minute);
                }

            }

        });

    }

    public void proceed(View view)
    {
        Bundle bundle = new Bundle();

        bundle.putInt("hour", Ohour);
        bundle.putInt("minute", Ominute);

        resultIntent = new Intent();
        resultIntent.putExtras(bundle);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void cancelChoose(View view)
    {
        Bundle bundle = new Bundle();
        bundle.putString("hour", "No value set");
        bundle.putString("minute", "No value set");

        resultIntent = new Intent();
        resultIntent.putExtras(bundle);
        setResult(RESULT_CANCELED, resultIntent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_picker, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
