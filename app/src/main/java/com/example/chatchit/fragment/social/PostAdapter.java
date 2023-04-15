package com.example.chatchit.fragment.social;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatchit.R;
import com.example.chatchit.fragment.user.UserAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    ArrayList<Post> listPost;
    Context context;
    Uri videoUri;

    public PostAdapter(ArrayList<Post> listPost, Context context) {
        this.listPost = listPost;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder( @NonNull ViewGroup parent, int viewType ) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_row,
                parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder( @NonNull PostViewHolder holder, int position ) {
        Post post = listPost.get(position);
        holder.userName.setText(post.getUserName());
        holder.postContent.setText(post.getPostContent());
        holder.postDatetime.setText(post.getPostDate());
        holder.userImage.setImageResource(R.drawable.ic_baseline_person_24);

        StorageReference storageReference = FirebaseStorage.getInstance("gs://chatchit-81b07.appspot.com/").getReference();

        if(post.getPostContentImage() == null && post.getPostContentVideo() == null){
            holder.postContentImage.setVisibility(View.GONE);
            holder.postContentVideo.setVisibility(View.GONE);
        }else if(post.getPostContentImage() != null && post.getPostContentVideo() == null){
            try {
                StorageReference photoRef = storageReference.child("Social Network/").child("Photos/").child(post.getPostContentImage());
                File localFile = File.createTempFile("images", "jpg");
                photoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath());
                        holder.postContentImage.setImageBitmap(bitmap);
                        holder.postContentImage.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(context, exception.getMessage() + post.getPostContentImage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if(post.getPostContentVideo() != null && post.getPostContentImage() == null){
            StorageReference videoRef = storageReference.child("Social Network/").child("Videos/").child(post.getPostContentVideo());

            try {
                File localFile = File.createTempFile("videos", "mp4");
                videoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        // Local temp file has been created
                        videoUri = Uri.parse(localFile.getAbsolutePath());
                        holder.postContentVideo.setVisibility(View.VISIBLE);
                        holder.postContentVideo.setMediaController(new MediaController(SocialFragment.getSocialFragmentContext()));
                        holder.postContentVideo.setVideoURI(videoUri);
                        holder.postContentVideo.requestFocus();
                        holder.postContentVideo.start();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(context, exception.getMessage() + post.getPostContentImage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final boolean[] liked = {false};
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            int likeAmount = post.getAmountLike();
            @Override
            public void onClick( View v ) {
                if(!liked[0]){
                    liked[0] = true;
                    post.setAmountLike(likeAmount + 1);
                    holder.countLike.setText(String.valueOf(likeAmount));
                }else if(liked[0]){
                    liked[0] = false;
                    post.setAmountLike(likeAmount - 1);
                    holder.countLike.setText(String.valueOf(likeAmount));
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listPost.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, postDatetime, postContent, countLike;
        private ImageView postContentImage, likeBtn;
        private VideoView postContentVideo;
        private CircleImageView userImage;
        public PostViewHolder( @NonNull View itemView ) {
            super(itemView);
            userName = itemView.findViewById(R.id.username);
            postContent = itemView.findViewById(R.id.postContent);
            postDatetime = itemView.findViewById(R.id.postDatetime);
            userImage = itemView.findViewById(R.id.userImage);
            postContentImage = itemView.findViewById(R.id.postContentImage);
            postContentVideo = itemView.findViewById(R.id.postContentVideo);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            countLike = itemView.findViewById(R.id.countLike);
        }
    }
    public void uploadLikes(Post post){
    }
}
