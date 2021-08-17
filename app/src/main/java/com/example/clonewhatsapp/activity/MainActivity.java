package com.example.clonewhatsapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.config.ConfigurateFirebase;
import com.example.clonewhatsapp.fragment.ChatFragment;
import com.example.clonewhatsapp.fragment.ContactsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference fireDB;
    private FirebaseAuth userAuth;

    private FragmentPagerItemAdapter tabAdapter;
    private MaterialSearchView searchView;



    // ##############                ACTIVITY LIFE CYCLE                       ############### //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("Clone WhatsApp");
        setSupportActionBar(toolbar);


        getFireBaseRef();

        tabAdapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(),
                FragmentPagerItems.with(MainActivity.this)
                        .add("Chat", ChatFragment.class)
                        .add("Contacts", ContactsFragment.class)
                        .create());

        final ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(tabAdapter);

        SmartTabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setViewPager(viewPager);

        searchView = findViewById(R.id.searchViewMessages);
        searchView.setOnQueryTextListener(
                new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                switch (viewPager.getCurrentItem()) {
                    case 0:

                        ChatFragment chatFragment = (ChatFragment) tabAdapter.getPage(0);

                        if (newText != null) {
                            if(newText.isEmpty()){
                                chatFragment.setRecyclerContactsChat(false);
                            }else {
                                chatFragment.queryMessageText(newText.toLowerCase());
                            }
                        }
                        break;

                    case 1:

                        ContactsFragment contactsFragment = (ContactsFragment) tabAdapter.getPage(1);

                        if (newText != null ) {
                            if( newText.isEmpty()) {
                                contactsFragment.setRecyclerViewContacts(false);
                            }else {
                                contactsFragment.queryUser(newText.toLowerCase());
                            }
                        }

                        break;
                }

                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

                ChatFragment chatFragment = (ChatFragment) tabAdapter.getPage(0);
                chatFragment.reloadChat();

            }
        });
    }



    // ############                    TOOLBAR                   ############ //

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem item = menu.findItem(R.id.menuSearch);
        searchView.setMenuItem(item);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.menuSearch:
                throwToast("Menu Search", Toast.LENGTH_SHORT);
                break;

            case R.id.menuConfig:
                openSettings();
                break;

            case R.id.menuSignOut:
                signOut();
                break;

        }

        return super.onOptionsItemSelected(item);
    }



    // ##############                UTILITIES                        ############### //

    public void openSettings(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void signOut(){

        Intent intent = new Intent(this, LoginActivity.class);

        try {

            userAuth.signOut();
            throwToast("Logged out.", Toast.LENGTH_SHORT);

        }catch (Exception e){

            throwToast(e.getMessage(), Toast.LENGTH_LONG);
        }

        finish();
        startActivity(intent);
    }

    public void getFireBaseRef(){

        fireDB = ConfigurateFirebase.getFireDBRef();
        userAuth = ConfigurateFirebase.getFirebaseAuth();

    }


    public void throwToast( String message, int lenght){
        Toast.makeText(
                this,
                message,
                lenght).show();


    }
}
