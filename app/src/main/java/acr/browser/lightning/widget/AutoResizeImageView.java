package acr.browser.lightning.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import net.chinaedu.aedu.utils.LogUtils;

/**
 * Created by MartinKent on 2017/5/21.
 */

@SuppressLint("AppCompatCustomView")
public class AutoResizeImageView extends ImageView {

    private Matrix matrix = new Matrix();

    public AutoResizeImageView(Context context) {
        super(context);
    }

    public AutoResizeImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoResizeImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (null != getDrawable() && getDrawable() instanceof BitmapDrawable) {
            Bitmap mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
            float ratio = mBitmap.getHeight() * 1.0f / mBitmap.getWidth();
            LogUtils.d("image w=" + mBitmap.getWidth());
            LogUtils.d("image h=" + mBitmap.getHeight());
            LogUtils.d("view w=" + width);
            LogUtils.d("view h=" + (int) (width * ratio));
            setMeasuredDimension(width, (int) (width * ratio));
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (null != getDrawable() && getDrawable() instanceof BitmapDrawable) {
            Bitmap mBitmap = ((BitmapDrawable) getDrawable()).getBitmap();
            float ratio = getWidth() * 1.0f / mBitmap.getWidth();
            matrix.postScale(ratio, ratio);
            setImageMatrix(matrix);
        }
        super.onDraw(canvas);
    }
}
