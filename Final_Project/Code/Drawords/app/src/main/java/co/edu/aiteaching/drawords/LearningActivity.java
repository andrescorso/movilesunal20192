package co.edu.aiteaching.drawords;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.edu.aiteaching.drawords.models.ElementCategory;

public class LearningActivity extends AppCompatActivity {

    private Spinner mSelectCategory;
    private Button mNextImage;
    private TextView mCategoryText;
    private TextView mName;
    private ImageSwitcher mImageSwitcher;
    private ImageView mImageView;
    private int images[] = {R.drawable.cat, R.drawable.cow, R.drawable.bee, R.drawable.dog, R.drawable.duck, R.drawable.frog};
    private String names[] = {"Cat", "Cow", "Bee", "Dog", "Duck", "Frog"};
    private int countImages = images.length;
    int currentImage = 0;
    private FirebaseStorage mStorageRef;
    private List<ElementCategory> elementCategories = new ArrayList<>();
    private String categorySelected;

    private HashMap<String,String> hm_images = new HashMap<String, String>();
    private HashMap<String,String> hm_names = new HashMap<String, String>();
    private HashMap<String,ArrayList<String>> hm_cat = new HashMap<String, ArrayList<String>>();

    private DatabaseReference mElementCategoryReference;
    private StorageReference imageReference;
    private StorageReference fileRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        mSelectCategory = (Spinner) findViewById(R.id.select_category);
        mNextImage = (Button) findViewById(R.id.next_image);
        mCategoryText = (TextView) findViewById(R.id.category);
        //mImageSwitcher = (ImageSwitcher) findViewById(R.id.image_switcher);
        mImageView = (ImageView) findViewById(R.id.image_switcher);
        mName = (TextView) findViewById(R.id.name);
        mSelectCategory.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        //mSelectCategory.setOnItemSelectedListener(new CustomOnItemSelectedListener(new FirebaseDatabaseHelper()));
        //FirebaseStorage storage = FirebaseStorage.getInstance();
        //mElementCategoryReference = FirebaseDatabase.getInstance().getReference().child("ElementCategory");

        hm_cat = (HashMap<String, ArrayList<String>>) getIntent().getSerializableExtra("hm_cat");
        hm_images = (HashMap<String, String>) getIntent().getSerializableExtra("hm_images");
        hm_names = (HashMap<String, String>) getIntent().getSerializableExtra("hm_names");
        setSpinner();


        // ## Create a Reference
        /*
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
                        setSpinner();
                        showimage();


                        //createdDialog(DIALOG_JOIN_ID).show();

                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
        //mDatabase.child("games").child("bd1490f71d3a7951").child("wait").setValue(false);


        */
        mNextImage.setOnClickListener(new NextImageButton());
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private void setSpinner() {

        ArrayList<String> arrayList = new ArrayList<String>();
        for (String str : hm_cat.keySet())
            arrayList.add(str);
        categorySelected = arrayList.get(0);
        ArrayAdapter< String > dataAdapter = new ArrayAdapter < String > (this, android.R.layout.simple_spinner_item, arrayList);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSelectCategory.setAdapter(dataAdapter);

        //showimage();

    }

    private void showimage() {
        Object[] keys = hm_images.keySet().toArray();
        currentImage += 1;
        if (currentImage == keys.length){
            currentImage = 0;
        }
        while (!hm_names.get(keys[currentImage]).equals(categorySelected)){

            currentImage += 1;
            if (currentImage == keys.length){
                currentImage = 0;
            }

        }
        new DownLoadImageTask(mImageView).execute(hm_images.get(keys[currentImage]));
        mCategoryText.setText((CharSequence) keys[currentImage]);

    }

    private class  NextImageButton implements View.OnClickListener{
        public void onClick(View view){
            showimage();
        }
    }
    public void setNextImage(String name) {
        mImageSwitcher.setImageResource(images[currentImage]);
        mName.setText(names[currentImage]);

    }


    public class CustomOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            System.out.println("OnItemSelected");
            String categorySelectedString = parent.getItemAtPosition(pos).toString();
            categorySelected = categorySelectedString;
            if (hm_names.size() > 0) {
                showimage();
            }
            Toast.makeText(parent.getContext(),
                    "Selected category : " + categorySelected,
                    Toast.LENGTH_SHORT).show();

        }
        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}