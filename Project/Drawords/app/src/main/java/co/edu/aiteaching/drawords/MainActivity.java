package co.edu.aiteaching.drawords;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private PaintView paintView; // custom drawing view
    private Button clear_b;
    private TextView textViewDraw;
    private String[] words = {"triangle","square","circle"};

    private int nrand;

/*
    PopupWindow popUp;
    LinearLayout layout;

 */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        paintView = (PaintView) findViewById(R.id.view_paint);
        paintView.init(); // initial drawing view

        clear_b = (Button) findViewById(R.id.button_clear);
        clear_b.setOnClickListener(new ButtonClear());
        textViewDraw = (TextView) findViewById(R.id.txt_draw);
/*
        popUp = new PopupWindow(this);
        layout = new LinearLayout(this);

 */
    }
    private class  ButtonClear implements View.OnClickListener{
        public void onClick(View view){
            nrand = (int) Math.floor(Math.random()*words.length);
            textViewDraw.setText(words[nrand]);
            paintView.clearView();
            /*
            popUp.showAtLocation(layout, Gravity.BOTTOM,10,10);
            popUp.update(50,50,300,80);

             */

        }
    }



}
