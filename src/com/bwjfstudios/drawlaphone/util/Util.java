package com.bwjfstudios.drawlaphone.util;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;

import com.android.internal.util.Predicate;
import com.bwjfstudios.drawlaphone.R;

import java.util.ArrayList;
import java.util.List;

// Damn, this is a useful util class
public class Util {

    public static Animation getFadeIn(int time) {
        Animation animation = new AlphaAnimation(0, 1);
        animation.setInterpolator(new DecelerateInterpolator()); //add this
        animation.setDuration(time);
        return animation;
    }

    public static Animation getSuccessAnim() {
        return getFadeIn(500);
    }

    public static Animation getFailAnim(Context context) {
        return AnimationUtils.loadAnimation(context, R.anim.wobble);
    }

     public static <E> List<E> filter(List<E> list, Predicate<E> predicate) {
         List<E> result = new ArrayList<E>();
         for (E e: list) {
             if(predicate.apply(e)) {
                 result.add(e);
             }
         }
         return result;
     }
}