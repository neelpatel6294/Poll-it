package com.example.patel.authui;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patel.authui.Utils.FirebaseUtil;
import com.example.patel.authui.Utils.GlideUtil;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public static final int RC_SIGN_IN = 1;

    @BindView(R.id.show_Feed)
    Button  mFeed;
    @BindView(R.id.profile_image)
    CircleImageView profile_image;
    @BindView(R.id.username)
    TextView userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        FirebaseApp.initializeApp(this);

        mFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,FeedsActivity.class);
                startActivity(intent);
            }
        });


       mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(MainActivity.this, "Signed In Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))

                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RC_SIGN_IN) {
            if (requestCode == RESULT_OK) {
                Toast.makeText(this, "SignIn", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign Out", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        if (currentUser != null && !currentUser.isAnonymous()) {
//            dismissProgressDialog();
            showSignedInUI(currentUser);
        } else {
            Toast.makeText(this, "Sign Out", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sign_out:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSignedInUI(FirebaseUser firebaseUser) {

        if (firebaseUser.getDisplayName() != null){
            userName.setText(firebaseUser.getDisplayName());
        }
        if (firebaseUser.getPhotoUrl() != null) {
            GlideUtil.loadProfileIcon(firebaseUser.getPhotoUrl().toString(), profile_image);
        }
        Map<String,Object> updateValues = new HashMap<>();
        updateValues.put("displayName", firebaseUser.getDisplayName());
        updateValues.put("photoUrl", firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null);

        FirebaseUtil.getPeopleRef().child(firebaseUser.getUid()).updateChildren(
                updateValues,
                new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError firebaseError, DatabaseReference databaseReference) {
                        if (firebaseError != null) {
                            Toast.makeText(MainActivity.this,
                                    "Couldn't save user data: " + firebaseError.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}
