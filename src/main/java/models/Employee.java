package models;

import java.util.List;

public class Employee {
    String phoneNum, name, team;
    int holiday_total, not_workhour_total;
    List<Object> workhour_list;
    public Employee(String phoneNum,String name, String team, int holiday_total, int not_workhour_total, List workhour_list) {
        this.phoneNum = phoneNum;
        this.name = name;
        this.team = team;
        this.holiday_total = holiday_total;
        this.not_workhour_total = not_workhour_total;
        this.workhour_list = workhour_list;
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

    public int getHoliday_total() {
        return holiday_total;
    }

    public int getNot_workhour_total() {
        return not_workhour_total;
    }

    public List<Object> getWorkhour_list() {
        return workhour_list;
    }

    public void setHoliday_total(int holiday_total) {
        this.holiday_total = holiday_total;
    }

    public void setNot_workhour_total(int not_workhour_total) {
        this.not_workhour_total = not_workhour_total;
    }

    public void setWorkhour_list(List<Object> workhour_list) {
        this.workhour_list = workhour_list;
    }
}
