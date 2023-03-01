package com.android.smartvirtualid.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String formatDate(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd - MM - yyyy", Locale.getDefault());
        return simpleDateFormat.format(date);
    }
}
