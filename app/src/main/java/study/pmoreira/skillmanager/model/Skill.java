package study.pmoreira.skillmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Skill extends Model implements Parcelable {

    public static final String JSON_NAME = "name";
    public static final String JSON_DESCRIPTION = "description";
    public static final String JSON_LEARN_MORE_URL = "learnMoreUrl";
    public static final String JSON_PICTURE_URL = "pictureUrl";

    private String name;
    private String description;
    private String learnMoreUrl;
    private String pictureUrl;

    public Skill() {}

    public Skill(String name, String description, String learnMoreUrl, String pictureUrl) {
        this.name = name;
        this.description = description;
        this.learnMoreUrl = learnMoreUrl;
        this.pictureUrl = pictureUrl;
    }

    private Skill(Parcel in) {
        setId(in.readString());
        name = in.readString();
        description = in.readString();
        learnMoreUrl = in.readString();
        pictureUrl = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLearnMoreUrl() {
        return learnMoreUrl;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(learnMoreUrl);
        dest.writeString(pictureUrl);
    }

    static final Parcelable.Creator<Skill> CREATOR = new Parcelable.Creator<Skill>() {

        public Skill createFromParcel(Parcel in) {
            return new Skill(in);
        }

        public Skill[] newArray(int size) {
            return new Skill[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Skill skill = (Skill) o;

        return name.equals(skill.name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
