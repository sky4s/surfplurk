package com.google.jplurk_oauth;

import com.google.jplurk_oauth.skeleton.DateTime;
import java.util.Calendar;

public class Offset {
    
    private DateTime dateTime;
    
    public Offset() {
        dateTime = DateTime.create(Calendar.getInstance());
    }
    
    public Offset(long offsetInMs)
    {
        dateTime = DateTime.create(offsetInMs);
    }

    public String formatted() {
        return dateTime.toTimeOffset();
    }
    

}
