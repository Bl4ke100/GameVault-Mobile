package com.blake.gamevault.activity;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blake.gamevault.R;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.android.material.shape.TriangleEdgeTreatment;

public class AuthActivity extends AppCompatActivity {

    private TextView tabLogin, tabSignUp;
    private LinearLayout layoutLogin, layoutSignUp;
    private boolean isLoginVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        tabLogin = findViewById(R.id.tabLogin);
        tabSignUp = findViewById(R.id.tabSignUp);
        layoutLogin = findViewById(R.id.layoutLogin);
        layoutSignUp = findViewById(R.id.layoutSignUp);

        tabSignUp.setOnClickListener(v -> {
            if (isLoginVisible) switchToSignUp();
        });

        tabLogin.setOnClickListener(v -> {
            if (!isLoginVisible) switchToLogin();
        });

//        CardView card = findViewById(R.id.cardContainer);
//
//        TypedValue typedValue = new TypedValue();
//        getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnPrimary, typedValue, true);
//        int themeColor = typedValue.data;
//
//        ShapeAppearanceModel shapeAppearanceModel = new ShapeAppearanceModel()
//                .toBuilder()
//                .setTopEdge(new TriangleEdgeTreatment(30, true)) // 'true' cuts IN, 'false' pops OUT
//                .build();
//
//        MaterialShapeDrawable shapeDrawable = new MaterialShapeDrawable(shapeAppearanceModel);
//        shapeDrawable.setFillColor(ColorStateList.valueOf(themeColor));
//        card.setBackground(shapeDrawable);
    }

    private void switchToSignUp() {
        isLoginVisible = false;

        // Tab UI updates
        tabSignUp.animate().alpha(0.5f).setDuration(300).start();
        tabLogin.animate().alpha(1.0f).setDuration(300).start();

        // Slide current out to the left
        layoutLogin.animate().translationX(-100f).alpha(0f).setDuration(300).withEndAction(() -> {
            layoutLogin.setVisibility(View.GONE);
        }).start();

        // Slide new in from the right
        layoutSignUp.setVisibility(View.VISIBLE);
        layoutSignUp.setTranslationX(100f);
        layoutSignUp.animate().translationX(0f).alpha(1f).setDuration(300).start();
    }

    private void switchToLogin() {
        isLoginVisible = true;

        // Tab UI updates
        tabLogin.animate().alpha(0.5f).setDuration(300).start();
        tabSignUp.animate().alpha(1.0f).setDuration(300).start();

        // Slide current out to the right
        layoutSignUp.animate().translationX(100f).alpha(0f).setDuration(300).withEndAction(() -> {
            layoutSignUp.setVisibility(View.GONE);
        }).start();

        // Slide new in from the left
        layoutLogin.setVisibility(View.VISIBLE);
        layoutLogin.setTranslationX(-100f);
        layoutLogin.animate().translationX(0f).alpha(1f).setDuration(300).start();
    }


}