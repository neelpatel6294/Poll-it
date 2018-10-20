//package com.example.patel.authui;
//
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.support.annotation.NonNull;
//import android.support.v7.app.AppCompatActivity;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.bumptech.glide.Glide;
//import com.example.patel.authui.Model.User;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//import com.google.firebase.storage.FirebaseStorage;
//import com.google.firebase.storage.StorageReference;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
//public class AddPostActivity extends AppCompatActivity {
//
//    private DatabaseReference mDatabase;
//    Button sumbit;
//    private FirebaseAuth mAuth;
//    private EditText nameText;
//    private EditText emailText;
//    FirebaseUser user1;
//    StorageReference mStorageReference;
//    FirebaseUser mCurrentUser;
//    private CircleImageView mProfilePhoto;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_post);
//
//        nameText = findViewById(R.id.name);
//        emailText = findViewById(R.id.email);
//        sumbit = findViewById(R.id.submit);
//        mProfilePhoto = findViewById(R.id.profile);
//        user1 = FirebaseAuth.getInstance().getCurrentUser();
//        mStorageReference = FirebaseStorage.getInstance().getReference();
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mCurrentUser = mAuth.getCurrentUser();
//        mAuth = FirebaseAuth.getInstance();
////        Log.d("user",user.toString());
//        sumbit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                submitPost();
//                Intent intent = new Intent(AddPostActivity.this,MainActivity.class);
//                startActivity(intent);
//            }
//        });
//
//
//    }
//
//    private void writeNewUser( String name, String email) {
//        String key = mDatabase.child("posts").push().getKey();
//        User user = new User( name, email);
////        mDatabase.child("users").setValue(user);
//        Map<String, Object> postValues = user.toMap();
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/posts/" + key, postValues);
//        childUpdates.put("/user-posts/" +  "/" + key, postValues);
//        mDatabase.updateChildren(childUpdates);
//    }
//
//    private void submitPost() {
//
//        final String uid = user1.getUid();
//        Log.d("user",uid);
//        final String n1 = nameText.getText().toString();
//        final String t1 = emailText.getText().toString();
//        Glide.with(getApplicationContext())
//                .load(mCurrentUser.getPhotoUrl()).into(mProfilePhoto);
//
//        mDatabase.child("users").child(uid).addListenerForSingleValueEvent(
//                new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        User user = dataSnapshot.getValue(User.class);
//                        if (user == null) {
//                            // User is null, error out
////                            Log.e(TAG, "User " + id + " is unexpectedly null");
//                            Toast.makeText(AddPostActivity.this,
//                                    "Error: could not fetch user.",
//                                    Toast.LENGTH_SHORT).show();
//                        } else {
//                            // Write new post
//                            writeNewUser( n1, t1);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                }
//        );
//    }
//
//}
//
//
