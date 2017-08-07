package study.pmoreira.skillmanager.ui.collaborator;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment.OnDateSetListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.plumillonforge.android.chipview.ChipView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.business.CollaboratorBusiness;
import study.pmoreira.skillmanager.business.SkillBusiness;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.infrastructure.exception.BusinessException;
import study.pmoreira.skillmanager.infrastructure.exception.ValidateException;
import study.pmoreira.skillmanager.model.Collaborator;
import study.pmoreira.skillmanager.model.Skill;
import study.pmoreira.skillmanager.ui.BaseActivity;
import study.pmoreira.skillmanager.ui.customview.StringChip;
import study.pmoreira.skillmanager.ui.main.MainActivity;
import study.pmoreira.skillmanager.utils.DateUtils;
import study.pmoreira.skillmanager.utils.FileUtils;

import static study.pmoreira.skillmanager.ui.collaborator.CollaboratorActivity.EXTRA_COLLABORATOR;
import static study.pmoreira.skillmanager.ui.collaborator.CollaboratorActivity.EXTRA_COLLABORATOR_SKILLS;
import static study.pmoreira.skillmanager.ui.collaborator.CollaboratorActivity.STATE_COLLABORATOR;

public class EditCollaboratorActivity extends BaseActivity implements OnRequestPermissionsResultCallback {

    private static final String TAG = EditCollaboratorActivity.class.getName();

    private static final int PICK_IMG_REQUEST = 1;

    private static final String STATE_IS_EDITING = "STATE_IS_EDITING";
    private static final String STATE_IMG_REF = "STATE_IMG_REF";
    private static final String STATE_IS_INVAlID_DATE = "STATE_IS_INVAlID_DATE";
    private static final String STATE_CHIP_LIST = "STATE_CHIP_LIST";
    private static final String STATE_LOAD_SKILL_FROM_EXTRA = "STATE_LOAD_SKILL_FROM_EXTRA";

    private static final int NAME_AUTOCOMPLETE_THRESHOLD = 3;
    private static final int PERMISSION_REQUEST_CONTACT = 777;
    private static final Long INVALID_DATE = 0L;

    @BindView(R.id.collab_name_edittext)
    AutoCompleteTextView mNameAutoCompleteTextView;

    @BindView(R.id.collab_birthdate_edittext)
    EditText mBirthDateEditText;

    @BindView(R.id.collab_role_edittext)
    EditText mRoleEditText;

    @BindView(R.id.collab_email_edittext)
    EditText mEmailEditText;

    @BindView(R.id.collab_phone_edittext)
    EditText mPhoneEditText;

    @BindView(R.id.collab_imageview)
    ImageView mImageView;

    @BindView(R.id.chip_view)
    ChipView mChipView;

    @BindView(R.id.progressbar)
    ProgressBar mProgressBar;

    AlertDialog mSkillDialog;

    private String mImgRef;

    private boolean mIsEditing;

    boolean mLoadSkillsFromExtra = true;

    /**
     * Cannot send null to parcel
     */
    private boolean mIsInvalidDate;

    String mPhotoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_collaborator);

        ButterKnife.bind(this);

        setTitle(getString(R.string.new_collaborator));

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

        setupSkillDialog();

        Glide.with(this)
                .load(R.drawable.collaborator_placeholder)
                .apply(new RequestOptions().error(getDrawable(R.drawable.collaborator_placeholder)))
                .into(mImageView);
    }

    private AlertDialog createSkillDialog(View view, final ListView listView) {
        return new Builder(this)
                .setTitle(getString(R.string.new_skills))
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        List<String> results = new ArrayList<>();

                        SparseBooleanArray checkedItemPositions = listView.getCheckedItemPositions();
                        for (int index = 0; index < listView.getCount(); index++) {
                            if (checkedItemPositions.get(index)) {
                                results.add((String) listView.getItemAtPosition(index));
                            }
                        }

                        StringChip.setChipList(mChipView, results);
                    }
                })
                .setNegativeButton(R.string.dismiss, null)
                .create();
    }

    @SuppressLint("InflateParams")
    private void setupSkillDialog() {
        View addSkillView = getLayoutInflater().inflate(R.layout.add_skill, null);
        final ListView addSkillListView = addSkillView.findViewById(R.id.add_skill_listview);
        final View emptyView = addSkillView.findViewById(R.id.empty_view);

        mSkillDialog = createSkillDialog(addSkillView, addSkillListView);

        SkillBusiness.findAllSingleEvent(new OperationListener<List<Skill>>() {
            @Override
            public void onSuccess(List<Skill> skills) {
                List<String> results = new ArrayList<>();
                for (Skill skill : skills) {
                    results.add(skill.getName());
                }

                Collections.sort(results);
                if (addSkillListView != null) {
                    addSkillListView.setAdapter(new ArrayAdapter<>(
                            EditCollaboratorActivity.this,
                            android.R.layout.simple_list_item_multiple_choice,
                            results));

                    loadCollaboratorSkills(getCollaboratorSkills(), addSkillListView, results);
                }

                emptyView.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private List<String> getCollaboratorSkills() {
        List<String> collabSkills;
        if (mLoadSkillsFromExtra && getIntent().hasExtra(EXTRA_COLLABORATOR_SKILLS)) {
            collabSkills = getIntent().getStringArrayListExtra(EXTRA_COLLABORATOR_SKILLS);
            mLoadSkillsFromExtra = false;
        } else {
            collabSkills = StringChip.getChipList(mChipView);
        }
        return collabSkills;
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
        outState.putBoolean(STATE_IS_INVAlID_DATE, mIsInvalidDate);
        outState.putStringArrayList(STATE_CHIP_LIST, StringChip.getChipList(mChipView));
        outState.putBoolean(STATE_LOAD_SKILL_FROM_EXTRA, mLoadSkillsFromExtra);

        if (mImgRef != null) {
            cancelUpload();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mIsInvalidDate = savedInstanceState.getBoolean(STATE_IS_INVAlID_DATE);
        fillFields((Collaborator) savedInstanceState.getParcelable(STATE_COLLABORATOR));
        mIsEditing = savedInstanceState.getBoolean(STATE_IS_EDITING);
        mImgRef = savedInstanceState.getString(STATE_IMG_REF);
        mLoadSkillsFromExtra = savedInstanceState.getBoolean(STATE_LOAD_SKILL_FROM_EXTRA);
        StringChip.setChipList(mChipView, savedInstanceState.getStringArrayList(STATE_CHIP_LIST));
    }

    private void fillFields(Collaborator collab) {
        mNameAutoCompleteTextView.setText(collab.getName());
        mRoleEditText.setText(collab.getRole());
        mEmailEditText.setText(collab.getEmail());
        mPhoneEditText.setText(collab.getPhone());
        mBirthDateEditText.setText(mIsInvalidDate ? null : DateUtils.format(new Date(collab.getBirthdate())));
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
                mImgRef = CollaboratorBusiness.uploadImage(imgBytes, new OnPictureUpload());
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

        Collaborator collab = newCollaborator();
        if (mIsInvalidDate) {
            collab.setBirthdate(null);
        }

        CollaboratorBusiness.saveOrUpdate(collab, getCollaboratorSkills(), new OnCollaboratorSave());
    }

    private void delete() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.attention))
                .setMessage(getString(R.string.do_you_want_to_delete, mNameAutoCompleteTextView.getText()))
                .setPositiveButton(R.string.yes_caps,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CollaboratorBusiness.delete(getCollaboratorId(), new OnCollaboratorDelete());
                            }
                        })
                .setNegativeButton(R.string.no_caps, null)
                .show();
    }

    private Collaborator newCollaborator() {
        Long birthdate = getBirthdate();

        Collaborator collab = new Collaborator(
                mNameAutoCompleteTextView.getText().toString(),
                birthdate,
                mRoleEditText.getText().toString(),
                mEmailEditText.getText().toString(),
                mPhoneEditText.getText().toString(),
                mPhotoUrl);

        collab.setId(getCollaboratorId());

        return collab;
    }

    private Long getBirthdate() {
        Long birthdate;
        try {
            birthdate = DateUtils.parse(mBirthDateEditText.getText().toString()).getTime();
            mIsInvalidDate = false;
        } catch (ParseException e) {
            Log.d(TAG, "newCollaborator: Failed to parse date");
            birthdate = INVALID_DATE;
            mIsInvalidDate = true;
        }

        return birthdate;
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

    private void loadCollaboratorSkills(List<String> collabSkills, final ListView addSkillListView,
                                        List<String> skillsName) {
        List<String> skillToChip = new ArrayList<>();
        for (String collabSkill : collabSkills) {
            for (int i = 0; i < skillsName.size(); i++) {
                if (collabSkill.equals(skillsName.get(i))) {
                    skillToChip.add(collabSkill);
                    addSkillListView.setItemChecked(i, true);
                    break;
                }
            }
        }
        StringChip.setChipList(mChipView, skillToChip);
    }

    public void addSkill(View view) {
        mSkillDialog.show();
    }

    private class OnPictureUpload extends OperationListener<String> {

        @Override
        public void onSuccess(String result) {
            if (!TextUtils.isEmpty(mPhotoUrl)) {
                CollaboratorBusiness.deleteImage(mPhotoUrl);
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
            if (CollaboratorBusiness.INVALID_COLLABORATOR_NAME == e.getCode()) {
                mNameAutoCompleteTextView.setError(getString(R.string.name_cannot_be_empty));
                mNameAutoCompleteTextView.requestFocus();
            }
            if (CollaboratorBusiness.INVALID_COLLABORATOR_BIRTHDATE == e.getCode()) {
                mBirthDateEditText.setError(getString(R.string.birthdate_cannot_be_empty));
            }
            if (CollaboratorBusiness.INVALID_COLLABORATOR_ROLE == e.getCode()) {
                mRoleEditText.setError(getString(R.string.role_cannot_be_empty));
                mRoleEditText.requestFocus();
            }
            if (CollaboratorBusiness.INVALID_COLLABORATOR_EMAIL == e.getCode()) {
                mEmailEditText.setError(getString(R.string.email_cannot_be_empty));
                mEmailEditText.requestFocus();
            }
            if (CollaboratorBusiness.INVALID_COLLABORATOR_PHONE == e.getCode()) {
                mPhoneEditText.setError(getString(R.string.phone_cannot_be_empty));
                mPhoneEditText.requestFocus();
            }
            if (CollaboratorBusiness.INVALID_COLLABORATOR_PICTURE == e.getCode()) {
                displayMessage(getString(R.string.picture_cannot_be_empty));
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
        startActivity(context, null, null, null);
    }

    public static void startActivity(Context context, Collaborator collaborator, List<String> collabSkills,
                                     @Nullable ActivityOptionsCompat options) {
        Intent intent = new Intent(context, EditCollaboratorActivity.class);

        if (collaborator != null) {
            intent.putExtra(EXTRA_COLLABORATOR, collaborator);
        }

        if (collabSkills != null) {
            intent.putStringArrayListExtra(EXTRA_COLLABORATOR_SKILLS, new ArrayList<>(collabSkills));
        }

        if (options != null) {
            context.startActivity(intent, options.toBundle());
        } else {
            context.startActivity(intent);
        }
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
