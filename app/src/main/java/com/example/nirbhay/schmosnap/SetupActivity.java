package com.example.nirbhay.schmosnap;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class SetupActivity extends AppCompatActivity {
    private EditText UserName,FullName,CountryName;
    private Button SaveInforamtionbutton;
    private CircleImageView ProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);


        UserName=(EditText)findViewById(R.id.setup_username);
        FullName=(EditText)findViewById(R.id.setup_full_name);
        CountryName=(EditText)findViewById(R.id.setup_country_name);
        SaveInforamtionbutton=(Button)findViewById(R.id.setup_information_button);
        ProfileImage=(CircleImageView)findViewById(R.id.setup_profile_image);
    }
}