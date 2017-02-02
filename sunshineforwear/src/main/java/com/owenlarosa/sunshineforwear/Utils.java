package com.owenlarosa.sunshineforwear;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import com.example.android.sunshine.R;

import static android.R.attr.bitmap;

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

    /**
     * Get text color (for time and date) based on weather condition
     * @param weatherId OpenWeatherMap condition ID
     * @return Resource ID of the color
     */
    public static int getTextColorForWeatherCondition(int weatherId) {

        /*
         * Based on weather code data for Open Weather Map.
         */
        if (weatherId >= 200 && weatherId <= 232) {
            return R.color.text_color_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.color.text_color_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.color.text_color_heavy_rain;
        } else if (weatherId == 511) {
            return R.color.text_color_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.color.text_color_heavy_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.color.text_color_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.color.text_color_fog;
        } else if (weatherId == 761 || weatherId == 771 || weatherId == 781) {
            return R.color.text_color_storm;
        } else if (weatherId == 800) {
            return R.color.text_color_clear;
        } else if (weatherId == 801) {
            return R.color.text_color_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.color.text_color_cloudy;
        } else if (weatherId >= 900 && weatherId <= 906) {
            return R.color.text_color_storm;
        } else if (weatherId >= 958 && weatherId <= 962) {
            return R.color.text_color_storm;
        } else if (weatherId >= 951 && weatherId <= 957) {
            return R.color.text_color_clear;
        }
        return R.color.text_color_storm;
    }

    /**
     * Get background color for tap half of screen based on weather
     * @param weatherId OpenWeatherMap condition ID
     * @return Resource ID of the color
     */
    public static int getBackgroundColorForWeatherCondition(int weatherId) {

        /*
         * Based on weather code data for Open Weather Map.
         */
        if (weatherId >= 200 && weatherId <= 232) {
            return R.color.back_color_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.color.back_color_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.color.back_color_heavy_rain;
        } else if (weatherId == 511) {
            return R.color.back_color_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.color.back_color_heavy_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.color.back_color_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.color.back_color_fog;
        } else if (weatherId == 761 || weatherId == 771 || weatherId == 781) {
            return R.color.back_color_storm;
        } else if (weatherId == 800) {
            return R.color.back_color_clear;
        } else if (weatherId == 801) {
            return R.color.back_color_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.color.back_color_cloudy;
        } else if (weatherId >= 900 && weatherId <= 906) {
            return R.color.back_color_storm;
        } else if (weatherId >= 958 && weatherId <= 962) {
            return R.color.back_color_storm;
        } else if (weatherId >= 951 && weatherId <= 957) {
            return R.color.back_color_clear;
        }
        return R.color.back_color_storm;
    }

    /**
     * Get resource ID of a vector drawable for weather condition
     * @param weatherId Weather ID from OpenWeatherMap
     * @return Resource ID of the drawable
     */
    public static int getDrawableForWeatherCondition(int weatherId) {
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 771 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        } else if (weatherId >= 900 && weatherId <= 906) {
            return R.drawable.art_storm;
        } else if (weatherId >= 958 && weatherId <= 962) {
            return R.drawable.art_storm;
        } else if (weatherId >= 951 && weatherId <= 957) {
            return R.drawable.art_clear;
        }
        return R.drawable.art_storm;
    }

    // referenced: http://stackoverflow.com/questions/33696488/getting-bitmap-from-vector-drawable
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap getBitmap(Context context, int resourceId) {
        VectorDrawable vectorDrawable = (VectorDrawable) ContextCompat.getDrawable(context, resourceId);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth()*2,
                vectorDrawable.getIntrinsicHeight()*2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }



}
