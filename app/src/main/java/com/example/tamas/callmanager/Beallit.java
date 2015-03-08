package com.example.tamas.callmanager;

/**
 * Created by Tamas on 2015.02.28..
 */
public class Beallit {
    private String title;
    private String lead;

    public Beallit(String title, String lead) {
        this.title = title;
        this.lead = lead;
    }

    public String getLead() {
        return lead;
    }

    public String getTitle() {
        return title;
    }
}
