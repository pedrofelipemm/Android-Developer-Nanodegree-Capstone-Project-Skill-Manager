package study.pmoreira.skillmanager.ui.collaborator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.model.Collaborator;

public class CollaboratorActivity extends AppCompatActivity {

    private static final String EXTRA_COLLABORATOR = "EXTRA_COLLABORATOR";
    private static final String STATE_COLLABORATOR = "STATE_COLLABORATOR";

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

    @BindView(R.id.collab_fab)
    FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collaborator);

        ButterKnife.bind(this);

        if (getIntent().hasExtra(EXTRA_COLLABORATOR)) {
            Collaborator collab = getIntent().getParcelableExtra(EXTRA_COLLABORATOR);
            setTitle(collab.getName());
            setupViews(collab);
        }
    }

    private void setupViews(Collaborator collab) {
        mNameTextView.setText(collab.getName());
        mRoleTextView.setText(collab.getRole());
        mEmailTextView.setText(collab.getEmail());
        mPhoneTextView.setText(formatPhone(collab.getPhone()));

        Picasso.with(this)
                .load(collab.getPictureUrl())
                .error(R.drawable.collaborator_placeholder)
                .into(mPicImageView);
    }

    private String formatPhone(String phone) {
        //TODO
        return phone;
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
