package me.nomi.urdutyper;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    EditText signupEmail, signupPassword;
    Button signupButton;
    TextView loginText;
    String typedEmail, typedPassword;
    ProgressBar signupProgressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupButton = findViewById(R.id.signup_button);
        loginText = findViewById(R.id.login_text);
        signupProgressBar = findViewById(R.id.signupprogressBar);

        mAuth = FirebaseAuth.getInstance();

        loginText.setOnClickListener(v -> finish());

        signupButton.setOnClickListener(v -> {
            signupProgressBar.setVisibility(View.VISIBLE);
            typedEmail = signupEmail.getText().toString().trim();
            typedPassword = signupPassword.getText().toString();

            if (!typedPassword.equals("") && Patterns.EMAIL_ADDRESS.matcher(typedEmail).matches() && typedPassword.length() > 9) {
                signupProgressBar.setVisibility(View.VISIBLE);
                mAuth.createUserWithEmailAndPassword(typedEmail, typedPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Email Registered, Please check your email and verify your account", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
                            }
                            signupProgressBar.setVisibility(View.GONE);
                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
                        signupProgressBar.setVisibility(View.GONE);
                    }
                });
            }

            if (typedPassword.length() <= 9) {
                signupPassword.setError("Password should be at least 10 of length");
                signupPassword.requestFocus();
                signupProgressBar.setVisibility(View.GONE);
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(typedEmail).matches()) {
                signupEmail.setError("Invalid Email Address");
                signupEmail.requestFocus();
                signupProgressBar.setVisibility(View.GONE);
            }

        });
    }
}