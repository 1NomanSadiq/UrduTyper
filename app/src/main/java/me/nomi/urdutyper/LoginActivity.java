package me.nomi.urdutyper;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {
    EditText loginEmail, loginPassword;
    Button loginButton;
    TextView signupText, forgotPasswordText;
    ProgressBar loginProgressBar;
    String typedEmail, typedPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.login_email);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);
        signupText = findViewById(R.id.signup_text);
        loginProgressBar = findViewById(R.id.loginprogressBar);
        forgotPasswordText = findViewById(R.id.forgot_password_text);

        mAuth = FirebaseAuth.getInstance();

        forgotPasswordText.setOnClickListener(v -> {

            loginProgressBar.setVisibility(View.VISIBLE);
            typedEmail = loginEmail.getText().toString().trim();

            if (typedEmail.equals("")) {
                loginEmail.setError("Please type your email first");
                loginEmail.requestFocus();
                loginProgressBar.setVisibility(View.GONE);
            } else if (!Patterns.EMAIL_ADDRESS.matcher(typedEmail).matches()) {
                loginEmail.setError("Invalid Email Address");
                loginEmail.requestFocus();
                loginProgressBar.setVisibility(View.GONE);
            } else {
                mAuth.sendPasswordResetEmail(typedEmail).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(getApplicationContext(), "An email has been sent to reset your password", Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(getApplicationContext(), "An error occurred", Toast.LENGTH_LONG).show();
                    loginProgressBar.setVisibility(View.GONE);
                });
            }
        });

        signupText.setOnClickListener(v1 -> {
            Intent signUp = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(signUp);
        });

        loginButton.setOnClickListener(v -> {
            loginProgressBar.setVisibility(View.VISIBLE);
            typedEmail = loginEmail.getText().toString().trim();
            typedPassword = loginPassword.getText().toString();

            if (!typedPassword.equals("") && Patterns.EMAIL_ADDRESS.matcher(typedEmail).matches() && typedPassword.length() > 9) {
                mAuth.signInWithEmailAndPassword(typedEmail, typedPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (Objects.requireNonNull(mAuth.getCurrentUser()).isEmailVerified()) {
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            mAuth.signOut();
                            Toast.makeText(getApplicationContext(), "Please verify your account first", Toast.LENGTH_LONG).show();
                            loginProgressBar.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed to login", Toast.LENGTH_LONG).show();
                        loginProgressBar.setVisibility(View.GONE);
                    }
                });
            }

            if (typedPassword.length() <= 9) {
                loginPassword.setError("Password should be at least 10 of length");
                loginPassword.requestFocus();
                loginProgressBar.setVisibility(View.GONE);
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(typedEmail).matches()) {
                loginEmail.setError("Invalid Email Address");
                loginEmail.requestFocus();
                loginProgressBar.setVisibility(View.GONE);
            }
        });
    }
}