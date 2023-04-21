package com.example.chatchit.fragment.social;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.example.chatchit.R;
import com.example.chatchit.fragment.user.User;
import com.example.chatchit.fragment.user.UserAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    ArrayList<Post> listPost;
    WebArticle webArticle;
    Context context;
    Uri videoUri;
    DatabaseReference db = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                           .getReference();
    public PostAdapter(ArrayList<Post> listPost, WebArticle webArticle, Context context) {
        this.listPost = listPost;
        this.context = context;
        this.webArticle = webArticle;
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

        StorageReference storageReference = FirebaseStorage.getInstance("gs://chatchit-81b07.appspot.com/").getReference();

        if(post.getPostContentImage() == null && post.getPostContentVideo() == null){
            holder.postContentImage.setVisibility(View.GONE);
            holder.postContentVideo.setVisibility(View.GONE);
        }
        else if(post.getPostContentImage() != null && post.getPostContentVideo() == null){
            if(post.getPostURL() != null){
                holder.URLtitle.setText(post.getPostURLTitle());
                holder.postContent.setText(post.getPostContent());
                holder.postContent.setVisibility(View.VISIBLE);
                SpannableString content = new SpannableString(post.getPostURL());
                content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
                holder.url.setText(content);
                holder.url.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        Intent intent = new Intent();
                        try {
                            intent.setPackage("com.android.chrome");
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(post.getPostURL()));
                            context.startActivity(intent);
                        }catch (Exception e){
                            Toast.makeText(SocialFragment.getSocialFragmentContext(), e.getMessage() + ". Vui lòng tải Google Chrome", Toast.LENGTH_LONG);
                            Log.d("link", e.getMessage());
                        }
                    }
                });
                String urlImg = post.getPostContentImage();

                holder.URLtitle.setVisibility(View.VISIBLE);
                holder.thumnails.setVisibility(View.VISIBLE);
                holder.url.setVisibility(View.VISIBLE);
                Glide.with(context).load(urlImg).into(holder.thumnails);
            }else{
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
        holder.likeBtn.setOnClickListener(new View.OnClickListener() {
            int likeAmount = post.getAmountLike();
            @Override
            public void onClick( View v ) {
                likeAmount++;
                post.setAmountLike(likeAmount);
                uploadLikes(post, likeAmount);
            }
        });
        holder.countLike.setText(String.valueOf(post.getAmountLike()));
    }

    @Override
    public int getItemCount() {
        return listPost.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private TextView userName, postDatetime, postContent, countLike;
        private ImageView postContentImage, likeBtn;
        private VideoView postContentVideo;
        private TextView  url, URLtitle;
        private ImageView thumnails;
        private CircleImageView userImage;
        public PostViewHolder( @NonNull View itemView ) {
            super(itemView);
            userName = itemView.findViewById(R.id.username);
            postContent = itemView.findViewById(R.id.postContent);
            postDatetime = itemView.findViewById(R.id.postDatetime);
            userImage = itemView.findViewById(R.id.userImage);
            postContentImage = itemView.findViewById(R.id.postContentImage);
            postContentVideo = itemView.findViewById(R.id.postContentVideo);
            url = itemView.findViewById(R.id.url);
            URLtitle = itemView.findViewById(R.id.title);
            thumnails = itemView.findViewById(R.id.thumbnail);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            countLike = itemView.findViewById(R.id.countLike);
        }
    }
    // Lưu số lượt like vào Database
    public void uploadLikes(Post post, int amountLike){
        db.child("Social").child("Post").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                for (DataSnapshot snap: snapshot.getChildren()){
                    Post _post = snap.getValue(Post.class);
                    if(post.getPostId().equals(_post.getPostId())){
                        HashMap<String, Object> taskMap = new HashMap<String, Object>();
                        taskMap.put("amountLike", amountLike);
                        snap.getRef().updateChildren(taskMap);
                    }
                }
            }
            @Override
            public void onCancelled( @NonNull DatabaseError error ) {
            }
        });
    }
}