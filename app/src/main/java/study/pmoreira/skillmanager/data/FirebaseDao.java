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
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import study.pmoreira.skillmanager.infrastructure.OperationListener;
import study.pmoreira.skillmanager.infrastructure.exception.BusinessException;

class FirebaseDao {

    private static FirebaseDatabase database;

    private FirebaseDao() {
    }

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

    static <T> void findAllListener(final Class<T> clazz, final String refPath, final OperationListener<List<T>>
            listener) {
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

    static void uploadImage(final Uri data, final String refPath, final OperationListener<String> listener) {
        FirebaseStorage.getInstance().getReference(refPath)
                .child(String.valueOf(new Date().getTime()))
                .putFile(data)
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

    static void deleteImage(String url) {
        FirebaseStorage.getInstance().getReferenceFromUrl(url).delete();
    }

}
