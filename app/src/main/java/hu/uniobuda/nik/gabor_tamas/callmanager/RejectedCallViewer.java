package hu.uniobuda.nik.gabor_tamas.callmanager;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tram on 2015.05.09..
 */
public class RejectedCallViewer extends ActionBarActivity {
    ListView lvCalls;
    ArrayList<RejectCalls> allrejectedCalls;
    RejectCallsAdapter callsAdapter;
    Button deleteButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);

        lvCalls = (ListView) findViewById(R.id.contactlist);
        deleteButton=(Button)findViewById(R.id.btnContactChoose);
        deleteButton.setText("Delete All");
        allrejectedCalls=new ArrayList<>();

        String calls=readRejectedCalls("/rcalls");
        if(calls!=null) {
            String[] tmp = calls.split("\n");
            for (int i = 0; i < tmp.length; i++) {
                String[] tmp2 = tmp[i].split("\t");
                RejectCalls rcall=new RejectCalls(tmp2[0],tmp2[1],tmp2[2]);
                allrejectedCalls.add(rcall);
            }
            callsAdapter = new RejectCallsAdapter(allrejectedCalls);
            lvCalls.setAdapter(callsAdapter);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/rcalls");
                boolean deleted = file.delete();
                if(!deleted){   //nem sikerült a törlés
                    Toast.makeText(RejectedCallViewer.this,"delete was not successful for some reason",Toast.LENGTH_SHORT).show();
                }
                else { //sikeres törlés esetén felület újratöltése frissítés okán
                    finish();
                    startActivity(getIntent());
                }
            }
        });
    }

    private String readRejectedCalls(String filePath){
        FileInputStream fis= null;
        byte[] buffer=null;
        int length;
        try {
            fis = new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+filePath);
            length=fis.available();
            buffer=new byte[length];
            fis.read(buffer);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        String tmp=new String(buffer);
        if(tmp.contains("\t"))
            return tmp;
        else
            return null;
    }
}
