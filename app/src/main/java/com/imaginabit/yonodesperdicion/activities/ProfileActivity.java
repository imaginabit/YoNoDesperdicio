package com.imaginabit.yonodesperdicion.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.imaginabit.yonodesperdicion.R;
import com.imaginabit.yonodesperdicion.adapters.UsersAdapter;
import com.imaginabit.yonodesperdicion.models.User;
import com.imaginabit.yonodesperdicion.utils.UserUtils;

public class ProfileActivity extends NavigationBaseActivity {

    private final String TAG = getClass().getSimpleName();
    private User mUser;
    private Context mContext;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = setSupportedActionBar();
        setDrawerLayout(toolbar);

        // Back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle data = getIntent().getExtras();
        User user = (User) data.getParcelable("user");

        if (user != null) {
            //TextView bodyView = (TextView) findViewById(R.id.ad_body);
            // get all user info from website
            getUserInfo(user.getUserId());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            Toast.makeText(ProfileActivity.this, "pulsado ", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getUserInfo(final int userid) {
        Log.d(TAG, "get user info ");
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        final Handler handler = new Handler();

        if (networkInfo != null && networkInfo.isConnected()) {
            UserUtils.fetchUser(userid,new UserUtils.FetchUserCallback() {
                @Override
                public void done(final User user, Exception e) {
                    if (user != null) {
                        mUser = user;
                        adapter = new UsersAdapter(user);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    } else {
                        Log.e(TAG, "error al obtener los datos");
                        e.printStackTrace();
                        //wait 5 secons to try again
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                getUserInfo(userid);
                            }
                        }, 5000);
                    }
                }
            });
        } else {
            Toast.makeText(this, "No hay conexion a internet.", Toast.LENGTH_SHORT).show();
        }
    }
}
