package co.edu.aiteaching.drawords;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SelectCategoryActivity extends AppCompatActivity {

    private Button mNextImage;
    private TextView mCategoryText;
    private TextView mName;
    private ImageSwitcher mImageSwitcher;
    private int images[] = {R.drawable.cat, R.drawable.cow, R.drawable.bee, R.drawable.dog, R.drawable.duck, R.drawable.frog};
    private String names[] = {"Cat", "Cow", "Bee", "Dog", "Duck", "Frog"};
    private int countImages = images.length;
    int currentImage = 0;
    private FirebaseStorage mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        mNextImage = (Button) findViewById(R.id.next_image);
        mCategoryText = (TextView) findViewById(R.id.category);
        mImageSwitcher = (ImageSwitcher) findViewById(R.id.image_switcher);
        mName = (TextView) findViewById(R.id.name);

        includesForReadReference();

        mImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
                return imageView;
            }
        });

        mImageSwitcher.setImageResource(images[currentImage]);
        mName.setText(names[0]);

        mNextImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentImage++;

                if(currentImage == countImages)
                    currentImage = 0;
                mImageSwitcher.setImageResource(images[currentImage]);
                mName.setText(names[currentImage]);
            }
        });
    }
    public void setNextImage (String name){
        mImageSwitcher.setImageResource(images[currentImage]);
        mName.setText(names[currentImage]);

    }

    public void includesForReadReference() {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // ## Create a Reference

        // [START create_storage_reference]
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        // [END create_storage_reference]

        // [START create_child_reference]
        // Create a child reference
        // imagesRef now points to "images"
        StorageReference categoriesRef = storageRef.child("images/categories");

    }
}