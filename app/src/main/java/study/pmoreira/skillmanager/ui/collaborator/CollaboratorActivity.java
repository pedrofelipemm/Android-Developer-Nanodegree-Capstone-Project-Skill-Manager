package study.pmoreira.skillmanager.ui.collaborator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.plumillonforge.android.chipview.ChipView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.business.CollaboratorSkillBusiness;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.Collaborator;
import study.pmoreira.skillmanager.ui.BaseActivity;
import study.pmoreira.skillmanager.ui.customview.StringChip;

public class CollaboratorActivity extends BaseActivity {

    public static final String EXTRA_COLLABORATOR = "EXTRA_COLLABORATOR";
    public static final String STATE_COLLABORATOR = "STATE_COLLABORATOR";

    @BindView(R.id.collab_name_textview)
    TextView mNameTextView;

    @BindView(R.id.collab_role_textview)
    TextView mRoleTextView;

    @BindView(R.id.collab_email_textview)
    TextView mEmailTextView;

    @BindView(R.id.collab_phone_textview)
    TextView mPhoneTextView;

    @BindView(R.id.collab_pic_imageview)
    ImageView mPicImageView;

    @BindView(R.id.chip_view)
    ChipView mChipView;

    @BindView(R.id.collab_fab)
    FloatingActionButton mFab;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    private CollaboratorSkillBusiness mCollaboratorSkillBusiness = new CollaboratorSkillBusiness();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collaborator);

        ButterKnife.bind(this);

        if (getIntent().hasExtra(EXTRA_COLLABORATOR)) {
            Collaborator collab = getIntent().getParcelableExtra(EXTRA_COLLABORATOR);
            setTitle(collab.getName());

            setupViews(collab);

            displayProgressbar(mProgressBar);
            mCollaboratorSkillBusiness.findCollaboratorSkillsName(collab.getId(),
                    new OperationListener<List<String>>() {
                        @Override
                        public void onSuccess(List<String> results) {
                            StringChip.setChipList(mChipView, results);
                            hideProgressbar(mProgressBar);
                        }
                    });
        }
    }

    private void setupViews(final Collaborator collab) {
        mNameTextView.setText(collab.getName());
        mRoleTextView.setText(collab.getRole());
        mEmailTextView.setText(collab.getEmail());
        mPhoneTextView.setText(collab.getPhone());

        Glide.with(this)
                .load(collab.getPictureUrl())
                .apply(new RequestOptions().error(R.drawable.collaborator_placeholder))
                .into(mPicImageView);

        mFab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                EditCollaboratorActivity.startActivity(CollaboratorActivity.this, collab);
            }
        });
    }

    public static void startActivity(Context context, Collaborator collaborator, int... flags) {
        Intent intent = new Intent(context, CollaboratorActivity.class);

        if (collaborator != null) {
            intent.putExtra(EXTRA_COLLABORATOR, collaborator);
        }

        for (int flag : flags) {
            intent.addFlags(flag);
        }

        context.startActivity(intent);
    }
}
