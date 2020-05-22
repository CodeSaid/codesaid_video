package com.codesaid.lib_base.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codesaid.lib_base.R;
import com.codesaid.lib_base.util.PixUtils;


/**
 * Created By codesaid
 * On :2020-05-21 20:19
 * Package Name: com.codesaid.lib_base.view
 * desc:
 */
public class RecordView extends View implements View.OnClickListener, View.OnLongClickListener {

    // 间距 100 毫秒
    private static final int PROGRESS_INTERVAL = 100;

    // 录制时进度条的最大值
    private int progressMaxValue;

    // 半径
    private final int radius;
    // 进度条宽度
    private final int progressWidth;
    // 进度条颜色
    private final int progressColor;
    // 背景色
    private final int fillColor;
    // 录制的 最大时长
    private final int maxDuration;
    private Paint mProgressPaint;
    private Paint mFillPaint;

    private int progressValue;

    // 是否在录制
    private boolean isRecording;

    private long startRecordTime;
    private onRecordListener mOnRecordListener;

    public RecordView(Context context) {
        this(context, null);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RecordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecordView, defStyleAttr, defStyleRes);
        radius = typedArray.getDimensionPixelOffset(R.styleable.RecordView_radius, 0);
        progressWidth = typedArray.getDimensionPixelOffset(R.styleable.RecordView_progress_width, PixUtils.dp2pix(3));
        progressColor = typedArray.getColor(R.styleable.RecordView_progress_color, Color.RED);
        fillColor = typedArray.getColor(R.styleable.RecordView_fill_color, Color.WHITE);
        maxDuration = typedArray.getInteger(R.styleable.RecordView_duration, 10);

        setMaxDuration(maxDuration);

        typedArray.recycle();

        mFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFillPaint.setColor(fillColor);
        mFillPaint.setStyle(Paint.Style.FILL);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(progressWidth);

        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                progressValue++;
                // 重绘
                postInvalidate();

                if (progressValue <= progressMaxValue) {
                    // 当前录制的时间 小于 规定的最大值，则继续录制，重绘
                    sendEmptyMessageDelayed(0, PROGRESS_INTERVAL);
                } else {
                    // 超过录制的最大时间，则主动结束录制
                    finishRecord();
                }
            }
        };
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) { // 开始录制
                    isRecording = true;
                    startRecordTime = System.currentTimeMillis();
                    handler.sendEmptyMessage(0);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    long nowTime = System.currentTimeMillis();
                    //  getLongPressTimeout(): 长按时间，超过此时间就认为是长按
                    if (nowTime - startRecordTime > ViewConfiguration.getLongPressTimeout()) {
                        finishRecord();
                    }
                    handler.removeCallbacksAndMessages(null);
                    isRecording = false;
                    startRecordTime = 0;
                    progressValue = 0;
                    postInvalidate();
                }
                return false;
            }
        });

        setOnClickListener(this);
        setOnLongClickListener(this);
    }

    private void finishRecord() {
        if (mOnRecordListener != null) {
            mOnRecordListener.onFinish();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        if (isRecording) {
            canvas.drawCircle(width / 2, height / 2, width / 2, mFillPaint);

            int left = 0;
            int top = 0;
            int right = width;
            int bottom = height;

            float sweepAngle = (progressValue * 1.0f / progressMaxValue) * 360;
            canvas.drawArc(left, top, right, bottom, -90, sweepAngle, false, mProgressPaint);
        } else {
            canvas.drawCircle(width / 2, height / 2, radius, mFillPaint);
        }
    }

    private void setMaxDuration(int duration) {
        this.progressMaxValue = duration * 1000 / PROGRESS_INTERVAL;
    }

    public void setOnRecordListener(onRecordListener onRecordListener) {
        this.mOnRecordListener = onRecordListener;
    }

    @Override
    public void onClick(View v) {
        if (mOnRecordListener != null) {
            mOnRecordListener.onClick();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mOnRecordListener != null) {
            mOnRecordListener.onLongClick();
        }
        return true;
    }

    public interface onRecordListener {
        void onClick();

        void onLongClick();

        void onFinish();
    }
}
