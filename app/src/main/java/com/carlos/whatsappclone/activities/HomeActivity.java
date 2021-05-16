package com.carlos.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.carlos.whatsappclone.R;
import com.carlos.whatsappclone.adapters.ViewPagerAdapter;
import com.carlos.whatsappclone.databinding.ActivityHomeBinding;
import com.carlos.whatsappclone.fragments.ChatsFragment;
import com.carlos.whatsappclone.fragments.ContactsFragment;
import com.carlos.whatsappclone.fragments.PhotoFragment;
import com.carlos.whatsappclone.fragments.StatusFragment;
import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.AuthProvider;
import com.carlos.whatsappclone.provides.UsersProvider;
import com.carlos.whatsappclone.utils.AppBackgroundHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.MissingFormatArgumentException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;

public class HomeActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    private ActivityHomeBinding binding;
    private MaterialSearchBar searchBar;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ChatsFragment chatsFragment;
    private ContactsFragment contactsFragment;
    private StatusFragment statusFragment;
    private PhotoFragment photoFragment;

    private AuthProvider authProvider;
    private UsersProvider usersProvider;

    private User user;

    int tabSelected = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());

        searchBar = binding.searchBar;
        tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;

        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();

        if(getIntent().getSerializableExtra("user") != null) {
           user = (User) getIntent().getSerializableExtra("user");
        }

        viewPager.setOffscreenPageLimit(3);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());


        chatsFragment = new ChatsFragment(user);
        contactsFragment = new ContactsFragment(user);
        statusFragment = new StatusFragment(user);
        photoFragment = new PhotoFragment(user);

        adapter.addFragments(photoFragment,"");
        adapter.addFragments(chatsFragment,"CHATS");
        adapter.addFragments(statusFragment,"ESTADOS");
        adapter.addFragments(contactsFragment,"СONTACTOS");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(tabSelected);

        setUpTabIcon();


        searchBar.setOnSearchActionListener(this);
        searchBar.inflateMenu(R.menu.main_menu);
        searchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.itemSignOut){
                    signOut();
                }else if (item.getItemId() == R.id.itemProfile){
                    goToProfile();
                }
                return  true;

            }
        });
        setContentView(binding.getRoot());

    }

    @Override
    protected void onStart() {
        super.onStart();
        AppBackgroundHelper.online(HomeActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppBackgroundHelper.online(HomeActivity.this);
    }

    private void goToProfile() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);

    }

    private void setUpTabIcon() {
        tabLayout.getTabAt(0).setIcon(R.drawable.icon_camera);
        LinearLayout linearLayout = ((LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(0));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.weight = 0.5f;
        linearLayout.setLayoutParams(layoutParams);
    }

    private void signOut() {
        authProvider.signOut();
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}