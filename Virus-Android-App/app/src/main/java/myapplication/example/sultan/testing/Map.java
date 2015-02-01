package myapplication.example.sultan.testing;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Iterator;


public class Map extends ActionBarActivity {

    RelativeLayout myNotif;
    ImageView heartImage;
    String address;
    View heartView;
    AnimationDrawable frameAnimation;
    RelativeLayout userStateLayout;
    double myX, myY;


    View userStateView;
    TextView userStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        Firebase.setAndroidContext(this);

        // new LongOperation().execute("");

        RegisterDevice();
        SetListeners();

        Thread listenerThread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SetListeners();
                                SpreadTheVirus();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };


        listenerThread.start();

    }

    @Override
    protected void onStart() {
        super.onStart();
        View view = findViewById(R.id.zombienotification);
        myNotif = (RelativeLayout) view;
        myNotif.setVisibility(View.INVISIBLE);


        userStateView = findViewById(R.id.userstate);
        userStateLayout = (RelativeLayout) userStateView;
        userStateView = findViewById(R.id.user_state);
        userStateTextView = (TextView) userStateView;

        heartView = findViewById(R.id.heart);
        heartImage = (ImageView) heartView;
        // set its background to our AnimationDrawable XML resource.
        heartImage.setBackgroundResource(R.drawable.heart_animation_healthy);

        /*
         * Get the background, which has been compiled to an AnimationDrawable
         * object.
         */
        frameAnimation = (AnimationDrawable) heartImage
                .getBackground();

        // Start the animation (looped playback by default).
        frameAnimation.start();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void RegisterDevice() {

        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        address = info.getMacAddress();


        // Connect to registration list
        Firebase sendData = new Firebase("https://virus.firebaseio.com/Active_List/");
        // Send data to database, register device
        sendData.child(address).child("pos").child("x").setValue("-1");
        sendData.child(address).child("pos").child("y").setValue("-1");
        sendData.child(address).child("status").setValue("Healthy");
        sendData.child(address).child("zone").setValue("-1");

        return;
    }

    public void SetListeners() {
        // Connect to statistics
        Firebase stats = new Firebase("https://virus.firebaseio.com/Stats");
        Firebase norm = new Firebase("https://virus.firebaseio.com/");


        // CHECK TOTAL INFECTED
        stats.child("Total_Infected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                TextView tv1 = (TextView) findViewById(R.id.zone_infected);
                tv1.setText(snapshot.getValue().toString());

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        // TOTAL HEALTHY
        stats.child("Total_Healthy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                TextView tv1 = (TextView) findViewById(R.id.zone_safe);
                tv1.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
/*
        stats.child("zone_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                TextView tv1 = (TextView) findViewById(R.id.zone_title);
                tv1.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        stats.child("zone_secondary").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                TextView tv1 = (TextView) findViewById(R.id.zone_secondary);
                tv1.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
*/
        // TIMER
        norm.child("time").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                TextView tv1 = (TextView) findViewById(R.id.countdown);
                tv1.setText(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
/*
        stats.child("zombie_nearby").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getValue().toString() == "true")
                    myNotif.setVisibility(View.VISIBLE);
                else if (snapshot.getValue().toString() == "false")
                    myNotif.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
        */
        Firebase active = new Firebase("https://virus.firebaseio.com/Active_List/" + address);
        active.child("status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (snapshot.getValue().toString().equals("Healthy")) {
                    userStateTextView.setText("Healthy");
                    userStateLayout.setBackgroundColor(Color.parseColor("#00FF00"));
                    heartImage.setBackgroundResource(R.drawable.heart_animation_healthy);
                } else if (snapshot.getValue().toString().equals("Infected")) {
                    userStateTextView.setText("Infected");
                    userStateLayout.setBackgroundColor(Color.parseColor("#FF6600"));
                    heartImage.setBackgroundResource(R.drawable.heart_animation_infected);
                } else
                    return;

                frameAnimation = (AnimationDrawable) heartImage
                        .getBackground();
                frameAnimation.start();
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });


    }

    public void SpreadTheVirus() {
        Firebase active = new Firebase("https://virus.firebaseio.com/Active_List/" + address);

        // GET X
        active.child("pos").child("x").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                myX = Double.valueOf(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        // GET Y
        active.child("pos").child("y").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                myY = Double.valueOf(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

/*
        Firebase actionList = new Firebase("https://virus.firebaseio.com/Active_List/");

        actionList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Iterable<DataSnapshot> myIterable = snapshot.getChildren();

               ArrayList<String> mystring;

                for (DataSnapshot child : myIterable){
                    mystring.child.getKey().toString();
                }


            }


            @Override
            public void onCancelled(FirebaseError error) {
            }
        });


*/


    }


}
