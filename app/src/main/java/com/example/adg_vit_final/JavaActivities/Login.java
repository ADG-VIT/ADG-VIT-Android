package com.example.adg_vit_final.JavaActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

import com.example.adg_vit_final.NetworkModels.LoginModelNetwork;
import com.example.adg_vit_final.NetworkModels.SignUpCallBack;
import com.example.adg_vit_final.NetworkModels.User;
import com.example.adg_vit_final.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.adg_vit_final.NetworkUtil.NetworkUtils.networkAPI;

public class Login extends AppCompatActivity {
    private TextView btnNewAccount;
    Button buttonLogin;
    private EditText email, password;
    TextView content;
    private Intent intent;
    private LoginModelNetwork login;
    private String Email, Password;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        sharedPreferences = getSharedPreferences("com.adgvit.externals",MODE_PRIVATE);
        myEdit = sharedPreferences.edit();

        progressDialog = new ProgressDialog(this);


        intent = getIntent();
        login = (LoginModelNetwork) intent.getSerializableExtra("LoginModelNetwork");

        btnNewAccount = findViewById(R.id.login_createAccount);
        buttonLogin = findViewById(R.id.login_button);
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);

        btnNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Signup1.class));
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                Email = email.getText().toString();
                Password = password.getText().toString();
                login = new LoginModelNetwork();
                login.setEmail(Email);
                login.setPassword(Password);
                loginUser();
            }
        });
    }

    //loginUser
    private void loginUser() {
        Call<SignUpCallBack> call = networkAPI.loginUser(login);

        call.enqueue(new Callback<SignUpCallBack>() {

            @Override
            public void onResponse(@NotNull Call<SignUpCallBack> call, @NotNull Response<SignUpCallBack> response) {
                if (!response.isSuccessful()){
                    try {
                        SignUpCallBack loginresp = response.body();
                        Toast.makeText(getApplicationContext(), loginresp.getMessage(), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Some error has occurred, Try Again",Toast.LENGTH_LONG).show();
                        System.out.println(e.getLocalizedMessage());
                        progressDialog.dismiss();
                    }
                }else {
                    try {
                        SignUpCallBack loginresp = response.body();
                        String message = loginresp.getMessage();
                        String token = loginresp.getToken();
                        myEdit.putString("Token", token);
                        myEdit.commit();
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        Intent intent1 = new Intent(getApplicationContext(),Home.class);
                        progressDialog.dismiss();
                        startActivity(intent1);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Login failed. Please try again!",Toast.LENGTH_LONG).show();
                        //System.out.println(e.getLocalizedMessage());
                        progressDialog.dismiss();
                    }
                }

            }

            @Override
            public void onFailure(Call<SignUpCallBack> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Login failed. Please try again!", Toast.LENGTH_LONG).show();
                //Log.i("TAG", "" + t.getMessage());
                progressDialog.dismiss();
            }
        });
    }
}