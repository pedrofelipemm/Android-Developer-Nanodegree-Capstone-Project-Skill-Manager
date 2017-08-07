package study.pmoreira.skillmanager.ui.skill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.model.Skill;
import study.pmoreira.skillmanager.ui.BaseActivity;

public class SkillActivity extends BaseActivity {

    private static final String TAG = SkillActivity.class.getName();

    public static final String STATE_SKILL = "STATE_SKILL";
    public static final String EXTRA_SKILL = "EXTRA_SKILL";

    @BindView(R.id.cardview)
    CardView mCardView;

    @Nullable
    @BindView(R.id.skill_name_textview)
    TextView mNameTextView;

    @BindView(R.id.skill_description_textview)
    TextView mDescriptionTextView;

    @BindView(R.id.skill_pic_imageview)
    ImageView mPicImageView;

    @BindView(R.id.skill_fab)
    FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill);

        ButterKnife.bind(this);

        if (getIntent().hasExtra(EXTRA_SKILL)) {
            Skill skill = getIntent().getParcelableExtra(EXTRA_SKILL);
            setTitle(skill.getName());
            setupViews(skill);
        }
    }

    public void setupViews(final Skill skill) {
        if (mNameTextView != null) mNameTextView.setText(skill.getName());
        mDescriptionTextView.setText(skill.getDescription());

        Glide.with(this)
                .load(skill.getPictureUrl())
                .apply(new RequestOptions().error(R.drawable.skill_placeholder))
                .into(mPicImageView);


        mCardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                launchChromeCustomTab(skill.getLearnMoreUrl());
            }
        });

        mFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SkillActivity.this,
                        mPicImageView, getString(R.string.transition_skill));

                EditSkillActivity.startActivity(SkillActivity.this, skill, options);
            }
        });
    }

    public static void startActivity(Context context, Skill skill, int... flags) {
        startActivity(context, skill, null, flags);
    }

    public static void startActivity(Context context, Skill skill, @Nullable ActivityOptionsCompat options,
                                     int... flags) {
        Intent intent = new Intent(context, SkillActivity.class);

        if (skill != null) {
            intent.putExtra(EXTRA_SKILL, skill);
        }

        for (int flag : flags) {
            intent.addFlags(flag);
        }

        if (options != null) {
            context.startActivity(intent, options.toBundle());
        } else {
            context.startActivity(intent);
        }
    }
}
