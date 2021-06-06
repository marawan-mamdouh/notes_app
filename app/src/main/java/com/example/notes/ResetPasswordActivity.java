package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText resetEmailEditText;
    public static String emailReset = "";
    SweetAlertDialog mPDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        resetEmailEditText = findViewById(R.id.editTextResetEmail);
    }

    @Override
    protected void onStart() {
        super.onStart();
        resetEmailEditText.setText(LoginActivity.emailLogin);
    }

    private void progress() {
        mPDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mPDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        mPDialog.setTitleText("Loading");
        mPDialog.setCancelable(false);
        mPDialog.show();
    }

    public void resetPassword(View view) {
        if (!resetEmailEditText.getText().toString().isEmpty()) {
            progress();
            RegisterActivity.mAuth.sendPasswordResetEmail(resetEmailEditText.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Password Reset Email Sent successfully",
                                        Toast.LENGTH_LONG).show();
                                emailReset = resetEmailEditText.getText().toString();
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            } else {
                                Toast.makeText(getApplicationContext(), "this is not registered email",
                                        Toast.LENGTH_LONG).show();
                                mPDialog.dismiss();
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), "Enter your email", Toast.LENGTH_SHORT).show();
        }
    }
}