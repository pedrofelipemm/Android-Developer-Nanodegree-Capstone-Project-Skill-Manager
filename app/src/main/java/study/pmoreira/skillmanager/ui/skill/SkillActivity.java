package study.pmoreira.skillmanager.ui.skill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.model.Skill;
import study.pmoreira.skillmanager.ui.BaseActivity;

public class SkillActivity extends BaseActivity {

//    @BindView(R.id.skill_learn_more)
//    TextView mLearnMoreTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skill);

        ButterKnife.bind(this);

//        mLearnMoreTextView.setMovementMethod(LinkMovementMethod.getInstance());
//        mLearnMoreTextView.setText(Html.fromHtml("<a href='http://www.google.com'> Google </a>"));
    }

    public static void startActivity(Context context, Skill skill) {
        Intent intent = new Intent(context, SkillActivity.class);

        context.startActivity(intent);
    }
}
