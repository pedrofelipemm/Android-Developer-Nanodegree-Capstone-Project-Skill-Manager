package study.pmoreira.skillmanager.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.List;

import study.pmoreira.skillmanager.data.SkillManagerContract.CollaboratorsEntry;

public class Collaborator extends Model implements Parcelable {

    public static final String JSON_NAME = "name";
    public static final String JSON_BIRTH_DATE = "birthDate";
    public static final String JSON_ROLE = "role";
    public static final String JSON_EMAIL = "email";
    public static final String JSON_PHONE = "phone";
    public static final String JSON_PICTURE_URL = "pictureUrl";

    private String name;
    private long birthDate;
    private String role;
    private String email;
    private String phone;
    private String pictureUrl;

    private List<Skill> skills = new ArrayList<>();

    public Collaborator() {}

    public Collaborator(String name, long birthDate, String role, String email, String phone, String pictureUrl) {
        this.name = name;
        this.birthDate = birthDate;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.pictureUrl = pictureUrl;
    }

    private Collaborator(Parcel in) {
        setId(in.readString());
        name = in.readString();
        birthDate = in.readLong();
        role = in.readString();
        email = in.readString();
        phone = in.readString();
        pictureUrl = in.readString();
        skills = in.createTypedArrayList(Skill.CREATOR);
    }

    public Collaborator(Cursor cursor) {
        setId(cursor.getString(cursor.getColumnIndex(CollaboratorsEntry.COLUMN_ID)));
        name = cursor.getString(cursor.getColumnIndex(CollaboratorsEntry.COLUMN_NAME));
        birthDate = cursor.getLong(cursor.getColumnIndex(CollaboratorsEntry.COLUMN_BIRTHD_ATE));
        role = cursor.getString(cursor.getColumnIndex(CollaboratorsEntry.COLUMN_ROLE));
        email = cursor.getString(cursor.getColumnIndex(CollaboratorsEntry.COLUMN_EMAIL));
        phone = cursor.getString(cursor.getColumnIndex(CollaboratorsEntry.COLUMN_PHONE));
        pictureUrl = cursor.getString(cursor.getColumnIndex(CollaboratorsEntry.COLUMN_PICTURE_URL));
    }

    public String getName() {
        return name;
    }

    public long getBirthDate() {
        return birthDate;
    }

    public String getRole() {
        return role;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    @Exclude
    public List<Skill> getSkills() {
        return skills;
    }

    @Exclude
    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public boolean addSkill(Skill skill) {
        return skills.add(skill);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(name);
        dest.writeLong(birthDate);
        dest.writeString(role);
        dest.writeString(email);
        dest.writeString(phone);
        dest.writeString(pictureUrl);
        dest.readTypedList(skills, Skill.CREATOR);
    }

    static final Creator<Collaborator> CREATOR = new Creator<Collaborator>() {

        public Collaborator createFromParcel(Parcel in) {
            return new Collaborator(in);
        }

        public Collaborator[] newArray(int size) {
            return new Collaborator[size];
        }
    };

    public ContentValues toContentValue() {
        ContentValues cv = new ContentValues();
        cv.put(CollaboratorsEntry.COLUMN_ID, getId());
        cv.put(CollaboratorsEntry.COLUMN_NAME, name);
        cv.put(CollaboratorsEntry.COLUMN_BIRTHD_ATE, birthDate);
        cv.put(CollaboratorsEntry.COLUMN_ROLE, role);
        cv.put(CollaboratorsEntry.COLUMN_EMAIL, email);
        cv.put(CollaboratorsEntry.COLUMN_PHONE, phone);
        cv.put(CollaboratorsEntry.COLUMN_PICTURE_URL, pictureUrl);

        return cv;
    }
}
