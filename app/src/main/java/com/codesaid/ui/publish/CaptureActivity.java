package com.codesaid.ui.publish;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codesaid.R;
import com.codesaid.lib_navannotation.ActivityDestination;

/**
 * Created By codesaid
 * On :2020-05-04 15:02
 * Package Name: com.codesaid.ui.publish
 * desc:
 *
 * @author codesaid
 */

@ActivityDestination(pageUrl = "main/tabs/publish", needLogin = true)
public class CaptureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_layout);
    }
}
