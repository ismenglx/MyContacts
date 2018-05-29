package com.ismenglx.mycontacts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ismenglx.mycontacts.fragment.CallFragment;
import com.ismenglx.mycontacts.fragment.ContactsFragment;
import com.ismenglx.mycontacts.fragment.GroupsFragment;
import com.ismenglx.mycontacts.view.MyTab;
import com.ismenglx.mycontacts.view.MultiFloatingActionButton;
import com.ismenglx.mycontacts.view.TagFabLayout;

public class MainActivity extends AppCompatActivity{
    private MenuItem searchItem;
    private static boolean show_select_view = true;
    private Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        MyTab mTab = findViewById(R.id.mTab);
        final ViewPager mViewPager = findViewById(R.id.mViewPager);
        MultiFloatingActionButton mFab = findViewById(R.id.main_fab);
        setSupportActionBar((Toolbar) findViewById(R.id.mToolbar));

        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setCurrentItem(1);
        mTab.setViewPager(mViewPager);

        //设置主界面悬浮按钮点击事件--------------------------------------------------------------------
        mFab.setOnFabItemClickListener(new MultiFloatingActionButton.OnFabItemClickListener() {
            @Override
            public void onFabItemClick(TagFabLayout view, int position) {
                switch (position){
                    case 2:
                        Intent intent = new Intent(MainActivity.this, AddNewActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        searchItem.setVisible(show_select_view);
                        show_select_view = !show_select_view;
                        break;
                    case 4:
                        intent = new Intent(MainActivity.this, AboutSoftwareActivity.class);
                        startActivity(intent);
                        break;
//                    case 5:
//                        new  AlertDialog.Builder(MainActivity.this)
//                                .setTitle(R.string.action_deleteAll)
//                                .setMessage("将要清空所有联系人信息！！！确定吗？？？" )
//                                .setPositiveButton("确定" , null)
//                                .setNegativeButton("取消" , null)
//                                .setIcon(R.mipmap.ic_danger)
//                                .show();
//                    Snackbar.make(view, "搜索联系人", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                        break;
                }
            }
        });
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private String[] titles = {getString(R.string.call),
                getString(R.string.contacts),
                getString(R.string.groups)};
        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return fragment = CallFragment.newInstance();
            } else if (position == 1) {
                return fragment = ContactsFragment.newInstance();
            } else {
                return fragment = GroupsFragment.newInstance();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO 搜索
        getMenuInflater().inflate(R.menu.menu_main_search, menu);
        searchItem = menu.findItem(R.id.main_search);
//        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                return true;
//            }
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                return true;
//            }
//        });
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("搜索联系人");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_search:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
