package com.example.patel.authui.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.patel.authui.Model.Post;
import com.example.patel.authui.Model.User;
import com.example.patel.authui.R;
import com.example.patel.authui.Utils.FirebaseUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class NewPostFragment extends Fragment {
    private static final String TAG = "NewPostTaskFragment";

    private Context mApplicationContext;
    private TaskCallbacks mCallbacks;
    private Bitmap selectedBitmap1;
    private Bitmap thumbnail1;
    private Bitmap selectedBitmap2;
    private Bitmap thumbnail2;

    public NewPostFragment() {
    }


    public static NewPostFragment newInstance() {
        return new NewPostFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TaskCallbacks) {
            mCallbacks = (TaskCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement TaskCallbacks");
        }
        mApplicationContext = context.getApplicationContext();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

//    public void uploadPost(Bitmap mResizedBitmap1, String bitmapPath, Bitmap mThumbnail1, String thumbnailPath, String lastPathSegment, String lastPathSegment1, String postText) {
//    }

    public interface TaskCallbacks {
        void onBitmapResized(Bitmap resizedBitmap, int mMaxDimension);

        void onPostUploaded(String error);
    }

    public void setSelectedBitmap1(Bitmap bitmap) {
        this.selectedBitmap1 = bitmap;
    }

    public Bitmap getSelectedBitmap1() {
        return selectedBitmap1;
    }

    public void setSelectedBitmap2(Bitmap bitmap) {
        this.selectedBitmap2 = bitmap;
    }

    public Bitmap getSelectedBitmap2() {
        return selectedBitmap2;
    }

    public void setThumbnail1(Bitmap thumbnail) {
        this.thumbnail1 = thumbnail;
    }

    public Bitmap getThumbnail1() {
        return thumbnail1;
    }

    public void setThumbnail2(Bitmap thumbnail) {
        this.thumbnail2 = thumbnail;
    }

    public Bitmap getThumbnail2() {
        return thumbnail2;
    }

    public void resizeBitmap1(Uri uri, int maxDimension) {
        LoadResizedBitmapTask task = new LoadResizedBitmapTask(maxDimension);
        task.execute(uri);
    }
    public void resizeBitmap2(Uri uri, int maxDimension) {
        LoadResizedBitmapTask task = new LoadResizedBitmapTask(maxDimension);
        task.execute(uri);
    }

    public void uploadPost(Bitmap bitmap1, Bitmap bitmap2, String inBitmapPath1, String inBitmapPath2,
                           Bitmap thumbnail1, Bitmap thumbnail2, String inThumbnailPath1, String inThumbnailPath2,
                           String inFileName1, String inFileName2, String inPostText) {

        UploadPostTask uploadTask = new UploadPostTask(bitmap1, bitmap2, inBitmapPath1, inBitmapPath2,
                thumbnail1, thumbnail2, inThumbnailPath1, inThumbnailPath2, inFileName1, inFileName2, inPostText);
        uploadTask.execute();
    }

    class UploadPostTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Bitmap> bitmapReference1;
        private WeakReference<Bitmap> thumbnailReference1;
        private WeakReference<Bitmap> bitmapReference2;
        private WeakReference<Bitmap> thumbnailReference2;
        private String postText;
        private String fileName;
        private String bitmapPath;
        private String thumbnailPath;

        public UploadPostTask(Bitmap bitmap1, Bitmap bitmap2, String inBitmapPath1, String inBitmapPath2,
                              Bitmap thumbnail1, Bitmap thumbnail2, String inThumbnailPath1, String inThumbnailPath2,
                              String inFileName1, String inFileName2, String inPostText) {

            bitmapReference1 = new WeakReference<>(bitmap1);
            thumbnailReference1 = new WeakReference<>(thumbnail1);
            bitmapReference2 = new WeakReference<>(bitmap2);
            thumbnailReference2 = new WeakReference<>(thumbnail2);
            postText = inPostText;
            fileName = inFileName1;
            fileName = inFileName2;
            bitmapPath = inBitmapPath1;
            thumbnailPath = inThumbnailPath1;
            bitmapPath = inBitmapPath2;
            thumbnailPath = inThumbnailPath2;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Bitmap fullSize1 = bitmapReference1.get();
            final Bitmap thumbnail1 = thumbnailReference1.get();
            if (fullSize1 == null || thumbnail1 == null) {
                return null;
            }

            Bitmap fullSize2 = bitmapReference2.get();
            final Bitmap thumbnail2 = thumbnailReference2.get();
            if (fullSize2 == null || thumbnail2 == null) {
                return null;
            }
            FirebaseStorage storageRef = FirebaseStorage.getInstance();
            StorageReference photoRef = storageRef.getReferenceFromUrl("gs://" + getString(R.string.google_storage_bucket));
            Long timestamp = System.currentTimeMillis();
            final StorageReference fullSizeRef1 =
                    photoRef.child(FirebaseUtil.getCurrentUserId()).child("full1").child(timestamp.toString()).child(fileName + ".jpg");
            final StorageReference thumbnailRef1 =
                    photoRef.child(FirebaseUtil.getCurrentUserId()).child("thumb1").child(timestamp.toString()).child(fileName + ".jpg");
            final StorageReference fullSizeRef2 =
                    photoRef.child(FirebaseUtil.getCurrentUserId()).child("full2").child(timestamp.toString()).child(fileName + ".jpg");
            final StorageReference thumbnailRef2 =
                    photoRef.child(FirebaseUtil.getCurrentUserId()).child("thumb2").child(timestamp.toString()).child(fileName + ".jpg");

            Log.d(TAG, fullSizeRef1.toString());
            Log.d(TAG, thumbnailRef1.toString());
            Log.d(TAG, fullSizeRef2.toString());
            Log.d(TAG, thumbnailRef2.toString());


            final User author = FirebaseUtil.getAuthor();
            final DatabaseReference ref = FirebaseUtil.getBaseRef();
            final DatabaseReference postsRef = FirebaseUtil.getPostsRef();
            final String newPostKey = postsRef.push().getKey();

           final DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("posts")
                    .child(newPostKey);

            ByteArrayOutputStream fullSizeStream1 = new ByteArrayOutputStream();
            ByteArrayOutputStream fullSizeStream2 = new ByteArrayOutputStream();

            fullSize1.compress(Bitmap.CompressFormat.JPEG, 90, fullSizeStream1);
            byte[] bytes1 = fullSizeStream1.toByteArray();

            fullSize2.compress(Bitmap.CompressFormat.JPEG, 90, fullSizeStream2);
            byte[] bytes2 = fullSizeStream2.toByteArray();

            fullSizeRef1.putBytes(bytes1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri fullSizeUrl1 =
                            taskSnapshot.getDownloadUrl();

                    ByteArrayOutputStream thumbnailStream = new ByteArrayOutputStream();
                    thumbnail1.compress(Bitmap.CompressFormat.JPEG, 70, thumbnailStream);
                    thumbnailRef1.putBytes(thumbnailStream.toByteArray())
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    final Uri thumbnailUrl1 =
                                            taskSnapshot.getDownloadUrl();
                                    if (author == null) {
//                                        FirebaseCrash.logcat(Log.ERROR, TAG, "Couldn't upload post: Couldn't get signed in user.");
                                        mCallbacks.onPostUploaded(mApplicationContext.getString(
                                                R.string.error_user_not_signed_in));
                                        return;
                                    }
                                    Post post = new Post(author, postText, fullSizeUrl1.toString(),
                                            "", thumbnailUrl1.toString(),
                                            "", thumbnailRef1.toString(),
                                           "", fullSizeRef1.toString(),
                                            "",
                                            ServerValue.TIMESTAMP,"");

                                    Map<String, Object> updatedUserData = new HashMap<>();
                                    updatedUserData.put(FirebaseUtil.getPeoplePath() + author.getUserId() + "/posts/"
                                            + newPostKey, true);
                                    updatedUserData.put(FirebaseUtil.getPostsPath() + newPostKey,
                                            new ObjectMapper().convertValue(post, Map.class));
                                    ref.updateChildren(updatedUserData, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                            if (databaseError == null) {
                                                mCallbacks.onPostUploaded(null);
                                            }
//                                            else {
//                                                Log.e(TAG, "Unable to create new post: " + firebaseError.getMessage());
//                                                FirebaseCrash.report(firebaseError.toException());
//                                                mCallbacks.onPostUploaded(mApplicationContext.getString(
//                                                        R.string.error_upload_task_create));
//                                            }

                                        }
                                    });

                                }
                            });
                }
            });

            fullSizeRef2.putBytes(bytes2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final Uri fullSizeUrl2 =
                            taskSnapshot.getDownloadUrl();

                    ByteArrayOutputStream thumbnailStream1 = new ByteArrayOutputStream();
                    thumbnail2.compress(Bitmap.CompressFormat.JPEG, 70, thumbnailStream1);
                    thumbnailRef2.putBytes(thumbnailStream1.toByteArray())
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final Uri thumbnailUrl2 =
                                            taskSnapshot.getDownloadUrl();
//                                    if (author == null) {
////                                        FirebaseCrash.logcat(Log.ERROR, TAG, "Couldn't upload post: Couldn't get signed in user.");
//                                        mCallbacks.onPostUploaded(mApplicationContext.getString(
//                                                R.string.error_user_not_signed_in));
//                                        return;
//                                    }

                                    databaseReference.child("imageUrl2").setValue(fullSizeUrl2.toString());
                                    databaseReference.child("thumb_url2").setValue(thumbnailUrl2.toString());
                                    databaseReference.child("thumb_storage_uri2").setValue(thumbnailRef2.toString());
                                    databaseReference.child("full_storage_uri2").setValue(fullSizeRef2.toString());
//                                    Post post = new Post(author, postText, "",
//                                            fullSizeUrl.toString(), "",
//                                            thumbnailUrl.toString(), thumbnailRef1.toString(),
//                                            thumbnailRef2.toString(), fullSizeRef1.toString(),
//                                            fullSizeRef2.toString(),
//                                            ServerValue.TIMESTAMP);
//                                    postsRef.child("imageUrl2").setValue(fullSizeUrl.toString());
//                                    postsRef.child("thumb_url2").setValue(thumbnailUrl.toString());
//////                                    ref.child("thumb_storage_uri2").setValue(thumbnailRef2.toString());
////                                    ref.child("full_storage_uri2").setValue(fullSizeRef2.toString());
//                                    postsRef.push();
//
//                                    Map<String, Object> updatedUserData = new HashMap<>();
//                                    updatedUserData.put(FirebaseUtil.getPeoplePath() + author.getUserId() + "/posts/"
//                                            + newPostKey, true);
//                                    updatedUserData.put(FirebaseUtil.getPostsPath() + newPostKey,
//                                            new ObjectMapper().convertValue(post, Map.class));
//                                    ref.updateChildren(updatedUserData, new DatabaseReference.CompletionListener() {
//                                        @Override
//                                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                                            if (databaseError == null) {
//                                                mCallbacks.onPostUploaded(null);
//                                            }
//                                        }
//                                    });

                                }
                            });

                }
            });
            return null;
        }

    }


    @SuppressLint("StaticFieldLeak")
    class LoadResizedBitmapTask extends AsyncTask<Uri, Void, Bitmap> {
        private int mMaxDimension;

        public LoadResizedBitmapTask(int maxDimension) {
            mMaxDimension = maxDimension;
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Uri... params) {
            Uri uri = params[0];
            if (uri != null) {
                // TODO: Currently making these very small to investigate modulefood bug.
                // Implement thumbnail + fullsize later.
                Bitmap bitmap = null;
                try {
                    bitmap = decodeSampledBitmapFromUri(uri, mMaxDimension, mMaxDimension);
                } catch (FileNotFoundException e) {
//                    Log.e(TAG, "Can't find file to resize: " + e.getMessage());
//                    FirebaseCrash.report(e);
                } catch (IOException e) {
//                    Log.e(TAG, "Error occurred during resize: " + e.getMessage());
//                    FirebaseCrash.report(e);
                }
                return bitmap;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mCallbacks.onBitmapResized(bitmap, mMaxDimension);
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private InputStream streamFromUri1(Uri fileUri) throws FileNotFoundException {
        return new BufferedInputStream(
                mApplicationContext.getContentResolver().openInputStream(fileUri));
    }

    public Bitmap decodeSampledBitmapFromUri(Uri fileUri, int reqWidth, int reqHeight)
            throws IOException {

        // First decode with inJustDecodeBounds=true to check dimensions
        InputStream stream = streamFromUri1(fileUri);
        stream.mark(stream.available());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(stream, null, options);
        stream.reset();

        // Decode bitmap with inSampleSize set
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        BitmapFactory.decodeStream(stream, null, options);
        stream.close();

        InputStream freshStream = streamFromUri1(fileUri);
        return BitmapFactory.decodeStream(freshStream, null, options);
    }
}