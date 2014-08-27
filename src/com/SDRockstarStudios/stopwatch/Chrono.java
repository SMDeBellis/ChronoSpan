package com.SDRockstarStudios.stopwatch;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews.RemoteView;
import android.widget.TextView;
/**
 * Modified version of the Chronometer widget
 * found in the android sdk to output time in 
 * centiseconds
 * @author graveyard
 *
 */
@SuppressLint({ "NewApi", "HandlerLeak" })
@RemoteView
public class Chrono extends TextView {
    @SuppressWarnings("unused")
	private static final String TAG = "Chrono";

    /**
     * A callback that notifies when the chronometer has incremented on its own.
     */
    public interface onChronoTickListener {

        /**
         * Notification that the chronometer has changed.
         */
        void onChronoTick(Chrono chronometer);

    }

    private long base;
    private boolean visible;
    private boolean started;
    private boolean running;
    
        
    private onChronoTickListener onChronoTickListener;
    
    private static final int TICK_WHAT = 2;
    
    /**
     * Initialize this Chronometer object.
     * Sets the base to the current time.
     */
    public Chrono(Context context) {
        this(context, null, 0);
    }

    /**
     * Initialize with standard view layout information.
     * Sets the base to the current time.
     */
    public Chrono(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Initialize with standard view layout information and style.
     * Sets the base to the current time.
     */
    public Chrono(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        this.base = SystemClock.elapsedRealtime();
        updateText(this.base);
    }

    /**
     * Set the time that the count-up timer is in reference to.
     *
     * @param base Use the {@link SystemClock#elapsedRealtime} time base.
     */
    
    public void setBase(long base) {
        this.base = base;
        dispatchChronometerTick();
        updateText(SystemClock.elapsedRealtime());
    }

    /**
     * Return the base time as set through {@link #setBase}.
     */
    public long getBase() {
        return this.base;
    }
 
    /**
     * Sets the listener to be called when the chronometer changes.
     * 
     * @param listener The listener.
     */
    public void setOnChronoTickListener(onChronoTickListener listener) {
        this.onChronoTickListener = listener;
    }

    /**
     * @return The listener (may be null) that is listening for chronometer change
     *         events.
     */
    public onChronoTickListener getOnChronoTickListener() {
    	return this.onChronoTickListener;
    }

    /**
     * Start counting up.  This does not affect the base as set from {@link #setBase}, just
     * the view display.
     * 
     * Chronometer works by regularly scheduling messages to the handler, even when the 
     * Widget is not visible.  To make sure resource leaks do not occur, the user should 
     * make sure that each start() call has a reciprocal call to {@link #stop}. 
     */
    public void start() {
    	this.started = true;
    	updateRunning();    	
    }

    /**
     * Stop counting up.  This does not affect the base as set from {@link #setBase}, just
     * the view display.
     * 
     * This stops the messages to the handler, effectively releasing resources that would
     * be held as the chronometer is running, via {@link #start}. 
     */
    public void stop() {
        this.started = false;
        updateRunning();
    }

    
    @Override
    protected void onDetachedFromWindow() {
    	Log.d("onDetachedFromWindow called", "");
        super.onDetachedFromWindow();
        this.visible = false;
        updateRunning();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
    	Log.d("onWindowVisibilityChanged called", "");
        super.onWindowVisibilityChanged(visibility);
        this.visible = visibility == VISIBLE;
        updateRunning();
    }

    private synchronized void updateText(long now) {
    	long seconds = now - this.base;

        String text;
        String hrStr;
        String minStr;
        String secStr;
        String centiStr;
        
        int hours = 0;
        int minutes = 0;
        int sec = 0;
        int centi = 0;
        long remaining = 0;
        
        hours = (int)(seconds / (3600*1000));
        remaining = seconds % (3600*1000);
        
        if(hours < 10)
        	hrStr = "0" + hours;
        else if(hours > 99)
        	hrStr = "00";
        else
        	hrStr = String.valueOf(hours);
        
        minutes = (int)(remaining / (60 * 1000));
        remaining = remaining % (60 * 1000);
        if(minutes < 10)
        	minStr = "0" + minutes;
        else if(minutes > 59)
        	minStr = "00";
        else
        	minStr = String.valueOf(minutes);
        
        sec = (int)(remaining / 1000);
        remaining = remaining % 1000;
        if(sec < 10)
        	secStr = "0" + sec;
        else if(sec > 59)
        	secStr = "00";
        else
        	secStr = String.valueOf(sec);
        
        centi = (int)remaining/10;
        centiStr = "00";
        if(centi < 10)
        	centiStr = "0" + centi;
        else
        	centiStr = String.valueOf(centi);
              	
        text = hrStr + ":"  + minStr + ":" +
        	   secStr + ":" + centiStr;
        
        setText(text);
    }

    private void updateRunning() {
    	boolean isRunning = this.visible && this.started;
        if (isRunning != running) {
            if (isRunning) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                handler.sendMessageDelayed(Message.obtain(handler, TICK_WHAT), 10);
            } else {
                handler.removeMessages(TICK_WHAT);
            }
            running = isRunning;
        }
    }
    
	private Handler handler = new Handler() {
        public void handleMessage(Message m) {
            if (running) {
                updateText(SystemClock.elapsedRealtime());
                dispatchChronometerTick();
                sendMessageDelayed(Message.obtain(this, TICK_WHAT), 10);
            }
        }
    };

    void dispatchChronometerTick() {
        if (this.onChronoTickListener != null) {
            this.onChronoTickListener.onChronoTick(this);
        }
    }

    @SuppressLint("NewApi")
	@Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(Chrono.class.getName());
    }

    @SuppressLint("NewApi")
	@Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(Chrono.class.getName());
    }
}
