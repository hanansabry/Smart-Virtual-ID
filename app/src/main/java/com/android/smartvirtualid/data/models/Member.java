package com.android.smartvirtualid.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Member implements Parcelable {

    private String id;
    private String userId;
    private String civilId;
    private String organizationId;
    private String organizationName;
    private String status;
    private String description;
    private String photoIdUrl;
    private String qrCode;

    public Member() {
    }

    public Member(String userId, String civilId, String organizationId, String organizationName, String status, String description, String photoIdUrl) {
        this.userId = userId;
        this.civilId = civilId;
        this.organizationId = organizationId;
        this.organizationName = organizationName;
        this.status = status;
        this.description = description;
        this.photoIdUrl = photoIdUrl;
    }

    protected Member(Parcel in) {
        id = in.readString();
        userId = in.readString();
        civilId = in.readString();
        organizationId = in.readString();
        status = in.readString();
        description = in.readString();
        photoIdUrl = in.readString();
        qrCode = in.readString();
    }

    public static final Creator<Member> CREATOR = new Creator<Member>() {
        @Override
        public Member createFromParcel(Parcel in) {
            return new Member(in);
        }

        @Override
        public Member[] newArray(int size) {
            return new Member[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCivilId() {
        return civilId;
    }

    public void setCivilId(String civilId) {
        this.civilId = civilId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPhotoIdUrl() {
        return photoIdUrl;
    }

    public void setPhotoIdUrl(String photoIdUrl) {
        this.photoIdUrl = photoIdUrl;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(userId);
        dest.writeString(civilId);
        dest.writeString(organizationId);
        dest.writeString(organizationName);
        dest.writeString(status);
        dest.writeString(description);
        dest.writeString(photoIdUrl);
        dest.writeString(qrCode);
    }
}
