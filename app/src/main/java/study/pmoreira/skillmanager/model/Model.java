package study.pmoreira.skillmanager.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.PropertyName;

public class Model {

    public static final String JSON_ID = "_ID";

    private String id;

    @PropertyName("_id")
    public String getId() {
        return id;
    }

    @PropertyName("_id")
    public void setId(String id) {
        this.id = id;
    }

    @Exclude
    public boolean isNew() {
        return id == null;
    }
}
