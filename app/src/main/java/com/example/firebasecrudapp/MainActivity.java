package com.example.firebasecrudapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CourseRVAdapter.CourseClickInterface {

    private final String TAG = "MainActivity";
    private FloatingActionButton addCourseFAB;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private RecyclerView courseRV;
    private FirebaseAuth mAuth;
    private ProgressBar loadingPB;
    private ArrayList<CourseRVModal> courseRVModalArrayList;
    private CourseRVAdapter courseRVAdapter;
    private RelativeLayout homeRL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // initializing all our variables.
        courseRV = findViewById(R.id.idRVCourses);
        homeRL = findViewById(R.id.idRLBSheet);
        loadingPB = findViewById(R.id.idPBLoading);
        addCourseFAB = findViewById(R.id.idFABAddCourse);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        courseRVModalArrayList = new ArrayList<>();
        // on below line we are getting database reference.
        databaseReference = firebaseDatabase.getReference("Courses");
        // on below line adding a click listener for our floating action button.
        addCourseFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opening a new activity for adding a course.
                Intent i = new Intent(MainActivity.this, AddCourseActivity.class);
                startActivity(i);
            }
        });
        Log.d(TAG, "onCreate: we are here");



        // on below line initializing our adapter class.
        courseRVAdapter = new CourseRVAdapter(courseRVModalArrayList, this, this::onCourseClick);
        // setting layout malinger to recycler view on below line.
        courseRV.setLayoutManager(new LinearLayoutManager(this));
        // setting adapter to recycler view on below line.
        courseRV.setAdapter(courseRVAdapter);
        // on below line calling a method to fetch courses from database.
        getCourses();
    }

    private void getCourses() {
        // on below line clearing our list.
        // on below line we are calling add child event listener method to read the data.
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                courseRVModalArrayList.clear();

                // on below line we are hiding our progress bar.
                loadingPB.setVisibility(View.GONE);
                // adding snapshot to our array list on below line.
                Log.d(TAG, "onChildAdded: we here" + snapshot.getValue(CourseRVModal.class).getCourseName());

                courseRVModalArrayList.add(snapshot.getValue(CourseRVModal.class));
                Log.d(TAG, "onChildAdded: we here" + courseRVModalArrayList.size());
                // notifying our adapter that data has changed.
                courseRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                courseRVModalArrayList.clear();

                // this method is called when new child is added
                // we are notifying our adapter and making progress bar
                // visibility as gone.
                loadingPB.setVisibility(View.GONE);
                courseRVAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                courseRVModalArrayList.clear();

                // notifying our adapter when child is removed.
                courseRVAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                courseRVModalArrayList.clear();

                // notifying our adapter when child is moved.
                courseRVAdapter.notifyDataSetChanged();
                loadingPB.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.d(TAG, "ongetCourse: we are here");

    }

    @Override
    public void onCourseClick(int position) {
        // calling a method to display a bottom sheet on below line.
        displayBottomSheet(courseRVModalArrayList.get(position));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // adding a click listener for option selected on below line.
        int id = item.getItemId();
        switch (id) {
            case R.id.idLogOut:
                // displaying a toast message on user logged out inside on click.
                Toast.makeText(getApplicationContext(), "User Logged Out", Toast.LENGTH_LONG).show();
                // on below line we are signing out our user.
                mAuth.signOut();
                // on below line we are opening our login activity.
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // on below line we are inflating our menu
        // file for displaying our menu options.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void displayBottomSheet(CourseRVModal modal) {
        Log.d(TAG, "displayBottomSheet: we are here");
        // on below line we are creating our bottom sheet dialog.
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        // on below line we are inflating our layout file for our bottom sheet.
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialogue, homeRL);
        // setting content view for bottom sheet on below line.
        bottomSheetDialog.setContentView(layout);
        // on below line we are setting a cancelable
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        // calling a method to display our bottom sheet.
        bottomSheetDialog.show();
        // on below line we are creating variables for
        // our text view and image view inside bottom sheet
        // and initialing them with their ids.
        TextView courseNameTV = layout.findViewById(R.id.idTVCourseName);
        Log.d(TAG, "displayBottomSheet: we are here 2");

        TextView courseDescTV = layout.findViewById(R.id.idTVCourseDesc);
        TextView suitedForTV = layout.findViewById(R.id.idTVSuitedFor);
        TextView priceTV = layout.findViewById(R.id.idTVCoursePrice);
        ImageView courseIV = layout.findViewById(R.id.idIVCourse);
        // on below line we are setting data to different views on below line.
        Log.d(TAG, "displayBottomSheet: we are here 2"+ modal.getCourseName());

        courseNameTV.setText(modal.getCourseName());
        courseDescTV.setText(modal.getCourseDescription());
        suitedForTV.setText("Suited for " + modal.getBestSuitedFor());
        priceTV.setText("Rs." + modal.getCoursePrice());
        Picasso.get().load(modal.getCourseImg()).into(courseIV);
        Button viewBtn = layout.findViewById(R.id.idBtnVIewDetails);
        Button editBtn = layout.findViewById(R.id.idBtnEditCourse);

        // adding on click listener for our edit button.
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are opening our EditCourseActivity on below line.
                Intent i = new Intent(MainActivity.this, EditCourseActivity.class);
                // on below line we are passing our course modal
                i.putExtra("course", modal);
                startActivity(i);
            }
        });
        // adding click listener for our view button on below line.
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on below line we are navigating to browser
                // for displaying course details from its url
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(modal.getCourseLink()));
                startActivity(i);
            }
        });
    }
}