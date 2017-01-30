package com.owenlarosa.sunshineforwear;

import com.example.android.sunshine.R;

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

    /**
     * Get color corresponding to degree range
     * Degree ranges are by 10s in Fahrenheit. If temperature is in celsius, degrees are first converted
     * @param degrees temperature
     * @param metric true if degree measurement in celsius
     * @return Color corresponding to a color range
     */
    public static int getColorForTemp(int degrees, boolean metric) {
        if (metric) {
            // convert to Fahrenheit if degrees provided in metric units
            degrees = (int) ((float) degrees * 1.8f + 32);
        }
        // ranges of below 10, 100 and above, and intervals of 10 degrees
        if (degrees >= 100) {
            return R.color.temp_100_plus;
        } else if (degrees >= 90) {
            return R.color.temp_90s;
        } else if (degrees >= 80) {
            return R.color.temp_80s;
        } else if (degrees >= 70) {
            return R.color.temp_70s;
        } else if (degrees >= 60 ) {
            return R.color.temp_60s;
        } else if (degrees >= 50) {
            return R.color.temp_50s;
        } else if (degrees >= 40) {
            return R.color.temp_40s;
        } else if (degrees >= 30) {
            return R.color.temp_30s;
        } else if (degrees >= 20) {
            return R.color.temp_20s;
        } else if (degrees >= 10) {
            return R.color.temp_10s;
        } else {
            return R.color.temp_below_10;
        }
    }

}
