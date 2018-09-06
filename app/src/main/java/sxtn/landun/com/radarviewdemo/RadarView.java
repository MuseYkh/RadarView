package sxtn.landun.com.radarviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * author: muse
 * created on: 2018/8/31 下午5:43
 * description:
 */

public class RadarView extends View {

    //默认数据个数
    private int dataCount = 5;
    private float angle;
    //网格最大半径
    private float radius = (float) (Math.PI * 2 / dataCount);
    private int centerX;
    private int centerY;
    private String[] titles = {"A", "B", "C", "D", "E"};
    private float[] data = {10, 30, 50, 70, 90};
    private float maxValue = 100;
    //雷达区画笔
    private Paint mainPaint;
    //数据区画笔
    private Paint valuePaint;
    //文本画笔
    private Paint textPaint;
    private float textSize;
    private int color;

    public RadarView(Context context) {
        super(context);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RadarView, 0, 0);
        color = typedArray.getColor(R.styleable.RadarView_radar_color, Color.CYAN);
        textSize = typedArray.getDimension(R.styleable.RadarView_radar_text_size, DensityUtil.sp2px(context, 12));
        typedArray.recycle();

        angle = (float) (Math.PI * 2 / dataCount);

        mainPaint = new Paint();
        mainPaint.setColor(Color.DKGRAY);           // 画笔颜色 - 黑色
        mainPaint.setStyle(Paint.Style.STROKE);    // 填充模式 - 描边
        mainPaint.setStrokeWidth(1);              // 边框宽度 - 10
        mainPaint.setAntiAlias(true);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);
        valuePaint = new Paint();
        valuePaint.setStyle(Paint.Style.FILL);
        valuePaint.setColor(color);
        valuePaint.setAntiAlias(true);
    }

    public RadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        radius = Math.min(w, h) / 2 * 0.8f;
        centerX = w / 2;
        centerY = h / 2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawPolygon(canvas);
        drawLine(canvas);
        drawText(canvas);
        drawValueRegion(canvas);
    }

    /**
     * 绘制正多边形
     *
     * @param canvas
     */
    private void drawPolygon(Canvas canvas) {
        Path path = new Path();
        float r = radius / (dataCount - 1);//r是蜘蛛丝之间的间距
        for (int i = 1; i < dataCount; i++) {//中心点不用绘制
            float curR = r * i;//当前半径
            path.reset();
            for (int j = 0; j < dataCount; j++) {
                if (j == 0) {
                    path.moveTo(centerX, centerY - curR);
                } else {
                    //根据半径，计算出蜘蛛丝上每个点的坐标
                    float x = (float) (centerX + curR * Math.cos(angle * j - Math.PI / 2));
                    float y = (float) (centerY + curR * Math.sin(angle * j - Math.PI / 2));
                    path.lineTo(x, y);
                }
            }
            path.close();//闭合路径
            canvas.drawPath(path, mainPaint);
        }
    }

    /**
     * 绘制每项的连接线
     *
     * @param canvas
     */
    private void drawLine(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < dataCount; i++) {
            path.reset();
            path.moveTo(centerX, centerY);
            float x = (float) (centerX + radius * Math.cos(angle * i - Math.PI / 2));
            float y = (float) (centerY + radius * Math.sin(angle * i - Math.PI / 2));
            path.lineTo(x, y);
            canvas.drawPath(path, mainPaint);
        }
    }

    /**
     * 绘制数据
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        // 计算出文字的高度以便偏移
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontHeight = fontMetrics.descent - fontMetrics.ascent;
        for (int i = 0; i < dataCount; i++) {
            float x = (float) (centerX + (radius + fontHeight / 2) * Math.cos(angle * i - Math.PI / 2));
            float y = (float) (centerY + (radius + fontHeight / 2) * Math.sin(angle * i - Math.PI / 2));
            // 如果是在第二第三象限，也就是y轴的左边，那么需要在绘制文字时，把基准点定位文本的末尾，即需要加上文本的长度
            float curAngle = (float) (angle * i - Math.PI / 2);
            //文本长度
            float dis = textPaint.measureText(titles[i]);
            if (curAngle == (float) -Math.PI / 2) {
                canvas.drawText(titles[i], x - dis / 2, y, textPaint);
            } else if (curAngle == (float) Math.PI / 2) {
                canvas.drawText(titles[i], x - dis / 2, y + fontHeight / 2, textPaint);
            } else if (curAngle > (float) -Math.PI / 2 && curAngle < (float) Math.PI / 2) { // y轴的右边
                canvas.drawText(titles[i], x, y, textPaint);
            } else { // y轴的左边
                canvas.drawText(titles[i], x - dis, y, textPaint);
            }
        }
    }

    /**
     * 绘制数据覆盖区域
     *
     * @param canvas
     */
    private void drawValueRegion(Canvas canvas) {
        Path path = new Path();
        valuePaint.setAlpha(255);
        for (int i = 0; i < dataCount; i++) {
            double percent = data[i] / maxValue;
            float x = (float) (centerX + radius * percent * Math.cos(angle * i - Math.PI / 2));
            float y = (float) (centerY + radius * percent * Math.sin(angle * i - Math.PI / 2));
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
            canvas.drawCircle(x, y, 10, valuePaint);
        }
        path.close();
        valuePaint.setAlpha(127); // 一半的透明度
        canvas.drawPath(path, valuePaint);
    }

    /**
     * 需确保标题和数据长度一致
     *
     * @param titles
     * @param values
     * @param maxValue
     */
    public void setData(String[] titles, float[] values, float maxValue) {
        if (titles.length != values.length) {
            throw new IllegalArgumentException("Titles length and datas length must be consistent");
        }
        dataCount = titles.length;
        this.titles = titles;
        this.data = values;
        this.maxValue = maxValue;
        angle = (float) (Math.PI * 2 / dataCount);
        invalidate();
    }

    public void setColor(@ColorInt int valueColor) {
        this.color = valueColor;
        valuePaint.setColor(this.color);
        invalidate();
    }

    /**
     * 单位为sp
     * @param size
     */
    public void setTextSize(float size) {
        this.textSize = DensityUtil.sp2px(getContext(), size);
        textPaint.setTextSize(textSize);
        invalidate();
    }
}
