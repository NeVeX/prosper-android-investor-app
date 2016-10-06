package com.mark.prosper.invest.util;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by NeVeX on 10/3/2015.
 */
public class UIAnimation {

    public enum ValueType { MONEY_WITH_FRACTIONAL, MONEY, TEXT, PERCENTILE, PERCENTILE_WITH_FRACTIONAL }
    private final static long ANIMATION_TIME_LENGTH = 1000;

    public static void animateValue(final TextView textView, double startValue, double endValue, final ValueType textType, final long duration)
    {
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(startValue, endValue);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                switch (textType) {
                    case MONEY:
                        textView.setText(UIUtil.getMoneyString((Double)animation.getAnimatedValue(), false));
                        break;
                    case PERCENTILE:
                        textView.setText(UIUtil.getPercentageString((Double)animation.getAnimatedValue(), false));
                        break;
                    case MONEY_WITH_FRACTIONAL:
                        textView.setText(UIUtil.getMoneyString((Double)animation.getAnimatedValue(), true));
                        break;
                    case PERCENTILE_WITH_FRACTIONAL:
                        textView.setText(UIUtil.getPercentageString((Double)animation.getAnimatedValue(), true));
                        break;
                    default:
                        textView.setText(String.valueOf(animation.getAnimatedValue()));
                }

            }
        });
        animator.setEvaluator(new TypeEvaluator<Double>() {
            public Double evaluate(float fraction, Double startValue, Double endValue) {
                if ( startValue != 0)
                {
                    double fractionalValue = (endValue - startValue) * fraction;
                    return startValue - ( fractionalValue * -1);
                }
                return (endValue - startValue) * fraction;
            }
        });
        animator.setDuration(duration);
        animator.start();
    }

//    public static void animateValue(final TextView textView, int startValue, int endValue, final ValueType textType)
//    {
//        ValueAnimator animator = new ValueAnimator();
//        animator.setObjectValues(startValue, endValue);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            public void onAnimationUpdate(ValueAnimator animation) {
//                switch (textType) {
//                    case MONEY:
//                        textView.setText(UIUtil.getMoneyString((Double)animation.getAnimatedValue()));
//                        break;
//                    case PERCENTILE:
//                        textView.setText(UIUtil.getPercentageString((Double) animation.getAnimatedValue()));
//                        break;
//                    default:
//                        textView.setText(String.valueOf(animation.getAnimatedValue()));
//                }
//
//            }
//        });
//        animator.setEvaluator(new TypeEvaluator<Integer>() {
//            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
//                return Math.round((endValue - startValue) * fraction);
//            }
//        });
//        animator.setDuration(ANIMATION_TIME_LENGTH);
//        animator.start();
//    }

    public static void animateValue(final ProgressBar progressBar, int startValue, int endValue, long duration)
    {
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(startValue, endValue);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                progressBar.setProgress((int) animation.getAnimatedValue());
            }
        });
        animator.setEvaluator(new TypeEvaluator<Integer>() {
            public Integer evaluate(float fraction, Integer startValue, Integer endValue) {
                return Math.round((endValue - startValue) * fraction);
            }
        });
        animator.setDuration(duration);
        animator.start();
    }
}
