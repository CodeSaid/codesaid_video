package com.codesaid.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.codesaid.R;
import com.codesaid.lib_network.ApiResponse;
import com.codesaid.lib_network.ApiService;
import com.codesaid.lib_network.callback.JsonCallback;
import com.codesaid.model.User;
import com.google.android.material.button.MaterialButton;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created By codesaid
 * On :2020-05-10 01:10
 * Package Name: com.codesaid.ui
 * desc:
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView action_close;
    private MaterialButton action_login;
    private Tencent mTencent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);
        action_close = findViewById(R.id.action_close);
        action_login = findViewById(R.id.action_login);

        action_close.setOnClickListener(this);
        action_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.action_close:
                finish();
                break;
            case R.id.action_login:
                login();
                break;
        }
    }

    private void login() {
        if (mTencent == null) {
            mTencent = Tencent.createInstance("appId", getApplicationContext());
        }

        mTencent.login(this, "all", new IUiListener() {
            @Override
            public void onComplete(Object o) {
                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                JSONObject json = (JSONObject) o;
                try {
                    String openId = json.getString("openid");
                    String access_token = json.getString("access_token");
                    String expires_in = json.getString("expires_in");
                    long expires_time = json.getLong("expires_time");

                    mTencent.setAccessToken(access_token, expires_in);
                    mTencent.setOpenId(openId);
                    QQToken token = mTencent.getQQToken();
                    getUserInfo(token, expires_time, openId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(LoginActivity.this,
                        "登录失败: " + uiError.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "登录取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserInfo(QQToken token, long expires_time, String openId) {
        UserInfo userInfo = new UserInfo(getApplicationContext(), token);
        userInfo.getUserInfo(new IUiListener() {
            @Override
            public void onComplete(Object o) {
                JSONObject json = (JSONObject) o;

                try {
                    String nickName = json.getString("nickname");
                    String figureurl_2 = json.getString("figureurl_2");

                    save(nickName, figureurl_2, openId, expires_time);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                Toast.makeText(LoginActivity.this,
                        "登录失败: " + uiError.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginActivity.this, "登录取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressWarnings("unchecked")
    private void save(String nickName, String avatar, String openId, long expires_time) {
        ApiService.get("/user/insert")
                .addParam("name", nickName)
                .addParam("avatar", avatar)
                .addParam("qqOpenId", openId)
                .addParam("expires_time", expires_time)
                .execute(new JsonCallback<User>() {
                    @Override
                    public void onSuccess(ApiResponse<User> response) {
                        if (response.body != null) {
                            UserManager.getInstance().save(response.body);
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(ApiResponse<User> response) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this,
                                        "登录失败: " + response.message, Toast.LENGTH_SHORT).show();
                            }
                        });
                        super.onError(response);
                    }
                });
    }
}
