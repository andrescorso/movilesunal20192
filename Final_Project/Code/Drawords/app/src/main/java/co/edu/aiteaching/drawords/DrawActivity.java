package co.edu.aiteaching.drawords;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import co.edu.aiteaching.drawords.models.ElementCategory;

import android.app.AlertDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class DrawActivity extends AppCompatActivity {

    private PaintView paintView; // custom drawing view
    private Button clear_b;
    private TextView textViewDraw;
    private TextView textViewCounter;
    private TextView textViewToDraw;
    private int images[] = {R.drawable.cat, R.drawable.bee, R.drawable.dog, R.drawable.cow, R.drawable.duck, R.drawable.frog};

    private String names[] = {"Cat", "Bird", "Hot Dog", "Ice Cream", "Apple", "Grapes", "Saw", "Sock", "T-shirt", "Screwdriver", "Anvil", "Tennis", "Moustache", "Shorts", "Basketball", "Axe", "Eye", "Pizza", "Shovel", "Line", "Square", "Hat", "Cookie", "Power Outlet", "Circle", "Butterfly", "Triangle" };
    private String categories[] = {"Animals", "Animals", "Food", "Food", "Fruits", "Fruits", "Tools", "Wearing", "Wearing", "Tools", "Tools", "Sports", "Human Body", "Wearing", "Sports", "Tools", "Human Body", "Food", "Tools", "Geometric Figures", "Geometric Figures", "Wearing", "Food", "Tools", "Geometric Figures", "Animals", "Geometric Figures" };

    private int nrand=0;

    private int times;
    private int timeOK=10;
    private int timeWrong = 12;

    private View hintView;
    private ImageView imageViewHint;
    //private View convertView;

    private CountDownTimer countDown;
    private CountDownTimer goodCountDown;


    private AlertDialog.Builder correct_dialog;
    private AlertDialog.Builder wrong_dialog;
    private AlertDialog.Builder hint_dialog;

    private static int PIXEL_WIDTH = 28;
    private SketchDetector sketchClassifier;
    private TextView textViewItis;


    private HashMap<String,String> hm_images = new HashMap<String, String>();
    private HashMap<String,String> hm_names = new HashMap<String, String>();
    private HashMap<String,ArrayList<String>> hm_cat = new HashMap<String, ArrayList<String>>();
    private String userID;
    private DatabaseReference mDatabase;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sketchClassifier = new SketchDetector(this);

        textViewItis = (TextView) findViewById(R.id.section_radar);

        paintView = (PaintView) findViewById(R.id.view_paint);

        userID = getIntent().getStringExtra("iduser");

        clear_b = (Button) findViewById(R.id.button_clear);
        clear_b.setOnClickListener(new ButtonClear());
        //textViewDraw = (TextView) findViewById(R.id.txt_draw);
        textViewCounter = (TextView) findViewById(R.id.txt_counter);
        textViewToDraw = (TextView) findViewById(R.id.txt_todraw);

        //loadFirebase();
        hm_cat = (HashMap<String, ArrayList<String>>) getIntent().getSerializableExtra("hm_cat");
        hm_images = (HashMap<String, String>) getIntent().getSerializableExtra("hm_images");
        hm_names = (HashMap<String, String>) getIntent().getSerializableExtra("hm_names");

        imageViewHint = (ImageView) findViewById(R.id.imageDialog);

        mDatabase = FirebaseDatabase.getInstance().getReference();



        correct_dialog = new AlertDialog.Builder(this);
        correct_dialog.setTitle(R.string.correct_title);
        correct_dialog.setMessage(R.string.correct_result);
        correct_dialog.setPositiveButton(R.string.correct_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface correct_dialog, int id) {
                correct_dialog.dismiss();
                newWord();
            }
        });

        correct_dialog.setNeutralButton(R.string.correct_reinforce,new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface correct_dialog, int id) {
                correct_dialog.dismiss();

                hint_dialog = new AlertDialog.Builder(DrawActivity.this);
                hint_dialog.setTitle(R.string.correct_reinforce);
                //hint_dialog.setMessage(R.string.again_result);
                LayoutInflater inflater = getLayoutInflater();
                final View convertView = (LinearLayout) inflater.inflate(R.layout.image_dialog, null);
                //convertView.setBackgroundResource(images[nrand]);

                hint_dialog.setView(convertView);
                //hint_dialog.setView(R.layout.image_dialog);

                hint_dialog.setPositiveButton(R.string.correct_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface hint_dialog, int id) {
                        hint_dialog.dismiss();
                        newWord();
                    }
                });

                hint_dialog.setCancelable(false);
                hint_dialog.show();
                ImageView ivHint = (ImageView) convertView.findViewById(R.id.imageDialog);
                new DownLoadImageTask(ivHint).execute(hm_images.get(names[nrand]));


                //Log.i("hola", String.valueOf(hint_dialog));
                //imageViewHint.setImageResource(images[nrand]);
            }
        });
        correct_dialog.setCancelable(false);

        wrong_dialog = new AlertDialog.Builder(this);
        wrong_dialog.setTitle(R.string.again_title);
        wrong_dialog.setMessage(R.string.again_result);
        wrong_dialog.setPositiveButton(R.string.again_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface wrong_dialog, int id) {
                wrong_dialog.dismiss();
                //Log.i("i", String.valueOf(imageViewHint==null));


                //Intent intent = new Intent(getApplicationContext(),ImageModal.class);
                //intent.putExtra("draw",images[nrand]);
                //startActivity(intent);
                hint_dialog = new AlertDialog.Builder(DrawActivity.this);
                hint_dialog.setTitle(R.string.hint_title);
                //hint_dialog.setMessage(R.string.again_result);
                LayoutInflater inflater = getLayoutInflater();
                final View convertView = (LinearLayout) inflater.inflate(R.layout.image_dialog, null);
                //convertView.setBackgroundResource(images[nrand]);

                hint_dialog.setView(convertView);
                //hint_dialog.setView(R.layout.image_dialog);

                hint_dialog.setPositiveButton(R.string.correct_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface hint_dialog, int id) {
                        hint_dialog.dismiss();
                        times++;
                        startTimer(timeOK);
                        //goodstartTimer(timeWrong);

                        //Study again
                        ////////////////////////////////////////////////////
                        // TODO
                    }
                });
                hint_dialog.setCancelable(false);

                hint_dialog.show();
                ImageView ivHint = (ImageView) convertView.findViewById(R.id.imageDialog);
                new DownLoadImageTask(ivHint).execute(hm_images.get(names[nrand]));


                //Log.i("hola", String.valueOf(hint_dialog));
                //imageViewHint.setImageResource(images[nrand]);
            }
        });
        wrong_dialog.setNegativeButton(R.string.again_try, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface wrong_dialog, int id) {
                wrong_dialog.dismiss();
                //Cancelar
                times++;
                startTimer(timeOK);
                //goodstartTimer(timeWrong);
                //newWord();
            }
        });
        wrong_dialog.setNeutralButton(R.string.again_skip, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface wrong_dialog, int id) {
                wrong_dialog.dismiss();
                //Cancelar
                newWord();

            }
        });


        wrong_dialog.setCancelable(false);

        paintView.init(sketchClassifier, textViewItis, correct_dialog, userID); // initial drawing view


        times = 1;
        newWord();

    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void loadFirebase(){

        FirebaseDatabase.getInstance().getReference().child("ElementCategory").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot

                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            //hm_images.put(dsp.)

                            ElementCategory element = dsp.getValue(ElementCategory.class);
                            hm_images.put(element.getName(),element.getPath());
                            hm_names.put(element.getName(),element.getCategory());
                            if (!hm_cat.containsKey(element.getCategory())) {
                                hm_cat.put(element.getCategory(),new ArrayList<String>());
                            }
                            hm_cat.get(element.getCategory()).add(element.getName());
                        }

                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        //File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File mypath=new File(directory,"draw.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
    private class  ButtonClear implements View.OnClickListener{
        public void onClick(View view){
            //nrand = (int) Math.floor(Math.random()*words.length);
            //newWord();
            /*
            popUp.showAtLocation(layout, Gravity.BOTTOM,10,10);
            popUp.update(50,50,300,80);
             */
            paintView.clearView();


        }
    }
    private void newWord(){
        times++;
        int lastnrand = nrand;
        while (nrand == lastnrand) {
            nrand = (int) Math.floor(Math.random() * names.length);//names.length);
        }
        textViewToDraw.setText("Draw: "+names[nrand]);
        //Log.i("hola",saveToInternalStorage(paintView.getmBitmap()));
        paintView.clearView();
        paintView.setObjective(names[nrand], categories[nrand]);

        startTimer(timeOK);
        textViewItis.setText("Practice");
        //goodstartTimer(timeWrong);
    }

    private void startTimer(int time){
        countDown = new CountDownTimer(time *1000, 1000) {

            public void onTick(long millisUntilFinished) {
                textViewCounter.setText("Seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                try {
                    wrong_dialog.show();
                    mDatabase.child("scores").child(userID).child("B"+categories[nrand]).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long nVal = (long)dataSnapshot.getValue() + 1;
                            mDatabase.child("scores").child(userID).child("B"+categories[nrand]).setValue(nVal);

                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //handle databaseError
                        }

                    });

                } catch (Exception e) {
                    Log.i("EXIT_PRACTICE", e.toString());
                }
            }
        };
        countDown.start();
        paintView.setCountDown(countDown);
    }


    private void goodstartTimer(int time) {
        if (times % 2 != 0) {
            goodCountDown = new CountDownTimer(time * 1000, 1000) {

                public void onTick(long millisUntilFinished) {
                    //textViewCounter.setText("Seconds remaining: " + millisUntilFinished / 1000);
                    return;
                }

                public void onFinish() {
                    countDown.cancel();
                    correct_dialog.show();
                }
            };
            goodCountDown.start();
        }
    }
}
