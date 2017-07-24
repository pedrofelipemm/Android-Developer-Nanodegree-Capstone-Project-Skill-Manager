package study.pmoreira.skillmanager.ui.skill;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.model.Skill;
import study.pmoreira.skillmanager.ui.BaseActivity;

public class SkillActivity extends BaseActivity {

    public static final String STATE_SKILL = "STATE_SKILL";
    public static final String EXTRA_SKILL = "EXTRA_SKILL";

    @BindView(R.id.cardview)
    CardView mCardView;

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
        mDescriptionTextView.setText(skill.getDescription());

        Picasso.with(this)
                .load(skill.getPictureUrl())
                .error(R.drawable.skill_placeholder)
                .into(mPicImageView);


        mCardView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(skill.getLearnMoreUrl()));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        mFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditSkillActivity.startActivity(SkillActivity.this, skill);
            }
        });
    }

    public static void startActivity(Context context, Skill skill) {
        Intent intent = new Intent(context, SkillActivity.class);

        if (skill != null) {
            intent.putExtra(EXTRA_SKILL, skill);
        }

        context.startActivity(intent);
    }
}
