package com.example.clinicsystem.config;
import org.springframework.core.convert.converter.Converter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomDateConverter implements Converter<String, Date> {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Date convert(String source) {
        try {
            return dateFormat.parse(source);
        } catch (ParseException e) {
            // Handle parse exception, e.g., return null or throw an exception
            return null;
        }
    }
}
