package com.example.ayomide.atsnote.Model;

public class User {

    private String name;
    private String password;
    private String phone;
    private String isStaff;
    private String securityCode;

    public User() {
    }

    public User(String name, String password, String phone, String securityCode) {
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.securityCode = securityCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }

    public String getSecurityCode() {
        return securityCode;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }
}
