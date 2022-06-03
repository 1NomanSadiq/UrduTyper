package me.nomi.urdutyper;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class Starter extends AppCompatActivity {
    private static FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starter);
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent;
        if (user != null) {
            intent = new Intent(Starter.this, DashboardActivity.class);
        } else {
            intent = new Intent(Starter.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}