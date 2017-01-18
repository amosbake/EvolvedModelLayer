package mopel.io.evolvedmodellayer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import mopel.io.evolvedmodellayer.model.Battery;
import mopel.io.evolvedmodellayer.model.BatteyTimeCompartor;

/**
 * Author: mopel
 * Date : 2017/1/18
 * 描绘步骤:
 * onMeasure:
 * 1. 测量控件大小
 * <p>
 * onLayout:
 * 1. 测量文字大小 如 '100','10:00' 以便后面精确定位
 * 2. 根据padding 获取实际画布边界
 * 3. 获取原点坐标 x=leftEdge+yMarkTextMaxWidth+xMarkPadding y = bottomEdge - xMarkTextMaxHeight - yMarkPadding
 * 4. 初始化坐标轴的渐变色
 * <p>
 * onDraw:
 * 1. 画坐标轴
 * 2. 画坐标轴上的标记文字
 */
public class ChartGraphView extends View {
    private int mWidth, mHeight, mMimWidth = 720, mMimHeight = 400/*fill view mim width*/;
    private static final int MAX_BATTERY = 100;
    private static final int MAX_TIME_IN_MINUTE = 24 * 60;
    private String[] timelines = new String[]{"02:00", "06:00", "10:00", "14:00", "18:00", "22:00"};
    private String[] batterylines = new String[]{"(%)", " 20", " 40", " 60", " 80", "100"};
    private int pointColor, mainAxisColor, semiAxisColor, microAxisColor, identifyColorX = Color.WHITE, identifyColorY = Color.WHITE;
    private int xMarkTextSize = 24, yMarkTextSize = 24, minPadding = 16, xMarkPadding = 8, yMarkPadding = 8;
    private Paint xMarkPaint, yMarkPaint, mainAxisPaint, semiAxisPaint, microAxisPaint, emptyPathPaint, actualPathPaint;
    private int xMarkTextMaxHeight, xMarkTextMaxWidth, yMarkMaxTextWidth, yMarkMaxTextHeight;
    private int leftEdge, rightEdge, topEdge, bottomEdge;
    private int originX, originY;
    private int gradieStartColor = -16711936;
    private int gradieEndColor = Color.WHITE;
    private Path emptyPath, actualPath;
    private Battery fakeBattery;
    private List<Battery> mBatteries;
    private PointF fakePoint, originPoint;
    private boolean containsFakePoint;

    public ChartGraphView(Context context) {
        this(context, null);
    }

    public ChartGraphView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChartGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        xMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xMarkPaint.setColor(identifyColorX);
        xMarkPaint.setTextSize(xMarkTextSize);

        yMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        yMarkPaint.setColor(identifyColorY);
        yMarkPaint.setTextSize(yMarkTextSize);

        mainAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        semiAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        microAxisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        emptyPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        actualPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        emptyPathPaint.setStyle(Paint.Style.STROKE);
        emptyPathPaint.setColor(Color.WHITE);
        actualPathPaint.setStyle(Paint.Style.STROKE);
        actualPathPaint.setColor(Color.WHITE);
        emptyPathPaint.setPathEffect(new DashPathEffect(new float[]{12.0f, 8.0f, 4.0f, 8.0f}, 1.0f));
        emptyPath = new Path();
        actualPath = new Path();
        originPoint = new PointF();
        fakeBattery = Battery.GraphFakePoint();
        mBatteries = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        emptyPath.reset();
        actualPath.reset();
        canvas.drawColor(Color.BLACK);
        drawAxis(canvas);
        drawMarkText(canvas);
        emptyPath.moveTo(originX, originY);
        if (!containsFakePoint) {
            emptyPath.lineTo(fakePoint.x, fakePoint.y);
            addBatteryDataToPath(fakePoint);
        } else {
            addBatteryDataToPath(originPoint);
        }
        canvas.drawPath(emptyPath, emptyPathPaint);
        canvas.drawPath(actualPath, actualPathPaint);

    }

    private void addBatteryDataToPath(PointF lastPoint) {
        if (mBatteries.isEmpty()) {
            emptyPath.lineTo(rightEdge, originY);
        } else {
            emptyPath.lineTo(lastPoint.x, lastPoint.y);
            PointF point;
            for (int i = 0, len = mBatteries.size(); i < len; i++) {
                point = resolveBattery(mBatteries.get(i));
                if (i == 0){
                    emptyPath.lineTo(point.x, point.y);
                    actualPath.moveTo(point.x, point.y);
                }else{
                    actualPath.lineTo(point.x, point.y);
                }

            }
        }
    }

    private void drawMarkText(Canvas canvas) {
        for (int i = 0, len = batterylines.length; i < len; i++) {
            int currentY = originY - i * (originY - topEdge) / (len-1) + yMarkMaxTextHeight / 2;
            canvas.drawText(batterylines[i], leftEdge, currentY, yMarkPaint);
        }

        for (int i = 0, len = timelines.length; i < len; i++) {
            int currentX = originX + (2 * i + 1) * (rightEdge - originX) / 12 - xMarkTextMaxWidth / 2;
            canvas.drawText(timelines[i], currentX, bottomEdge, xMarkPaint);
        }
    }

    private void drawAxis(Canvas canvas) {
        canvas.drawLine(originX, originY, rightEdge, originY, mainAxisPaint);
        canvas.drawLine(originX, originY, originX, topEdge, mainAxisPaint);

        canvas.drawLine((originX + rightEdge) / 2, originY, (originX + rightEdge) / 2, topEdge, semiAxisPaint);

        canvas.drawLine(originX, (originY + topEdge) / 2, rightEdge, (originY + topEdge) / 2, microAxisPaint);
        for (int i = 1; i < 12; i++) {
            int currentX = originX + i * (rightEdge - originX) / 12;
            if (i == 6) {
                continue;
            }

            canvas.drawLine(currentX, originY, currentX, topEdge, microAxisPaint);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode != MeasureSpec.AT_MOST && heightMode != MeasureSpec.AT_MOST) {
            if (mWidth < mMimWidth)
                mWidth = mMimWidth;
            if (mHeight < mMimWidth)
                mHeight = mMimWidth;
        } else if (widthMeasureSpec != MeasureSpec.AT_MOST) {
            if (mWidth < mMimWidth)
                mWidth = mMimWidth;
        } else if (heightMeasureSpec != MeasureSpec.AT_MOST) {
            if (mHeight < mMimWidth)
                mHeight = mMimWidth;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        measureText();
        measureDimensions();
        setupAxisPaints();
        fakePoint = resolveBattery(fakeBattery);
        originPoint.x = originX;
        originPoint.y = originY;
    }

    private void setupAxisPaints() {
        Shader mainGradient = new LinearGradient(0f, bottomEdge, 0, topEdge, gradieStartColor, gradieEndColor, Shader.TileMode.CLAMP);
        Shader semiGradient = new LinearGradient(0f, bottomEdge, 0, topEdge, gradieStartColor, gradieEndColor, Shader.TileMode.CLAMP);
        Shader microGradient = new LinearGradient(0f, bottomEdge, 0, topEdge, gradieStartColor, gradieEndColor, Shader.TileMode.CLAMP);

        mainAxisPaint.setShader(mainGradient);
        semiAxisPaint.setShader(semiGradient);
        microAxisPaint.setShader(microGradient);
    }

    private void measureText() {
        Rect _rect = new Rect();
        String textText = "99.99";
        xMarkPaint.getTextBounds(textText, 0, textText.length(), _rect);
        xMarkTextMaxHeight = _rect.bottom - _rect.top;
        xMarkTextMaxWidth = _rect.right - _rect.left;
        textText = "999";
        yMarkPaint.getTextBounds(textText, 0, textText.length(), _rect);
        yMarkMaxTextWidth = _rect.right - _rect.left;
        yMarkMaxTextHeight = _rect.bottom - _rect.top;
    }

    private void measureDimensions() {
        int outPaddingLeft = Math.max(0, minPadding - getPaddingLeft());
        int outPaddingRight = Math.max(0, minPadding - getPaddingRight());
        int outPaddingTop = Math.max(0, minPadding - getPaddingTop());
        int outPaddingBottom = Math.max(0, minPadding - getPaddingBottom());

        leftEdge = outPaddingLeft;
        rightEdge = mWidth - outPaddingRight;
        topEdge = outPaddingTop;
        bottomEdge = mHeight - outPaddingBottom;

        originX = leftEdge + yMarkMaxTextWidth + xMarkPadding;
        originY = bottomEdge - xMarkTextMaxHeight - yMarkPadding;
    }

    private PointF resolveBattery(@NonNull Battery battery) {
        PointF _pointF = new PointF();
        float xStep = 1.0f * (rightEdge - originX) / MAX_TIME_IN_MINUTE;
        float yStep = 1.0f * (originY - topEdge) / MAX_BATTERY;
        int healthCount = (int) battery.health();
        int timeCount = battery.getMinuteOfDay();
        _pointF.set(originX + xStep * timeCount, originY - healthCount * yStep);
        return _pointF;
    }

    public void setBatteryData(@NonNull Collection<Battery> batteryData) {
        if (!batteryData.isEmpty()) {
            mBatteries.clear();
            mBatteries.addAll(batteryData);
            Collections.sort(mBatteries, new BatteyTimeCompartor());

            containsFakePoint = mBatteries.get(0).getMinuteOfDay() < fakeBattery.getMinuteOfDay();
            postInvalidate(originX, topEdge, rightEdge, originY);
        }
    }

    public void addBatteryData(@NonNull Battery battery) {
        mBatteries.add(battery);
        Collections.sort(mBatteries, new BatteyTimeCompartor());
        containsFakePoint = mBatteries.get(0).getMinuteOfDay() < fakeBattery.getMinuteOfDay();
        postInvalidate(originX, topEdge, rightEdge, originY);
    }

}
