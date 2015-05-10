package hu.uniobuda.nik.gabor_tamas.callmanager;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Tram on 2015.03.24..
 */
public class Contacts implements Parcelable {
    private String name;
    private String number;
    private boolean checked;

    public Contacts(String name, String number,boolean checked) {
        this.name = name;
        this.number = number;
        this.checked=checked;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    //Parcelable-hoz szükséges metódusok
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name); //Parcel objektumba írás
        dest.writeString(number);
        if(checked)
            dest.writeInt(1);
        else
            dest.writeInt(0);
    }

    //parcel működéséhez szükséges
    public static final Creator<Contacts> CREATOR = new Creator<Contacts>() {
        public Contacts createFromParcel(Parcel parcel) {
            final Contacts contacts=new Contacts(parcel.readString(),parcel.readString(),parcel.readInt()==1?true:false);
            return contacts;
        }

        public Contacts[] newArray(int size) {
            return new Contacts[size];
        }
    };

    //a megadott számok ellenőrzése, átalakítása a híváskor való ellenőrzéshez
    static public String makeComparableNumber(String number){
        if(number.length() < 1)
            return "hiba";
        String numbercreate="";
        int i=0;
        if(number.substring(0,2).equals("06")) {
            numbercreate="+36"; //magyarországi szám esetén +36-ra javítás
            i = 2;
            while(i<number.length()){
                if(number.charAt(i)!=' ')
                    numbercreate+=number.charAt(i);
                i++;
            }
        }
        else {  //nem magyarországi szám esetén nincs korrigálás
            while(i<number.length()){
                if(number.charAt(i)!=' ')
                    numbercreate+=number.charAt(i);
                i++;
            }
        }
        if(numbercreate.length() < 1)   //üres string esetén hiba
            return "hiba";
        else
            return numbercreate;
    }
}
