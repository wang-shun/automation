package com.gome.test.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.gome.test.mock.common.Command;

public class Person implements java.io.Serializable {
    private static final long serialVersionUID = -3622186980485257834L;

    private String id;
    private String name;
    private String cardNo;
    private String age;
    private String sex;
    private String job;

    public Person() {
        this.id = Command.GUID();
        this.name = Command.getRandomString(4 + "");
        this.cardNo = Command.getRandomInt(5 + "");
        this.age = Command.getRandomInt(2 + "");
        this.sex = this.randomSex();
        this.job = this.randomJob();
    }

    public Person(String name) {
        this.id = Command.GUID();
        this.name = name;
        this.cardNo = Command.getRandomInt(5 + "");
        this.age = Command.getRandomInt(2 + "");
        this.sex = this.randomSex();
        this.job = this.randomJob();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNo() {
        return this.cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getAge() {
        return this.age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getJob() {
        return this.job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    private String randomSex() {
        if (Math.random() > 0.5) {
            return "nan";
        }
        return "nv";
    }

    private String randomJob() {
        Map<Integer, String> jobMap = new HashMap<Integer, String>();
        jobMap.put(1, "www");
        jobMap.put(2, "ww");
        jobMap.put(3, "ee");
        jobMap.put(4, "rr");
        jobMap.put(5, "tt");
        return jobMap.get(new Random().nextInt(4) + 1);
    }

    //重写Object类的toString()方法
    @Override
    public String toString() {
        return "id=" + this.id + ", name=" + this.name + ", cardNo=" + this.cardNo + ", age=" + this.age + ", sex=" + this.sex + ", job=" + this.job;
    }
}
