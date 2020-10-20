package models;

public class Employee {
    String phoneNum, name, team;
    public Employee(String phoneNum,String name,  String team) {
        this.phoneNum = phoneNum;
        this.name = name;
        this.team = team;
    }
    public String getName() {
        return this.name;
    }
    public String getPhoneNum() {
        return this.phoneNum;
    }
    public String getTeam() {
        return this.team;
    }
}
