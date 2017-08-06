package study.pmoreira.skillmanager.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Arrays;
import java.util.List;

public class SkillManagerContract {

    public static final String CONTENT_AUTHORITY = "study.pmoreira.skillmanager";

    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_COLLABORATORS = "collaborators";

    public static final class CollaboratorsEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_COLLABORATORS).build();

        public static final String TABLE_NAME = "collaborator";

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_COLLABORATORS;

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BIRTHDATE = "birthdate";
        public static final String COLUMN_ROLE = "role";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PHONE = "phone";
        public static final String COLUMN_PICTURE_URL = "picture_url";

        public static final List<String> ALL_COLUMNS = Arrays.asList(COLUMN_ID, COLUMN_NAME, COLUMN_BIRTHDATE,
                COLUMN_ROLE, COLUMN_EMAIL, COLUMN_PHONE, COLUMN_PICTURE_URL);

        public static final int ALL_COLUMNS_SIZE = ALL_COLUMNS.size();

        public static final String ORDER_BY_NAME = COLUMN_NAME + " ASC";
    }
}