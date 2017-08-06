package study.pmoreira.skillmanager.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsIntent.Builder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import study.pmoreira.skillmanager.R;

public abstract class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getName();

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        hideKeyboard();
    }

    public void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    public int getInteger(int id) {
        return getResources().getInteger(id);
    }

    //TODO snackbar
    public void displayMessage(String message) {
        if (TextUtils.isEmpty(message)) return;

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void displayProgressbar(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideProgressbar(ProgressBar progressBar) {
        progressBar.setVisibility(View.INVISIBLE);
    }

    public boolean isLoading(ProgressBar progressBar) {
        return progressBar.isShown();
    }

    public void launchChromeCustomTab(String url) {
        CustomTabsIntent customTabsIntent = new Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left)
                .setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .build();

        if (customTabsIntent.intent.resolveActivity(getPackageManager()) != null) {
            try {
                customTabsIntent.launchUrl(this, Uri.parse(url));
            } catch (Exception e) {
                Log.e(TAG, getString(R.string.unable_to_handle_actionview, url), e);
            }
        } else {
            Log.e(TAG, getString(R.string.unable_to_handle_actionview, url));
        }
    }
}
