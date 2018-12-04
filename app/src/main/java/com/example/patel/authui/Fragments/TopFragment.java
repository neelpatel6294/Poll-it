package com.example.patel.authui.Fragments;

import android.support.v4.app.Fragment;

import com.example.patel.authui.Utils.FirebaseUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class TopFragment extends PostListFragment {

    public TopFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {

        String myUserId = getUid();
        Query myTopPostsQuery = databaseReference.child("votes");
        return myTopPostsQuery;
    }
}
