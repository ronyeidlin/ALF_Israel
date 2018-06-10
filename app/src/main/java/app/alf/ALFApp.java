package app.alf;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;

import io.fabric.sdk.android.Fabric;
/**
 * Created by rony on 13/09/2017.
 */

public class ALFApp extends Application{


    @Override
    public void onCreate() {
        super.onCreate();


        Fabric.with(this, new Crashlytics());

        String appID = getResources().getString(R.string.facebook_app_id);
        Log.d("facebook_app_id:",appID);
        FacebookSdk.setApplicationId(appID);





    }

}
