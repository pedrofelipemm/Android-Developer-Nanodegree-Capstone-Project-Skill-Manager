package study.pmoreira.skillmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CollaboratorSkill extends Model implements Parcelable {

    private String collaboratorId;
    private String skillId;

    @SuppressWarnings("unused")
    public CollaboratorSkill() {}

    public CollaboratorSkill(String collaboratorId, String skillId) {
        this.collaboratorId = collaboratorId;
        this.skillId = skillId;
    }

    public CollaboratorSkill(Parcel in) {
        collaboratorId = in.readString();
        skillId = in.readString();
    }

    public String getCollaboratorId() {
        return collaboratorId;
    }

    public String getSkillId() {
        return skillId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(collaboratorId);
        dest.writeString(skillId);
    }

    static final Parcelable.Creator<CollaboratorSkill> CREATOR = new Parcelable.Creator<CollaboratorSkill>() {

        public CollaboratorSkill createFromParcel(Parcel in) {
            return new CollaboratorSkill(in);
        }

        public CollaboratorSkill[] newArray(int size) {
            return new CollaboratorSkill[size];
        }
    };
}
