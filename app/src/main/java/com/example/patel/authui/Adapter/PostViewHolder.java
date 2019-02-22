package com.example.patel.authui.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.patel.authui.R;
import com.example.patel.authui.UserDetailActivity;
import com.example.patel.authui.Utils.GlideUtil;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;

public class PostViewHolder extends RecyclerView.ViewHolder {

    private final View mView;
    private PostClickListener mListener;
    public DatabaseReference mPostRef;
    public ValueEventListener mPostListener;
    public String Totalsuffix1;
    String leftSuffix, rightSuffix;

    public void setNumVotes(VoteStatus voted, FragmentActivity activity) {
    }

    public enum LikeStatus {LIKED, NOT_LIKED}

    public enum VoteStatus {VOTED, NOT_VOTED}

    int numvotescounter1 = 1;


    private final ImageView mLikeIcon;
    private static final int POST_TEXT_MAX_LINES = 6;
    private ImageView mPhotoView1;
    private ImageView mPhotoView2;
    private ImageView mIconView;
    private TextView mAuthorView;
    private TextView mPostTextView;
    private TextView mTimestampView;
    private TextView mNumLikesView;
    private TextView mNumPick1Votes, mNumPick2Votes;
    public String mPostKey;
    public ValueEventListener mLikeListener;
    public ValueEventListener mVoteListener;
    public ProgressBar mProgress;
    public TextView mNumVotes;


    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
        mPhotoView1 = itemView.findViewById(R.id.post_photo1);
        mPhotoView2 = itemView.findViewById(R.id.post_photo2);
        mTimestampView = itemView.findViewById(R.id.timeStamp);
        mIconView = mView.findViewById(R.id.post_author_icon);
        mAuthorView = mView.findViewById(R.id.post_author_name);
        mPostTextView = itemView.findViewById(R.id.post_text);
        mNumLikesView = itemView.findViewById(R.id.number_of_Likes);
        mNumVotes = itemView.findViewById(R.id.number_of_Total_Votes);
        mNumPick1Votes = itemView.findViewById(R.id.number_of_Firstpick_Votes);
        mNumPick2Votes = itemView.findViewById(R.id.number_of_Right_Votes);


        itemView.findViewById(R.id.post_comment_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.showComments();
            }
        });


        mLikeIcon = itemView.findViewById(R.id.post_like_icon);
        mLikeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.toggleLike();
            }
        });

        mPhotoView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.votes();
                mListener.votesCounter1();

            }
        });

        mPhotoView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.votes();
                mListener.votesCounter2();

            }
        });
    }

    public void setPhoto1(String url) {
        GlideUtil.loadImage(url, mPhotoView1);
    }

    public void setPhoto2(String url) {
        GlideUtil.loadImage(url, mPhotoView2);
    }

    public void setIcon(String url, final String authorId) {
        GlideUtil.loadProfileIcon(url, mIconView);
        mIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(authorId);
            }
        });
    }

    public void setAuthor(String author, final String authorId) {
        if (author == null || author.isEmpty()) {
            author = mView.getResources().getString(R.string.user_info_no_name);
        }
        mAuthorView.setText(author);
        mAuthorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail(authorId);
            }
        });
    }


    private void showUserDetail(String authorId) {
//
//        Bundle bundle = new Bundle();
//        bundle.putString(UserDetails.USER_ID_EXTRA_NAME, authorId);
//        Fragment userDetails = new UserDetails();
//        userDetails.setArguments(bundle);
//
//        FeedsActivity activity = (FeedsActivity) mView.getContext();
//        activity.getSupportFragmentManager().beginTransaction()
//                .replace(R.id.frame_container, userDetails)
//                .addToBackStack(null).commit();
//

        Context context = mView.getContext();
        Intent userDetailIntent = new Intent(context, UserDetailActivity.class);
        userDetailIntent.putExtra(UserDetailActivity.USER_ID_EXTRA_NAME, authorId);
        context.startActivity(userDetailIntent);

    }

    public void deletePost(int position) {

    }

    public void setText(final String text) {
        if (text == null || text.isEmpty()) {
            mPostTextView.setVisibility(View.GONE);
            return;
        } else {
            mPostTextView.setVisibility(View.VISIBLE);
            mPostTextView.setText(text);
            mPostTextView.setMaxLines(POST_TEXT_MAX_LINES);
            mPostTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPostTextView.getMaxLines() == POST_TEXT_MAX_LINES) {
                        mPostTextView.setMaxLines(Integer.MAX_VALUE);
                    } else {
                        mPostTextView.setMaxLines(POST_TEXT_MAX_LINES);
                    }
                }
            });
        }
    }


    public void setNumVotes(long numVotes) {
        Totalsuffix1 = numVotes == 1 ? " Vote" : " Votes";
        mNumVotes.setText(numVotes + Totalsuffix1);
    }

    public void setNumVotesCounter1(long childrenCount) {
        leftSuffix = childrenCount == 1 ? " Vote" : " Votes";

        mNumPick1Votes.setText(childrenCount + leftSuffix);

    }

    public void setNumVotesCounter2(long childrenCount) {
        rightSuffix = childrenCount == 1 ? " Vote" : " Votes";
//        int a = 100;
//        String aa =  s * String.valueOf(a);

        mNumPick2Votes.setText(childrenCount + rightSuffix);
    }

    public void setTimestamp(String timestamp) {
        mTimestampView.setText(timestamp);
    }

    public void setLikeStatus(LikeStatus status, Context context) {
        mLikeIcon.setImageDrawable(ContextCompat.getDrawable(context,
                status == LikeStatus.LIKED ? R.drawable.ic_baseline_favorite_24px : R.drawable.ic_baseline_favorite_border_24px));
    }

    public void setNumLikes(long numLikes) {
        String suffix = numLikes == 1 ? " like" : " likes";
        mNumLikesView.setText(numLikes + suffix);
    }

    public void setPostClickListener(PostClickListener listener) {
        mListener = listener;
    }

    public interface PostClickListener {
        void showComments();

        void toggleLike();

        void votes();

        void votesCounter1();

        void votesCounter2();
    }

}
