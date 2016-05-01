package cn.edu.tjut.ecggraduationproject.model;

/**
 * Created by Administrator on 2016/3/23 0023.
 */
public class MyTaskParams {
    long userid;
    String userinfogsonstring;

    public MyTaskParams(long userid, String userinfogsonstring) {
        this.userid = userid;
        this.userinfogsonstring = userinfogsonstring;
    }
    public long getUserid() {
        return userid;
    }

    public String getUserinfogsonstring() {
        return userinfogsonstring;
    }
}
