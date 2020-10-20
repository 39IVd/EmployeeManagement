package models;

public class User {
    int type; // 0 : admin(관리자), 1 : employee(직원)
    String phoneNum, name, team;
    public User(int type, String phoneNum, String name, String team) {
        this.type = type;
        this.phoneNum = phoneNum;
        this.name = name;
        this.team = team;
    }
    public int getType() {
        return this.type;
    }
    public String getPhoneNum() {
        return this.phoneNum;
    }

    public String getName() {
        return name;
    }
    public String getTeam() {
        return team;
    }
}
