package me.nomi.urdutyper;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.Iterator;
import java.util.Objects;

public class DashboardActivity extends AppCompatActivity implements MyAdapter.OnImageClickListener {
    RecyclerView recyclerView;
    DatabaseReference databaseReference;
    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String url;
    String[] urls;
    String[] filenames;
    MyAdapter myAdapter;
    Button logout;
    FloatingActionButton button;
    ImageView viewImage, saveBigImage, delete;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        recyclerView = findViewById(R.id.recView);
        button = findViewById(R.id.newImage);
        logout = findViewById(R.id.logOut);
        relativeLayout = findViewById(R.id.dialog);

        saveBigImage = findViewById(R.id.saveBigImages);
        viewImage = findViewById(R.id.bigImage);
        delete = findViewById(R.id.delete);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        databaseReference = FirebaseDatabase.getInstance().getReference().child(uid).child("Images");
        databaseReference.keepSynced(true);

        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        button.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            startActivity(intent);
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Iterator<DataSnapshot> iterator = snapshot.getChildren().iterator();
                    Iterator<DataSnapshot> iterator2 = snapshot.getChildren().iterator();
                    int length = (int) snapshot.getChildrenCount();
                    filenames = new String[length];
                    urls = new String[length];
                    for (int i = 0; i < length; i++) {
                        filenames[i] = Objects.requireNonNull(iterator.next().getKey());
                        urls[i] = Objects.requireNonNull(iterator2.next().getValue()).toString();
                    }
                    myAdapter = new MyAdapter(filenames, urls, uid, DashboardActivity.this, DashboardActivity.this);
                    recyclerView.setAdapter(myAdapter);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            }

        );


    }

    @Override
    public void onImageClick(int position) {
        url = urls[position];
        Picasso.get().load(url).error(R.drawable.not_found).networkPolicy(NetworkPolicy.OFFLINE).into(viewImage, new Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(url).error(R.drawable.not_found).into(viewImage);
            }
        });

//        MultiTouchListener viewImageTouchListener = new MultiTouchListener(this);
//        viewImageTouchListener.isRotateEnabled = false;
//        viewImage.setOnTouchListener(viewImageTouchListener);

        saveBigImage.setOnClickListener(v1 -> {

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(DashboardActivity.this, "Please allow access to storage then try again", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(DashboardActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            } else {

                Uri uri = null;

                try {
                    uri = ImageMaker.getImageFromImageView(DashboardActivity.this, viewImage, filenames[position]);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Notification.create(this, filenames[position] + ".jpg", uri);
                Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_LONG).show();
            }
        });

        delete.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder =
                    new AlertDialog.Builder(DashboardActivity.this)
                            .setTitle("Delete File")
                            .setMessage("Are you sure want to delete this file?")
                            .setPositiveButton("Yes", (dialog, which) -> {
                                databaseReference.child(filenames[position]).removeValue();
                                StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urls[position]);
                                storageReference.delete();
                                Toast.makeText(DashboardActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                relativeLayout.setVisibility(View.GONE);
                            })
                            .setNegativeButton("No", (dialog, which) -> dialog.cancel());
            alertDialogBuilder.show();
        });
        relativeLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed() {
        if (relativeLayout.getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            relativeLayout.setVisibility(View.GONE);
        }
    }
}