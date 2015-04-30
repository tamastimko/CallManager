package hu.uniobuda.nik.gabor_tamas.callmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.FileInputStream;
import java.lang.reflect.Method;

/**
 * Created by Tram on 2015.03.24..
 * bejövő hívás esetén elinduló programrész
 */
public class IncomingCall extends BroadcastReceiver {
    TelephonyManager tmgr;
    boolean isBlackList;
    String[] numbers;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            //először ellenőrizzük, hogy be van-e kapcsolva a funkció
            SharedPreferences sp=context.getSharedPreferences("callEnabled",context.MODE_PRIVATE);
            if(sp.getBoolean("enabled",false)) {
                FileInputStream fis;
                byte[] buffer;
                int length;
                String tmp;
                //a megfelelő listából kivesszük a számokat
                if(sp.getBoolean("isBlackList",true)){
                    isBlackList=true;
                    fis=context.openFileInput("BlackListContacts");
                    length=fis.available();
                    buffer=new byte[length];
                    fis.read(buffer);
                    fis.close();
                    tmp=new String(buffer);
                    if(tmp.contains("\t")) {
                        numbers=tmp.split("\n");
                    }else
                        return;
                }else {
                    isBlackList=false;
                    fis=context.openFileInput("WhiteListContacts");
                    length=fis.available();
                    buffer=new byte[length];
                    fis.read(buffer);
                    fis.close();
                    tmp=new String(buffer);
                    if(tmp.contains("\t")){
                        numbers=tmp.split("\n");
                    }else
                        return;
                }
                tmgr = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);
                //Create Listener
                MyPhoneStateListener PhoneListener = new MyPhoneStateListener();
                // Register listener for LISTEN_CALL_STATE
                tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);
            }else
                return;
        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }
    }

    static  boolean done=false; //ez a metódus többször hívódik meg híváskor ezért ezzel a boolean-nal korrigáljuk
    private class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            String[] tmp;
            //listától függően a teendő megvalósítása
            if(isBlackList && !done){
                done=true;  //done-al biztosítjuk, hogy a metódus egyszer lépjen be a feltétel ágba
                for (int i=0;i<numbers.length;i++){
                    tmp=numbers[i].split("\t"); //blacklistnél ha a listában levő szám hív elutasítjuk
                    if(tmp[1].equals(incomingNumber)){
                        rejectCall();
                        break;
                    }
                }
            }else if(!isBlackList && !done) {
                done=true;
                for (int i=0;i<numbers.length;i++){
                    tmp=numbers[i].split("\t");
                    if(tmp[1].equals(incomingNumber)){
                        tmgr=null;
                        return; //whitelistnél ha nincs a listában a szám akkor utasítjuk el
                    }
                }
                rejectCall();
            }
        }
    }

    //a hívás elutasításának metódusa
    private void rejectCall() {
        try {
            // Get the getITelephony() method
            Class<?> classTelephony = Class.forName(tmgr.getClass().getName());
            Method method = classTelephony.getDeclaredMethod("getITelephony");
            // Disable access check
            method.setAccessible(true);

            // Invoke getITelephony() to get the ITelephony interface
            Object telephonyInterface = method.invoke(tmgr);
            // Get the endCall method from ITelephony
            Class<?> telephonyInterfaceClass = Class.forName(telephonyInterface.getClass().getName());
            Method methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall");
            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
