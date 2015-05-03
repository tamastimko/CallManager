package hu.uniobuda.nik.gabor_tamas.callmanager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.GregorianCalendar;


public class SilentOptions extends Activity {
    Button startTimeButton, endTimeButton;
    TextView startTimeTextView, endTimeTextView;
    RadioButton vibrateRadioBtn, silentRadioBtn;

    private AudioManager myAudioManager;
    private NotificationManager myNotificationManager;

    private static final String defaultStartTimeTextViewContent = "Start Time: No value set";
    private static final String defaultEndTimeTextViewContent = "End Time: No value set";

    public static final String fileName = "MySettings";
    protected final static int ACTIVITY_START = 1;
    protected final static int ACTIVITY_END = 2;
    private int startHour, startMinute, endHour, endMinute;
    private Date startDate, endDate, currentDate;
    private String startDateString, endDateString, currentDateString;
    private boolean existingPendingIntent;
    private int notificationID = 100;

    AlarmManager alarmManager;
    Intent intentAlarm;
    PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_silent_options);

        startTimeButton = (Button) findViewById(R.id.btn_kezdo);
        endTimeButton = (Button) findViewById(R.id.btn_vege);
        startTimeTextView = (TextView) findViewById(R.id.startTime);
        endTimeTextView = (TextView) findViewById(R.id.endTime);
        vibrateRadioBtn = (RadioButton) findViewById(R.id.rb_vibrate);
        silentRadioBtn = (RadioButton) findViewById(R.id.rb_silent);

        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        myNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        startDate = null;
        endDate = null;
        currentDate = null; 

        startHour = 0;
        startMinute = 0;
        endHour = 0;
        endMinute = 0;

        startDateString = "";
        endDateString = "";
        currentDateString = "";


        //a lementett adatok betöltése, amennyiben voltak, ha nem voltak default értékek adása
        SharedPreferences settings = getSharedPreferences(fileName,0);
        startTimeTextView.setText(settings.getString("start",defaultStartTimeTextViewContent));
        endTimeTextView.setText(settings.getString("end",defaultEndTimeTextViewContent));
        vibrateRadioBtn.setChecked(settings.getBoolean("vibrate",false));
        silentRadioBtn.setChecked(settings.getBoolean("silent",false));
        existingPendingIntent = settings.getBoolean("existingPendingIntent",false);

    }

    //SharedPreference használata az aktuális adatok lementésére
    public void onStop()
    {
        super.onStop();
        if(pendingIntent != null)
        {
            SharedPreferences settings = getSharedPreferences(fileName,0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("start","Start Time: " + startDateString);
            editor.putString("end","End Time: " + endDateString);
            editor.putBoolean("silent",silentRadioBtn.isChecked());
            editor.putBoolean("vibrate",vibrateRadioBtn.isChecked());
            editor.putBoolean("existingPendingIntent",existingPendingIntent);
            editor.commit();
        }


    }

    public void startTimePicker(View view)
    {
        Intent timePickerActivity = new Intent(this, TimePickerActivity.class);
        startActivityForResult(timePickerActivity, ACTIVITY_START);
    }

    public void endTimePicker(View view)
    {
        Intent timePickerActivity = new Intent(this, TimePickerActivity.class);
        startActivityForResult(timePickerActivity, ACTIVITY_END);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Megnézzük mi jött vissza, azt kiírjuk egy textViewba illetve megkapja az adott string is értékkent, amit később használunk
        if(resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();
            switch (requestCode) {
                case ACTIVITY_START:
                    startHour = extras.getInt("hour");
                    startMinute = extras.getInt("minute");
                    //meg kell nézni, hogy a perc 10-nél kisebb-e, így kerüljük el ezt a formátumot: 20:9. Ehelyett lesz 20:09
                    if (startMinute < 10) {
                        startTimeTextView.setText("Start Time: " + startHour + ":0" + startMinute);
                        startDateString = startHour + ":0" + startMinute;
                    } else {
                        startTimeTextView.setText("Start Time: " + startHour + ":" + startMinute);
                        startDateString = startHour + ":" + startMinute;
                    }
                    break;

                case ACTIVITY_END:
                    endHour = extras.getInt("hour");
                    endMinute = extras.getInt("minute");
                    if (endMinute < 10) {
                        endTimeTextView.setText("End Time: " + endHour + ":0" + endMinute);
                        endDateString = endHour + ":0" + endMinute;
                    } else {
                        endTimeTextView.setText("End Time: " + endHour + ":" + endMinute);
                        endDateString = endHour + ":" + endMinute;
                    }
                    break;
            }

        }
        //ha cancellel tér vissza, akkor kiírjuk, hogy nincs beállítva érték
        else if (resultCode == RESULT_CANCELED)
        {
            switch (requestCode)
            {
                case ACTIVITY_START:
                    startTimeTextView.setText(getString(R.string.startTimeNoValue));
                    break;
                case ACTIVITY_END:
                    endTimeTextView.setText(getString(R.string.endTimeNoValue));
                    break;
            }


        }

    }

    //főképernyők, ha OK-ot nyomtak..
    public void applyMode(View view)
    {
        //Ellenőrzés, hogy minden adat létezik-e, amire szükség van.
        if (!startDateString.equals("") && !endDateString.equals(""))
        {
            if (!startDateString.equals("0:0") && !endDateString.equals("0:0"))
            {
                if (silentRadioBtn.isChecked() || vibrateRadioBtn.isChecked())
                {
                    //adatok betevése bundlebe, átadása az alarmreceiver osztálynak..
                    Bundle bundle = new Bundle();
                    bundle.putString("startDate", startDateString);
                    bundle.putString("endDate", endDateString);
                    if (silentRadioBtn.isChecked()) {
                        bundle.putBoolean("silent", true);
                        bundle.putBoolean("vibrate", false);
                    } else if (vibrateRadioBtn.isChecked()) {
                        bundle.putBoolean("silent", false);
                        bundle.putBoolean("vibrate", true);
                    }


                    intentAlarm = new Intent(this, AlarmReceiver.class);
                    intentAlarm.putExtras(bundle);

                    Long time = new GregorianCalendar().getTimeInMillis(); //1 nap = 24*60*60*1000

                    //30 mp-enként nézi meg, hogy a beállított időpont megegyezik-e az aktuálissal
                    pendingIntent = PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);
                    existingPendingIntent = true;
                    alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 30 * 1000, pendingIntent);
                    Toast.makeText(this, getString(R.string.optionSet), Toast.LENGTH_SHORT).show();

                    //Notification létrehozása
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                    mBuilder.setContentTitle(getString(R.string.notificationTitle));
                    mBuilder.setContentText("Between " + startDateString + " and " + endDateString);
                    mBuilder.setTicker(getString(R.string.notificationTicker));
                    mBuilder.setSmallIcon(R.mipmap.ic_launcher);
                    mBuilder.setOngoing(true); //nem swipeolható


                    myNotificationManager.notify(notificationID,mBuilder.build());


                }
                else //Ha nem állt rendelkezésre minden adat, figyelmeztetés feldobása, hogy valami nem OK.
                {
                    AlertDialog.Builder choseOptionDialog = new AlertDialog.Builder(this);
                    choseOptionDialog.setTitle(getString(R.string.cannotProceed));
                    choseOptionDialog.setMessage(getString(R.string.chooseVibrateOrSilent));
                    choseOptionDialog.setPositiveButton(R.string.PositiveButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
                    AlertDialog choseOption = choseOptionDialog.create();
                    choseOption.show();
                }

            }
            else
            {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(getString(R.string.cannotProceed));
                alertDialogBuilder.setMessage(getString(R.string.selectStartAndEndTime));
                alertDialogBuilder.setPositiveButton(R.string.PositiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }

        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.cannotProceed));
            alertDialogBuilder.setMessage(getString(R.string.selectStartAndEndTime));
            alertDialogBuilder.setPositiveButton(R.string.PositiveButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return;
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    //Ha ki akarja kapcsolni...
    public void cancelAlarm(View view)
    {
        if (existingPendingIntent) //létezik-e beállított
        {
            if(intentAlarm != null) //létezik-e beállított
            {
                alarmManager.cancel(PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
                Toast.makeText(this, getString(R.string.serviceCancelled), Toast.LENGTH_LONG).show();
                existingPendingIntent = false;
                startTimeTextView.setText(defaultStartTimeTextViewContent);
                endTimeTextView.setText(defaultEndTimeTextViewContent);
                vibrateRadioBtn.setChecked(false);
                silentRadioBtn.setChecked(false);
                startDateString = "";
                endDateString = "";
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                myNotificationManager.cancel(notificationID);
            }
            else //Hiba, egyenlőre nincs működő megoldásom, így kezelek annyit, hogy ne fagyjon ki, illetve egy figyelmeztetés feldobása
            {
                AlertDialog.Builder aBuilder = new AlertDialog.Builder(this);
                aBuilder.setTitle(getString(R.string.underConst));
                aBuilder.setMessage("A service was started. It was set between " + startTimeTextView.getText().toString() + " and " + endTimeTextView.getText().toString() + ". For cancel, please specify a new interval.");
                aBuilder.setPositiveButton(R.string.PositiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });

                AlertDialog alertDialog = aBuilder.create();
                alertDialog.show();
            }

        }
        else //ha nem volt beállítva semmi...
        {
            Toast.makeText(this,getString(R.string.notingToDo),Toast.LENGTH_LONG).show();
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
