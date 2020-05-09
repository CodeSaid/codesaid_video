package com.codesaid;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;

import com.codesaid.model.Destination;
import com.codesaid.model.User;
import com.codesaid.ui.login.UserManager;
import com.codesaid.utils.AppConfig;
import com.codesaid.utils.NavGraphBuilder;
import com.codesaid.view.AppBottomBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author codesaid
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private NavController mNavController;
    private AppBottomBar mNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavView = findViewById(R.id.nav_view);

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);

        mNavView.setOnNavigationItemSelectedListener(this);

        NavGraphBuilder.build(mNavController, this, fragment.getId());
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        HashMap<String, Destination> destConfig = AppConfig.getDestConfig();
        Iterator<Map.Entry<String, Destination>> iterator = destConfig.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Destination> entry = iterator.next();
            Destination destination = entry.getValue();
            if (destination != null
                    && !UserManager.getInstance().isLogin()
                    && destination.isNeedLogin()
                    && destination.getId() == menuItem.getItemId()) {
                UserManager.getInstance().login(this).observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        mNavView.setSelectedItemId(menuItem.getItemId());
                    }
                });
                return false;
            }
        }


        mNavController.navigate(menuItem.getItemId());
        return !TextUtils.isEmpty(menuItem.getTitle());
    }
}
