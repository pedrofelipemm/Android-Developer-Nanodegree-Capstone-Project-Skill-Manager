package study.pmoreira.skillmanager.ui.collaborator;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.business.CollaboratorBusiness;
import study.pmoreira.skillmanager.business.CollaboratorSkillBusiness;
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

public class EditCollaboratorActivity extends BaseActivity implements OnRequestPermissionsResultCallback {

    private static final String TAG = EditCollaboratorActivity.class.getName();

    private static final int PICK_IMG_REQUEST = 1;

    private static final String STATE_IS_EDITING = "STATE_IS_EDITING";
    private static final String STATE_IMG_REF = "STATE_IMG_REF";

    private static final int NAME_AUTOCOMPLETE_THRESHOLD = 3;
    private static final int PERMISSION_REQUEST_CONTACT = 777;

    private CollaboratorBusiness mCollaboratorBusiness = new CollaboratorBusiness();
    private CollaboratorSkillBusiness mCollaboratorSkillBusiness = new CollaboratorSkillBusiness();

    @BindView(R.id.collab_name_edittext)
    AutoCompleteTextView mNameAutoCompleteTextView;

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

        if (getIntent().hasExtra(EXTRA_COLLABORATOR)) {
            Collaborator collaborator = getIntent().getParcelableExtra(EXTRA_COLLABORATOR);

            setTitle(collaborator.getName());
            fillFields(collaborator);
            mIsEditing = true;
        }

        askForContactPermission();
    }

    private void setupViews() {
        findViewById(R.id.parent).requestFocus();

        mNameAutoCompleteTextView.setThreshold(NAME_AUTOCOMPLETE_THRESHOLD);

        mBirthDateEditText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(new OnDatePickerChangeListener())
                        .setDoneText(getString(R.string.yes_caps))
                        .setCancelText(getString(R.string.no_caps))
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
        mNameAutoCompleteTextView.setText(collab.getName());
        mRoleEditText.setText(collab.getRole());
        mEmailEditText.setText(collab.getEmail());
        mPhoneEditText.setText(collab.getPhone());
        mBirthDateEditText.setText(DateUtils.format(new Date(collab.getBirthDate())));
        mPhotoUrl = collab.getPictureUrl();
        loadCollaboratorPicture();
        loadCollaboratorSkills(collab);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            if (permissionReadContactsGranted(requestCode, permissions[i], grantResults[i])) {
                new FindContactsAsyncTask().execute();
                break;
            }
        }
    }

    private boolean permissionReadContactsGranted(int requestCode, String permission, int grantResult) {
        return PERMISSION_REQUEST_CONTACT == requestCode &&
                permission.equals(Manifest.permission.READ_CONTACTS) &&
                grantResult == PackageManager.PERMISSION_GRANTED;
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

                Intent intent = new Intent(this, CollaboratorActivity.class);
                intent.putExtra(EXTRA_COLLABORATOR, newCollaborator());
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
                .setMessage(getString(R.string.do_you_want_to_delete, mNameAutoCompleteTextView.getText()))
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
        Date birthDate;
        try {
            birthDate = DateUtils.parse(mBirthDateEditText.getText().toString());
        } catch (ParseException e) {
            Log.d(TAG, "newCollaborator: Failed to parse date");
            Toast.makeText(this, getString(R.string.confirm_birthdate), Toast.LENGTH_SHORT).show();
            birthDate = new Date();
        }

        Collaborator collab = new Collaborator(
                mNameAutoCompleteTextView.getText().toString(),
                birthDate.getTime(),
                mRoleEditText.getText().toString(),
                mEmailEditText.getText().toString(),
                mPhoneEditText.getText().toString(),
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

    void loadCollaboratorPicture() {
        if (TextUtils.isEmpty(mPhotoUrl)) return;
        Glide.with(EditCollaboratorActivity.this)
                .load(mPhotoUrl)
                .apply(new RequestOptions().error(getDrawable(R.drawable.collaborator_placeholder)))
                .into(mImageView);
    }

    private void loadCollaboratorSkills(Collaborator collab) {
        mCollaboratorSkillBusiness.findCollaboratorSkillsName(collab.getId(),
                new OperationListener<List<String>>() {
                    @Override
                    public void onSuccess(List<String> results) {
                        if (results == null || results.isEmpty()) {
//TODO
//                            mChipView.setVisibility(View.GONE);
                        } else {
//                            mChipView.setChipList(StringChip.toChipList(results));
                        }
                        hideProgressbar(mProgressBar);
                    }
                });
    }

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

        @Override
        public void onError(BusinessException e) {
            displayMessage(e.getMessage());
            hideProgressbar(mProgressBar);
        }
    }

    public void askForContactPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                new AlertDialog.Builder(this)
                        .setTitle(getString(R.string.ask_permision_read_contacts_title))
                        .setMessage(getString(R.string.ask_permision_read_contacts_message))
                        .setPositiveButton(android.R.string.ok, null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                ActivityCompat.requestPermissions(EditCollaboratorActivity.this,
                                        new String[]{Manifest.permission.READ_CONTACTS},
                                        PERMISSION_REQUEST_CONTACT);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(EditCollaboratorActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        PERMISSION_REQUEST_CONTACT);
            }
        } else {
            new FindContactsAsyncTask().execute();
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
                mNameAutoCompleteTextView.setError(getString(R.string.name_cannot_be_empty));
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
                    getString(R.string.error_deleting, mNameAutoCompleteTextView.getText()), Toast.LENGTH_SHORT).show();
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

    private class FindContactsAsyncTask extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> contacts = new ArrayList<>();
            ContentResolver contentResolver = getContentResolver();
            Cursor cursor = contentResolver.query(Contacts.CONTENT_URI, null, null, null, null);

            if (cursor == null) {
                return contacts;
            }

            while (cursor.moveToNext()) {
                contacts.add(cursor.getString(cursor.getColumnIndex(Contacts.DISPLAY_NAME)));
            }

            cursor.close();
            return contacts;
        }

        @Override
        protected void onPostExecute(List<String> contacts) {
            mNameAutoCompleteTextView.setAdapter(
                    new ArrayAdapter<>(EditCollaboratorActivity.this, android.R.layout.select_dialog_item, contacts)
            );
        }
    }
}
