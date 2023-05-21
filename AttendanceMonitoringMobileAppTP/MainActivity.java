package com.example.AttendanceMonitoringMobileAppTP;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.AttendanceMonitoringMobileAppTP.Adapter.ClassListAdapter;
import com.example.AttendanceMonitoringMobileAppTP.realm.Class_Names;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton to_insert_class_act;
    RecyclerView recyclerView;
    TextView sample;
    SearchView search_view;
    ClassListAdapter mAdapter;
    Realm realm;
    ImageView imageEmpty;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);

        // Initialize search_view
        search_view =findViewById(R.id.search_view);

        boolean animation = getIntent().getBooleanExtra("ANIMATION", false);
        if (animation) {
            overridePendingTransition(R.anim.slide_up, 0);
        }

        getWindow().setEnterTransition(null);


        to_insert_class_act = findViewById(R.id.plus_insert_class);
        to_insert_class_act.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Insert_class_Activity.class);
                startActivity(intent);
            }
        });
        //Realm instance
        realm = Realm.getDefaultInstance();

        // Retrieve all Class_Names objects from Realm
        RealmResults<Class_Names> results;
        results = realm.where(Class_Names.class)
                .findAll();


        search_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
        });



        sample = findViewById(R.id.classes_sample);
        recyclerView = findViewById(R.id.recyclerView_main);
        imageEmpty = findViewById(R.id.imageEmpty);
        imageEmpty.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        // Create an instance of ClassListAdapter and set it as the adapter for the RecyclerView
        mAdapter = new ClassListAdapter(results, MainActivity.this);
        recyclerView.setAdapter(mAdapter);




        }


    private void filter(String newText) {
        RealmResults<Class_Names> results = realm.where(Class_Names.class).findAll();
        List<Class_Names> filteredList = new ArrayList<>();

        for (Class_Names item : results){
            if(item.getName_subject().toLowerCase().startsWith(newText.toLowerCase()) || item.getClass_name().toLowerCase().startsWith(newText.toLowerCase())){
                filteredList.add(item);
            }
        }
        RealmResults<Class_Names> filteredResults = null;

        // Filter the RealmResults based on the filteredList
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            filteredResults = realm.where(Class_Names.class).in("id", filteredList.stream().map(Class_Names::getId).toArray(String[]::new)).findAll();

        }
        // Update the adapter's data with the filtered results
        mAdapter.filterList(filteredResults);

        if (filteredResults.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
    }

    private void showEmptyView() {
        TransitionManager.beginDelayedTransition((ViewGroup) recyclerView.getParent());
        findViewById(R.id.imageEmpty).setVisibility(View.VISIBLE);
        findViewById(R.id.emptyMessage).setVisibility(View.VISIBLE);
    }

    private void hideEmptyView() {
        TransitionManager.beginDelayedTransition((ViewGroup) recyclerView.getParent());
        findViewById(R.id.imageEmpty).setVisibility(View.GONE);
        findViewById(R.id.emptyMessage).setVisibility(View.GONE);
    }



    @Override
    protected void onResume() {
        realm.refresh();
        realm.setAutoRefresh(true);
        super.onResume();
        RealmResults<Class_Names> results = realm.where(Class_Names.class)
                .findAll();
        mAdapter.updateData(results);
        if (results.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }


}