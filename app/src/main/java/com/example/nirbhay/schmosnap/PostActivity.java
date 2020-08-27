package com.example.nirbhay.schmosnap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostActivity extends AppCompatActivity
{
     private Toolbar mToolbar;
     private ProgressDialog loadingBar;

     private ImageButton SelectPostImage;
     private Button UpdatePostButton;
     private EditText PostDescription;

     private static  final int Gallery_Pick=1;
     private Uri ImageUri;
     private String Description;


     private StorageReference PostImagesReference;
    private DatabaseReference UsersRef,PostRef;
    private FirebaseAuth mAuth;

     private String  saveCurrentDate,saveCurrentTime,postRandomName,downloadUrl,current_user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();

        PostImagesReference= FirebaseStorage.getInstance().getReference();
        //for cretaing node
        UsersRef= FirebaseDatabase.getInstance().getInstance().getReference().child("Users");
        PostRef=FirebaseDatabase.getInstance().getReference().child("Posts");

        SelectPostImage=(ImageButton)findViewById(R.id.select_post_image);
        UpdatePostButton=(Button)findViewById(R.id.update_post_button);
        PostDescription=(EditText)findViewById(R.id.post_description);
        loadingBar=new ProgressDialog(this);

        mToolbar=(Toolbar)findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Update Post");

        SelectPostImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                OpenGallery();

            }
        });

        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                ValidatePostInfo();

            }
        });
    }

    //to check idf user has selecte dor not
    private void ValidatePostInfo()
    {
        Description=PostDescription.getText().toString();

        if(ImageUri == null)
        {
            Toast.makeText(this,"Please Select Post Image....",Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(Description))
        {
            Toast.makeText(this,"Please write something about your image....",Toast.LENGTH_SHORT).show();
        }
        else
        {
            StoringImageToFirebaseStorage();
        }
    }

    //to Store Post Image uniquely using time as two posts can be same also,more efficient than generating random keys
    private void StoringImageToFirebaseStorage()
    {
        //Date
        Calendar calFordDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate=currentDate.format(calFordDate.getTime());

//Time
        Calendar calFordTime=Calendar.getInstance();
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        saveCurrentTime=currentTime.format(calFordDate.getTime());

        postRandomName=saveCurrentDate + saveCurrentTime;


        final StorageReference filepath=PostImagesReference.child("Post Images").child(ImageUri.getLastPathSegment()+ postRandomName+ ".jpg");

        //After imge get stored on Stored note:here add on Complete Listener does ot work
        filepath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override public void onSuccess(Uri uri)
                    {
                        loadingBar.setTitle("Add New Post");
                        loadingBar.setMessage("Please Wait ,while we are updating your new post...");
                        downloadUrl=uri.toString();
                        SavingPostInformationToDatabase();

                    }
                });

            }
        });


    }

    private void SavingPostInformationToDatabase()
    {
        UsersRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    String userFullName=snapshot.child("fullname").getValue().toString();
                    String userProfileImage=snapshot.child("profileimage").getValue().toString();

                    HashMap postsMap=new HashMap();
                    postsMap.put("uid",current_user_id);
                    postsMap.put("date",saveCurrentDate);
                    postsMap.put("time",saveCurrentTime);
                    postsMap.put("description",Description);
                    postsMap.put("postimage",downloadUrl);
                    postsMap.put("profileimage", userProfileImage);
                    postsMap.put("fullname",userFullName);
                    PostRef.child(current_user_id+postRandomName).updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {
                                        SendUserToMainActivity();
                                        Toast.makeText(PostActivity.this,"New Post is updated Sucessfully",Toast.LENGTH_SHORT).show();
                                       loadingBar.dismiss();
                                    }
                                    else
                                    {
                                        Toast.makeText(PostActivity.this,"Error occured while updating Your Post",Toast.LENGTH_SHORT).show();
                                       loadingBar.dismiss();
                                    }

                                }
                            });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void OpenGallery()
    {
        Intent galleyIntent=new Intent();
        galleyIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleyIntent.setType("image/*");
        startActivityForResult(galleyIntent,Gallery_Pick);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Gallery_Pick && resultCode==RESULT_OK && data!=null)
        {
            ImageUri=data.getData();
            SelectPostImage.setImageURI(ImageUri);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        int id=item.getItemId();
           //for back button
        if(id==android.R.id.home)
        {
            SendUserToMainActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent=new Intent(PostActivity.this,MainActivity.class);
        startActivity(mainIntent);
    }
}