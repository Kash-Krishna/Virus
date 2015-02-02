package myapplication.example.sultan.testing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.File;


public class testPhotos extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_photos);

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_photos, menu);
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

    float myX;
    float myY;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


        Resources res = getResources();

        String address;

        WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = manager.getConnectionInfo();
        address = info.getMacAddress();
        System.out.println(address);
        Firebase active = new Firebase("https://virus.firebaseio.com/Active_List/"+address);

        // GET X
        active.child("pos").child("x").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                myX = Float.valueOf(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        // GET Y
        active.child("pos").child("y").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                myY = Float.valueOf(snapshot.getValue().toString());
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });

        Bitmap bitmap = BitmapFactory.decodeResource(res, R.drawable.campus);
        bitmap = bitmap.copy(bitmap.getConfig(),true);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);

        // create canvas to draw on the bitmap
        Canvas canvas = new Canvas(bitmap);
        canvas.drawCircle(Float.valueOf(String.valueOf(myX*6.34)), Float.valueOf(String.valueOf((myY*6.35))), 35, paint);

        System.out.println(Float.valueOf(String.valueOf(myX*6.34)));
        System.out.println(myX);

        ImageView imageView = (ImageView)findViewById(R.id.testcircle);
        imageView.setAdjustViewBounds(true);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onStart() {
        super.onStart();




    }
}
