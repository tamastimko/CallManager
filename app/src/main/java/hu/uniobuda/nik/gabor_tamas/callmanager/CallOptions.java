package hu.uniobuda.nik.gabor_tamas.callmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CallOptions extends ActionBarActivity {

    Switch swManTurner;
    Button btAddNumber, btConfirmChanges;
    RadioButton rbBlack, rbWhite;
    ListView lvNumbers;
    List<Contacts> conts;
    ContactsAdapter contactsAdapter;
    String blackFileName,whiteFileName;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_options);

        init(); //változók inicializálása

        //a legutolsó beállítások betöltése sharedpreference tárolóból
        if(sp.getBoolean("enabled",false)){
            swManTurner.setChecked(true);
            String listItems;
            if (sp.getBoolean("isBlackList",true)){
                rbBlack.setChecked(true);
                listItems=readFromFile(blackFileName);  //a neveket sima belső tárolóból olvassa
                if(listItems!=null){
                    String[] lines=listItems.split("\n");   //az egyes nevek soronként vannak
                    String[] line;
                    for (int i=0;i<lines.length;i++){
                        line=lines[i].split("\t");  //3 adat 1 sorban tab-al elválasztva
                        conts.add(new Contacts(line[0],line[1],true));  //név telefonszám és érvényesség
                    }
                }
            } else {    //ugyanaz a működés mint blacklistnél
                rbWhite.setChecked(true);
                listItems=readFromFile(whiteFileName);
                if(listItems!=null){
                    String[] lines=listItems.split("\n");
                    String[] line;
                    for (int i=0;i<lines.length;i++){
                        line=lines[i].split("\t");
                        conts.add(new Contacts(line[0],line[1],true));
                    }
                }
            }
            if(conts.size()>0) {    //ha vannak nevek a fájlban akkor betölti a listába
                contactsAdapter.notifyDataSetChanged();
                contactsAdapter=new ContactsAdapter(conts);
                lvNumbers.setAdapter(contactsAdapter);
            }else {
                btConfirmChanges.setEnabled(false);
            }
        } else {    //ha ki van kapcsolva a funkció
            btAddNumber.setEnabled(false);
            rbWhite.setEnabled(false);
            rbBlack.setEnabled(false);
            lvNumbers.setEnabled(false);
            btConfirmChanges.setEnabled(false);
        }

        //bekapcsoló, kikapcsológomb
        swManTurner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){  //bekapcsoláskor újra betölt mindent mint onCreate-nél
                    SharedPreferences.Editor edit=sp.edit();
                    edit.putBoolean("enabled",true);    //sharedpreference értékének átállítása
                    edit.commit();
                    lvNumbers.setEnabled(true);
                    btAddNumber.setEnabled(true);
                    rbWhite.setEnabled(true);
                    rbBlack.setEnabled(true);
                    String listItems;
                    if (sp.getBoolean("isBlackList",true)){
                        rbBlack.setChecked(true);
                        listItems=readFromFile(blackFileName);
                        if(listItems!=null){
                            conts.clear();
                            String[] lines=listItems.split("\n");
                            String[] line;
                            for (int i=0;i<lines.length;i++){
                                line=lines[i].split("\t");
                                conts.add(new Contacts(line[0],line[1],true));
                            }
                        }
                    } else {
                        rbWhite.setChecked(true);
                        listItems=readFromFile(whiteFileName);
                        if(listItems!=null){
                            conts.clear();
                            String[] lines=listItems.split("\n");
                            String[] line;
                            for (int i=0;i<lines.length;i++){
                                line=lines[i].split("\t");
                                conts.add(new Contacts(line[0],line[1],true));
                            }
                        }
                    }
                    if(conts.size()>0) {
                        contactsAdapter.notifyDataSetChanged();
                        btConfirmChanges.setEnabled(true);
                    }
                }else { //kikapcsoláskor zárolja a felületet
                    SharedPreferences.Editor edit=sp.edit();
                    edit.putBoolean("enabled",false);   //sharedpreference frissítése
                    edit.commit();
                    btAddNumber.setEnabled(false);
                    rbWhite.setEnabled(false);
                    rbBlack.setEnabled(false);
                    if(conts.size()>0) {
                        conts.clear();
                        contactsAdapter.notifyDataSetChanged();
                        lvNumbers.setEnabled(false);
                    }
                    btConfirmChanges.setEnabled(false);
                }
            }
        });

        //BlackList RadioButton változásfigyelő
        rbBlack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String listItems;
                if(isChecked){  //bekapcsolás esetén sharedpreference és lista frissítése
                    SharedPreferences.Editor edit=sp.edit();
                    edit.putBoolean("isBlackList",true);
                    edit.commit();
                    conts.clear();
                    listItems=readFromFile(blackFileName);
                    if(listItems!=null){
                        String[] lines=listItems.split("\n");
                        String[] line;
                        for (int i=0;i<lines.length;i++){
                            line=lines[i].split("\t");
                            conts.add(new Contacts(line[0],line[1],true));
                        }
                    }
                    contactsAdapter.notifyDataSetChanged();
                    btConfirmChanges.setEnabled(true);
                    if(conts.size()<1)
                        btConfirmChanges.setEnabled(false);
                }

            }
        });

        //WhiteList RadioButton változásfigyelő
        rbWhite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){  //bekapcsolás esetén sharedpreference és lista frissítése
                    String listItems;
                    SharedPreferences.Editor edit=sp.edit();
                    edit.putBoolean("isBlackList",false);
                    edit.commit();
                    conts.clear();
                    listItems=readFromFile(whiteFileName);
                    if(listItems!=null){
                        String[] lines=listItems.split("\n");
                        String[] line;
                        for (int i=0;i<lines.length;i++){
                            line=lines[i].split("\t");
                            conts.add(new Contacts(line[0],line[1],true));
                        }
                    }
                    contactsAdapter.notifyDataSetChanged();
                    btConfirmChanges.setEnabled(true);
                    if(conts.size()<1)
                        btConfirmChanges.setEnabled(false);
                }

            }
        });

        //név hozzáadás gomb kattintásfigyelője
        btAddNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ab=new AlertDialog.Builder(CallOptions.this);
                ab.setTitle(R.string.app_name); //új dialog ami a bekérés módját kéri be
                ab.setMessage("How do you want to give numbers?");
                ab.setNegativeButton("With typing",new DialogInterface.OnClickListener(){
                    @Override   //kézzel megadás esetén egy új saját dialog megjelenítése
                    public void onClick(DialogInterface dialog, int which) {
                        final Dialog customDialog =new Dialog(CallOptions.this);
                        customDialog.setContentView(R.layout.number_input_layout);
                        customDialog.setTitle("Insert Number");
                        //felület elemeinek osztályokhoz rendelése
                        final EditText nameEdit=(EditText)customDialog.findViewById(R.id.etName);
                        final EditText numberEdit=(EditText)customDialog.findViewById(R.id.etNumber);
                        numberEdit.setRawInputType(Configuration.KEYBOARD_12KEY);
                        Button dialogButton=(Button)customDialog.findViewById(R.id.btnNumberAdd);
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String number=numberEdit.getText().toString();
                                number=Contacts.makeComparableNumber(number);   //megadott szám összehasonlításra állítása
                                if(nameEdit.getText().toString().length() > 0 && !number.equals("hiba")) {

                                    //String CountryIso= ((TelephonyManager)MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE)).getSimCountryIso();
                                    //String number = PhoneNumberUtils.formatNumber(numberEdit.getText().toString(),CountryIso);
                                    conts.add(new Contacts(nameEdit.getText().toString(), number, true));
                                    String fileString = "";
                                    for (int i = 0; i < conts.size(); i++) {
                                        fileString += conts.get(i).getName() + '\t' + conts.get(i).getNumber() + '\n';
                                    }
                                    if (rbBlack.isChecked())
                                        writeToFile(blackFileName, fileString);
                                    else
                                        writeToFile(whiteFileName, fileString);
                                    contactsAdapter.notifyDataSetChanged();
                                    btConfirmChanges.setEnabled(true);
                                } //nem megfelelő adatok esetén nem adjuk a listába
                                else
                                    Toast.makeText(CallOptions.this, "no name or number", Toast.LENGTH_SHORT);
                                customDialog.dismiss(); //Dialógus eltűntetése megadás után
                            }
                        });
                        customDialog.show();
                        dialog.dismiss();
                    }
                });
                ab.setPositiveButton("Choose from contacts",new DialogInterface.OnClickListener() {
                    @Override   //névjegyből választás esetén új activity
                    public void onClick(DialogInterface dialogInterface, int which) {
                        Intent i=new Intent(CallOptions.this, ContactChooser.class);
                        ArrayList<Contacts> tmp=new ArrayList<>(conts); //a már kiválasztott nevek átadása
                        i.putParcelableArrayListExtra("obj",tmp);
                        startActivityForResult(i, 22);
                    }
                });
                ab.show();
            }
        });

        //a listboxban a nevek melletti pipák változtatásainak érvényesítésére szolgáló gomb kattintásfigyelője
        btConfirmChanges.setOnClickListener(new View.OnClickListener() {
            @Override   //az kiválaszott nevek amelyeknél kivettük a pipát azoknak a neveknek a kivétele a listából
            public void onClick(View v) {
                for (int i=0;i<conts.size();i++){
                    if(!conts.get(i).isChecked()){
                        conts.remove(i--);
                        Toast.makeText(getApplicationContext(), "Done",Toast.LENGTH_SHORT).show();
                    }
                }
                contactsAdapter.notifyDataSetChanged();
                if(conts.size()<1) {
                    btConfirmChanges.setEnabled(false);
                    if(sp.getBoolean("isBlackList",true))
                        writeToFile(blackFileName,"");
                    else
                        writeToFile(whiteFileName,"");
                }
                else {  //a frissítés beírása a fájlba is
                    String fileString="";
                    for (int i=0;i<conts.size();i++) {
                        fileString+=conts.get(i).getName()+'\t'+conts.get(i).getNumber()+'\n';
                    }
                    if(sp.getBoolean("isBlackList",true))
                        writeToFile(blackFileName,fileString);
                    else
                        writeToFile(whiteFileName,fileString);
                }
                Toast.makeText(getApplicationContext(), "Done",Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Névjegyból választás esetén itt dolgozzuk fel a kiválasztott neveket
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==22){    //a névjegy visszatéréséhez ezt a számot csatoltam
            if(resultCode==RESULT_OK){
                if(data!=null){ //Visszakapott Bundle feldolgozása
                    Bundle bundle=data.getExtras();
                    if(bundle!=null){
                        ArrayList<Contacts> conList=bundle.getParcelableArrayList("chosenConts");   //parcelable objektumok a gyorsaság miatt
                        if(conList!=null){  //ha kaptunk vissza értéket akkor dolgozunk csak
                            conts.clear();
                            String fileString="";
                            String tmp;
                            for (int i=0;i<conList.size();i++) {
                                tmp=Contacts.makeComparableNumber(conList.get(i).getNumber());
                                if(!tmp.equals("hiba")) {   //lista frissítése
                                    conts.add(new Contacts(conList.get(i).getName(),conList.get(i).getNumber(),true));
                                    fileString += conList.get(i).getName() + '\t' + tmp + '\n';
                                }
                                else    //ha esetleg a telefonkönyvbe nem megfelelő számérték van
                                    conts.add(new Contacts(conList.get(i).getName(),"the number is wrong",false));
                            }   //fájl frissítése
                            if(sp.getBoolean("isBlackList",true))
                                writeToFile(blackFileName,fileString);
                            else
                                writeToFile(whiteFileName,fileString);
                            contactsAdapter.notifyDataSetChanged();
                            if (conts.size()>0){    //nem üres lista esetén frissítés gomb használatának engedélyezése
                                btConfirmChanges.setEnabled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    //fájlba írás művelet kiszervezése metódusba kódismétlés elkerülése végett
    private void writeToFile(String fileName,String content){
        FileOutputStream fos= null;
        try {
            fos = openFileOutput(fileName,MODE_PRIVATE);
            fos.write(content.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //fájlmegnyitás kiszervezése
    private String readFromFile(String fileName){
        FileInputStream fis= null;
        byte[] buffer=null;
        int length;
        try {
            fis = openFileInput(fileName);
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

    private void init(){
        blackFileName="BlackListContacts";
        whiteFileName="WhiteListContacts";
        btAddNumber=(Button)findViewById(R.id.btnadd);
        swManTurner=(Switch)findViewById(R.id.blswitch);
        rbBlack=(RadioButton)findViewById(R.id.rBblack);
        rbWhite=(RadioButton)findViewById(R.id.rBwhite);
        lvNumbers=(ListView)findViewById(R.id.listView);
        btConfirmChanges=(Button)findViewById(R.id.btnConfirmChanges);
        sp=getSharedPreferences("callEnabled",MODE_PRIVATE);
        conts=new ArrayList<>();
        contactsAdapter=new ContactsAdapter(conts);
        lvNumbers.setAdapter(contactsAdapter);
    }
}
