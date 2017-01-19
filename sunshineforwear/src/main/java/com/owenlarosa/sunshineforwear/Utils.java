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

}
