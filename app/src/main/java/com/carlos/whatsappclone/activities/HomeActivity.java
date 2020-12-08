package com.carlos.whatsappclone.activities;

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
import com.google.android.material.tabs.TabLayout;
import com.mancj.materialsearchbar.MaterialSearchBar;

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

    private User user;

    int tabSelected = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        searchBar = binding.searchBar;
        tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;

        viewPager.setOffscreenPageLimit(3);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        chatsFragment = new ChatsFragment();
        contactsFragment = new ContactsFragment();
        statusFragment = new StatusFragment();
        photoFragment = new PhotoFragment();
        adapter.addFragments(photoFragment,"");
        adapter.addFragments(chatsFragment,"CHATS");
        adapter.addFragments(statusFragment,"ESTADOS");
        adapter.addFragments(contactsFragment,"СONTACTOS");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(tabSelected);

        setUpTabIcon();

        authProvider = new AuthProvider();

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