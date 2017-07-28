package study.pmoreira.skillmanager.model;

import android.os.Parcel;
import android.os.Parcelable;

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

    @SuppressWarnings("unused")
    public Collaborator() {
    }

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
    }

    static final Creator<Collaborator> CREATOR = new Creator<Collaborator>() {

        public Collaborator createFromParcel(Parcel in) {
            return new Collaborator(in);
        }

        public Collaborator[] newArray(int size) {
            return new Collaborator[size];
        }
    };

}
