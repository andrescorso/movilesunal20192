package co.edu.aiteaching.drawords;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import co.edu.aiteaching.drawords.models.ElementCategory;

public class MenuActivity extends AppCompatActivity {

    private Button mLearnButton;
    private Button mPracticeButton;
    private String userID;
    private String userName;
    private Button mProgressButton;

    private HashMap<String,String> hm_images = new HashMap<String, String>();
    private HashMap<String,String> hm_names = new HashMap<String, String>();
    private HashMap<String,ArrayList<String>> hm_cat = new HashMap<String, ArrayList<String>>();


    /** Called when the user clicks the Send button */
    public void showFlashcards(View view) {
        Intent intent = new Intent(this, LearningActivity.class);
        intent.putExtra("hm_images",hm_images);
        intent.putExtra("hm_cat",hm_cat);
        intent.putExtra("hm_names",hm_names);

        startActivity(intent);
    }

    public void showCanvas(View view) {
        Intent intent = new Intent(this, DrawActivity.class);
        intent.putExtra("iduser", userID);
        intent.putExtra("hm_images",hm_images);
        intent.putExtra("hm_cat",hm_cat);
        intent.putExtra("hm_names",hm_names);

        startActivity(intent);
    }
    public void showProgress(View view) {
        Intent intent = new Intent(this, ProgressActivity.class);
        intent.putExtra("iduser", userID);

        startActivity(intent);
    }
    public void logoutActivity(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("uLogout",true);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        if(userID == null) {
            userID = getIntent().getStringExtra("iduser");
            userName = getIntent().getStringExtra("username");
            Toast.makeText(getApplicationContext(),"Welcome " + userName,Toast.LENGTH_LONG).show();
        }



        mLearnButton = (Button) findViewById(R.id.learn_button);
        mPracticeButton = (Button) findViewById(R.id.practice_button);
        mProgressButton = (Button) findViewById(R.id.progress_button);

        mLearnButton.setEnabled(false);
        mPracticeButton.setEnabled(false);
        mProgressButton.setEnabled(false);

        FirebaseDatabase.getInstance().getReference().child("ElementCategory").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot

                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            //hm_images.put(dsp.)

                            ElementCategory element = dsp.getValue(ElementCategory.class);
                            Log.i("COSAS",element.getName() + ":" + element.getCategory());
                            hm_images.put(element.getName(),element.getPath());
                            hm_names.put(element.getName(),element.getCategory());
                            if (!hm_cat.containsKey(element.getCategory())) {
                                hm_cat.put(element.getCategory(),new ArrayList<String>());
                            }
                            hm_cat.get(element.getCategory()).add(element.getName());
                        }
                        mLearnButton.setEnabled(true);
                        mPracticeButton.setEnabled(true);
                        mProgressButton.setEnabled(true);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

    }

}

