package com.example.patel.authui.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.patel.authui.Adapter.PostViewHolder;
import com.example.patel.authui.Model.Post;
import com.example.patel.authui.Model.User;
import com.example.patel.authui.R;
import com.example.patel.authui.Utils.FirebaseUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class PostFragment extends PostListFragment {

    public PostFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        String myUserId = getUid();
        Query allPostsQuery = databaseReference.child("posts");

        return allPostsQuery ;

    }

//    private static final String KEY_TYPE = "type";
//    public static final String TAG = "PostsFragment";
//    private static final String KEY_LAYOUT_POSITION = "layoutPosition";
//    public static final int TYPE_HOME = 1001;
//    public static final int TYPE_FEED = 1002;
//    private int mRecyclerViewPosition = 0;
//    private OnPostSelectedListener mListener;
//    private RecyclerView mRecyclerView;
//    private RecyclerView.Adapter<PostViewHolder> mAdapter;
//
//
//    public static PostFragment newInstance(int type) {
//        PostFragment fragment = new PostFragment();
//        Bundle args = new Bundle();
//        args.putInt(KEY_TYPE, type);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View rootView = inflater.inflate(R.layout.fragment_posts, container, false);
//        rootView.setTag(TAG);
//
//        mRecyclerView = rootView.findViewById(R.id.my_recycler_view);
//        return rootView;
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        linearLayoutManager.setReverseLayout(true);
//        linearLayoutManager.setStackFromEnd(true);
//
//
//        mRecyclerView.setLayoutManager(linearLayoutManager);
//        if (savedInstanceState != null) {
//            // Restore saved layout manager type.
//            mRecyclerViewPosition = (int) savedInstanceState
//                    .getSerializable(KEY_LAYOUT_POSITION);
//            mRecyclerView.scrollToPosition(mRecyclerViewPosition);
//            // TODO: RecyclerView only restores position properly for some tabs.
//        }
//
//        switch (getArguments().getInt(KEY_TYPE)){
//            case TYPE_HOME:
//                Query allPostsQuery = FirebaseUtil.getPostsRef();
//                mAdapter = getFirebaseRecyclerAdapter(allPostsQuery);
//                mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//                    @Override
//                    public void onItemRangeInserted(int positionStart, int itemCount) {
//                        super.onItemRangeInserted(positionStart, itemCount);
//                        // TODO: Refresh feed view.
//                    }
//                });
//                break;
//        }
//        mRecyclerView.setAdapter(mAdapter);
//    }


//
//    public interface OnPostSelectedListener {
//        void onPostComment(String postKey);
//
//        void onPostLike(String postKey);
//
//
//    }
//
//    private FirebaseRecyclerAdapter<Post, PostViewHolder> getFirebaseRecyclerAdapter(Query query) {
//        FirebaseRecyclerOptions<Post> options = new FirebaseRecyclerOptions.Builder<Post>()
//                .setLifecycleOwner(this)
//                .setQuery(query, Post.class)
//                .build();
//
//        return new FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
//            @Override
//            protected void onBindViewHolder(@NonNull PostViewHolder postViewHolder,
//                                            int position,
//                                            @NonNull Post post) {
//                setupPost(postViewHolder, post, position, null);
//            }
//
//            @Override
//            public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.rc_post_item, parent, false);
//
//                return new PostViewHolder(view);
//            }
//        };
//    }
//
//
//    private void setupPost(final PostViewHolder postViewHolder, final Post post, final int position, final String inPostKey) {
//        postViewHolder.setPhoto(post.getImageUrl());
//        postViewHolder.setText(post.getQuestion());
////        postViewHolder.setTimestamp(DateUtils.getRelativeTimeSpanString(
////                (long) post.getTimestamp()).toString());
//        final String postKey;
//        if (mAdapter instanceof FirebaseRecyclerAdapter) {
//            postKey = ((FirebaseRecyclerAdapter) mAdapter).getRef(position).getKey();
//        } else {
//            postKey = inPostKey;
//        }
//
//        User author = post.getCommentUser();
//        postViewHolder.setAuthor(author.getUsername(), author.getUserId());
//        postViewHolder.setIcon(author.getProfile_picture(), author.getUserId());
//
////        ValueEventListener likeListener = new ValueEventListener() {
////            @Override
////            public void onDataChange(DataSnapshot dataSnapshot) {
////                postViewHolder.setNumLikes(dataSnapshot.getChildrenCount());
////                if (dataSnapshot.hasChild(FirebaseUtil.getCurrentUserId())) {
////                    postViewHolder.setLikeStatus(PostViewHolder.LikeStatus.LIKED, getActivity());
////                } else {
////                    postViewHolder.setLikeStatus(PostViewHolder.LikeStatus.NOT_LIKED, getActivity());
////                }
////            }
////
////            @Override
////            public void onCancelled(DatabaseError databaseError) {
////
////            }
////        };
////        FirebaseUtil.getLikesRef().child(postKey).addValueEventListener(likeListener);
////        postViewHolder.mLikeListener = likeListener;
////
////        postViewHolder.setPostClickListener(new PostViewHolder.PostClickListener() {
////            @Override
////            public void showComments() {
////                Log.d(TAG, "Comment position: " + position);
////                mListener.onPostComment(postKey);
////            }
////
////            @Override
////            public void toggleLike() {
////                Log.d(TAG, "Like position: " + position);
////                mListener.onPostLike(postKey);
////            }
////        });
//    }
//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        // Save currently selected layout manager.
//        int recyclerViewScrollPosition = getRecyclerViewScrollPosition();
//        Log.d(TAG, "Recycler view scroll position: " + recyclerViewScrollPosition);
//        savedInstanceState.putSerializable(KEY_LAYOUT_POSITION, recyclerViewScrollPosition);
//        super.onSaveInstanceState(savedInstanceState);
//    }
//
//    private int getRecyclerViewScrollPosition() {
//        int scrollPosition = 0;
//        // TODO: Is null check necessary?
//        if (mRecyclerView != null && mRecyclerView.getLayoutManager() != null) {
//            scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
//                    .findFirstCompletelyVisibleItemPosition();
//        }
//        return scrollPosition;
//    }
//
////    @Override
////    public void onAttach(Context context) {
////        super.onAttach(context);
////        if (context instanceof OnPostSelectedListener) {
////            mListener = (OnPostSelectedListener) context;
////        } else {
////            throw new RuntimeException(context.toString()
////                    + " must implement OnPostSelectedListener");
////        }
////    }
////
////    @Override
////    public void onDetach() {
////        super.onDetach();
////        mListener = null;
////    }
}
