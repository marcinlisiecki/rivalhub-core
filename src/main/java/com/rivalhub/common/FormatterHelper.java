package com.rivalhub.common;

import java.time.format.DateTimeFormatter;

public class FormatterHelper {

    public static DateTimeFormatter formatter(){
        return DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    }
}
