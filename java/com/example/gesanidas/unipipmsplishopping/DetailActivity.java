package com.example.gesanidas.unipipmsplishopping;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.gesanidas.unipipmsplishopping.data.CartContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class DetailActivity extends AppCompatActivity implements LocationListener
{
    TextView title,description,price,date;
    ImageView picture;
    Button button;
    Product product;
    LocationManager locationManager;
    NotificationManager mNotificationManager;
    SharedPreferences sharedPreferences;
    TTS tts;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //location and notification managers
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //initialise the Text-to-speech object
         tts=new TTS(this);


        title=(TextView)findViewById(R.id.textView);
        date=(TextView)findViewById(R.id.textView2);
        description=(TextView)findViewById(R.id.textView3);
        price=(TextView)findViewById(R.id.textView4);
        picture=(ImageView)findViewById(R.id.imageView2);
        button=(Button)findViewById(R.id.button);


        //utilise the user preferences set in the settings activity
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int fontSize=sharedPreferences.getInt("font",12);
        description.setTextSize(fontSize);
        title.setBackgroundColor(Color.parseColor(sharedPreferences.getString("color","purple").toUpperCase()));


        //get the product info through the intent
        product=(Product)getIntent().getSerializableExtra("product");

        title.setText(product.getTitle());
        date.setText(product.getDate());
        description.setText(product.getDescription());
        price.setText(String.valueOf(product.getPrice()+" €"));
        Glide.with(this).load(product.getPhotoUrl()).into(picture);

        start();





    }

    public void order(View view)
    {
        //this fucntion creates an order node in the firebase database and also adds the item in the local database
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String name = user.getDisplayName();
        long timestamp=System.currentTimeMillis();
        Order order=new Order(name,product.getId(),timestamp);
        MainActivity.mOrderDatabaseReference.push().setValue(order);
        Uri mUri = getContentResolver().insert(CartContract.CartEntry.CONTENT_URI, getContentValues(product));
        //if order is succesfully saved in the local db , the user is notified by speech
        if(mUri !=null) tts.speak("Order submitted");
    }






    public void start()
    {
        //user is asked to allow the gps service to track his position
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION}, 8);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,this);

    }



    @Override
    public void onLocationChanged(Location location)
    {
        float[] result=new float[1];
        Location.distanceBetween(location.getLongitude(),location.getLatitude(),product.getLongitude(),product.getLatitude(),result);
        Log.i("distance",String.valueOf(result[0]));
        Integer radius=sharedPreferences.getInt("radius",150);   //gets user preferred radius to track
        boolean notification=sharedPreferences.getBoolean("notification",false);    //gets user preference on enabling  the service
        if (result[0]<radius && notification)
        {
            Intent notificationIntent = new Intent(getApplicationContext(), DetailActivity.class);
            notificationIntent.putExtra("product",product);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setContentIntent(contentIntent)
                            .setSmallIcon(android.R.drawable.sym_contact_card)
                            .setContentTitle(String.valueOf(product.getTitle()))
                            .setContentText(String.valueOf("is close"));
            mNotificationManager.notify(product.getId(),mBuilder.build());         //allow multiple notifications if more than one product is nearby

        }


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }




    public ContentValues getContentValues (Product product)
    {
        //this function stores the product attributes to content values to enter them in the sqlite database
        ContentValues contentValues = new ContentValues();
        String title,release_date,photo_url;
        double price;

        //get product attributes
        price=product.getPrice();
        title=product.getTitle();
        photo_url=product.getPhotoUrl();
        release_date=product.getDate();

        contentValues.put(CartContract.CartEntry.COLUMN_TITLE,title);
        contentValues.put(CartContract.CartEntry.COLUMN_PHOTO_URL,photo_url);
        contentValues.put(CartContract.CartEntry.COLUMN_PRICE,price);
        contentValues.put(CartContract.CartEntry.COLUMN_RELEASEDATE,release_date);

        return contentValues;

    }
}
