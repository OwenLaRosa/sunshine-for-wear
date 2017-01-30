/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.owenlarosa.sunshineforwear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.example.android.sunshine.R;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 */
public class WatchFace extends CanvasWatchFaceService {
    private static final Typeface NORMAL_TYPEFACE =
            Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL);

    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    static Resources resources;

    // weather data to be displayed on the screen
    public static String units = "C";
    public static int high = 0;
    public static int low = 0;
    public static String type = "";
    // color to be shown on bottom half of screen
    public static Integer tempColor = null;

    public static void setTempColor() {
        // average the high and low, then determine the result's color based on which temps it falls between
        int colorId = Utils.getColorForTemp((int) Math.floor(((high + low)/2)), units.equals("C"));
        tempColor = resources.getColor(colorId);
    }

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<WatchFace.Engine> mWeakReference;

        public EngineHandler(WatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            WatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine {
        final Handler mUpdateTimeHandler = new EngineHandler(this);
        boolean mRegisteredTimeZoneReceiver = false;
        Paint mBackgroundPaint;
        // different font attributes for different text
        Paint mTimePaint;
        Paint mDateAndTempPaint;

        boolean mAmbient;
        Calendar mCalendar;
        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };
        float mXOffset;
        float mYOffset;

        // vertical space inbetween text elements
        float mTextYInterSpace;

        // starting point to draw the text that shows the date
        float mDateTextStartY;

        // space from screen center and high/low text
        float mWeatherVerticalOffset;

        // sample image where weather graphic is drawn
        Bitmap placeholderBitmap;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(WatchFace.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_VARIABLE)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());
            resources = WatchFace.this.getResources();
            mYOffset = resources.getDimension(R.dimen.digital_y_offset);

            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(resources.getColor(R.color.sunshine_color_primary));

            mTimePaint = createTextPaint(resources.getColor(R.color.digital_text));
            mTimePaint.setTextAlign(Paint.Align.CENTER);
            mTimePaint.setColor(resources.getColor(R.color.black));
            mDateAndTempPaint = createTextPaint(resources.getColor(R.color.digital_text));
            mDateAndTempPaint.setTextAlign(Paint.Align.CENTER);
            placeholderBitmap = BitmapFactory.decodeResource(resources,
                    R.mipmap.ic_launcher);

            mCalendar = Calendar.getInstance();

            Intent serviceIntent = new Intent(WatchFace.this, DataLayerListenerService.class);
            startService(serviceIntent);
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            super.onDestroy();
        }

        private Paint createTextPaint(int textColor) {
            Paint paint = new Paint();
            paint.setColor(textColor);
            paint.setTypeface(NORMAL_TYPEFACE);
            paint.setAntiAlias(true);
            return paint;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            WatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            WatchFace.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = WatchFace.this.getResources();
            boolean isRound = insets.isRound();
            mXOffset = resources.getDimension(isRound
                    ? R.dimen.digital_x_offset_round : R.dimen.digital_x_offset);
            mTextYInterSpace = resources.getDimension(R.dimen.text_y_inter_space);

            float timePaintSize = resources.getDimension(isRound
                    ? R.dimen.time_text_size_round : R.dimen.time_text_size);
            mTimePaint.setTextSize(timePaintSize);
            float datePaintSize = resources.getDimension(isRound ?
                    R.dimen.date_text_size_round : R.dimen.date_text_size);
            mDateAndTempPaint.setTextSize(datePaintSize);
            float tempPaintSize = resources.getDimensionPixelSize(isRound
                    ? R.dimen.temp_text_size_round : R.dimen.temp_text_size);

            mDateTextStartY = mYOffset + timePaintSize + mTextYInterSpace;

            mWeatherVerticalOffset = resources.getDimension(R.dimen.weather_text_vertical_offset);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            if (mAmbient != inAmbientMode) {
                mAmbient = inAmbientMode;
                if (mLowBitAmbient) {
                    mTimePaint.setAntiAlias(!inAmbientMode);
                }
                invalidate();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            // Draw the background.
            Resources resources = getResources();
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
            } else {
                mBackgroundPaint.setColor(resources.getColor(R.color.sunshine_color_alternate));
                canvas.drawRect(0, 0, bounds.width(), bounds.height()/2, mBackgroundPaint);
                mBackgroundPaint.setColor(tempColor != null ? tempColor : resources.getColor(R.color.sunshine_color_primary));
                canvas.drawRect(0, bounds.height()/2, bounds.width(), bounds.height(), mBackgroundPaint);
            }

            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);

            if (!isInAmbientMode()) {
                // text is normally white, but the date should be black when not in ambient
                mDateAndTempPaint.setColor(resources.getColor(R.color.date_text_color));
            }
            if (isInAmbientMode()) {
                // white text will be shown against dark background in ambient
                mTimePaint.setColor(resources.getColor(R.color.white));
            } else {
                // black text will be shown on light background when active
                mTimePaint.setColor(resources.getColor(R.color.black));
            }
            String timeText = mAmbient
                    ? String.format("%d:%02d", mCalendar.get(Calendar.HOUR),
                    mCalendar.get(Calendar.MINUTE))
                    : String.format("%d:%02d:%02d", mCalendar.get(Calendar.HOUR),
                    mCalendar.get(Calendar.MINUTE), mCalendar.get(Calendar.SECOND));
            canvas.drawText(timeText,
                    bounds.width()/2,
                    bounds.height()/2 - mWeatherVerticalOffset*2.5f - mDateAndTempPaint.getTextSize(),
                    mTimePaint);

            String dateText = String.format("%s, %s %d %d",
                    Utils.getDayString(mCalendar.get(Calendar.DAY_OF_WEEK)),
                    Utils.getMonthString(mCalendar.get(Calendar.MONTH)),
                    mCalendar.get(Calendar.DAY_OF_MONTH),
                    mCalendar.get(Calendar.YEAR));
            canvas.drawText(dateText,
                    bounds.width()/2,
                    bounds.height()/2 - mWeatherVerticalOffset*1.5f,
                    mDateAndTempPaint);

            mDateAndTempPaint.setColor(resources.getColor(R.color.white));
            String tempText = String.format("%d / %d °%s",
                    high,
                    low,
                    units);
            canvas.drawText(tempText,
                    bounds.width()/2,
                    bounds.height()/2 + mDateAndTempPaint.getTextSize() + mWeatherVerticalOffset,
                    mDateAndTempPaint);

            canvas.drawBitmap(placeholderBitmap,
                    bounds.width()/2 - placeholderBitmap.getWidth()/2,
                    bounds.height()/2 + mDateAndTempPaint.getTextSize() + mWeatherVerticalOffset*2,
                    null);
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }
    }
}
