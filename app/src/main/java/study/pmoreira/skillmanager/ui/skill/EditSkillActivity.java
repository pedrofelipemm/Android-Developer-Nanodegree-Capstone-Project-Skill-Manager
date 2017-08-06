package study.pmoreira.skillmanager.ui.skill;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.business.SkillBusiness;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.infrastructure.exception.BusinessException;
import study.pmoreira.skillmanager.infrastructure.exception.ValidateException;
import study.pmoreira.skillmanager.model.Skill;
import study.pmoreira.skillmanager.ui.BaseActivity;
import study.pmoreira.skillmanager.ui.main.MainActivity;
import study.pmoreira.skillmanager.utils.FileUtils;

import static study.pmoreira.skillmanager.ui.skill.SkillActivity.EXTRA_SKILL;
import static study.pmoreira.skillmanager.ui.skill.SkillActivity.STATE_SKILL;

public class EditSkillActivity extends BaseActivity {

    private static final String TAG = EditSkillActivity.class.getName();

    private static final int PICK_IMG_REQUEST = 1;

    private static final String STATE_IS_EDITING = "STATE_IS_EDITING";
    private static final String STATE_IMG_REF = "STATE_IMG_REF";

    private SkillBusiness mSkillBusiness;

    @BindView(R.id.skill_name_edittext)
    EditText mNameEdiText;

    @BindView(R.id.skill_description_edittext)
    EditText mDescriptionEdiText;

    @BindView(R.id.learn_more_edittext)
    EditText mLearnMoreEditText;

    @BindView(R.id.skill_imageview)
    ImageView mImageView;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    private String mImgRef;

    private boolean mIsEditing;

    String mPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_skill);

        ButterKnife.bind(this);
        mSkillBusiness = new SkillBusiness();

        setTitle(getString(R.string.new_skill));

        setupViews();

        if (getIntent().hasExtra(EXTRA_SKILL)) {
            Skill skill = getIntent().getParcelableExtra(EXTRA_SKILL);

            setTitle(skill.getName());
            fillFields(skill);
            mIsEditing = true;
        }
    }

    private void setupViews() {
        findViewById(R.id.parent).requestFocus();

        int skillDescriptionMaxLength = getInteger(R.integer.skill_description_max_length);
        mDescriptionEdiText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(skillDescriptionMaxLength)});

        int skillNameMaxLength = getInteger(R.integer.skill_name_max_length);
        mNameEdiText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(skillNameMaxLength)});

        Glide.with(this)
                .load(R.drawable.skill_placeholder)
                .apply(new RequestOptions().error(getDrawable(R.drawable.skill_placeholder)))
                .apply(new RequestOptions().fitCenter())
                .into(mImageView);
    }

    public void onClickChangeView(View view) {
        if (isLoading(mProgressBar)) return;
        startActivityForResult(
                new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),
                PICK_IMG_REQUEST);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_SKILL, newSkill());
        outState.putBoolean(STATE_IS_EDITING, mIsEditing);

        if (mImgRef != null) {
            cancelUpload();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fillFields((Skill) savedInstanceState.getParcelable(STATE_SKILL));
        mIsEditing = savedInstanceState.getBoolean(STATE_IS_EDITING);
        mImgRef = savedInstanceState.getString(STATE_IMG_REF);
    }

    private void fillFields(Skill skill) {
        mNameEdiText.setText(skill.getName());
        mDescriptionEdiText.setText(skill.getDescription());
        mLearnMoreEditText.setText(skill.getLearnMoreUrl());
        mPhotoUrl = skill.getPictureUrl();
        loadSkillPicture();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK && intent != null && intent.getData() != null) {
            displayProgressbar(mProgressBar);
            try {
                byte[] imgBytes = FileUtils.getBytes(getContentResolver().openInputStream(intent.getData()));
                mImgRef = mSkillBusiness.uploadImage(imgBytes, new OnPictureUpload());
            } catch (Exception e) {
                Log.d(TAG, "onActivityResult: ", e);
                displayMessage(getString(R.string.error_image_upload));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!mIsEditing) {
            menu.findItem(R.id.menu_delete).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                cancelUpload();

                Intent intent = new Intent(this, EditSkillActivity.class);
                intent.putExtra(EXTRA_SKILL, newSkill());
                NavUtils.navigateUpTo(this, intent);
                return true;
            case R.id.menu_save:
                save();
                return true;
            case R.id.menu_delete:
                delete();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelUpload();
    }

    private void cancelUpload() {
        if (TextUtils.isEmpty(mImgRef)) return;

        StorageReference imgRef = FirebaseStorage.getInstance().getReferenceFromUrl(mImgRef);
        for (UploadTask task : imgRef.getActiveUploadTasks()) {
            task.cancel();
        }
    }

    public static void startActivity(Context context) {
        startActivity(context, null);
    }

    public static void startActivity(Context context, Skill skill) {
        Intent intent = new Intent(context, EditSkillActivity.class);

        if (skill != null) {
            intent.putExtra(EXTRA_SKILL, skill);
        }

        context.startActivity(intent);
    }

    private void save() {
        if (isLoading(mProgressBar)) {
            Toast.makeText(this, getString(R.string.wait_img_upload), Toast.LENGTH_SHORT).show();
            return;
        }

        mSkillBusiness.saveOrUpdate(newSkill(), new OnSkillSave());
    }

    private void delete() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.attention))
                .setMessage(getString(R.string.do_you_want_to_delete, mNameEdiText.getText()))
                .setPositiveButton(R.string.yes_caps,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mSkillBusiness.delete(getSkillId(), new OnSkillDelete());
                            }
                        })
                .setNegativeButton(R.string.no_caps, null)
                .create()
                .show();
    }

    private Skill newSkill() {
        Skill skill = new Skill(
                mNameEdiText.getText().toString(),
                mDescriptionEdiText.getText().toString(),
                mLearnMoreEditText.getText().toString(),
                mPhotoUrl);

        skill.setId(getSkillId());

        return skill;
    }

    String getSkillId() {
        String skillId = null;
        if (getIntent().hasExtra(EXTRA_SKILL)) {
            skillId = ((Skill) getIntent().getParcelableExtra(EXTRA_SKILL)).getId();
        }
        return skillId;
    }

    void loadSkillPicture() {
        if (TextUtils.isEmpty(mPhotoUrl)) return;
        Glide.with(EditSkillActivity.this)
                .load(mPhotoUrl)
                .apply(new RequestOptions().error(getDrawable(R.drawable.skill_placeholder)))
                .into(mImageView);
    }

    private class OnPictureUpload extends OperationListener<String> {

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
            displayMessage(e.getMessage());
            hideProgressbar(mProgressBar);
        }
    }

    private class OnSkillSave extends OperationListener<Skill> {
        @Override
        public void onSuccess(Skill skill) {
            displayMessage(getString(R.string.skill_successfully_saved));
            SkillActivity.startActivity(EditSkillActivity.this, skill, Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
        }

        @Override
        public void onValidationError(ValidateException e) {
            if (SkillBusiness.INVALID_SKILL_NAME == e.getCode()) {
                mNameEdiText.setError(getString(R.string.name_cannot_be_empty));
                mNameEdiText.requestFocus();
            }
            if (SkillBusiness.INVALID_SKILL_DESCRIPTION == e.getCode()) {
                mDescriptionEdiText.setError(getString(R.string.description_cannot_be_empty));
                mDescriptionEdiText.requestFocus();
            }
            if (SkillBusiness.INVALID_SKILL_LEARN_MORE_URL == e.getCode()) {
                mLearnMoreEditText.setError(getString(R.string.learn_more_cannot_be_empty));
                mLearnMoreEditText.requestFocus();
            }
            if (SkillBusiness.INVALID_SKILL_PICTURE_URL == e.getCode()) {
                displayMessage(getString(R.string.picture_cannot_be_empty));
            }
        }
    }

    private class OnSkillDelete extends OperationListener<String> {
        @Override
        public void onSuccess(String result) {
            displayMessage(getString(R.string.succesful_deleted));
            MainActivity.startActivity(EditSkillActivity.this);
        }

        @Override
        public void onError(BusinessException e) {
            Toast.makeText(EditSkillActivity.this,
                    getString(R.string.error_deleting, mNameEdiText.getText()), Toast.LENGTH_SHORT).show();
        }
    }
}
