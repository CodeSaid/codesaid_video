package com.codesaid;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.alibaba.fastjson.JSONObject;
import com.codesaid.lib_network.ApiResponse;
import com.codesaid.lib_network.callback.JsonCallback;
import com.codesaid.lib_network.request.GetRequest;
import com.codesaid.utils.NavGraphBuilder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

/**
 * @author codesaid
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController mNavController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);

        navView.setOnNavigationItemSelectedListener(this);

        NavGraphBuilder.build(mNavController, this, fragment.getId());

        GetRequest<JSONObject> request = new GetRequest<>("www.imooc.com");
        request.execute();

        request.execute(new JsonCallback<JSONObject>() {
            @Override
            public void onSuccess(ApiResponse<JSONObject> response) {
                super.onSuccess(response);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mNavController.navigate(menuItem.getItemId());
        return !TextUtils.isEmpty(menuItem.getTitle());
    }
}
