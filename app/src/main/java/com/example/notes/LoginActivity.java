package com.example.notes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class LoginActivity extends AppCompatActivity {

    SweetAlertDialog mPDialog;
    public static String emailLogin = "";

    //TODO google login @start 1
    GoogleSignInClient mGoogleSignInClient;
    final int RC_SIGN_IN = 153;

    //TODO facebook login @start 1
    CallbackManager mCallbackManager;
    EditText editTextEmail;
    EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        RegisterActivity.mAuth = FirebaseAuth.getInstance();
        ImageView imageView = findViewById(R.id.login);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editTextEmail.getText().toString().isEmpty() && !editTextPassword.getText().toString().isEmpty()) {
                    //reset password
                    emailLogin = editTextEmail.getText().toString();

                    progress();
                    RegisterActivity.mAuth.signInWithEmailAndPassword(editTextEmail.getText().toString(), editTextPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        mPDialog.dismiss();
                                        FirebaseUser user = RegisterActivity.mAuth.getCurrentUser();
                                        if (user != null) {
                                            if (user.isEmailVerified()) {
                                                Toast.makeText(getApplicationContext(), "Login successfully",
                                                        Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Please, Verify your Email, and try again",
                                                        Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    } else {
                                        mPDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "Wrong Email or Password.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(getApplicationContext(), "Enter Email and/or Password",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        //TODO google login @start 2
        ImageView signInButton = findViewById(R.id.googleLogin);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //TODO facebook login @start 2 // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton  = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        //FacebookSdk.sdkInitialize(getApplicationContext());
        RegisterActivity.mAuth = FirebaseAuth.getInstance();
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("TAG", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("TAG", "facebook:onError", error);
            }
        });
    }

    //TODO Methods

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (!RegisterActivity.emailRegister.isEmpty()) {
            editTextEmail.setText(RegisterActivity.emailRegister);
        } else {
            editTextEmail.setText(ResetPasswordActivity.emailReset);
        }
        if (RegisterActivity.mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    public void registerNow(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    private void progress() {
        mPDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mPDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mPDialog.setTitleText("Loading");
        mPDialog.setCancelable(false);
        mPDialog.show();
    }

    //TODO google login @start 3
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            Exception exception = task.getException();
            if (task.isSuccessful()) {
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null) {
                        Toast.makeText(getApplicationContext(), "firebase Auth With Google " + account.getId(),
                                Toast.LENGTH_LONG).show();
                    }
                    if (account != null) {
                        firebaseAuthWithGoogle(account.getIdToken());
                    }
                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    Toast.makeText(getApplicationContext(), "Google sign in failed",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), exception + "Can't Login",
                        Toast.LENGTH_LONG).show();
            }

        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        RegisterActivity.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(getApplicationContext(), "Login successfully",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(getApplicationContext(), "Can't Login",
                                    Toast.LENGTH_LONG).show();

                        }
                    }
                });
    }

    //TODO facebook login @start 3
    private void handleFacebookAccessToken(AccessToken token) {
//        Log.d("TAG", "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        RegisterActivity.mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser user = RegisterActivity.mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(), "Login successfully",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Facebook sign in failed",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void forgetPassword(View view) {
        editTextPassword.setText("");
        startActivity(new Intent(this, ResetPasswordActivity.class));
    }
}