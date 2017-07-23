package study.pmoreira.skillmanager.data;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.infrastructure.exception.BusinessException;

class FirebaseDao {

    private static final String TAG = FirebaseDao.class.getName();

    private static final int OPERATION_CANCELLED = -1;

    private static FirebaseDatabase database;

    private FirebaseDao() {}

    static FirebaseDatabase getDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance();
            database.setPersistenceEnabled(true);
        }
        return database;
    }

    static <T> void findAll(final Class<T> clazz, final String refPath, final OperationListener<List<T>> listener) {
        getDatabase().getReference(refPath)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<T> results = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            results.add(snapshot.getValue(clazz));
                        }
                        listener.onSuccess(results);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    static <T> void findAllListener(final Class<T> clazz, final String refPath,
                                    final OperationListener<List<T>> listener) {
        getDatabase().getReference(refPath)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<T> results = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            results.add(snapshot.getValue(clazz));
                        }
                        Log.d("PEDRO", String.valueOf(results.size()));
                        listener.onSuccess(results);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
    }

    static <T> void save(final T model, final String refPath, final OperationListener<T> listener) {
        DatabaseReference dbRef = getDatabase().getReference(refPath).push();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listener.onSuccess((T) dataSnapshot.getValue(model.getClass()));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                listener.onError(new BusinessException(error.getMessage(), error.getCode()));
            }
        });

        dbRef.setValue(model);
    }

    static void delete() {
        //TODO
    }

    static String uploadImage(Uri data, String refPath, OperationListener<String> listener) {
        StorageReference ref = FirebaseStorage.getInstance()
                .getReference(refPath)
                .child(String.valueOf(new Date().getTime()));

        addUploadListeners(ref.putFile(data), listener);

        return ref.toString();
    }

    private static void addUploadListeners(final UploadTask task, final OperationListener<String> listener) {
        task.addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
            @Override
            public void onSuccess(TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                if (downloadUrl != null) {
                    Log.d(TAG, downloadUrl.toString());
                    listener.onSuccess(downloadUrl.toString());
                }
            }
        }).addOnProgressListener(new OnProgressListener<TaskSnapshot>() {
            @Override
            public void onProgress(TaskSnapshot task) {
                int progress = (int) ((100.0 * task.getBytesTransferred()) / task.getTotalByteCount());
                Log.d(TAG, "Uploading.. " + progress + "%");
                listener.onProgress(progress);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (task.isCanceled()) {
                    listener.onError(new BusinessException(null, OPERATION_CANCELLED));
                } else {
                    Log.e(TAG, "onFailure: ", e);
                    listener.onError(new BusinessException(e.getMessage(), e));
                }
            }
        });
    }

    static void deleteImage(String url) {
        FirebaseStorage.getInstance().getReferenceFromUrl(url).delete();
    }

}
