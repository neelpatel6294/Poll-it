package com.example.patel.authui;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patel.authui.Fragments.NewPostFragment;
import com.example.patel.authui.Utils.FirebaseUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class NewPostActivity extends BaseActivity implements
        EasyPermissions.PermissionCallbacks,
        NewPostFragment.TaskCallbacks {

    public static final String TAG = "NewPostActivity";
    public static final String TAG_TASK_FRAGMENT = "newPostUploadTaskFragment";


    @BindView(R.id.question_text)
    EditText mQuestion_view;
    @BindView(R.id.image_new1)
    ImageView mImageView1;
    @BindView(R.id.image_new2)
    ImageView mImageView2;
    @BindView(R.id.submit_button)
    Button mSubmit;


    private Uri mFileUri1, mFileUri2;
    private static final int THUMBNAIL_MAX_DIMENSION = 640;
    private static final int FULL_SIZE_MAX_DIMENSION = 1280;
    private Bitmap mResizedBitmap1;
    private Bitmap mThumbnail1;
    private Bitmap mResizedBitmap2;
    private Bitmap mThumbnail2;
    private NewPostFragment mTaskFragment;
    private boolean selected;

    private static final int TC_PICK_IMAGE1 = 10111;
    private static final int TC_PICK_IMAGE2 = 10222;

    private static final int RC_CAMERA_PERMISSIONS = 102;

    private static final String[] cameraPerms = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        ButterKnife.bind(this);


        // find the retained fragment on activity restarts
        FragmentManager fm = getSupportFragmentManager();
        mTaskFragment = (NewPostFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        // create the fragment and data the first time
        if (mTaskFragment == null) {
            // add the fragment
            mTaskFragment = new NewPostFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();


//-----------------------IMAGE BUTTON 1-------------------------------------------------------------
            mImageView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showImagePicker();
                    selected = true;
                }
            });
//-----------------------IMAGE BUTTON 2-------------------------------------------------------------

            mImageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showImagePicker1();
                    selected = false;
                }
            });


            Bitmap selectedBitmap1 = mTaskFragment.getSelectedBitmap1();
            Bitmap thumbnail1 = mTaskFragment.getThumbnail1();

            Bitmap selectedBitmap2 = mTaskFragment.getSelectedBitmap2();
            Bitmap thumbnail2 = mTaskFragment.getThumbnail2();

            if (selectedBitmap1 != null) {
                mImageView1.setImageBitmap(selectedBitmap1);
                mResizedBitmap1 = selectedBitmap1;
            }
            if (thumbnail1 != null) {
                mThumbnail1 = thumbnail1;
            }
            if (selectedBitmap2 != null) {
                mImageView2.setImageBitmap(selectedBitmap2);
                mResizedBitmap2 = selectedBitmap2;
            }
            if (thumbnail2 != null) {
                mThumbnail2 = thumbnail2;
            }
        }


        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mResizedBitmap1 == null || mResizedBitmap2 == null) {
                    Toast.makeText(NewPostActivity.this, "Select an image first.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                String postText = mQuestion_view.getText().toString();
                if (TextUtils.isEmpty(postText)) {
                    Toast.makeText(NewPostActivity.this, "Enter Question",
                            Toast.LENGTH_SHORT).show();

                    return;
                }
                showProgressDialog(getString(R.string.post_upload_progress_message));
                mSubmit.setEnabled(false);
                Long timestamp = System.currentTimeMillis();

                String bitmapPath1 = "/" + FirebaseUtil.getCurrentUserId() + "/full1/" + timestamp.toString() + "/";
                String thumbnailPath1 = "/" + FirebaseUtil.getCurrentUserId() + "/thumb1/" + timestamp.toString() + "/";
                String bitmapPath2 = "/" + FirebaseUtil.getCurrentUserId() + "/full2/" + timestamp.toString() + "/";
                String thumbnailPath2 = "/" + FirebaseUtil.getCurrentUserId() + "/thumb2/" + timestamp.toString() + "/";

                mTaskFragment.uploadPost(mResizedBitmap1 ,mResizedBitmap2
                        ,bitmapPath1,bitmapPath2, mThumbnail1,mThumbnail2,thumbnailPath1,thumbnailPath2,
                        mFileUri1.getLastPathSegment(), mFileUri2.getLastPathSegment(),
                        postText);
//                Intent intent = new Intent(NewPostActivity.this, FeedsActivity.class);
//                startActivity(intent);
            }
        });


    }

    @Override
    public void onPostUploaded(final String error) {
        NewPostActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mImageView1.setEnabled(true);
                mImageView2.setEnabled(true);
//                dismissProgressDialog();
                if (error == null) {
                    Toast.makeText(NewPostActivity.this, "Post created!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(NewPostActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TC_PICK_IMAGE1) {
            if (resultCode == Activity.RESULT_OK) {
                final boolean isCamera;

                if (data.getData() == null) {
                    isCamera = true;
                } else {
                    isCamera = MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
                }
                if (!isCamera) {
                    mFileUri1 = data.getData();
                }
//                Log.d(TAG, "Received file uri: " + mFileUri1.getPath()+ mFileUri2.getPath());

//                mImageView1.setImageURI(mFileUri1);

                mTaskFragment.resizeBitmap1(mFileUri1, THUMBNAIL_MAX_DIMENSION);
                mTaskFragment.resizeBitmap1(mFileUri1, FULL_SIZE_MAX_DIMENSION);
            }
        }

        if (requestCode == TC_PICK_IMAGE2) {
            if (resultCode == Activity.RESULT_OK) {
                final boolean isCamera;
                if (data.getData() == null) {
                    isCamera = true;
                } else {
                    isCamera = MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
                }
                if (!isCamera) {
                    mFileUri2 = data.getData();
                }
//                Log.d(TAG, "Received file uri: " + mFileUri1.getPath()+ mFileUri2.getPath());
//                mImageView2.setImageURI(mFileUri2);
                mTaskFragment.resizeBitmap2(mFileUri2, THUMBNAIL_MAX_DIMENSION);
                mTaskFragment.resizeBitmap2(mFileUri2, FULL_SIZE_MAX_DIMENSION);
            }
        }
    }

    @Override
    public void onDestroy() {
        // store the data in the fragment
        if (mResizedBitmap1 != null) {
            mTaskFragment.setSelectedBitmap1(mResizedBitmap1);
        }
        if (mThumbnail1 != null) {
            mTaskFragment.setThumbnail1(mThumbnail1);
        }
        if (mResizedBitmap2 != null) {
            mTaskFragment.setSelectedBitmap2(mResizedBitmap2);
        }
        if (mThumbnail2 != null) {
            mTaskFragment.setThumbnail2(mThumbnail2);
        }
        super.onDestroy();
    }


    @AfterPermissionGranted(RC_CAMERA_PERMISSIONS)
    private void showImagePicker() {
        // Check for camera permissions
        if (!EasyPermissions.hasPermissions(this, cameraPerms)) {
            EasyPermissions.requestPermissions(this,
                    "This sample will upload a picture from your Camera",
                    RC_CAMERA_PERMISSIONS, cameraPerms);
            return;
        }

        // Choose file storage location
        File file = new File(getExternalCacheDir(), UUID.randomUUID().toString());
        mFileUri1 = Uri.fromFile(file);

        // Camera
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri1);
            cameraIntents.add(intent);
        }

        // Image Picker
        Intent pickerIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooserIntent = Intent.createChooser(pickerIntent,
                getString(R.string.picture_chooser_title));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new
                Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, TC_PICK_IMAGE1);
    }

    @AfterPermissionGranted(RC_CAMERA_PERMISSIONS)
    private void showImagePicker1() {
        // Check for camera permissions
        if (!EasyPermissions.hasPermissions(this, cameraPerms)) {
            EasyPermissions.requestPermissions(this,
                    "This sample will upload a picture from your Camera",
                    RC_CAMERA_PERMISSIONS, cameraPerms);
            return;
        }

        // Choose file storage location
        File file = new File(getExternalCacheDir(), UUID.randomUUID().toString());
        mFileUri2 = Uri.fromFile(file);

        // Camera
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri2);
            cameraIntents.add(intent);
        }

        // Image Picker
        Intent pickerIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooserIntent = Intent.createChooser(pickerIntent,
                getString(R.string.picture_chooser_title));
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new
                Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, TC_PICK_IMAGE2);
    }

    @Override
    public void onBitmapResized(Bitmap resizedBitmap, int mMaxDimension) {
        if (resizedBitmap == null) {
            Log.e(TAG, "Couldn't resize bitmap in background task.");
            Toast.makeText(getApplicationContext(), "Couldn't resize bitmap.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (mMaxDimension == THUMBNAIL_MAX_DIMENSION) {
            if (selected) {
                mThumbnail1 = resizedBitmap;
            } else {
                mThumbnail2 = resizedBitmap;

            }
        } else if (mMaxDimension == FULL_SIZE_MAX_DIMENSION) {
            if (selected) {
                mResizedBitmap1 = resizedBitmap;
                mImageView1.setImageBitmap(mResizedBitmap1);
            } else {
                mResizedBitmap2 = resizedBitmap;
                mImageView2.setImageBitmap(mResizedBitmap2);
            }
        }
        if (mThumbnail1 != null && mResizedBitmap1 != null && mThumbnail2 != null && mResizedBitmap2 != null) {
            mSubmit.setEnabled(true);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }
}
