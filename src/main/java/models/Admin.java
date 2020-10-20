package models;

public class Admin {
    String phoneNum, name;
    public Admin(String phoneNum, String name) {
        this.phoneNum = phoneNum;
        this.name = name;
    }
    public String getPhoneNum() {
        return this.phoneNum;
    }

    public String getName() {
        return this.name;
    }
}
