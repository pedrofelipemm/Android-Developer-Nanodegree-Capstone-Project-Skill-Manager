package study.pmoreira.skillmanager.ui.collaborator;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment.OnDateSetListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.business.CollaboratorBusiness;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.infrastructure.exception.BusinessException;
import study.pmoreira.skillmanager.infrastructure.exception.ValidateException;
import study.pmoreira.skillmanager.model.Collaborator;
import study.pmoreira.skillmanager.ui.BaseActivity;
import study.pmoreira.skillmanager.ui.main.MainActivity;
import study.pmoreira.skillmanager.utils.DateUtils;
import study.pmoreira.skillmanager.utils.FileUtils;

import static study.pmoreira.skillmanager.ui.collaborator.CollaboratorActivity.EXTRA_COLLABORATOR;
import static study.pmoreira.skillmanager.ui.collaborator.CollaboratorActivity.STATE_COLLABORATOR;

public class EditCollaboratorActivity extends BaseActivity {

    private static final String TAG = EditCollaboratorActivity.class.getName();

    private static final int PICK_IMG_REQUEST = 1;

    private static final String STATE_IS_EDITING = "STATE_IS_EDITING";
    private static final String STATE_IMG_REF = "STATE_IMG_REF";

    private CollaboratorBusiness mCollaboratorBusiness = new CollaboratorBusiness();

    @BindView(R.id.collab_name_edittext)
    EditText mNameEdiText;

    @BindView(R.id.collab_birth_date_edittext)
    EditText mBirthDateEditText;

    @BindView(R.id.collab_role_edittext)
    EditText mRoleEditText;

    @BindView(R.id.collab_email_edittext)
    EditText mEmailEditText;

    @BindView(R.id.collab_phone_edittext)
    EditText mPhoneEditText;

    @BindView(R.id.collab_imageview)
    ImageView mImageView;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    private String mImgRef;

    private boolean mIsEditing;

    String mPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_collaborator);

        ButterKnife.bind(this);

        setupViews();
    }

    private void setupViews() {
        findViewById(R.id.parent).requestFocus();

        mBirthDateEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new OnDatePickerChangeListener())
//                        .setFirstDayOfWeek(Calendar.SUNDAY)
//                        .setPreselectedDate(towDaysAgo.getYear(), towDaysAgo.getMonthOfYear() - 1, towDaysAgo
// .getDayOfMonth())
//                        .setDateRange(minDate, null)
                        .setDoneText("Yay")
                        .setCancelText("Nop")
//                        .setThemeDark()
                        .show(getSupportFragmentManager(), "bunda");
            }
        });

        Glide.with(this)
                .load(R.drawable.collaborator_placeholder)
                .apply(new RequestOptions().error(getDrawable(R.drawable.collaborator_placeholder)))
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
        outState.putParcelable(STATE_COLLABORATOR, newCollaborator());
        outState.putBoolean(STATE_IS_EDITING, mIsEditing);

        if (mImgRef != null) {
            cancelUpload();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fillFields((Collaborator) savedInstanceState.getParcelable(STATE_COLLABORATOR));
        mIsEditing = savedInstanceState.getBoolean(STATE_IS_EDITING);
        mImgRef = savedInstanceState.getString(STATE_IMG_REF);
    }

    private void fillFields(Collaborator collab) {
        //TODO
        mNameEdiText.setText(collab.getName());
//        mDescriptionEdiText.setText(collab.getDescription());
//        mLearnMoreEditText.setText(collab.getLearnMoreUrl());
        mPhotoUrl = collab.getPictureUrl();
        loadCollaboratorPicture();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == PICK_IMG_REQUEST && resultCode == RESULT_OK && intent != null && intent.getData() != null) {
            displayProgressbar(mProgressBar);
            try {
                byte[] imgBytes = FileUtils.getBytes(getContentResolver().openInputStream(intent.getData()));
                mImgRef = mCollaboratorBusiness.uploadImage(imgBytes, new OnPictureUpload());
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
                NavUtils.navigateUpFromSameTask(this);
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

    private void save() {
        if (isLoading(mProgressBar)) {
            Toast.makeText(this, getString(R.string.wait_img_upload), Toast.LENGTH_SHORT).show();
            return;
        }

        mCollaboratorBusiness.saveOrUpdate(newCollaborator(), new OnCollaboratorSave());
    }

    private void delete() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.attention))
                .setMessage(getString(R.string.do_you_want_to_delete, mNameEdiText.getText()))
                .setPositiveButton(R.string.yes_caps,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCollaboratorBusiness.delete(getCollaboratorId(), new OnCollaboratorDelete());
                            }
                        })
                .setNegativeButton(R.string.no_caps, null)
                .create()
                .show();
    }

    private Collaborator newCollaborator() {
        Collaborator collab = new Collaborator(
                mNameEdiText.getText().toString(),
                1,
                "role",
                "email",
                "phone",
                mPhotoUrl);

        collab.setId(getCollaboratorId());

        return collab;
    }

    String getCollaboratorId() {
        String collabId = null;
        if (getIntent().hasExtra(EXTRA_COLLABORATOR)) {
            collabId = ((Collaborator) getIntent().getParcelableExtra(EXTRA_COLLABORATOR)).getId();
        }
        return collabId;
    }

    @SuppressWarnings("ConstantConditions")
    void loadCollaboratorPicture() {
        if (TextUtils.isEmpty(mPhotoUrl)) return;
        Glide.with(EditCollaboratorActivity.this)
                .load(mPhotoUrl)
                .apply(new RequestOptions().error(getDrawable(R.drawable.collaborator_placeholder)))
                .into(mImageView);
    }

    //TODO: progressbar

    private class OnPictureUpload extends OperationListener<String> {

        @Override
        public void onSuccess(String result) {
            if (!TextUtils.isEmpty(mPhotoUrl)) {
                mCollaboratorBusiness.deleteImage(mPhotoUrl);
            }

            mPhotoUrl = result;
            loadCollaboratorPicture();
            hideProgressbar(mProgressBar);
        }

// TODO: ???
//        @Override
//        public void onProgress(int progress) {
//            mProgressBar.setProgress(progress);
//        }

        @Override
        public void onError(BusinessException e) {
            displayMessage(e.getMessage());
            hideProgressbar(mProgressBar);
        }
    }

    private class OnCollaboratorSave extends OperationListener<Collaborator> {
        @Override
        public void onSuccess(Collaborator collab) {
            displayMessage(getString(R.string.collaborator_successfully_saved));
            CollaboratorActivity.startActivity(EditCollaboratorActivity.this, collab, Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
        }

        @Override
        public void onValidationError(ValidateException e) {
            //TODO
            if (CollaboratorBusiness.INVALID_COLLABORATOR_NAME == e.getCode()) {
                mNameEdiText.setError(getString(R.string.name_cannot_be_empty));
            }
        }
    }

    private class OnCollaboratorDelete extends OperationListener<String> {
        @Override
        public void onSuccess(String result) {
            displayMessage(getString(R.string.succesful_deleted));
            MainActivity.startActivity(EditCollaboratorActivity.this);
        }

        @Override
        public void onError(BusinessException e) {
            Toast.makeText(EditCollaboratorActivity.this,
                    getString(R.string.error_deleting, mNameEdiText.getText()), Toast.LENGTH_SHORT).show();
        }
    }

    public static void startActivity(Context context) {
        startActivity(context, null);
    }

    public static void startActivity(Context context, Collaborator collaborator) {
        Intent intent = new Intent(context, EditCollaboratorActivity.class);

        if (collaborator != null) {
            intent.putExtra(EXTRA_COLLABORATOR, collaborator);
        }

        context.startActivity(intent);
    }

    private class OnDatePickerChangeListener implements OnDateSetListener {
        @Override
        public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
            mBirthDateEditText.setText(formatDate(dayOfMonth, monthOfYear, year));
        }

        private String formatDate(int dayOfMonth, int monthOfYear, int year) {
            return DateUtils.format(dayOfMonth, monthOfYear, year);
        }
    }
}
