package com.example.chatchit.fragment.user;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatchit.IOnClickListener;
import com.example.chatchit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> implements IOnClickListener {
    private static ArrayList<User> Users;
    private IOnClickListener listener;
    Context context;

    public UserAdapter(Context context, ArrayList<User> users, IOnClickListener listener) {
        Users = users;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_row_layout,
                                                                     parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        User user = Users.get(position);
        holder.userName.setText(user.getUserName());
        holder.useremail.setText(user.getUserEmail());

        /*
        * Tạo file tạm, ghi ảnh từ Cloud Storage vào file tạm
        * Chuyển file thành dạng bitmap
        * Set ảnh của user
        * */
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference islandRef = storageReference.child("Profile Images/").child(user.getUserImg());

        islandRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess( Uri uri ) {
                String url = uri.toString();
                Glide.with(context)
                        .load(url)
                        .into(holder.userImg);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure( @NonNull Exception e ) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("userImg", e.getMessage());
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                listener.onClickListener(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return Users.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView userName, useremail;
        public CircleImageView userImg;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            useremail = itemView.findViewById(R.id.useremail);
            userImg = itemView.findViewById(R.id.userImage);
        }
    }

    @Override
    public void onClickListener(User user) {
    }
    public void sortUserByAlphabet(){
        Collections.sort(Users, new Comparator<User>() {
            @Override
            public int compare( User o1, User o2 ) {
                return o1.getUserName().compareTo(o2.getUserName());
            }
        });
        notifyDataSetChanged();
    }
    public void sortUserByAlphabetReverse(){
        Collections.sort(Users, new Comparator<User>() {
            @Override
            public int compare( User o1, User o2 ) {
                return o2.getUserName().compareTo(o1.getUserName());
            }
        });
        notifyDataSetChanged();
    }
}
