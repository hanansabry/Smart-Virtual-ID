package com.android.smartvirtualid.data.models;

public class Person {

    public static final String PERSON_ROLE = "person";

    private String id;
    private String name;
    private String gender;
    private String birthDate;
    private String nationality;
    private String civilId;
    private String martialStatus;
    private String residency;
    private String email;
    private String password;
    private String phone;
    private String job;
    private String disability;
    private String photoUrl;
    private String qrCodeUrl;

    public Person() {
    }

    public Person(String name, String gender, String birthDate, String nationality, String civilId, String martialStatus, String residency, String email, String password, String phone, String job, String disability, String photoUrl) {
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.nationality = nationality;
        this.civilId = civilId;
        this.martialStatus = martialStatus;
        this.residency = residency;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.job = job;
        this.disability = disability;
        this.photoUrl = photoUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getCivilId() {
        return civilId;
    }

    public void setCivilId(String civilId) {
        this.civilId = civilId;
    }

    public String getMartialStatus() {
        return martialStatus;
    }

    public void setMartialStatus(String martialStatus) {
        this.martialStatus = martialStatus;
    }

    public String getResidency() {
        return residency;
    }

    public void setResidency(String residency) {
        this.residency = residency;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getDisability() {
        return disability;
    }

    public void setDisability(String disability) {
        this.disability = disability;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}
