package com.example.android.travelmantics;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.addjournal_fab)
    FloatingActionButton addJournalFab;
    @BindView(R.id.rvJournalEntries)
    RecyclerView rvTravelEntries;

    private TravelEntryAdapter travelEntryAdapter;
    private ArrayList<TravelManticsModel> entryList = new ArrayList<>();

    public static final String DATABASE_UPLOADS = "Travel Entries";
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setElevation(0);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Travel Mantics");
            ((TextView) toolbar.getChildAt(0)).setTextColor(getResources().getColor(android.R.color.white));

        }



        FirebaseApp.initializeApp(getApplicationContext());



        mAuth = FirebaseAuth.getInstance();

        String currentUid = mAuth.getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        //firebaseDatabase.setPersistenceEnabled(true);
        mDatabase = firebaseDatabase.getReference(DATABASE_UPLOADS).child("Mantics");

        if(mAuth.getCurrentUser().getEmail().equalsIgnoreCase("admin@gmail.com")){

            addJournalFab.setVisibility(View.VISIBLE);
        }
        else{
            addJournalFab.setVisibility(View.INVISIBLE);
        }

        travelEntryAdapter = new TravelEntryAdapter(entryList, this);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvTravelEntries.setLayoutManager(mLayoutManager);
        rvTravelEntries.setItemAnimator(new DefaultItemAnimator());

        rvTravelEntries.setAdapter(travelEntryAdapter);
        rvTravelEntries.setNestedScrollingEnabled(false);



        addJournalFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TravelEntry.class));
            }
        });

        getEntries();
    }

    private void getEntries() {

        mDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               // entryList.clear();
                if (dataSnapshot.exists()) {
                   for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {

                        TravelManticsModel travelManticsModel = npsnapshot.getValue(TravelManticsModel.class);
                        entryList.add(travelManticsModel);
                    }

                    travelEntryAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "An error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.mainmenu, menu);

        return true;

    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.item_signout){

            if(mAuth.getCurrentUser() != null){
                mAuth.signOut();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}