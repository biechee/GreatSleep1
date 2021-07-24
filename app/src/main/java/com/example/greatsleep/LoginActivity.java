package com.example.greatsleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    //Google
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private final int RC_SIGN_IN=100;
    Button google_login;
    //Facebook
    private FirebaseAuth mAuthFB;
    CallbackManager callbackManager;
    private Button FBloginButton;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //FB  KEY:WCduEgfoGEaqC76pOI+oviOlT/U=
        //FB
        callbackManager = CallbackManager.Factory.create();
        mAuthFB=FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager=CallbackManager.Factory.create();

        FBloginButton=(Button)findViewById(R.id.button2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            FBloginButton.setStateListAnimator(null);
        }
        FBloginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FBloginButton.setEnabled(false);
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email","public_profile"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                   @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.v("a","FB  on success ");
                        handleFacebookToken(loginResult.getAccessToken());
                    }
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException error) {

                    }
                });
            }
        });

        //Google
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        checkUser();

        google_login=(Button)findViewById(R.id.button);
        //按鈕置頂
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            google_login.setStateListAnimator(null);
        }
        google_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("ss","begin google sign in");
                Intent intent=mGoogleSignInClient.getSignInIntent();
                startActivityForResult(intent,RC_SIGN_IN);
            }
        });
    }
    private void handleFacebookToken(AccessToken token) {
        Log.v("a","Handle Facebook Token ");
        AuthCredential credential= FacebookAuthProvider.getCredential(token.getToken());
        mAuthFB.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.v("a","sign with credential Facebook successful");
                            FirebaseUser user=mAuthFB.getCurrentUser();
                            FBloginButton.setEnabled(true);
                            updateUI();
                        }
                        else{
                            Log.v("a","sign with credential Facebook Fail");
                            Toast.makeText(LoginActivity.this,"登入失敗",Toast.LENGTH_SHORT).show();
                            FBloginButton.setEnabled(true);
                            updateUI();
                        }
            }
        });
    }

    private void updateUI() {
        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Google Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.d("ss", "on activity:Google signin intent result");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.v("ss", "Google sign in failed" + e.getMessage());
            }
        }
        //Facebook
        callbackManager.onActivityResult(requestCode,resultCode,data);

    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d("ss", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        String uid=user.getUid();
                        String email=user.getEmail();
                        Log.d("ss", "success on uid"+uid);
                        Log.d("ss", "success on email"+email);
                        //判斷使用者是否已存在
                        if(authResult.getAdditionalUserInfo().isNewUser()){
                            Log.d("ss", "create new user"+email);
                            Toast.makeText(LoginActivity.this,"Account create...\n"+email,Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Log.d("ss", "Existing user"+email);
                            Toast.makeText(LoginActivity.this,"User have Exist...\n"+email,Toast.LENGTH_SHORT).show();
                        }
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("ss", "signInWithCredential:failure"+e.getMessage());
                    }
                });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkUser();
    }
    private void checkUser() {
        FirebaseUser user=mAuth.getCurrentUser();
        //如果使用者存在直接登入
        if(user!=null) {
            Log.v("ss", "already login");
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
    @Override
    protected void onStart() {
       super.onStart();
       FirebaseUser user=mAuthFB.getCurrentUser();
       if(user!=null){
           updateUI();
       }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}