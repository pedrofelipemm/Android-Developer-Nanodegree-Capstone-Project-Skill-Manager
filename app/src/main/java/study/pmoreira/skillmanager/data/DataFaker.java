package study.pmoreira.skillmanager.data;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import org.apache.commons.lang3.mutable.MutableInt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import study.pmoreira.skillmanager.business.CollaboratorBusiness;
import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.model.Collaborator;
import study.pmoreira.skillmanager.model.Skill;

import static java.io.File.separator;
import static study.pmoreira.skillmanager.data.FirebaseDao.getDatabase;

public class DataFaker {

    private static final String TAG = DataFaker.class.getName();

    private static final String FOLDER_FAKE_DATA = "fakeData";
    private static final String FOLDER_SKILLS = "skills";
    private static final String FOLDER_COLLABORATORS = "collaborators";

    private static final String FOLDER_FAKE_DATA_SKILLS = FOLDER_FAKE_DATA + separator + FOLDER_SKILLS;
    private static final String FOLDER_FAKE_DATA_COLLABORATOR = FOLDER_FAKE_DATA + separator + FOLDER_COLLABORATORS;

    private static final int MAX_NUMBER_SKILLS = 4;

    private DataFaker() {}

    /**
     * ###TEST PURPOSE ONLY ###
     * <br/><br/>
     * <p>
     * There is a bug related to deleting firebase storage files,
     * it's not going to be fixed now since it's not core functionality.
     * <br/>
     * This code is going to be moved to backed in the future.
     */
    public static void insertFakeData(Context context) {
        dropDatabase(context);
    }

    private static void insertData(Context context) {
        final List<Collaborator> collabs = getCollaborators(context);
        final Map<String, Skill> skills = getSkills(context);

        insertFakeCollaborators(context, collabs);
        insertFakeSkills(context, skills);

        whenCollaboratorsInserted(collabs.size(), new OperationListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                whenSkillsInserted(skills.size(), new OperationListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        insertFakeCollaboratorSkills(collabs, new ArrayList<>(skills.values()));
                    }
                });
            }
        });
    }

    private static void dropDatabase(final Context context) {
        getDatabase().getReference("_STORAGE_REFERENCES")
                .addListenerForSingleValueEvent(new OnDataChange() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        final MutableInt numRemoved = new MutableInt();

                        if (dataSnapshot.getChildrenCount() == 0) {
                            insertData(context);
                        }

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            FirebaseDao.deleteImage(String.valueOf(snapshot.getValue()), new OperationListener<Task<Void>>() {
                                @Override
                                public void onSuccess(Task<Void> result) {
                                    numRemoved.increment();
                                    if (numRemoved.getValue() == dataSnapshot.getChildrenCount()) {
                                        getDatabase().getReference(SkillDao.SKILLS_PATH).removeValue();
                                        getDatabase().getReference(CollaboratorDao.COLLABORATORS_PATH).removeValue();
                                        getDatabase().getReference(CollaboratorSkillDao.COLLABORATOR_SKILLS_PATH)
                                                .removeValue();

                                        insertData(context);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private static void whenCollaboratorsInserted(final int collabsSize, final OperationListener<Void> listener) {
        final DatabaseReference ref = getDatabase().getReference(CollaboratorDao.COLLABORATORS_PATH);
        ref.addValueEventListener(new OnDataChange() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == collabsSize) {
                    ref.removeEventListener(this);
                    listener.onSuccess(null);
                }
            }
        });
    }

    private static void whenSkillsInserted(final int skillsSize, final OperationListener<Void> listener) {
        final DatabaseReference ref = getDatabase().getReference(SkillDao.SKILLS_PATH);
        ref.addValueEventListener(new OnDataChange() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() == skillsSize) {
                    ref.removeEventListener(this);
                    listener.onSuccess(null);
                }
            }
        });
    }

    private static void insertFakeSkills(Context context, final Map<String, Skill> skills) {
        final Map<String, byte[]> images = getBytesFromAssets(context, FOLDER_FAKE_DATA_SKILLS);

        for (final Entry<String, byte[]> img : images.entrySet()) {
            Log.d(TAG, "Upload started: " + img.getKey());

            FirebaseDao.uploadImage(img.getValue(), SkillDao.SKILL_IMAGES_PATH,
                    new OperationListener<String>() {
                        @Override
                        public void onSuccess(String picUrl) {
                            Log.d(TAG, "Upload complete: " + img.getKey());
                            try {
                                Skill skill = skills.get(img.getKey());
                                setField(skill, Skill.JSON_PICTURE_URL, picUrl);
                                FirebaseDao.saveOrUpdate(skill, SkillDao.SKILLS_PATH, new OperationListener<Skill>());
                            } catch (Exception e) {
                                Log.e(TAG, "onSuccess: ", e);
                            }
                        }
                    });
        }
    }

    private static Map<String, Skill> getSkills(Context context) {
        Map<String, Skill> result = new HashMap<>();

        String skillsJson = getJsonFromAssets(context, FOLDER_FAKE_DATA_SKILLS + separator + "skills.json");
        try {
            JSONArray skills = new JSONObject(skillsJson).getJSONArray("skills");
            for (int i = 0; i < skills.length(); i++) {
                JSONObject skillJson = skills.getJSONObject(i);
                result.put(skillJson.getString(Skill.JSON_NAME), new Skill(
                        skillJson.getString(Skill.JSON_NAME),
                        skillJson.getString(Skill.JSON_DESCRIPTION),
                        skillJson.getString(Skill.JSON_LEARN_MORE_URL),
                        ""
                ));
            }
        } catch (JSONException e) {
            Log.e(TAG, "getSkills: ", e);
        }

        return result;
    }

    private static void insertFakeCollaborators(Context context, final List<Collaborator> collabs) {

        final Map<String, byte[]> images = getBytesFromAssets(context, FOLDER_FAKE_DATA_COLLABORATOR);

        for (final Entry<String, byte[]> img : images.entrySet()) {
            Log.d(TAG, "Upload started: " + img.getKey());

            FirebaseDao.uploadImage(img.getValue(), CollaboratorDao.COLLABORATORS_IMAGES_PATH,
                    new OperationListener<String>() {
                        @Override
                        public void onSuccess(String picUrl) {
                            Log.d(TAG, "Upload complete: " + img.getKey());
                            try {
                                for (Collaborator collab : collabs) {
                                    setField(collab, Collaborator.JSON_PICTURE_URL, picUrl);
                                    FirebaseDao.saveOrUpdate(collab, CollaboratorDao.COLLABORATORS_PATH,
                                            new OperationListener<Collaborator>());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "onSuccess: ", e);
                            }
                        }
                    });
        }
    }

    private static List<Collaborator> getCollaborators(Context context) {
        List<Collaborator> result = new ArrayList<>();

        String collabsJson = getJsonFromAssets(context,
                FOLDER_FAKE_DATA_COLLABORATOR + separator + "collaborators.json");

        try {
            JSONArray collabs = new JSONObject(collabsJson).getJSONArray("collaborators");
            for (int i = 0; i < collabs.length(); i++) {
                JSONObject collabJson = collabs.getJSONObject(i);
                result.add(new Collaborator(
                        collabJson.getString(Collaborator.JSON_NAME),
                        collabJson.getLong(Collaborator.JSON_BIRTHDATE),
                        collabJson.getString(Collaborator.JSON_ROLE),
                        collabJson.getString(Collaborator.JSON_EMAIL),
                        collabJson.getString(Collaborator.JSON_PHONE),
                        ""
                ));
            }
        } catch (JSONException e) {
            Log.e(TAG, "getCollaborators: ", e);
        }

        return result;
    }

    private static void insertFakeCollaboratorSkills(List<Collaborator> collabs, List<Skill> skills) {
        Random random = new Random();
        CollaboratorBusiness collaboratorBusiness = new CollaboratorBusiness();
        Set<Skill> uniqueSkills = new HashSet<>();

        for (Collaborator collab : collabs) {
            int skillSize = random.nextInt(MAX_NUMBER_SKILLS);
            for (int i = 0; i < skillSize; i++) {
                uniqueSkills.add(skills.get(random.nextInt(skills.size())));
            }

            collab.setSkills(new ArrayList<>(uniqueSkills));
            collaboratorBusiness.saveOrUpdate(collab, new OperationListener<Collaborator>());
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static Map<String, byte[]> getBytesFromAssets(Context context, String path) {
        Map<String, byte[]> results = new HashMap<>();
        try {
            for (String image : context.getAssets().list(path)) {
                if (image.lastIndexOf(".png") == -1) continue;

                try (InputStream is = context.getAssets().open(path + separator + image)) {
                    byte[] imageBytes = new byte[is.available()];
                    is.read(imageBytes);
                    results.put(image.substring(0, image.indexOf(".png")), imageBytes);
                } catch (IOException e) {
                    Log.e(TAG, "onSuccess: ", e);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "getBytesFromAssets: ", e);
        }

        return results;
    }

    private static String getJsonFromAssets(Context context, String filePath) {
        StringBuilder builder = new StringBuilder();

        try (InputStream in = context.getAssets().open(filePath)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            Log.e(TAG, "getJsonFromAssets: ", e);
        }

        return builder.toString();
    }

    private static <T> void setField(T o, String fieldName, String val)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = o.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(o, val);
    }
}
