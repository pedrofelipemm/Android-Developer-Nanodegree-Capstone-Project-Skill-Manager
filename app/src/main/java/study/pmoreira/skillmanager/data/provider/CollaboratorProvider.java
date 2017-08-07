package study.pmoreira.skillmanager.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import study.pmoreira.skillmanager.R;
import study.pmoreira.skillmanager.data.DbHelper;
import study.pmoreira.skillmanager.data.SkillManagerContract;
import study.pmoreira.skillmanager.data.SkillManagerContract.CollaboratorsEntry;

public class CollaboratorProvider extends ContentProvider {

    private static final String TAG = CollaboratorProvider.class.getName();

    private static final int COD_COLLABORATORS = 100;

    private static final long DATABASE_ERROR = -1;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private Context mContext;
    private DbHelper mDbHelper;

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = SkillManagerContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, SkillManagerContract.PATH_COLLABORATORS, COD_COLLABORATORS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        mDbHelper = new DbHelper(mContext);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case COD_COLLABORATORS:
                cursor = db.query(CollaboratorsEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(mContext.getString(R.string.error_unknown_uri, uri));
        }

        cursor.setNotificationUri(mContext.getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COD_COLLABORATORS:
                return insertItem(uri, values);
            default:
                throw new IllegalArgumentException(
                        mContext.getString(R.string.error_provider_operation_not_supported, "Insert", uri));
        }
    }

    private Uri insertItem(@NonNull Uri uri, ContentValues values) {
        validateItem(values);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long rowId = db.insertWithOnConflict(CollaboratorsEntry.TABLE_NAME, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);

        if (rowId == DATABASE_ERROR) {
            Log.e(TAG, mContext.getString(R.string.error_insertion_failed, uri));
            return null;
        }

        mContext.getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, rowId);
    }

    private void validateItem(ContentValues values) {
        String name = values.getAsString(CollaboratorsEntry.COLUMN_NAME);
        if (StringUtils.isBlank(name.trim())) {
            throw new IllegalArgumentException(
                    mContext.getString(R.string.field_required, CollaboratorsEntry.COLUMN_NAME));
        }

        Long birthdate = values.getAsLong(CollaboratorsEntry.COLUMN_BIRTHDATE);
        if (birthdate == null || birthdate < 0) {
            throw new IllegalArgumentException(
                    mContext.getString(R.string.field_required, CollaboratorsEntry.COLUMN_BIRTHDATE));
        }

        String phone = values.getAsString(CollaboratorsEntry.COLUMN_PHONE);
        if (StringUtils.isBlank(phone.trim())) {
            throw new IllegalArgumentException(
                    mContext.getString(R.string.field_required, CollaboratorsEntry.COLUMN_PHONE));
        }

        String email = values.getAsString(CollaboratorsEntry.COLUMN_EMAIL);
        if (StringUtils.isBlank(email.trim())) {
            throw new IllegalArgumentException(
                    mContext.getString(R.string.field_required, CollaboratorsEntry.COLUMN_EMAIL));
        }

        String role = values.getAsString(CollaboratorsEntry.COLUMN_ROLE);
        if (StringUtils.isBlank(role.trim())) {
            throw new IllegalArgumentException(
                    mContext.getString(R.string.field_required, CollaboratorsEntry.COLUMN_ROLE));
        }

        String pictureUrl = values.getAsString(CollaboratorsEntry.COLUMN_PICTURE_URL);
        if (StringUtils.isBlank(pictureUrl.trim())) {
            throw new IllegalArgumentException(
                    mContext.getString(R.string.field_required, CollaboratorsEntry.COLUMN_PICTURE_URL));
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s,
                      @Nullable String[] strings) {
        throw new IllegalArgumentException(mContext.getString(R.string.error_unknown_uri, uri));
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case COD_COLLABORATORS:
                numRowsDeleted = db.delete(CollaboratorsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(mContext.getString(R.string.error_unknown_uri, uri));
        }

        if (numRowsDeleted != 0) {
            mContext.getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case COD_COLLABORATORS:
                return CollaboratorsEntry.CONTENT_LIST_TYPE;
            default:
                throw new IllegalArgumentException(mContext.getString(R.string.error_unknown_uri, uri));
        }
    }
}
