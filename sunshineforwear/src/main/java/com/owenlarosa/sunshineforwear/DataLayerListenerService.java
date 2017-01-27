package com.owenlarosa.sunshineforwear;

import android.net.Uri;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Owen LaRosa on 1/25/17.
 */

public class DataLayerListenerService extends WearableListenerService {

    GoogleApiClient mGoogleApiClient;

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
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);

        if (!mGoogleApiClient.isConnected()) {
            return;
        }
        for (DataEvent event : dataEventBuffer) {
            Uri uri = event.getDataItem().getUri();
            String path = uri.getPath();
            if (path.equals("/weather")) {
                final DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                WatchFace.units = dataMap.getString(KEY_UNITS);
                WatchFace.high = dataMap.getInt(KEY_HIGH);
                WatchFace.low = dataMap.getInt(KEY_LOW);
                WatchFace.type = dataMap.getString(KEY_TYPE);
            }
        }
    }
}
