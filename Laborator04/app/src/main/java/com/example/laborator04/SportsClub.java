package com.example.laborator04;

import android.os.Parcel;
import android.os.Parcelable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SportsClub implements Parcelable {
    private String name;
    private int memberCount;
    private boolean isPrivate;
    private SportType sportType;
    private float rating;
    private boolean hasEquipment;
    private String category;
    private boolean isOpen;
    private Date establishmentDate;

    public SportsClub(String name, int memberCount, boolean isPrivate, SportType sportType, float rating, boolean hasEquipment, String category, boolean isOpen, Date establishmentDate) {
        this.name = name;
        this.memberCount = memberCount;
        this.isPrivate = isPrivate;
        this.sportType = sportType;
        this.rating = rating;
        this.hasEquipment = hasEquipment;
        this.category = category;
        this.isOpen = isOpen;
        this.establishmentDate = establishmentDate;
    }

    protected SportsClub(Parcel in) {
        name = in.readString();
        memberCount = in.readInt();
        isPrivate = in.readByte() != 0;
        sportType = SportType.valueOf(in.readString());
        rating = in.readFloat();
        hasEquipment = in.readByte() != 0;
        category = in.readString();
        isOpen = in.readByte() != 0;
        establishmentDate = new Date(in.readLong());
    }

    public static final Creator<SportsClub> CREATOR = new Creator<SportsClub>() {
        @Override
        public SportsClub createFromParcel(Parcel in) {
            return new SportsClub(in);
        }

        @Override
        public SportsClub[] newArray(int size) {
            return new SportsClub[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(memberCount);
        dest.writeByte((byte) (isPrivate ? 1 : 0));
        dest.writeString(sportType.name());
        dest.writeFloat(rating);
        dest.writeByte((byte) (hasEquipment ? 1 : 0));
        dest.writeString(category);
        dest.writeByte((byte) (isOpen ? 1 : 0));
        dest.writeLong(establishmentDate != null ? establishmentDate.getTime() : -1);
    }

    public String getName() { return name; }
    public int getMemberCount() { return memberCount; }
    public boolean isPrivate() { return isPrivate; }
    public SportType getSportType() { return sportType; }
    public float getRating() { return rating; }
    public boolean hasEquipment() { return hasEquipment; }
    public String getCategory() { return category; }
    public boolean isOpen() { return isOpen; }
    public Date getEstablishmentDate() { return establishmentDate; }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateStr = establishmentDate != null ? sdf.format(establishmentDate) : "N/A";

        return "Name: " + name + "\n" +
                "Members: " + memberCount + "\n" +
                "Private: " + (isPrivate ? "Yes" : "No") + "\n" +
                "Sport: " + sportType + "\n" +
                "Rating: " + rating + "\n" +
                "Equipment: " + (hasEquipment ? "Yes" : "No") + "\n" +
                "Category: " + category + "\n" +
                "Status: " + (isOpen ? "Open" : "Closed") + "\n" +
                "Date: " + dateStr;
    }
}
