package com.gaurav.robotics_society;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.gaurav.robotics_society.Adapters.Achive;
import com.gaurav.robotics_society.Models.Achivements_Model;
import com.gaurav.robotics_society.app_update_checker.UpdateHelper;
import com.gaurav.robotics_society.prashikshan.prakish_check;
import com.gaurav.robotics_society.user.profile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by GAURAV on 09-01-2019.
 */

public class Home_Page extends AppCompatActivity implements UpdateHelper.onUpdateCheckListener {

    List<Achivements_Model> productList = new ArrayList<Achivements_Model>();
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drw;
    private ActionBarDrawerToggle mtoggle;
    private ProgressBar pb;
    private RecyclerView recycler;
    private Achive adapter;
    private DatabaseReference dbProducts;

    private NavigationView mNav;
    private View mHeader;
    private String user_id = null;
    private String email = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achivements);
        this.setTitle("Achievements");
        Firebase.setAndroidContext(this);

        SharedPreferences mSharedPreference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String email = (mSharedPreference.getString("user_email", "email"));
        //String user_id = (mSharedPreference.getString("user_id", "id"));
        mSharedPreference.edit().putBoolean("signIn", true).commit();

        mNav = (NavigationView) findViewById(R.id.navigation_View);
        mHeader = mNav.getHeaderView(0);
        TextView details = (TextView) mHeader.findViewById(R.id.user_details);

        if (email != null) {
            details.setText(String.valueOf(email));
        }
        UpdateHelper.with(this)
                .onUpdateCheck(this)
                .check();
        send_time_to_server();
        send_token_to_server();

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Society");

        drw = (DrawerLayout) findViewById(R.id.drawer_layout);
        mtoggle = new ActionBarDrawerToggle(this, drw, R.string.Open, R.string.Close);
        drw.addDrawerListener(mtoggle);

        mtoggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.navigation_View);
        navigationView.getMenu().getItem(1).setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch(menuItem.getItemId()){
                            case R.id.nav_profile:
                                Intent prof = new Intent(Home_Page.this, profile.class);
                                startActivity(prof);
                                break;


                            case R.id.nav_achievements:
                                Toast.makeText(Home_Page.this, "These are the Achievements of Society", Toast.LENGTH_LONG).show();
                                drw.closeDrawers();
                                break;


                            case R.id.nav_noti:
                                //Toast.makeText(Home_Page.this, "Notification", Toast.LENGTH_SHORT).show();
                                Intent eve = new Intent(getBaseContext(), events.class);
                                startActivity(eve);
                                break;


                            case R.id.nav_projects:
                                //Toast.makeText(Home_Page.this, "Projects", Toast.LENGTH_SHORT).show();
                                Intent projects = new Intent(getBaseContext(), com.gaurav.robotics_society.projects.class);
                                startActivity(projects);
                                break;
                            case R.id.nav_awards:
                                //Toast.makeText(Home_Page.this, "Awards", Toast.LENGTH_SHORT).show();
                                Intent awards = new Intent(getBaseContext(), awards.class);
                                startActivity(awards);
                                break;

                            case R.id.prakish:
                                Intent prakish = new Intent(getBaseContext(), prakish_check.class);
                                startActivity(prakish);
                                break;


                            case R.id.nav_samavesh2020:
                                Intent s = new Intent(getBaseContext(), samavesh2020.class);
                                startActivity(s);
                                break;


                            case R.id.nav_logout:
                                //Toast.makeText(Home_Page.this, "Signing Out", Toast.LENGTH_SHORT).show();
                                PreferenceManager.getDefaultSharedPreferences(Home_Page.this).edit().putBoolean("signIn", false).commit();
                                PreferenceManager.getDefaultSharedPreferences(Home_Page.this).edit().putString("user_email", "email").commit();
                                PreferenceManager.getDefaultSharedPreferences(Home_Page.this).edit().putString("user_id", "id").commit();

                                finish();
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(Home_Page.this, HomeActivity.class));
                                break;


                            case R.id.nav_support:
                                //Toast.makeText(Home_Page.this, "Support", Toast.LENGTH_SHORT).show();
                                Intent sup = new Intent(Home_Page.this, support.class);
                                startActivity(sup);
                                break;


                            case R.id.nav_aboutus:
                                //Toast.makeText(Home_Page.this, "AboutUs", Toast.LENGTH_SHORT).show();
                                Intent abt = new Intent(getBaseContext(), about.class);
                                startActivity(abt);
                                break;


                            default:
                                Toast.makeText(Home_Page.this, "Default", Toast.LENGTH_SHORT).show();
                                return false;
                        }


                        return true;
                    }
                }
        );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pb = (ProgressBar) findViewById(R.id.ProgressBB);
        pb.setVisibility(View.VISIBLE);

        recycler = (RecyclerView) findViewById(R.id.recyclerView);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        productList = new ArrayList<>();

        dbProducts = FirebaseDatabase.getInstance().getReference("Data");
        dbProducts.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot productSnapshot : dataSnapshot.getChildren()){
                        Achivements_Model p = productSnapshot.getValue(Achivements_Model.class);
                        Achivements_Model ach = new Achivements_Model();

                        String desc = p.getDesc();
                        String image = p.getImage();
                        String title = p.getTitle();
                        String url = p.getUrl();

                        ach.setDesc(desc);
                        ach.setImage(image);
                        ach.setTitle(title);
                        ach.setUrl(url);
                        productList.add(ach);
                    }

                    pb.setVisibility(View.INVISIBLE);

                    adapter = new Achive(Home_Page.this, productList);
                    recycler.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       /* productList.add(new Achivements_Model("decription of post 01",
                "https://firebasestorage.googleapis.com/v0/b/robotics-society-99fe7.appspot.com/o/image1.jpg?alt=media&token=ac970a6c-a43f-41bf-87fa-a9035a3c1d28",
                "Post 01"));

        productList.add(new Achivements_Model("decription of post 02",
                "https://firebasestorage.googleapis.com/v0/b/robotics-society-99fe7.appspot.com/o/image2.jpg?alt=media&token=6ca72b46-d28c-446e-9326-bb006bbd0606",
                "Post 02"));

        productList.add(new Achivements_Model("decription of post 03",
                "https://firebasestorage.googleapis.com/v0/b/robotics-society-99fe7.appspot.com/o/image3.jpg?alt=media&token=d0f86afd-f68e-4c2d-adc9-c202ede6890a",
                "Post 03"));

        productList.add(new Achivements_Model("decription of post 04",
                "https://firebasestorage.googleapis.com/v0/b/robotics-society-99fe7.appspot.com/o/image4.jpg?alt=media&token=a2272c6e-d794-44d6-9779-baeb692feb36",
                "Post 04"));

        productList.add(new Achivements_Model("decription of post 05",
                "https://firebasestorage.googleapis.com/v0/b/robotics-society-99fe7.appspot.com/o/image5.jpg?alt=media&token=051b23d5-7100-4579-ae1b-6496eda2d448",
                "Post 05"));
        productList.add(new Achivements_Model("decription of post 05",
                "https://firebasestorage.googleapis.com/v0/b/robotics-society-99fe7.appspot.com/o/image6.jpg?alt=media&token=dab04237-7a9c-418c-89cb-202732f1fece",
                "Post 05"));

        adapter = new Achive(this, productList);
        recycler.setAdapter(adapter);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mtoggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUpdateCheckListener(final String urlApp) {
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Newer Version is Available")
                .setMessage("Please Update to continue")
                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Toast.makeText(Home_Page.this, "Updating!!!" + urlApp, Toast.LENGTH_SHORT).show();
                        Intent i1 = new Intent(android.content.Intent.ACTION_VIEW);
                        i1.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.gaurav.robotics_society"));
                        finish();
                        startActivity(i1);
                    }
                }).create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        drw.closeDrawers();
    }

    private void send_time_to_server() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        String user_id = (PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("user_id", "id"));

        Firebase mfire = new Firebase("https://robotics-society-99fe7.firebaseio.com/Users/" + user_id);
        Firebase Fullname = mfire.child("last_login");
        Fullname.setValue(date);

        Firebase version = mfire.child("version");
        version.setValue(getAppVersion(this));
    }

    private void send_token_to_server() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        // Get new Instance ID token
                        String user_id = (PreferenceManager.getDefaultSharedPreferences(getBaseContext()).getString("user_id", "id"));
                        String token = task.getResult().getToken();
                        Firebase mfire = new Firebase("https://robotics-society-99fe7.firebaseio.com/Users/" + user_id);

                        Firebase Fullname = mfire.child("token");
                        Fullname.setValue(token);
                    }
                });
        // [END retrieve_current_token]
    }

    private String getAppVersion(Context context) {
        String result = "";
        try {
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }
}
