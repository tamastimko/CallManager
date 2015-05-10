package hu.uniobuda.nik.gabor_tamas.callmanager;

/**
 * Created by Tram on 2015.05.09..
 */
public class RejectCalls {
    private String name;
    private String number;
    private String time;

    public RejectCalls(String name,String number,String time){
        this.name=name;
        this.number=number;
        this.time=time;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getTime(){
        return  time;
    }
}
