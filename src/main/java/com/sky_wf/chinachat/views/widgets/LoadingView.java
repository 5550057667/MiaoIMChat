package com.sky_wf.chinachat.views.widgets;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.sky_wf.chinachat.R;


/**
 * @Date : 2018/6/20
 * @Author : WF
 * @Description :
 */
public class LoadingView extends View
{
    private final int DEFAULT_LOADING_COLOR = Color.WHITE;
    private final int DEFAULT_SUCESS_COLOR = Color.BLUE;
    private final int DEFAULT_FAILED_COLOR = Color.RED;
    private final int DEFAULT_VIEW_WIDTH = transFormPx(50);
    private final int DEFAULT_VIEW_HEIGHT = transFormPx(50);
    private final int DEFAULT_STROKE_WIDTH = transFormPx(2);
    private final boolean DEFAULT_IS_SHOWRESULT = false;
    private int loadingColor, successColor, failedColor;
    private Paint loadingPaint;
    private Paint successPaint;
    private Paint failedPaint;
    private float successValue;
    private float leftFailedValue;
    private float rightFailedValue;
    private Path desPath;
    private Path successPath;
    private Path leftFailPath;
    private Path rightFailPath;
    private PathMeasure pathMeasure;
    private RectF rectF;
    private int width;
    private int height;
    private int strokeWidth;
    private int radius;
    private int halfStrokeWidth;
    private int rotateDelta = 4;
    private int curAngle = 0;
    private int minAngle = -90;
    private int startAngle = -90;
    private int endAngle = 0;
    private boolean isShowResult;
    private StatusEnum statusEnum = StatusEnum.LOADING;
    private ValueAnimator successAnimator;
    private ValueAnimator leftFailAnimator;
    private ValueAnimator rightFailAnimator;
    private AnimatorSet animatorSet;

    public LoadingView(Context context)
    {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs)
    {
        TypedArray typedArray = null;
        try
        {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.loadingView);
            loadingColor = typedArray.getColor(R.styleable.loadingView_loadingColor,
                    DEFAULT_LOADING_COLOR);
            successColor = typedArray.getColor(R.styleable.loadingView_successColor,
                    DEFAULT_SUCESS_COLOR);
            failedColor = typedArray.getColor(R.styleable.loadingView_failedColor,
                    DEFAULT_FAILED_COLOR);
            strokeWidth = (int) typedArray.getDimension(R.styleable.loadingView_strokeWidth,
                    DEFAULT_STROKE_WIDTH);
            isShowResult = typedArray.getBoolean(R.styleable.loadingView_isShowResult,
                    DEFAULT_IS_SHOWRESULT);
        } catch (Exception e)
        {

        } finally
        {
            if (typedArray != null)
            {
                typedArray.recycle();
            }
        }

        loadingPaint = createLoadingPaint(loadingColor, strokeWidth, Paint.Style.STROKE);
        successPaint = createSuceessPaint(successColor, strokeWidth, Paint.Style.STROKE);
        failedPaint = createFailedPaint(failedColor, strokeWidth, Paint.Style.STROKE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        radius = Math.min(width, height) / 2;
        halfStrokeWidth = strokeWidth / 2;

        rectF = new RectF(halfStrokeWidth - radius, halfStrokeWidth - radius,
                radius - halfStrokeWidth, radius - halfStrokeWidth);

        successPath = new Path();
        successPath.moveTo(-radius * 2 / 3f, 0f);
        successPath.lineTo(-radius / 8f, radius / 2f);
        successPath.lineTo(radius / 2f, -radius / 3);

        leftFailPath = new Path();
        leftFailPath.moveTo(-radius / 3f, -radius / 3f);
        leftFailPath.lineTo(radius / 3f, radius / 3f);

        rightFailPath = new Path();
        rightFailPath.moveTo(radius / 3f, -radius / 3f);
        rightFailPath.lineTo(-radius / 3f, radius / 3f);

        desPath = new Path();
        pathMeasure = new PathMeasure();

        initSuccessAnimator();
        initFailedAnimator();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST)
        {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_VIEW_WIDTH, MeasureSpec.EXACTLY);
        }

        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST)
        {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_VIEW_HEIGHT,
                    MeasureSpec.EXACTLY);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        canvas.save();
        canvas.translate(width / 2, height / 2);
        desPath.reset();
//        desPath.lineTo(0, 0);
        if (statusEnum == StatusEnum.LOADING)
        {
            if (endAngle >= 300 || startAngle > minAngle)
            {
                startAngle += 6;
                if (endAngle > 20)
                {
                    endAngle -= 6;
                }
            }
            if (startAngle > minAngle + 300)
            {
                minAngle = startAngle;
                endAngle = 20;
            }

            canvas.rotate(curAngle += rotateDelta, 0, 0);
            canvas.drawArc(rectF, startAngle, endAngle, false, loadingPaint);
            if (startAngle == minAngle)
            {
                endAngle += 6;
            }
            Log.d("wftt","startAngle>>"+startAngle+"-----endAngle>>"+endAngle+"-----minAngle>>>"+minAngle);
            invalidate();
        }

        if (isShowResult)
        {
            if (statusEnum == StatusEnum.LOADING_SUCESS)
            {
                pathMeasure.setPath(successPath, false);
                canvas.drawCircle(0, 0, radius - halfStrokeWidth, successPaint);
                pathMeasure.getSegment(0, successValue * pathMeasure.getLength(), desPath, true);
                canvas.drawPath(desPath, successPaint);
            } else if (statusEnum == StatusEnum.LOADING_FAILED)
            {
                canvas.drawCircle(0, 0, radius - halfStrokeWidth, failedPaint);
                pathMeasure.setPath(rightFailPath, false);
                pathMeasure.getSegment(0, rightFailedValue * pathMeasure.getLength(), desPath,
                        true);
                if (rightFailedValue == 1)
                {
                    pathMeasure.setPath(leftFailPath, false);
                    pathMeasure.nextContour();
                    pathMeasure.getSegment(0, leftFailedValue * pathMeasure.getLength(), desPath,
                            true);
                }
                canvas.drawPath(desPath, failedPaint);
            }
        }
        canvas.restore();
    }

    private void initSuccessAnimator()
    {
        successAnimator = ValueAnimator.ofFloat(0, 1f);
        successAnimator.setDuration(1000);
        successAnimator.setInterpolator(new LinearInterpolator());
        successAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                successValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }

    private void initFailedAnimator()
    {
        leftFailAnimator = ValueAnimator.ofFloat(0, 1f);
        leftFailAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                leftFailedValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        rightFailAnimator = ValueAnimator.ofFloat(0, 1f);
        rightFailAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                rightFailedValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        animatorSet = new AnimatorSet();
        animatorSet.play(leftFailAnimator).after(rightFailAnimator);
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new LinearInterpolator());
    }

    /**
     * 创建loading圈画笔
     *
     * @param color
     * @param strokeWidth
     * @param style
     * @return
     */
    private Paint createLoadingPaint(int color, int strokeWidth, Paint.Style style)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        paint.setStyle(style);
        return paint;
    }

    /**
     * 创建加载成功画笔
     *
     * @param color
     * @param strokeWidth
     * @param style
     * @return
     */
    private Paint createSuceessPaint(int color, int strokeWidth, Paint.Style style)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        paint.setStyle(style);
        return paint;
    }

    /**
     * 创建加载失败画笔
     *
     * @param color
     * @param strokeWidth
     * @param style
     * @return
     */
    private Paint createFailedPaint(int color, int strokeWidth, Paint.Style style)
    {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        paint.setStyle(style);
        return paint;
    }

    /**
     * 将dp转化为像素px
     *
     * @param dpValue
     * @return
     */
    private int transFormPx(int dpValue)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue,
                getResources().getDisplayMetrics());
    }

    public enum StatusEnum
    {
        LOADING, // 加载中
        LOADING_SUCESS, // 加载成功，显示对号
        LOADING_FAILED, // 加载失败，显示叉号
        END // loading执行结束
    }

    public void endAnim()
    {
        statusEnum = StatusEnum.END;
    }

    public void updateStatue(StatusEnum statusEnum)
    {

        this.statusEnum = statusEnum;
        if(statusEnum == StatusEnum.LOADING_SUCESS)
        {
            successAnimator.start();
        }else if(statusEnum == StatusEnum.LOADING_FAILED)
        {
            animatorSet.start();
        }else if(statusEnum == StatusEnum.LOADING)
        {
            successAnimator.cancel();
            animatorSet.cancel();
            invalidate();
        }
    }
}
