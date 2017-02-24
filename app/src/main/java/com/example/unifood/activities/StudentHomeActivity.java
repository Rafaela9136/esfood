package com.example.unifood.activities;

import android.app.Fragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import com.example.unifood.R;
import com.example.unifood.adapters.RestaurantListAdapter;
import com.example.unifood.controllers.FirebaseHelper;
import com.example.unifood.firebase.utils.LoadRestaurants;
import com.example.unifood.firebase.utils.Utilities;
import com.example.unifood.fragments.RestaurantListFragment;
import com.example.unifood.models.Restaurant;
import com.example.unifood.models.University;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.ButterKnife;

import static com.google.android.gms.common.stats.zzc.Ar;

public class StudentHomeActivity extends AppCompatActivity  {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference mDatabase;
    private ArrayList<University> dataSet = new ArrayList<>();
    private ArrayList<Restaurant> restaurantSet = new ArrayList<>();
    private ArrayList<Restaurant> faveRestaurantSet = new ArrayList<>();

    private ArrayList<String> faveReferences = new ArrayList<>();
    RestaurantListAdapter restAdapter;

    private Utilities util;

    private TabHost tabHost;
    private TabSpec spec1,spec2,spec3;

    DatabaseReference ref;
    DatabaseReference auxRef;
    DatabaseReference restRef;
    ValueEventListener getFaveListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        ButterKnife.inject(this);

        setUpFirebase();

        setUpHostBar();

        loadAllRestaurants();

        loadSavedRestaurants();

        paintRestaurants();

        for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
            View v = tabHost.getTabWidget().getChildAt(i);
            v.setBackgroundResource(R.color.colorPrimary);

            TextView tv = (TextView) tabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(getResources().getColor(R.color.white));
        }

    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }


    /* needs to be uncommented in order to not go back to future loading screen.
    @Override
    public void onBackPressed() {
    }*/
    private void loadAllRestaurants(){
        restRef = mDatabase.child("restaurants");
        restRef.addValueEventListener (new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                new LoadRestaurants(snapshot, restaurantSet, StudentHomeActivity.this, R.id.all_restaurants).execute();
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("The read failed: " ,firebaseError.getMessage());
            }
        });

    }

    private void loadSavedRestaurants() {

        String uid = mFirebaseUser.getUid();
        ref = mDatabase.child("users").child(uid).child("studentInfo").child("favRestaurants");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshotx) {
                faveRestaurantSet = new ArrayList<>();

                for (DataSnapshot postSnapshotx: snapshotx.getChildren()) {
                    String refr = postSnapshotx.getValue(String.class);
                    auxRef = mDatabase.child("restaurants").child(refr);


                    auxRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshoty) {
                            Restaurant rest = snapshoty.getValue(Restaurant.class);
                            faveRestaurantSet.add(rest);
                            restAdapter.notifyDataSetChanged();
                            
                        }


                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            Log.e("The read failed: " ,firebaseError.getMessage());
                        }
                    });


                }
            }
            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.e("The read failed: " ,firebaseError.getMessage());
            }
        });
    }




    private void paintRestaurants(){
        restAdapter = new RestaurantListAdapter(this, faveRestaurantSet);
        RestaurantListFragment fragment = (RestaurantListFragment) getFragmentManager().findFragmentById(R.id.saved_restaurants);
        fragment.updateRecycler(restAdapter);
    }
    private void setUpHostBar(){

        tabHost =(TabHost)findViewById(R.id.host_bar);
        tabHost.setup();

        spec1 = tabHost.newTabSpec("Favoritas");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Favoritas");

        spec2 = tabHost.newTabSpec("Todas");
        spec2.setIndicator("Todas");
        spec2.setContent(R.id.tab2);

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);


    }

    public void setUpFirebase(){
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.update_user_info) {
            startEditActivity();
            return true;
        }
        else if(id == R.id.user_sign_off){

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startEditActivity(){
        Class editActivity = StudentEditActivity.class;
        Intent goToEdit = new Intent(this, editActivity);
        startActivity(goToEdit);
    }

}
