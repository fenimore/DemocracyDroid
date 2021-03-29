/*
 *
 *   Copyright (C) 2014-2015 Fenimore Love
 *
 *   This program is free software: you can redistribute it and/or modify
     it under the terms of the GNU General Public License as published by
     the Free Software Foundation, either version 3 of the License, or
     (at your option) any later version.
 *   This program is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     GNU General Public License for more details.
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.workingagenda.democracydroid;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.preference.PreferenceManager;

import com.google.android.material.tabs.TabLayout;
import com.workingagenda.democracydroid.databinding.ActivityMainBinding;
import com.workingagenda.democracydroid.tabfragment.PodcastFragment;
import com.workingagenda.democracydroid.tabfragment.StoryFragment;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.mainToolbar);
        // Shared Preferences
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int DEFAULT_TAB = Integer.parseInt(preferences.getString("pref_default_tab", "0"));
        boolean PREF_FIRST_TIME = preferences.getBoolean("first_preference", true);
        // TODO: have splash screen for new users
        Log.d("First time", String.valueOf(PREF_FIRST_TIME));
        // Tab Layouts
        binding.mainTabLayout.addTab(binding.mainTabLayout.newTab().setIcon(R.drawable.ic_library_books));
        binding.mainTabLayout.addTab(binding.mainTabLayout.newTab().setIcon(R.drawable.ic_live_tv));
        binding.mainTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        binding.mainViewPager.setOffscreenPageLimit(1);  // ???
        binding.mainViewPager.setAdapter(mSectionsPagerAdapter);
        binding.mainViewPager.setCurrentItem(DEFAULT_TAB);
        // Gather the Episode Lists
        // Set up the tab and View Pager
        binding.mainViewPager.addOnPageChangeListener(
                new TabLayout.TabLayoutOnPageChangeListener(binding.mainTabLayout));
        binding.mainTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.mainViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                binding.mainViewPager.setCurrentItem(1);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                binding.mainViewPager.setCurrentItem(tab.getPosition());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_main_settings:
                Intent intent1 = new Intent(this, SettingsActivity.class);
                startActivity(intent1);
                return true;
            case R.id.menu_main_refresh:
                // Don't let user click before async tasks are done
                item.setEnabled(false);
                // Call Fragment refresh methods
                getSupportFragmentManager().getFragments();
                for (Fragment x : getSupportFragmentManager().getFragments()) {
                    if (x instanceof PodcastFragment)
                        ((PodcastFragment) x).refresh();
                    if (x instanceof StoryFragment)
                        ((StoryFragment) x).refresh();
                }
                // FIXME: Somehow enable this after async call...
                item.setEnabled(true);
                return true;
            case R.id.menu_main_exclusives:
                String url = "https://www.democracynow.org/categories/web_exclusive";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
            case R.id.menu_main_site:
                String url1 = "http://www.democracynow.org/";
                Intent i1 = new Intent(Intent.ACTION_VIEW);
                i1.setData(Uri.parse(url1));
                startActivity(i1);
                return true;
            case R.id.menu_main_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivityForResult(intent, 0);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                default: // Deliberate fall-through to story fragment
                case 0:
                    return new StoryFragment();
                case 1:
                    return new PodcastFragment();
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
}
