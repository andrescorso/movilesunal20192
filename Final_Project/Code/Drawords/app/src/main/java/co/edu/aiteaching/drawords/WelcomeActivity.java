package co.edu.aiteaching.drawords;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ImageButton mWelcomeButton = (ImageButton) findViewById(R.id.welcome);
        mWelcomeButton.setBackgroundColor(Color.WHITE);
        mWelcomeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}
