package study.pmoreira.skillmanager.data;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

abstract class OnDataChange implements ValueEventListener {

    @Override
    public abstract void onDataChange(DataSnapshot dataSnapshot);

    @Override
    public void onCancelled(DatabaseError databaseError) {}
}
