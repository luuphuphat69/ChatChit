package com.example.chatchit.fragment.social;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.chatchit.MyEditText;
import com.example.chatchit.R;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.UUID;

public class SocialFragment extends Fragment{
    ArrayList<Post> listPost = new ArrayList<>();
    DatabaseReference db = FirebaseDatabase.getInstance("https://chatchit-81b07-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    Uri uri;
    Uri videoUri;
    String photosRandomUUID;
    String videosRandomUUID;
    String URL;
    RecyclerView recyclerView;
    PostAdapter adapter;
    WebArticle webArticle;

    MyEditText myEditText;
    String postId;
    String content;
    String username;
    String postDate;

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

        myEditText = view.findViewById(R.id.postEditText);
        recyclerView = view.findViewById(R.id.postRecyclerView);
        ImageButton postBtn = view.findViewById(R.id.postBtn);
        ImageButton imagePicker = view.findViewById(R.id.ImagePicker);
        ImageButton videoPicker = view.findViewById(R.id.VideoPicker);
        ImageButton insertLink = view.findViewById(R.id.insertLink);

        // Inflate popup window
        insertLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick( View v ) {
                View popupView = LayoutInflater.from(getContext()).inflate(R.layout.insert_link_window, null);
                int width = LinearLayout.LayoutParams.MATCH_PARENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);

                Button submitLink = popupView.findViewById(R.id.submitLink);

                submitLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        popupWindow.setContentView(LayoutInflater.from(getContext()).inflate(R.layout.insert_link_window, null, false));
                        final EditText insertLinkEditText = popupView.findViewById(R.id.insertLink);
                        URL = insertLinkEditText.getText().toString();
                      //  myEditText.setText(URL);
                        Toast.makeText(getContext(), "Đã lấy link", Toast.LENGTH_LONG).show();
                        popupWindow.dismiss();
                    }
                });
                popupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
                // Click bên ngoài cửa sổ, cửa sổ sẽ đóng lại
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        return true;
                    }
                });
            }
        });

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
                postId = String.valueOf(UUID.randomUUID());
                content  = myEditText.getText().toString();
                username = auth.getCurrentUser().getDisplayName();
                postDate  = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().getTime());
                if(photosRandomUUID == null && videosRandomUUID == null && URL == null){
                    db.child("Social").child("Post").push().setValue(new Post(postId, content, null, null, null,null, username, auth.getUid(), postDate, 0)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete( @NonNull Task<Void> task ) {
                            myEditText.setText("");
                            MyEditText.setLink();
                        }
                    });
                }else if(photosRandomUUID != null){
                    db.child("Social").child("Post").push().setValue(new Post(postId, content, photosRandomUUID, null, null, null, username, auth.getUid(), postDate, 0)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete( @NonNull Task<Void> task ) {
                            myEditText.setText("");
                            photosRandomUUID = null;
                            MyEditText.setLink();
                        }
                    });
                }else if(videosRandomUUID != null){
                    db.child("Social").child("Post").push().setValue(new Post(postId, content, null, videosRandomUUID,null, null, username, auth.getUid(), postDate, 0)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete( @NonNull Task<Void> task ) {
                            myEditText.setText("");
                            videosRandomUUID = null;
                            MyEditText.setLink();
                        }
                    });
                }else if(URL != null){
                    PareseURL pareseURL = new PareseURL();
                    pareseURL.execute(URL);
                }
            }
        });
        adapter = new PostAdapter(listPost, webArticle, getContext());
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

    // load bài đăng theo ngày đăng
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
    // inner class
    // AsyncTask<Params, Progress, Result>
    //Những đối số nào không sử dụng trong quá trình thực thi tiến trình thì ta thay bằng Void.
    /*
      +) Params: Là giá trị ((biến) được truyền vào khi gọi thực thi tiến trình và nó sẽ được truyền vào doInBackground
      +) Progress: Là giá trị (biến) dùng để update giao diện diện lúc tiến trình thực thi, biến này sẽ được truyền vào hàm onProgressUpdate.
      +) Result: Là biến dùng để lưu trữ kết quả trả về sau khi tiến trình thực hiện xong.*/
    private class PareseURL extends AsyncTask<String, Void, WebArticle> {
        private ProgressDialog dialog = new ProgressDialog(SocialFragment.getSocialFragmentContext());
        public PareseURL(){
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Đang tải...");
            dialog.show();
        }

        @Override
        protected WebArticle doInBackground(String... params) {
            WebArticle webArticle = new WebArticle();
            try {
                Document  document = Jsoup.connect(params[0]).get();

                // Lấy title
                String title = document.title();
                webArticle.setTitle(title);
                // Lấy ảnh
                /*Trong header của mỗi trang báo thường có tag meta có chứa attribute property="og:image"
                * itemprop="thumbnailUrl"(option) dùng để chứa link thumbnail
                * */
                Elements elements = document.getElementsByTag("meta");
                for(Element element: elements){
                    String thumnail = element.attr("content");
                    String property = element.attr("property");
                    if(property.equals("og:image")){
                        webArticle.setThumbnail(thumnail);
                    }
                }
               return webArticle;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(WebArticle s) {
            super.onPostExecute(s);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
           Log.d("ABC", s.toString());

            db.child("Social").child("Post").push().setValue(new Post(postId, content, s.getThumbnail(), null, URL, s.getTitle(), username, auth.getUid(), postDate, 0)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete( @NonNull Task<Void> task ) {
                    myEditText.setText("");
                    Toast.makeText(getContext(), "Lưu đường dẫn thành công", Toast.LENGTH_LONG);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure( @NonNull Exception e ) {
                    Toast.makeText(getContext() ,e.getMessage() ,Toast.LENGTH_SHORT).show();
                    Log.d("AB", e.getMessage());
                }
            });
        }
    }
    public static Context getSocialFragmentContext(){
        return SocialFragment.context;
    }
}
