import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by William.Qin on 6/17/2015.
 */
public class AutoScrollImageView extends HorizontalScrollView implements Runnable {
    Drawable drawable;
    MyHandler handler = new MyHandler(this);
    private boolean circleScroll = true;
    int maxScrollX;
    int targetScrollX = 0;
    int K = 1;
    boolean scrollToRight = true;

    public AutoScrollImageView(Context context) {
        super(context);
        setHorizontalScrollBarEnabled(false);
    }

    public AutoScrollImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setHorizontalScrollBarEnabled(false);
    }

    public AutoScrollImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);
    }

    public void setImage(Drawable d) {
        drawable = d;
        ImageView imageView = new ImageView(getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        imageView.setImageDrawable(d);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.requestLayout();
        this.addView(imageView);
        this.requestLayout();
    }

    public void setImageResource(int id) {
        setImage(getContext().getResources().getDrawable(id));
    }

    public void startScroll() {
        //start scroll
        this.post(this);
    }

    /**
     * default is true, scroll to end then back scroll
     *
     * @param b set true to circle scroll
     */
    public void setCircleScroll(boolean b) {
        circleScroll = b;
    }

    public void setTouchAble(boolean b) {
        if (!b) {
            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return true;
                }
            });
        }
    }

    @Override
    public void run() {
        maxScrollX = getChildAt(0).getMeasuredWidth() - getMeasuredWidth();
        if (maxScrollX > 0) {
            new Thread(delay).start();
        }
        Log.e("scroll", getChildAt(0).getMeasuredWidth() + " " + targetScrollX + scrollToRight);
    }

    private static class MyHandler extends Handler {
        private WeakReference<AutoScrollImageView> activityWeakReference;

        public MyHandler(AutoScrollImageView activity) {
            activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            AutoScrollImageView autoView = activityWeakReference.get();
            if (autoView != null) {
                if (autoView.circleScroll) {
                    if (autoView.targetScrollX <= autoView.maxScrollX && autoView.targetScrollX >= 0) {
                        if (autoView.scrollToRight) {
                            autoView.targetScrollX += autoView.K;
                        } else {
                            autoView.targetScrollX -= autoView.K;
                        }
                    } else {
                        if (autoView.scrollToRight) {
                            autoView.targetScrollX -= autoView.K;
                        } else {
                            autoView.targetScrollX += autoView.K;
                        }
                        autoView.scrollToRight = !autoView.scrollToRight;
                    }
                } else {
                    if (autoView.targetScrollX <= autoView.getChildAt(0).getMeasuredWidth()) {
                        if (autoView.scrollToRight) {
                            autoView.targetScrollX += autoView.K;
                        } else {
                            autoView.targetScrollX -= autoView.K;
                        }
                    }
                }
                autoView.smoothScrollTo(autoView.targetScrollX, 0);
                autoView.requestLayout();
            }
        }

    }

    Runnable delay = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }
    };
}
