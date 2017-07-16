package study.pmoreira.skillmanager.ui.skill;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.business.SkillBusiness;
import study.pmoreira.skillmanager.infrastructure.BusinessException;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.Skill;
import study.pmoreira.skillmanager.ui.BaseActivity;

public class EditSkillActivity extends BaseActivity {

    private static final String TAG = EditSkillActivity.class.getName();

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String STATE_SKILL = "STATE_SKILL";

    public static final String EXTRA_SKILL = "EXTRA_SKILL";

    private SkillBusiness mSkillBusiness;

    @BindView(R.id.skill_imageview)
    ImageView mImageView;

    @BindView(R.id.skill_name_edittext)
    EditText mNameEdiText;

    @BindView(R.id.skill_description_edittext)
    EditText mDescriptionEdiText;

    @BindView(R.id.learn_more_edittext)
    EditText mLearnMoreEditText;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    String mPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_skill);

        ButterKnife.bind(this);
        mSkillBusiness = new SkillBusiness(this);

        setTitle(getString(R.string.new_skill));

        setupViews();

        if (getIntent().hasExtra(EXTRA_SKILL)) {
            fillFields((Skill) getIntent().getParcelableExtra(EXTRA_SKILL));
        }
    }

    private void setupViews() {
        findViewById(R.id.parent).requestFocus();

        int skillDescriptionMaxLength = getInteger(R.integer.skill_description_max_length);
        mDescriptionEdiText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(skillDescriptionMaxLength)});

        int skillNameMaxLength = getInteger(R.integer.skill_description_max_length);
        mNameEdiText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(skillNameMaxLength)});

        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                        PICK_IMAGE_REQUEST);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_SKILL, newSkill());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fillFields((Skill) savedInstanceState.getParcelable(STATE_SKILL));
    }

    private void fillFields(Skill skill) {
        mNameEdiText.setText(skill.getName());
        mDescriptionEdiText.setText(skill.getDescription());
        mLearnMoreEditText.setText(skill.getLearnMoreUrl());
        mPhotoUrl = skill.getPictureUrl();
        loadSkillPicture();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            displayProgressbar(mProgressBar);
            mSkillBusiness.uploadImage(data.getData(), new OnPictureUpload());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                save();
                return true;
            case R.id.menu_delete:
                delete();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void save() {
        mSkillBusiness.save(newSkill(), new OnSkillSave());
    }

    private void delete() {
        //TODO: delete from database and storage
    }

    private Skill newSkill() {
        return new Skill(
                mNameEdiText.getText().toString(),
                mDescriptionEdiText.getText().toString(),
                mLearnMoreEditText.getText().toString(),
                mPhotoUrl);
    }

    @SuppressWarnings("ConstantConditions")
    void loadSkillPicture() {
        Picasso.with(EditSkillActivity.this)
                .load(mPhotoUrl)
                .error(getDrawable(R.drawable.skill_placeholder))
                .into((ImageView) findViewById(R.id.skill_imageview));
    }

    private class OnPictureUpload implements OperationListener<String> {

        @Override
        public void onSuccess(String result) {
            if (!TextUtils.isEmpty(mPhotoUrl)) {
                mSkillBusiness.deleteImage(mPhotoUrl);
            }

            mPhotoUrl = result;
            loadSkillPicture();
            hideProgressbar(mProgressBar);
        }

        @Override
        public void onError(BusinessException e) {
            Log.e(TAG, "onError: ", e);
            displayMessage(getString(R.string.error_image_upload));
            hideProgressbar(mProgressBar);
        }
    }

    private class OnSkillSave implements OperationListener<Skill> {
        @Override
        public void onSuccess(Skill skill) {
            displayMessage(getString(R.string.skill_successfully_saved));
            SkillActivity.startActivity(EditSkillActivity.this, skill);
            finish();
        }

        @Override
        public void onError(BusinessException e) {
            if (SkillBusiness.INVALID_SKILL_NAME == e.getCode()) {
                mNameEdiText.setError(e.getMessage());
            }
            if (SkillBusiness.INVALID_SKILL_DESCRIPTION == e.getCode()) {
                mDescriptionEdiText.setError(e.getMessage());
            }
            if (SkillBusiness.INVALID_SKILL_LEARN_MORE_URL == e.getCode()) {
                mLearnMoreEditText.setError(e.getMessage());
            }
            if (SkillBusiness.INVALID_SKILL_PICTURE_URL == e.getCode()) {
                displayMessage(e.getMessage());
            }
        }
    }
}
