package com.example.ayomide.atsnote.Model;

public class Pupil {
    private String name, image, age, grade, address, phone, gName, gEmail, gOfficeAddress, categoryId, entryCode, reportPdf, billPdf;

    public Pupil() {
    }

    public Pupil(String name, String image, String age, String grade, String address, String phone, String gName, String gEmail, String gOfficeAddress, String categoryId, String entryCode, String reportPdf, String billPdf) {
        this.name = name;
        this.image = image;
        this.age = age;
        this.grade = grade;
        this.address = address;
        this.phone = phone;
        this.gName = gName;
        this.gEmail = gEmail;
        this.gOfficeAddress = gOfficeAddress;
        this.categoryId = categoryId;
        this.entryCode = entryCode;
        this.reportPdf = reportPdf;
        this.billPdf = billPdf;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getgName() {
        return gName;
    }

    public void setgName(String gName) {
        this.gName = gName;
    }

    public String getgEmail() {
        return gEmail;
    }

    public void setgEmail(String gEmail) {
        this.gEmail = gEmail;
    }

    public String getgOfficeAddress() {
        return gOfficeAddress;
    }

    public void setgOfficeAddress(String gOfficeAddress) {
        this.gOfficeAddress = gOfficeAddress;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getEntryCode() {
        return entryCode;
    }

    public void setEntryCode(String entryCode) {
        this.entryCode = entryCode;
    }

    public String getReportPdf() {
        return reportPdf;
    }

    public void setReportPdf(String reportPdf) {
        this.reportPdf = reportPdf;
    }

    public String getBillPdf() {
        return billPdf;
    }

    public void setBillPdf(String billPdf) {
        this.billPdf = billPdf;
    }
}
