package study.pmoreira.skillmanager.ui.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.data.DataFaker;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    @BindView(R.id.viewPager)
    ViewPager mvViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setupViews();

        if (getResources().getBoolean(R.bool.insert_fake_data)) {
            DataFaker.insertFakeData(this);
        }
    }

    private void setupViews() {
        findViewById(R.id.parent).requestFocus();

        mvViewPager.setAdapter(new PageAdapter(this, getSupportFragmentManager()));
        mTabLayout.setupWithViewPager(mvViewPager);
    }
}
