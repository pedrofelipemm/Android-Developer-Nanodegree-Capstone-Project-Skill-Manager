package study.pmoreira.skillmanager.ui.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class RoundedImageView extends AppCompatImageView {
    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() != null) {
            getDrawable().draw(canvas);
        }
    }

    @Override
    public Drawable getDrawable() {
        Drawable d = super.getDrawable();
        if (d == null || d instanceof VectorDrawable) {
            return d;
        }

        Bitmap b = ((BitmapDrawable) d).getBitmap();
        RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), b);
        roundedBitmapDrawable.setCircular(true);
        roundedBitmapDrawable.setBounds(new Rect(0, 0, getHeight(), getHeight()));

        return roundedBitmapDrawable;
    }
}
