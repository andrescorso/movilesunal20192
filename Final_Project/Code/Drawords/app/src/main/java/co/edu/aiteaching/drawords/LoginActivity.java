package co.edu.aiteaching.drawords;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import co.edu.aiteaching.drawords.models.ElementCategory;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private AccessTokenTracker tokenTracker;

    private SignInButton signInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton signOutButton;

    private Button guestButton;

    private String idUser;
    private String nameUser;

    final int RC_SIGN_IN=1;
    private HashMap<String,String> hm_images = new HashMap<String, String>();
    private HashMap<String,String> hm_names = new HashMap<String, String>();
    private HashMap<String,ArrayList<String>> hm_cat = new HashMap<String, ArrayList<String>>();
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        
        Intent receiveIntent = getIntent();
        boolean isLogout = receiveIntent.getBooleanExtra("uLogout",false);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        if ((isLoggedIn || acct != null) && !isLogout){
            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
            if (isLoggedIn) {
                idUser = "F" + Profile.getCurrentProfile().getId();
                nameUser = Profile.getCurrentProfile().getName();
                intent.putExtra("username", nameUser);
                intent.putExtra("iduser", idUser);
                startActivity(intent);
            }else {
                idUser = "G" + acct.getId();
                nameUser = acct.getDisplayName();
                intent.putExtra("username", nameUser);
                intent.putExtra("iduser", idUser);
                startActivity(intent);
            }

        }
        setContentView(R.layout.activity_login);
        guestButton = (Button) findViewById(R.id.guest_button);
        guestButton.setOnClickListener(new ButtonGuest());


        callbackManager = CallbackManager.Factory.create();
        loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {

                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {

                                        setProfileToView(object);
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Toast.makeText(getApplicationContext(),"Login canceled",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Toast.makeText(getApplicationContext(),"Login error"+exception.toString(),Toast.LENGTH_LONG).show();
                    }


                });
        /*
        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList(
                        "public_profile"
                )
        );

         */
        tokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    logOut();
                }
            }
        };
        tokenTracker.startTracking();


        //////////////GOOGLE
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.google_login_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }

        });
        signOutButton = (SignInButton) findViewById(R.id.sign_out_button);
        TextView textView = (TextView) signOutButton.getChildAt(0);
        textView.setText("Log out");
        printHashKey(getApplicationContext());
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        if (acct != null && isLogout){
            signOutButton.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
        }

    }

    private class ButtonGuest implements View.OnClickListener{
        public void onClick(View view){
            guest();

        }
    }
    private void guest(){
        idUser = "N" + UUID.randomUUID().toString();
        nameUser = "Guest";
        getCat();

    }

    private void signOut() {

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        signOutButton.setVisibility(View.GONE);
                        signInButton.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(),"Google say: Good bye!",Toast.LENGTH_LONG).show();
                    }
                });
    }


    private void logOut() {
        AccessToken.setCurrentAccessToken(null);
        LoginManager.getInstance().logOut();

        idUser = null;
        Toast.makeText(getApplicationContext(),"Bye bye! From Facebook",Toast.LENGTH_LONG).show();
    }

    private void setProfileToView(JSONObject jsonObject) {
        try {
            idUser = "F"+jsonObject.getString("id");
            nameUser = jsonObject.getString("name");
            //Toast.makeText(getApplicationContext(),"Welcome "+jsonObject.getString("name"),Toast.LENGTH_LONG).show();
            getCat();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void getCat(){
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
                        mDatabase.child("scores").child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.getValue() == null) {
                                    // The child doesn't exist
                                    for (String cat: hm_cat.keySet()) {
                                        mDatabase.child("scores").child(idUser).child("G"+cat).setValue(1);
                                        mDatabase.child("scores").child(idUser).child("B"+cat).setValue(0);
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });
                        Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                        intent.putExtra("username", nameUser);
                        intent.putExtra("iduser", idUser);
                        startActivity(intent);


                        //createdDialog(DIALOG_JOIN_ID).show();

                    }


                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            idUser = "G"+completedTask.getResult().getId();
            nameUser = completedTask.getResult().getGivenName();
            getCat();


            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("Eroor", "signInResult:failed code=" + e.getStatusCode());

        }
    }
    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("HOLAAA", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("HOLAAA", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("HOLAAA", "printHashKey()", e);
        }
    }



}
