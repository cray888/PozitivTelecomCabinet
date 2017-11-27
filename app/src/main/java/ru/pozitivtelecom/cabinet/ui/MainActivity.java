package ru.pozitivtelecom.cabinet.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.wooplr.spotlight.SpotlightView;

import java.util.HashMap;
import java.util.Map;

import ru.pozitivtelecom.cabinet.R;
import ru.pozitivtelecom.cabinet.adapters.AccountsSpinnerAdapter;
import ru.pozitivtelecom.cabinet.app.ApplicationClass;
import ru.pozitivtelecom.cabinet.app.PreferencesClass;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //UI
    private DrawerLayout mDrawer;
    private Snackbar mSnackbarExit;
    private FloatingActionButton fab;

    //Public variable

    //Private variable
    //private int mCurentAccountID = 0;
    private AccountsSpinnerAdapter spinnerAdapter;
    private Map <Integer, Fragment> mFragmentMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerLayout = navigationView.getHeaderView(0);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        mSnackbarExit = Snackbar.make(findViewById(R.id.mainView), R.string.action_press_to_exit, Snackbar.LENGTH_SHORT);

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        fab.setVisibility(View.INVISIBLE);

        LinearLayout mHeaderNav = headerLayout.findViewById(R.id.head_nav);
        mHeaderNav.setPadding(
                (int) getResources().getDimension(R.dimen.activity_horizontal_margin),
                (int) getResources().getDimension(R.dimen.activity_vertical_margin) + getStatusBarHeight(),
                (int) getResources().getDimension(R.dimen.activity_horizontal_margin),
                (int) getResources().getDimension(R.dimen.activity_vertical_margin)
        );

        TextView mPersonName = headerLayout.findViewById(R.id.txt_abonent);
        mPersonName.setText(ApplicationClass.getAppData(this).PersonFIOShort);

        ImageView mImageViewPay = headerLayout.findViewById(R.id.image_pay);
        mImageViewPay.setClickable(true);
        mImageViewPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawer.closeDrawer(GravityCompat.START);
                int mCurrentAccountID = ApplicationClass.getCurrentAccount(MainActivity.this);
                Intent intent = new Intent(MainActivity.this, PayActivity.class);
                intent.putExtra("accountNO", spinnerAdapter.getListItem(mCurrentAccountID).AccountNO);
                intent.putExtra("recommendedPay", String.format("%.2f", spinnerAdapter.getListItem(mCurrentAccountID).RecommendedPay));
                startActivity(intent);
            }
        });

        Spinner mSpinnerAccounts = headerLayout.findViewById(R.id.spnr_accounts);
        mSpinnerAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((ApplicationClass)getApplication()).CurrentAccount = i;
                ApplicationClass.setCurrentAccount(MainActivity.this, i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerAdapter = new AccountsSpinnerAdapter(this);
        mSpinnerAccounts.setAdapter(spinnerAdapter);

        navigationView.getMenu().getItem(0).setChecked(true);
        Fragment fragment = MainFragment.newInstance();
        mFragmentMap.put(R.id.nav_home, fragment);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        setTitle(navigationView.getMenu().getItem(0).getTitle());
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);

    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (mSnackbarExit.isShown()) super.onBackPressed();
            else mSnackbarExit.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int itemID = item.getItemId();
        Fragment fragment = mFragmentMap.get(itemID);
        boolean changeFragment = true;
        boolean newFragment = false;

        switch (itemID)
        {
            case R.id.nav_home:
                /*Intent intent_map = new Intent(this, MapActivity.class);
                startActivity(intent_map);
                changeFragment = false;*/
                if (fragment == null) {
                    fragment = MainFragment.newInstance();
                    newFragment = true;
                }
                break;
            case R.id.nav_about:
                if (fragment == null) {
                    fragment = AboutFragment.newInstance();
                    newFragment = true;
                }
                break;
            case R.id.nav_logout:
                PreferencesClass.Preferences.SetPreferences("token", "");
                Intent intent_logout = new Intent(this, LoginActivity.class);
                intent_logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent_logout.putExtra("message", "");
                startActivity(intent_logout);
                changeFragment = false;
                break;
            default:
                fragment = BlankFragment.newInstance("","");
        }

        if (changeFragment) {
            if (newFragment) mFragmentMap.put(itemID, fragment);

            // Insert the fragment by replacing any existing fragment
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();

            // Highlight the selected item has been done by NavigationView
            //item.setChecked(true);
            // Set action bar title
            setTitle(item.getTitle());
        }


        // Close the navigation mDrawer
        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public int getStatusBarHeight() {
        int result = 0;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }

    public void showLoadingDialog() {

    }

    public void hideLoadingDialog() {

    }

    private void openPayPage() {

    }

    private void showHelp(View view, String uid, String title, String text) {
        new SpotlightView.Builder(this)
                .introAnimationDuration(400)
                .enableRevealAnimation(true)
                .performClick(true)
                .fadeinTextDuration(400)
                .headingTvColor(getResources().getColor(R.color.colorPrimary))
                .headingTvSize(32)
                .headingTvText("Love")
                .subHeadingTvColor(Color.parseColor("#ffffff"))
                .subHeadingTvSize(16)
                .subHeadingTvText("Like the picture?\nLet others know.")
                .maskColor(Color.parseColor("#dc000000"))
                .target(view)
                .lineAnimDuration(400)
                .lineAndArcColor(getResources().getColor(R.color.colorPrimary))
                .dismissOnTouch(true)
                .dismissOnBackPress(true)
                .enableDismissAfterShown(true)
                .usageId(uid) //UNIQUE ID
                .show();
    }

}
