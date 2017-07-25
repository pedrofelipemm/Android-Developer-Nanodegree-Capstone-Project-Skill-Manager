package study.pmoreira.skillmanager.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.infrastructure.exception.BusinessException;
import study.pmoreira.skillmanager.model.Skill;

import static study.pmoreira.skillmanager.data.FirebaseDao.getDatabase;

public class DataFaker {

    private static final String TAG = DataFaker.class.getName();

    public static void insertFakeData(Context context) throws UnsupportedEncodingException {

        final DatabaseReference skillsRef = getDatabase().getReference(SkillDao.SKILLS_PATH);
        skillsRef.removeValue();

        final Map<String, Skill> skills = getSkills();
        Map<String, byte[]> images = getBytesFromAssets(context);

        for (final Entry<String, byte[]> img : images.entrySet()) {
            Log.d(TAG, "Upload started: " + img.getKey());

            FirebaseDao.uploadImage(img.getValue(), SkillDao.SKILL_IMAGES_PATH,
                    new OperationListener<String>() {
                        @Override
                        public void onSuccess(String picUrl) {
                            try {
                                Skill skill = skills.get(img.getKey());
                                setField(skill, "pictureUrl", picUrl);
                                FirebaseDao.save(skill, SkillDao.SKILLS_PATH, new OperationListener<Object>());
                                Log.d(TAG, "Upload complete: " + img.getKey());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

//            uploadImage(img.getValue(), "skillImages", img.getKey(),
//                    new OperationListener<String>() {
//                        @Override
//                        public void onSuccess(String picUrl) {
//                            try {
//                                Skill skill = skills.get(img.getKey());
//                                setField(skill, "pictureUrl", picUrl);
//                                skillsRef.push().setValue(skill);
//                                Log.d(TAG, "Upload complete: " + img.getKey());
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
        }
    }

    //TODO; move to assets
    private static Map<String, Skill> getSkills() {
        Map<String, Skill> result = new HashMap<>();

        result.put("Android", new Skill(
                "Android",
                "Android is a mobile operating system developed by Google, based on the Linux kernel and designed " +
                        "primarily for touchscreen mobile devices such as smartphones and tablets.",
                "https://en.wikipedia.org/wiki/Android_(operating_system)",
                ""
        ));

        result.put("Java", new Skill(
                "Java",
                "Java is a general-purpose computer programming language that is concurrent, class-based, " +
                        "object-oriented, and specifically designed to have as few implementation dependencies as " +
                        "possible.",
                "https://en.wikipedia.org/wiki/Java_(programming_language)",
                ""
        ));

        result.put("Ruby", new Skill(
                "Ruby",
                "Ruby is a dynamic, reflective, object-oriented, general-purpose programming language.",
                "https://en.wikipedia.org/wiki/Ruby_(programming_language)",
                ""
        ));

        result.put("Python", new Skill(
                "Python",
                "Python is a widely used high-level programming language for general-purpose programming, created by " +
                        "Guido van Rossum and first released in 1991.",
                "https://en.wikipedia.org/wiki/Python_(programming_language)",
                ""
        ));

        return result;
    }

    private static void uploadImage(final byte[] data, final String refPath, final String fileName,
                                    final OperationListener<String> listener) {

        FirebaseStorage.getInstance().getReference(refPath)
                .child(fileName)
                .putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
                    @Override
                    public void onSuccess(TaskSnapshot taskSnapshot) {
                        if (taskSnapshot.getDownloadUrl() != null) {
                            listener.onSuccess(taskSnapshot.getDownloadUrl().toString());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onError(new BusinessException(e.getMessage(), e));
                    }
                });
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static Map<String, byte[]> getBytesFromAssets(Context context) {
        Map<String, byte[]> results = new HashMap<>();

        try {
            for (String image : context.getAssets().list("fakeData")) {
                try (InputStream is = context.getAssets().open("fakeData/" + image)) {
                    byte[] imageBytes = new byte[is.available()];
                    is.read(imageBytes);
                    results.put(image.substring(0, image.indexOf(".png")), imageBytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    private static <T> void setField(T o, String fieldName, String val)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = o.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(o, val);
    }
}
