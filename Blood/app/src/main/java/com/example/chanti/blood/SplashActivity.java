package com.example.chanti.blood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

/**
 * Created by Ravi-PC on 27-02-2016.
 **/
public class SplashActivity extends Activity implements View.OnClickListener{

    private CallbackManager callbackManager;
    private LoginButton loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_splash);
        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(this);
        Button register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();

        loginBtn = (LoginButton) findViewById(R.id.fb_login_btn);

        loginBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Intent m = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(m);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Intent m = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(m);
    }

    @Override
    public void onClick(View v) {
        Intent r = null;
        switch (v.getId()){
            case R.id.register:
                r = new Intent(SplashActivity.this, RegistrationActivity.class);
                break;
            case R.id.login:
                r = new Intent(SplashActivity.this, LoginActivity.class);
                break;
            default:
                break;
        }
        if(null != r){
            startActivity(r);
            finish();
        }
    }
}
