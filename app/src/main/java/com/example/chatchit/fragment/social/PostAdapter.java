package com.example.chatchit.fragment.social;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.ContactsContract;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
    Context context;
    Uri videoUri;
    DatabaseReference db = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/")
                                           .getReference();
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

        StorageReference storageReference = FirebaseStorage.getInstance("gs://chatchit-81b07.appspot.com/").getReference();

        switch (getPostContentType(post)){
            case 0: // no image, video and link
                holder.postContentImage.setVisibility(View.GONE);
                holder.postContentVideo.setVisibility(View.GONE);
                break;
            case 1: // image and no url
            StorageReference photoRef = storageReference.child("Social Network/").child("Photos/").child(post.getPostContentImage());
            photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess( Uri uri ) {
                    String url = uri.toString();
                    Glide.with(context)
                            .load(url)
                            .into(holder.postContentImage);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure( @NonNull Exception e ) {
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.d("postImg", e.getMessage());
                }
            });
            holder.postContentImage.setVisibility(View.VISIBLE);
            break;
            case 2: // image and url
                String urlImg = post.getPostContentImage();

                holder.URLtitle.setText(post.getPostURLTitle());
                holder.postContent.setText(post.getPostContent());
                holder.postContent.setVisibility(View.VISIBLE);
                // Gạch chân link
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
                            Toast.makeText(SocialFragment.getSocialFragmentContext(), e.getMessage() + ". Vui lòng tải Google Chrome", Toast.LENGTH_LONG).show();
                            Log.d("openLink", e.getMessage());
                        }
                    }
                });

                holder.URLtitle.setVisibility(View.VISIBLE);
                holder.thumnails.setVisibility(View.VISIBLE);
                holder.url.setVisibility(View.VISIBLE);
                Glide.with(context).load(urlImg).into(holder.thumnails);
                break;
            case 3: // video
                StorageReference videoRef = storageReference.child("Social Network/").child("Videos/").child(post.getPostContentVideo());
                try {
                    File localFile = File.createTempFile("video", "mp4");
                    videoRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // Local temp file has been created
                            videoUri = Uri.parse(localFile.getAbsolutePath());

                            holder.postContentVideo.setVisibility(View.VISIBLE);
                            MediaController mediaController = new MediaController(SocialFragment.getSocialFragmentContext());
                            mediaController.setAnchorView(holder.postContentVideo);
                            holder.postContentVideo.setMediaController(mediaController);
                            holder.postContentVideo.setVideoURI(videoUri);
                            holder.postContentVideo.setFocusable(true);
                            holder.postContentVideo.start();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            localFile.delete();
                            Toast.makeText(context, exception.getMessage() + post.getPostContentImage(), Toast.LENGTH_SHORT).show();
                            Log.d("getFileVideo", exception.getMessage());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("video", e.getMessage());
                }
                break;
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
        private CircleImageView userImg;
        public PostViewHolder( @NonNull View itemView ) {
            super(itemView);
            userName = itemView.findViewById(R.id.username);
            postContent = itemView.findViewById(R.id.postContent);
            postDatetime = itemView.findViewById(R.id.postDatetime);
            userImg = itemView.findViewById(R.id.userImage);
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
                Log.d("uploadLikes", error.getMessage());
                Toast.makeText(context ,error.getMessage() ,Toast.LENGTH_SHORT).show();
            }
        });
    }
    private int getPostContentType(Post post) {
        if (post.getPostContentImage() == null && post.getPostContentVideo() == null && post.getPostURL() == null) {
            return 0;
        } else if (post.getPostContentImage() != null && post.getPostContentVideo() == null && post.getPostURL() == null) {
            return 1;
        } else if (post.getPostContentImage() != null && post.getPostContentVideo() == null && post.getPostURL() != null) {
            return 2;
        }else if(post.getPostContentVideo() != null && post.getPostContentImage() == null){
            return 3;
        }
        return -1;
    }
}
