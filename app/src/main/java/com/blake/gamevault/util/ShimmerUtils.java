package com.blake.gamevault.util;
import com.facebook.shimmer.Shimmer;
import com.facebook.shimmer.ShimmerDrawable;
public class ShimmerUtils {
    public static ShimmerDrawable getShimmerDrawable() {
        Shimmer shimmer = new Shimmer.AlphaHighlightBuilder()
                .setDuration(1500) 
                .setBaseAlpha(0.85f)
                .setHighlightAlpha(0.6f)
                .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
                .setAutoStart(true)
                .build();
        ShimmerDrawable shimmerDrawable = new ShimmerDrawable();
        shimmerDrawable.setShimmer(shimmer);
        return shimmerDrawable;
    }
}