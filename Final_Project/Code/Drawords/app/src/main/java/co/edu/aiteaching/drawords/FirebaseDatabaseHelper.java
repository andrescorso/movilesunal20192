package co.edu.aiteaching.drawords;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import co.edu.aiteaching.drawords.models.ElementCategory;

public class FirebaseDatabaseHelper {

    private FirebaseDatabase mDataBase;
    private DatabaseReference mReferenceElementCategory;
    private List<ElementCategory> elements = new ArrayList<>();

    public interface DataStatus {
        void dataIsLoaded(List<ElementCategory> elements, List<String> keys);
        void dataIsInserted();
        void dataIsUpdated();
        void dataIsDeleted();
    }

    public FirebaseDatabaseHelper(){
        System.out.println("FirebaseDatabasehelper!!! construc");
        mDataBase = FirebaseDatabase.getInstance();
        mReferenceElementCategory = mDataBase.getReference("ElementCategory");
    }

    public void readElements(final DataStatus dataStatus){
        System.out.println("ReadElements: ");
        mReferenceElementCategory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("OnDataChange!!!!!!!!!!!!!222222222222");
                elements.clear();
                List<String> keys = new ArrayList<>();
                System.out.println(dataSnapshot.getChildrenCount());
                for(DataSnapshot keyNode: dataSnapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    ElementCategory element = keyNode.getValue(ElementCategory.class);
                    elements.add(element);
                    System.out.println("ELEMENT: " + element.getName());
                }
                dataStatus.dataIsLoaded(elements,keys);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error"+ "onCancelled: " + databaseError.getMessage());
            }
        });
    }
}
