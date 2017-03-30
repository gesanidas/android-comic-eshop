package com.example.gesanidas.unipipmsplishopping;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
{

    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;
    private String mUsername;

    private ProductAdapter productAdapter;
    private RecyclerView recyclerView;
    GridLayoutManager layoutManager;
    public static FirebaseDatabase mFireDatabase;
    public static DatabaseReference mProductsDatabaseReference;
    public static DatabaseReference mUsersDatabaseReference;
    public static DatabaseReference mOrderDatabaseReference;
    public static FirebaseStorage mFirebaseStorage;
    public static StorageReference mProductsPhotosReference;
    public static FirebaseAuth mFirebaseAuth;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    SharedPreferences sharedPreferences;








    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUsername = ANONYMOUS;
        mFirebaseAuth = FirebaseAuth.getInstance();


        //  firebase and shared prefernces variables




        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        //  set color that user saved in shared preferences
        setActivityBackgroundColor(Color.parseColor(sharedPreferences.getString("color","white").toUpperCase()));


        //init();   this functions is called only once to populate the firebase database



        //this is the listener for the firebae authentication
        mAuthStateListener=new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user=firebaseAuth.getCurrentUser();
                if (user!=null)
                {
                    onSignedInInitialize(user.getDisplayName());
                }
                else
                {
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }


            }
        };

        displayProducts();

    }



    private void onSignedInInitialize(String username)
    {
        mUsername = username;
        displayProducts();
    }


    private void onSignedOutCleanup()
    {
        mUsername = ANONYMOUS;

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                storeUser();
            }
            else if (resultCode == RESULT_CANCELED)
            {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    //this function sets the color argument to the activity background
    public  void setActivityBackgroundColor(int color)
    {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }


    // we remove the authentication listener when onpause runs. we reattach it onresume
    @Override
    protected void onPause()
    {
        super.onPause();
        if (mAuthStateListener != null)
        {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }

    }


    @Override
    protected void onResume()
    {
        super.onResume();
        setActivityBackgroundColor(Color.parseColor(sharedPreferences.getString("color","white").toUpperCase()));
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }







    //menu functions
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    //menu function:implements the user actions through intents or signing out
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.sign_out_menu:
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_cart:
                Intent cartIntent = new Intent(this, CartActivity.class);
                startActivity(cartIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //stores the user info in the firebase database and also saves the username in shared preferences so that
    //it may appear in the settings screen

    private void storeUser()
    {
        FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        String name = current_user.getDisplayName();
        String email = current_user.getEmail();
        String uid = current_user.getUid();
        User user=new User(name,uid,email);
        mUsersDatabaseReference.child(uid).setValue(user);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("username",name);
        editor.commit();

    }

    private void displayProducts()
    {
        mFireDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mProductsDatabaseReference = mFireDatabase.getReference().child("products");
        mUsersDatabaseReference = mFireDatabase.getReference().child("users");
        mOrderDatabaseReference=mFireDatabase.getReference().child("orders");
        mProductsPhotosReference = mFirebaseStorage.getReferenceFromUrl("gs://shopping-427ab.appspot.com");

        layoutManager= new GridLayoutManager(this,2);
        recyclerView = (RecyclerView)findViewById(R.id.product_recycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(layoutManager);
        productAdapter=new ProductAdapter(Product.class,R.layout.product,ProductHolder.class,mProductsDatabaseReference,this);
        recyclerView.setAdapter(productAdapter);
    }



    //this is the function that populated the firebase database. it's only called once
    private void init()
    {
        Product product = new Product(1,"Wolverine: Old Man Logan","A future world savaged and sundered by super villains, the United States ain't what it used to be. In California, now a wasteland controlled by the evil Hulk Gang, the former Wolverine seeks to live in peace. He's retired, finally free from the violence of his former existence as an X-Man, and he wants to keep it that way. If only they'd let him. Now, Logan and an aged, blind Hawkeye are forced into a cross-country jaunt through villain-ruled lands, on a collision course with the worst of them all! Can Old Man Logan maintain his pacifi st vow, and make his last stand without doing what he does best? Mark Millar and Steve McNiven unite for a riotous romp through a future world of death, despair and dynamic action!","February 14, 2017",6.19,"https://firebasestorage.googleapis.com/v0/b/shopping-427ab.appspot.com/o/wolverine.jpg?alt=media&token=87eb9239-ff8b-4676-8d66-6019a2035674",38.0802186,23.7080114);

        mProductsDatabaseReference.push().setValue(product);

        product = new Product(2,"X-Men: Days of Future Past","It was the best of times, it was the worst of times: Relive the legendary first journey into the dystopian future of 2013 - where Sentinels stalk the Earth, and the X-Men are humanity's only hope...until they die! Also featuring the first appearance of Alpha Flight, the return of the Wendigo, the history of the X-Men from Cyclops himself...and a demon for Christmas!? Collecting UNCANNY X-MEN (1963) #138-143 and X-MEN ANNUAL #4.","December 21, 2011",1.51,"https://firebasestorage.googleapis.com/v0/b/shopping-427ab.appspot.com/o/xmen.jpg?alt=media&token=b927e849-180b-46e0-884b-4c7d0dd733fe",38.062474,23.7080114);
        mProductsDatabaseReference.push().setValue(product);


         product = new Product(3,"The Death of Superman","One of the best selling graphic novels of all time is back in a new edition. Can even Superman stop the unstoppable creature called Doomsday? If the assembled might of the JLA can't do it, what hope does he have?","February 26, 2013",9.61,"https://firebasestorage.googleapis.com/v0/b/shopping-427ab.appspot.com/o/superman.jpg?alt=media&token=3cfc0ad0-7952-4125-9dc1-5c4885ece5f8",38.0521076,23.7231643);

        mProductsDatabaseReference.push().setValue(product);



         product = new Product(4,"Batman: The Killing Joke","Presented for the first time with stark, stunning new coloring by Bolland, BATMAN: THE KILLING JOKE is Alan Moore's unforgettable meditation on the razor-thin line between sanity and insanity, heroism and villainy, comedy and tragedy.","March 19, 2008",12.39,"https://firebasestorage.googleapis.com/v0/b/shopping-427ab.appspot.com/o/batman.jpg?alt=media&token=9d602ab6-29dc-4973-b68c-6fbd60fc7743",38.0521076,23.7231643);

        mProductsDatabaseReference.push().setValue(product);


         product = new Product(5,"The Outlaw: Origins ","A masked vigilante stalks the streets of downtown Los Angeles, disrupting crime and creating havoc. After being spotted on security cameras and thrust into the national spotlight, he is pursued by both the media and powerful new enemies. Little does the world know the Outlaw is just High School junior Chase Jackson wearing a mask and hoping the beautiful brunette down the street notices.","June 2, 2015",3.71,"https://firebasestorage.googleapis.com/v0/b/shopping-427ab.appspot.com/o/outlaw.jpg?alt=media&token=fd604bb0-0ba4-4b84-8486-2266e2352acd",37.9698752,23.7303741);

        mProductsDatabaseReference.push().setValue(product);


         product = new Product(6,"Aquaman: A Celebration of 75 Years ","Though often overshadowed or overlooked, Aquaman is among the greatest and most enduring characters in the DC Universe. The King of Atlantis is one of the few Golden Age characters to survive into the present day. He’s also a founding member of the Justice League, the first DC hero to start a family and soon will star in his own big-screen franchise. ","October 25, 2016",29.99,"https://firebasestorage.googleapis.com/v0/b/shopping-427ab.appspot.com/o/aquaman.jpg?alt=media&token=5c8299bb-e94a-4155-91f3-5097bb26d34c",37.9698752,23.7303741);

        mProductsDatabaseReference.push().setValue(product);


         product = new Product(7,"Green Lantern: A Celebration of 75 Years","Bringing together stories from more than seven decades of comics, GREEN LANTERN: A CELEBRATION OF 75 YEARS features stories from all of Earth’s Green Lanterns—from the wartime avenger Alan Scott to brash Guy Gardner, from solemn John Stewart to young, cool Kyle. And, of course, read the rise, fall and redemption of the greatest Green Lantern of them all, Hal Jordan. ","October 20, 2015",28.84,"https://firebasestorage.googleapis.com/v0/b/shopping-427ab.appspot.com/o/greenlantern.jpg?alt=media&token=6c446b45-5352-4b15-b3a9-d2481ba80ae1",37.9698752,23.7303741);

        mProductsDatabaseReference.push().setValue(product);

         product = new Product(8,"The Witcher Volume 1","Travelling near the edge of the Brokilon forest, monster hunter Geralt meets a widowed fisherman who's dead and murderous wife resides in a eerie mansion known as the House of Glass, which seems to have endless rooms, nothing to fill them with, and horror around every corner.","October 7, 2014",9.88,"https://firebasestorage.googleapis.com/v0/b/shopping-427ab.appspot.com/o/witcher.jpg?alt=media&token=fd5d06e6-c3c2-490d-9feb-5587cedba9a3",37.9698752,23.7303741);

        mProductsDatabaseReference.push().setValue(product);


         product = new Product(9,"Civil War","The landscape of the Marvel Universe is changing, and it's time to choose: Whose side are you on? A conflict has been brewing from more than a year, threatening to pit friend against friend, brother against brother - and all it will take is a single misstep to cost thousands their lives and ignite the fuse.","April 11, 2007",12.39,"https://firebasestorage.googleapis.com/v0/b/shopping-427ab.appspot.com/o/civilwar.jpg?alt=media&token=45de8d2c-eee5-4fb9-a0ce-d466957b4414",37.9468661,23.7197311);
        mProductsDatabaseReference.push().setValue(product);


         product = new Product(10,"Suicide Squad Vol. 2: Basilisk Rising","As the surviving Squad members attempt to recover from their disastrous Gotham City mission, we learn a dark secret that has been festering in the team since issue #1: a traitor stalks the Suicide Squad! The saboteur's mission: Assassinate Amanda Waller, expose the Squad and leave Task Force X in ruins! The Basilisk strikes!","February 19, 2013",12.39,"https://firebasestorage.googleapis.com/v0/b/shopping-427ab.appspot.com/o/suicidesquad.jpg?alt=media&token=060f3b61-521d-4474-a67b-11d9721f612d",37.9468661,23.7197311);


        mProductsDatabaseReference.push().setValue(product);


         product = new Product(11,"Justice League Vol. 2: The Villain's Journey ","The Justice League is the greatest force for good the world has ever seen. But not everyone sees them that way. Their never-ending battle against evil results in casualties beyond its super-powered, costumed combatants. The League’s attempts to safeguard innocent lives cannot save everybody. Unbeknownst to Earth’s greatest champions, their greatest triumph may contain the seeds of their greatest defeat.","October 1, 2013",16.11,"https://firebasestorage.googleapis.com/v0/b/shopping-427ab.appspot.com/o/justiceleague.jpg?alt=media&token=05758fdf-3e00-4f09-a4cf-0f536f3e330b",37.942897,23.6965622);
        mProductsDatabaseReference.push().setValue(product);



         product = new Product(12,"Forever Evil","The Justice League is DEAD! And the villains shall INHERIT the Earth! In a flash of light, the world's most powerful heroes vanish as the Crime Syndicate arrives from Earth-3! As this evil version of the Justice League takes over the DC Universe, no one stands in the way of them and complete domination ... no one except for Lex Luthor. ","May 19, 2015",14.95,"https://firebasestorage.googleapis.com/v0/b/shopping-427ab.appspot.com/o/foreverevil.jpg?alt=media&token=012a2243-4280-40da-a133-701e1752c322",37.942897,23.6965622);
        mProductsDatabaseReference.push().setValue(product);
    }



}
