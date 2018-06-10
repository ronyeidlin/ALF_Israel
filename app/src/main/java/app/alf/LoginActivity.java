package app.alf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import app.alf.Utils.SettingsUtils;
import app.alf.Utils.SharedPrefUtils;
import app.alf.enums.AlarmIntervalOption;
import app.alf.enums.DaysBeforeOption;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);








        callbackManager = CallbackManager.Factory.create();
        info = (TextView)findViewById(R.id.info);
        loginButton = (LoginButton)findViewById(R.id.login_button);

        loginButton.setReadPermissions(Arrays.asList("user_status","pages_show_list","user_events","user_likes"));


        SettingsUtils.setDaysBefore(getApplicationContext(), DaysBeforeOption.getValueFromEnum(DaysBeforeOption.day_3_before));
        SettingsUtils.setDaysBeforePosition(getApplicationContext(),DaysBeforeOption.day_3_before.getValue());

        SettingsUtils.setInterval(getApplicationContext(), AlarmIntervalOption.getValueFromEnum(AlarmIntervalOption.Every_day_from_first_Alarm));
        SettingsUtils.setIntervalPosition(getApplicationContext(),AlarmIntervalOption.Every_day_from_first_Alarm.getValue());


        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                SharedPrefUtils.setString(getApplicationContext(),SharedPrefUtils.GENERAL, SharedPrefUtils.FACEBOOK_TOKEN,loginResult.getAccessToken().getToken());
                SharedPrefUtils.setString(getApplicationContext(),SharedPrefUtils.GENERAL, SharedPrefUtils.FACEBOOK_USER_ID, loginResult.getAccessToken().getUserId());
                SharedPrefUtils.setBoolean(getApplicationContext(),SharedPrefUtils.GENERAL, SharedPrefUtils.IS_FACEBOOK_LOGIN, true);

                Log.i(TAG,"User ID: "
                        + loginResult.getAccessToken().getUserId()
                        + "\n" +
                        "Auth Token: "
                        + loginResult.getAccessToken().getToken());



                Crashlytics.setUserIdentifier(loginResult.getAccessToken().getUserId());


                Intent i = new Intent(getApplicationContext(), ALFMainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();


            }

            @Override
            public void onCancel() {

                info.setText("Login attempt canceled.");
                Log.e(TAG, " CAN'T LOGIN FACEBOOK : USER CANCEL");

            }

            @Override
            public void onError(FacebookException e) {

                info.setText("Login attempt failed. exception" + e.getMessage());
                Log.e(TAG, " CAN'T LOGIN FACEBOOK : EXCEPTION :" + e.getMessage());


            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}

