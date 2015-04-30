package hu.uniobuda.nik.gabor_tamas.callmanager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Tram on 2015.03.24..
 */
public class ContactChooser extends Activity {
    ListView lvConts;
    ArrayList<Contacts> allconts;
    ContactsAdapter contsAdapter;
    Button setAvailableContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_list);
        lvConts = (ListView) findViewById(R.id.contactlist);
        setAvailableContacts = (Button) findViewById(R.id.btnContactChoose);
        allconts = new ArrayList<>(); //a már kiválasztott névjegyek betöltése, ha van
        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                allconts = bundle.getParcelableArrayList("obj");
            }
        }
        contsAdapter = new ContactsAdapter(allconts);
        lvConts.setAdapter(contsAdapter);
        //telefonkönyvtár lekérésének módja
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER};
        Cursor people = getContentResolver().query(uri, projection, null, null, null);
        int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        if(people.moveToFirst()) {
            boolean used = false;
            int givenContsCount = allconts.size();
            do {    //nevek betöltése, amelyek még nincsenek kiválasztva
                String name = people.getString(indexName);
                String number = people.getString(indexNumber);
                for (int i = 0; i < givenContsCount; i++) {    //a már kiválasztott nevek ellenőrzése
                    if (allconts.get(i).getName().equals(name)) {
                        used = true;
                        break;
                    }
                }
                if (!used)   //listába adás, ha nincs még kiválasztva
                    allconts.add(new Contacts(name, number, false));
                used = false;
            } while (people.moveToNext());
            contsAdapter.notifyDataSetChanged();
        }
        //az update changes gomb kattintásfigyelője
        setAvailableContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent vissza = new Intent();
                ArrayList<Contacts> checkedConts = new ArrayList<>();
                for (Contacts conItem : allconts) {
                    if (conItem.isChecked())
                        checkedConts.add(conItem);
                } //a kiválasztott nevek visszaküldése a CallOptions Activitynek
                vissza.putParcelableArrayListExtra("chosenConts", checkedConts);
                setResult(RESULT_OK, vissza);
                finish();
            }
        });
    }
}
