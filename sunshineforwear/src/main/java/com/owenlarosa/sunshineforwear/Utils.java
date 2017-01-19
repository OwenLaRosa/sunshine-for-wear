package com.owenlarosa.sunshineforwear;

/**
 * Created by Owen LaRosa on 1/18/17.
 */

public class Utils {

    /**
     * Get an abbreviation for a day of the week
     * @param day index of the day, sunday is 1
     * @return three letter abbreviation is between 1 and 7 (inclusive), otherwise empty string
     */
    public static String getDayString(int day) {
        switch (day) {
            case 1:
                return "SUN";
            case 2:
                return "MON";
            case 3:
                return "TUE";
            case 4:
                return "WED";
            case 5:
                return "THU";
            case 6:
                return "FRI";
            case 7:
                return "SAT";
            default:
                return "";
        }
    }

    /**
     * Get an abbreviation for a month
     * @param month index of the month, starting at 0
     * @return three letter abbreviation if between 0 and 11 (inclusive), otherwise empty string
     */
    public static String getMonthString(int month) {
        switch (month) {
            case 0:
                return "JAN";
            case 1:
                return "FEB";
            case 2:
                return "MAR";
            case 3:
                return "APR";
            case 4:
                return "MAY";
            case 5:
                return "JUN";
            case 6:
                return "JUL";
            case 7:
                return "AUG";
            case 8:
                return "SEP";
            case 9:
                return "OCT";
            case 10:
                return "NOV";
            case 11:
                return "DEC";
            default:
                return "";
        }
    }

}
