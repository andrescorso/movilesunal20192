package co.edu.aiteaching.drawords;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ImageModal extends Activity {
    private ImageView imageViewHint;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageViewHint = (ImageView) findViewById(R.id.imageDialog);
        Log.i("hola", String.valueOf(getIntent().getIntExtra("draw",0)));
        imageViewHint.setImageResource(getIntent().getIntExtra("draw",0));
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.image_dialog);
    }
}
