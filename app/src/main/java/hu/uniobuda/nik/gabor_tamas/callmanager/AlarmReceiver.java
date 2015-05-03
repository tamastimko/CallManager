package hu.uniobuda.nik.gabor_tamas.callmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tram on 2015.04.25..
 */
public class AlarmReceiver extends BroadcastReceiver {
    Date startDate, endDate, currentDate;


    @Override
    public void onReceive(Context context, Intent intent)
    {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        Bundle bundle = intent.getExtras();
        String startDateString = bundle.getString("startDate");
        String endDateString = bundle.getString("endDate");
        boolean vibrate = bundle.getBoolean("vibrate");
        boolean silent = bundle.getBoolean("silent");


        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String currentDateString = df.format(new Date());

        try
        {
            startDate = df.parse(startDateString);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        try
        {
            endDate = df.parse(endDateString);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        try
        {
            currentDate = df.parse(currentDateString);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        if(currentDate.after(startDate) && currentDate.before(endDate))
        {
            if (vibrate && audioManager.getRingerMode() != AudioManager.RINGER_MODE_VIBRATE)
            {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                Toast.makeText(context, context.getString(R.string.vibrateSet), Toast.LENGTH_LONG).show();
            }
            else if(silent && audioManager.getRingerMode() != AudioManager.RINGER_MODE_SILENT)
            {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                Toast.makeText(context,context.getString(R.string.silentModeSet),Toast.LENGTH_LONG).show();
            }
        }
        else if (audioManager.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
        {
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Toast.makeText(context,context.getString(R.string.normalModeSet),Toast.LENGTH_LONG).show();
        }

    }
}
