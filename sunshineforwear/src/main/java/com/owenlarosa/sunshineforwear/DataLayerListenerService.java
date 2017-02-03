package com.owenlarosa.sunshineforwear;

import android.net.Uri;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Owen LaRosa on 1/25/17.
 */

public class DataLayerListenerService extends WearableListenerService {

    // keys for values in weather data items

    // Units of the temperature: "F" for Imperial or "C" for metric
    private static final String KEY_UNITS = "units";
    // high temperature as int
    private static final String KEY_HIGH = "high";
    // low temperature as int
    private static final String KEY_LOW = "low";
    // string describing the type of weather
    private static final String KEY_TYPE = "type";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);

        for (DataEvent event : dataEventBuffer) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            if (path.equals("/weather")) {
                final DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                WatchFace.units = dataMap.getString(KEY_UNITS);
                double high = dataMap.getDouble(KEY_HIGH);
                double low = dataMap.getDouble(KEY_LOW);
                if (WatchFace.units.equals("C")) {
                    WatchFace.high = high;
                    WatchFace.low = low;
                } else {
                    WatchFace.high = (high * 1.8) + 32;
                    WatchFace.low = (low * 1.8) + 32;
                }
                WatchFace.type = dataMap.getInt(KEY_TYPE);
                // temp color should be calculated after all the other values have been set
                WatchFace.setTempColor();
                WatchFace.setWeatherTextColor();
                WatchFace.setWeatherBackgroundColor();
                int imageId = Utils.getDrawableForWeatherCondition(WatchFace.type);
                WatchFace.weatherImageBitmap = Utils.getBitmap(this, imageId);
                WatchFace.setWeatherTypeText();
            }
        }
    }
}
