package study.pmoreira.skillmanager.ui.main;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.data.DataFaker;
import study.pmoreira.skillmanager.ui.BaseActivity;
import study.pmoreira.skillmanager.ui.OnTextChanged;
import study.pmoreira.skillmanager.ui.skill.EditSkillActivity;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;

    @BindView(R.id.viewPager)
    ViewPager mvViewPager;

    @BindView(R.id.search_edittext)
    EditText mSearchEditText;

    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mPagerAdapter = new PagerAdapter(this, getSupportFragmentManager());

        setupViews();

        insertFakeData();
    }

    private void insertFakeData() {
        if (getResources().getBoolean(R.bool.insert_fake_data)) {
            try {
                DataFaker.insertFakeData(this);
            } catch (UnsupportedEncodingException e) {
                Log.d(TAG, "onCreate: " + e);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO:
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        //TODO
    }

    private void setupViews() {
        findViewById(R.id.parent).requestFocus();

        setSupportActionBar(mToolbar);

        mSearchEditText.addTextChangedListener(new OnTextChanged() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPagerAdapter.filter(s);
            }
        });

        mvViewPager.setAdapter(mPagerAdapter);
        mTabLayout.setupWithViewPager(mvViewPager);
    }

    public void onClickFab(View view) {
        EditSkillActivity.startActivity(this);
    }
}
