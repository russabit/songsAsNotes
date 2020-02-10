package com.example.rnotes.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {
    public static String getCurrentTimeStamp() {

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy");
            String currentDateTime = dateFormat.format(new Date());

            return currentDateTime;
        }
        catch (Exception e) {
            return null;
        }
    }
}
