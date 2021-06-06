package com.example.notes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public void logout(MenuItem item) {
        RegisterActivity.mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void resetPasswordMenu(MenuItem item) {
        RegisterActivity.mAuth.signOut();
        startActivity(new Intent(this, ResetPasswordActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void maro(MenuItem item) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName("maro a5oya")
                .setPhotoUri(Uri.parse("https://example.com/jane-q-user/profile.jpg"))
                .build();

        Objects.requireNonNull(RegisterActivity.mAuth.getCurrentUser()).updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), RegisterActivity.mAuth.getCurrentUser().getDisplayName() + "",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}