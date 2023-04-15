package com.example.chatchit.fragment.social;

import static android.app.Activity.RESULT_OK;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.chatchit.InsertLinkDialog;
import com.example.chatchit.MyEditText;
import com.example.chatchit.R;
import com.example.chatchit.login_signup.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.UUID;

public class SocialFragment extends Fragment {
    ArrayList<Post> listPost = new ArrayList<>();
    DatabaseReference db = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Uri uri;
    Uri videoUri;
    String photosRandomUUID;
    String videosRandomUUID;
    RecyclerView recyclerView;
    PostAdapter adapter;

    public static Context context;

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult( ActivityResult result ) {
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                uri = data.getData();
                StorageReference storageRef = FirebaseStorage.getInstance("gs://chatchit-81b07.appspot.com/").getReference();
                photosRandomUUID = UUID.randomUUID().toString();
                StorageReference ref = storageRef.child("Social Network");
                ref.child("Photos").child(photosRandomUUID).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess( UploadTask.TaskSnapshot taskSnapshot ) {
                        Toast.makeText(getContext() ,"Upload image successful" ,Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( @NonNull Exception e ) {
                        Toast.makeText(getContext() ,e.getMessage() ,Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    });
    ActivityResultLauncher<Intent> launcherPickVideos = registerForActivityResult(new ActivityResultContracts.StartActivityForResult() ,new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult( ActivityResult result ) {
            if(result.getResultCode() == RESULT_OK){
                Intent data = result.getData();
                videoUri = data.getData();
                videosRandomUUID = UUID.randomUUID().toString();
                StorageReference storageRef = FirebaseStorage.getInstance("gs://chatchit-81b07.appspot.com/").getReference();
                StorageReference ref = storageRef.child("Social Network");

                ref.child("Videos").child(videosRandomUUID).putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess( UploadTask.TaskSnapshot taskSnapshot ) {
                        Toast.makeText(getContext(),"Upload video successful",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure( @NonNull Exception e ) {
                        Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    });

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated( @NonNull View view, @Nullable Bundle savedInstanceState ) {
        super.onViewCreated(view, savedInstanceState);

        SocialFragment.context = getContext();

        MyEditText myEditText = view.findViewById(R.id.postEditText);
        recyclerView = view.findViewById(R.id.postRecyclerView);
        ImageButton postBtn = view.findViewById(R.id.postBtn);
        ImageButton imagePicker = view.findViewById(R.id.ImagePicker);
        ImageButton videoPicker = view.findViewById(R.id.VideoPicker);
        ImageButton insertLink = view.findViewById(R.id.insertLink);

        InsertLinkDialog insertLinkDialog = new InsertLinkDialog();
        insertLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                insertLinkDialog.show(getParentFragmentManager(), "game");
            }
        });
        String link = insertLinkDialog.getInput();
        imagePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent photoPicker = new Intent(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                launcher.launch(photoPicker);
            }
        });

        videoPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                Intent videoPicker = new Intent();
                videoPicker.setType("video/*");
                videoPicker.setAction(Intent.ACTION_GET_CONTENT);
                launcherPickVideos.launch(videoPicker);
            }
        });

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                String postId = String.valueOf(UUID.randomUUID());
                String content = myEditText.getText().toString();
                String username = auth.getCurrentUser().getDisplayName();
                String postDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
                if(photosRandomUUID == null && videosRandomUUID == null){
                    db.child("Social").child("Post").push().setValue(new Post(postId, content, null, null, username, auth.getUid(), postDate, 0)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete( @NonNull Task<Void> task ) {
                            myEditText.setText("");
                            MyEditText.setLink();
                        }
                    });
                }else if(photosRandomUUID != null){
                    db.child("Social").child("Post").push().setValue(new Post(postId, content, photosRandomUUID, null, username, auth.getUid(), postDate, 0)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete( @NonNull Task<Void> task ) {
                            myEditText.setText("");
                            photosRandomUUID = null;
                            MyEditText.setLink();
                        }
                    });
                }else if(videosRandomUUID != null){
                    db.child("Social").child("Post").push().setValue(new Post(postId, content, null, videosRandomUUID, username, auth.getUid(), postDate, 0)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete( @NonNull Task<Void> task ) {
                            myEditText.setText("");
                            videosRandomUUID = null;
                            MyEditText.setLink();
                        }
                    });
                }
            }
        });
        loadPost();
        adapter = new PostAdapter(listPost, getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
    }

    @Override
    public void onStart() {
        super.onStart();
        loadPost();
    }

    public void loadPost(){
        db.child("Social").child("Post").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( @NonNull DataSnapshot snapshot ) {
                listPost.clear();
                for(DataSnapshot snap: snapshot.getChildren()){
                    Post post = snap.getValue(Post.class);
                    listPost.add(post);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    listPost.sort(new Comparator<Post>() {
                        @Override
                        public int compare( Post o1, Post o2 ) {
                            return o2.getPostDate().compareTo(o1.getPostDate());
                        }
                    });
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled( @NonNull DatabaseError error ) {
                Log.d(getTag(), error.getMessage());
            }
        });
    }
    public static Context getSocialFragmentContext(){
        return SocialFragment.context;
    }
}