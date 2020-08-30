package com.example.nirbhay.schmosnap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity
{
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RecyclerView postList;
    private Toolbar mToolbar;

    private CircleImageView NavProfileImage;
    private TextView NavProfileUserName;
    private ImageButton AddNewPostButton;

    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef,PostsRef;

    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        UsersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        PostsRef=FirebaseDatabase.getInstance().getReference().child("Posts");

        mToolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Home");

        AddNewPostButton=(ImageButton)findViewById(R.id.add_new_post_button);

        drawerLayout=(DrawerLayout) findViewById(R.id.drawable_layout);
        actionBarDrawerToggle=new ActionBarDrawerToggle(MainActivity.this,drawerLayout,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Using Nav View we can deal with Nav Headrer
         navigationView=(NavigationView)findViewById(R.id.navigation_view);

         postList=(RecyclerView)findViewById(R.id.all_users_post_list);
         postList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);//for posting from curent to past
        linearLayoutManager.setStackFromEnd(true);
        postList.setLayoutManager(linearLayoutManager);

        View navView=navigationView.inflateHeaderView(R.layout.navigation_header);


        NavProfileImage=(CircleImageView)navView.findViewById(R.id.nav_profile_image);
        NavProfileUserName=(TextView)navView.findViewById(R.id.nav_user_full_name);

        //for diplaying image of current user on profile navigation image
        UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.exists())
                {
                    if(snapshot.hasChild("fullname"))
                    {
                        String fullname=snapshot.child("fullname").getValue().toString();
                        NavProfileUserName.setText(fullname);
                    }

                    if(snapshot.hasChild("profileimage")) {
                        String image = snapshot.child("profileimage").getValue().toString();
                        Picasso.get().load(image).placeholder(R.drawable.profile).into(NavProfileImage);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"Profile Do not exists ...",Toast.LENGTH_SHORT).show();
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem)
            {
                UserMenuSelector(menuItem);
                return false;
            }
        });

        AddNewPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SendUserToPostActivity();

            }
        });

        DisplayAllUsersPost();  //whenever the user logins it would be diplayed the posts
    }

    //here we would use Recycler Adapter to retrieve all posts
    private void DisplayAllUsersPost()
    {
        FirebaseRecyclerAdapter<Posts,PostsViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                (Posts.class,  //ModuleClass
                        R.layout.all_posts_layout, //Layout
                        PostsViewHolder.class,  //View Holder
                        PostsRef  //Datadbse Reference
                ) {
            @Override
            protected void populateViewHolder(PostsViewHolder postsViewHolder, Posts posts, int position)
            {
                //To get Post Key for editing & deleting
                final String PostKey=getRef(position).getKey();

                postsViewHolder.setFullname(posts.getFullname());
                postsViewHolder.setTime(posts.getTime());
                postsViewHolder.setDate(posts.getDate());
                postsViewHolder.setDescription(posts.getDescription());
                postsViewHolder.setPostimage(getApplicationContext(),posts.getPostimage());
                postsViewHolder.setProfileimage(getApplicationContext(),posts.getProfileimage());

                //this for if user clicks the post for editing & deleting then this sends the post key to ClickPost Activity
                postsViewHolder.mView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        Intent clickPostIntent=new Intent(MainActivity.this,ClickPostActivity.class);
                        clickPostIntent.putExtra("PostKey",PostKey);
                        startActivity(clickPostIntent);

                    }
                });
            }
        };

        postList.setAdapter(firebaseRecyclerAdapter);

    }

//For Posting data of  Posts from Posts.class module to layout using holder
    public static class PostsViewHolder extends RecyclerView.ViewHolder
   {
    View mView;
    public PostsViewHolder(@NonNull View itemView)
    {
        super(itemView);
        mView = itemView;
    }

    public void setFullname(String fullname)
    {
        TextView username = (TextView) mView.findViewById(R.id.post_user_name);
        username.setText(fullname);

    }

    public void setProfileimage(Context ctx, String profileimage) {
        CircleImageView image = (CircleImageView) mView.findViewById(R.id.post_profile_image);
        Picasso.get().load(profileimage).into(image);
    }

    public void setTime(String time)
    {
        TextView PostTime= (TextView)mView.findViewById(R.id.post_time);
        PostTime.setText(" "+time);
    }

    public void setDate(String date)
    {
        TextView PostDate=(TextView)mView.findViewById(R.id.post_date);
       PostDate.setText(" "+date);
    }
    public void setDescription(String description)
    {
        TextView PostDescription=(TextView)mView.findViewById(R.id.post_description);
        PostDescription.setText(description);
    }
    public void setPostimage(Context ctx,String postimage)
    {
        ImageView PostImage = (ImageView) mView.findViewById(R.id.post_image);
        Picasso.get().load(postimage).into(PostImage);
    }
}


private void SendUserToPostActivity()
    {
        Intent addNewPostIntent=new Intent(MainActivity.this,PostActivity.class);
        startActivity(addNewPostIntent);
    }

    //For Authentiaction
    protected  void     onStart()
    {
        super.onStart();

        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser==null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            CheckUserExistence();
        }
    }

    private void CheckUserExistence()
    {
        final String current_user_id=mAuth.getCurrentUser().getUid();

        UsersRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(!snapshot.hasChild(current_user_id))
                {
                    SendUserToSetUpActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    private void SendUserToSetUpActivity()
    {
        Intent setUpIntent=new Intent(this,SetupActivity.class);
        setUpIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setUpIntent);
        finish();
    }

    private void SendUserToLoginActivity()
    {
        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if(actionBarDrawerToggle.onOptionsItemSelected(item))
        {
            return  true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UserMenuSelector(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.nav_post:
                //Toast.makeText(this,"Profile",Toast.LENGTH_SHORT).show();
                SendUserToPostActivity();
                break;


            case R.id.nav_profile:
                Toast.makeText(this,"Profile",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_home:
                Toast.makeText(this,"Home ",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_friends:
                Toast.makeText(this,"Friend List",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_message:
                Toast.makeText(this,"Messages",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_settings:
                Toast.makeText(this,"Settings",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_Logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                //Toast.makeText(this,"Logout",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}