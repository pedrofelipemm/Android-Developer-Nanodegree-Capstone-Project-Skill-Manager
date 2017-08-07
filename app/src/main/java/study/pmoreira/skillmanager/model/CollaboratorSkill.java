package study.pmoreira.skillmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CollaboratorSkill extends Model implements Parcelable {

    public static final String JSON_SKILL_ID = "skillId";
    public static final String JSON_COLLABORATOR_ID = "collaboratorId";

    private String collaboratorId;
    private String skillId;
    private String skillName;

    @SuppressWarnings("unused")
    public CollaboratorSkill() {}

    public CollaboratorSkill(String collaboratorId, String skillId, String skillName) {
        this.collaboratorId = collaboratorId;
        this.skillId = skillId;
        this.skillName = skillName;
    }

    private CollaboratorSkill(Parcel in) {
        collaboratorId = in.readString();
        skillId = in.readString();
        skillName = in.readString();
    }

    public String getCollaboratorId() {
        return collaboratorId;
    }

    public String getSkillName() {
        return skillName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(collaboratorId);
        dest.writeString(skillId);
        dest.writeString(skillName);
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
