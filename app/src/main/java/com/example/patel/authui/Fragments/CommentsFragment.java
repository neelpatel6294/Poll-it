/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.patel.authui.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.patel.authui.CommentsActivity;
import com.example.patel.authui.FeedsActivity;
import com.example.patel.authui.Model.Comment;
import com.example.patel.authui.Model.User;
import com.example.patel.authui.R;
import com.example.patel.authui.UserDetailActivity;
import com.example.patel.authui.Utils.FirebaseUtil;
import com.example.patel.authui.Utils.GlideUtil;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;


/**
 * This fragment shows the comments for a post.
 * Use the {@link CommentsFragment#newInstance} factory method to
 * create an instance of this fragment and pass in the Firebase
 * post key ref as the argument.
 */
public class CommentsFragment extends Fragment {
    public static final String TAG = "CommentsFragment";
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String POST_REF_PARAM = "post_ref_param";
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 256;
    private EditText mEditText;

    private FirebaseRecyclerAdapter<Comment, CommentViewHolder> mAdapter;

    private String mPostRef;

    public CommentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param postRef The firebase post ref.
     * @return A new instance of fragment CommentsFragment.
     */
    public static CommentsFragment newInstance(String postRef) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putString(POST_REF_PARAM, postRef);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPostRef = getArguments().getString(POST_REF_PARAM);
        } else {
            throw new RuntimeException("You must specify a post reference.");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_comments, container, false);
        RecyclerView mCommentsView = rootView.findViewById(R.id.comment_list);
        mEditText = rootView.findViewById(R.id.editText);
        final Button sendButton = rootView.findViewById(R.id.send_comment);


        final DatabaseReference commentsRef = FirebaseUtil.getCommentsRef().child(mPostRef);

        FirebaseRecyclerOptions<Comment> options = new FirebaseRecyclerOptions.Builder<Comment>()
                .setQuery(commentsRef, Comment.class)
                .build();

        mAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(options) {
            @Override
            public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_item, parent, false);

                return new CommentViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder viewHolder,
                                            int position,
                                            @NonNull Comment comment) {
                User author = comment.getAuthor();
                viewHolder.commentAuthor.setText(author.getUsername());
                GlideUtil.loadProfileIcon(author.getProfile_picture(), viewHolder.commentPhoto);

                viewHolder.authorRef = author.getUserId();
                viewHolder.commentTime
                        .setText(DateUtils.getRelativeTimeSpanString((long) comment.getTimestamp
                                ()));
                viewHolder.commentText.setText(comment.getText());
            }
        };

        mAdapter.startListening();

        sendButton.setEnabled(false);
        mEditText.setHint(R.string.new_comment_hint);
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter
                (DEFAULT_MSG_LENGTH_LIMIT)});
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear input box and hide keyboard.
                final Editable commentText = mEditText.getText();
                mEditText.setText("");
                InputMethodManager inputManager =
                        (InputMethodManager) getActivity().
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(
                        mEditText.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
//                    Toast.makeText(getActivity(), R.string.user_logged_out_error,
//                            Toast.LENGTH_SHORT).show();
                }

                User author = new User(user.getDisplayName(),
                        user.getPhotoUrl().toString(), user.getUid());

                Comment comment = new Comment(author, commentText.toString(),
                        ServerValue.TIMESTAMP);
                commentsRef.push().setValue(comment, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError error, DatabaseReference firebase) {
                        if (error != null) {
                            Log.w(TAG, "Error posting comment: " + error.getMessage());
                            Toast.makeText(getActivity(), "Error posting comment.", Toast
                                    .LENGTH_SHORT).show();
                            mEditText.setText(commentText);
                        }
                    }
                });
            }
        });
        mCommentsView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCommentsView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        public final ImageView commentPhoto;
        public final TextView commentText;
        public final TextView commentAuthor;
        public final TextView commentTime;
        public String authorRef;

        public CommentViewHolder(View itemView) {
            super(itemView);
            commentPhoto = itemView.findViewById(R.id.comment_author_icon);
            commentText = itemView.findViewById(R.id.comment_text);
            commentAuthor = itemView.findViewById(R.id.comment_name);
            commentTime = itemView.findViewById(R.id.comment_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (authorRef != null) {
                        Context context = v.getContext();
                        Intent userDetailIntent = new Intent(context, UserDetailActivity.class);
                        userDetailIntent.putExtra(UserDetailActivity.USER_ID_EXTRA_NAME,
                                authorRef);
                        context.startActivity(userDetailIntent);

//                        Bundle bundle = new Bundle();
//                        String details = UserDetails.USER_ID_EXTRA_NAME;
//                        bundle.putString(details, authorRef);
//                        Fragment userDetails = new UserDetails();
//                        userDetails.setArguments(bundle);
//
//                        FeedsActivity activity = (FeedsActivity) v.getContext();
//                        activity.getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.frame_container, userDetails)
//                                .addToBackStack(null).commit();

                    }
                }
            });
        }
    }
}
