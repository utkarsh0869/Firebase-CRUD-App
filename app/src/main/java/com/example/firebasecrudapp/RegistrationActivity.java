package com.example.firebasecrudapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputEditText userNameEdt, pwdEdt, cnfPwdEdt;
    private Button registerBtn;
    private ProgressBar loadingPB;
    private FirebaseAuth mAuth;
    private static final String TAG = "RegistrationActivity";

    private TextView loginTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        userNameEdt = findViewById(R.id.idEditUserName);
        pwdEdt = findViewById(R.id.idEditPwd);
        cnfPwdEdt = findViewById(R.id.idedtCnf);
        registerBtn = findViewById(R.id.idBtnRegister);
        loadingPB = findViewById(R.id.idPBLoading);
        loginTV = findViewById(R.id.idTVLogin);

        mAuth = FirebaseAuth.getInstance();

        loginTV.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                loadingPB.setVisibility(View.VISIBLE);
                String userName = userNameEdt.getText().toString();
                String pwd = pwdEdt.getText().toString();
                String cnfPwd = cnfPwdEdt.getText().toString();
                if(!pwd.equals(cnfPwd)){
                    Toast.makeText(RegistrationActivity.this, "Make sure that both your passwords match", Toast.LENGTH_SHORT).show();
                } else if(TextUtils.isEmpty(userName) && TextUtils.isEmpty(pwd) && TextUtils.isEmpty(cnfPwd) ){
                    Toast.makeText(RegistrationActivity.this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(userName, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                loadingPB.setVisibility(View.GONE);
                                Log.d(TAG, "onComplete: success");
                                Toast.makeText(RegistrationActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent (RegistrationActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            } else{
                                loadingPB.setVisibility(View.GONE);
                                Log.w(TAG, "onComplete: failure", task.getException());
                                Toast.makeText(RegistrationActivity.this, "Failed to register user", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}