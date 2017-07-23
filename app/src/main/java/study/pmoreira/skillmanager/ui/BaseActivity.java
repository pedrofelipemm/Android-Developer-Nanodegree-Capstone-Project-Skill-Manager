package study.pmoreira.skillmanager.ui;

import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;


public abstract class BaseActivity extends AppCompatActivity {

    public int getInteger(int id) {
        return getResources().getInteger(id);
    }

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
}
