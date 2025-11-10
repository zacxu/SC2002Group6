package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;



/**
 * This class  provides methods to format dates
 * 
 * handles date formatting and parsing, and checks if a date range is valid
 */



public class DateUtils {


    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public static String formatDate(LocalDate date) {
        if (date == null) return "";
        
        return date.format(DATE_FORMATTER);


    }
    


    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(dateString.trim(), DATE_FORMATTER);

        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format: " + dateString + ". Expected format: yyyy-MM-dd");
        }
    }
    


    public static boolean isValidDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false;

        }

        return !endDate.isBefore(startDate);
    }

}


