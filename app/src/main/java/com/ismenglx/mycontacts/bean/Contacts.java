package com.ismenglx.mycontacts.bean;

/**
 * Created by lenovo on 2018/5/13.
 * 联系人属性
 */

public class Contacts {
    //联系人ID
    private int id;
    //联系人姓名
    private String name;
    //头像
    private String photo_uri;
    //手机号
    private String phone_number;
    //其他号码
    private String other_phone;
    //家庭电话
    private String home_number;
    //公司
    private String company;
    //职位
    private String duty;
    //群组
    private String group;
    //邮箱
    private String email;
    //邮编
    private String zipCode;
    //即时通讯号码
    private String im;
    //备注
    private String note;
    //排序字母
    private String sortKey;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getHome_number() {
        return home_number;
    }

    public void setHome_number(String home_number) {
        this.home_number = home_number;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getPhone_number() {
        return phone_number;
    }
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getSortKey() {
        return sortKey;
    }
    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }

    public String getCompany() {
        return company;
    }
    public void setCompany(String company) {
        this.company = company;
    }

    public String getOther_phone() {
        return other_phone;
    }
    public void setOther_phone(String other_phone) {
        this.other_phone = other_phone;
    }

    public String getDuty() {
        return duty;
    }
    public void setDuty(String duty) {
        this.duty = duty;
    }

    public String getGroup() {
        return group;
    }
    public void setGroup(String group) {
        this.group = group;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto_uri() {
        return photo_uri;
    }
    public void setPhoto_uri(String photo_uri) {
        this.photo_uri = photo_uri;
    }

    public String getZipCode() {
        return zipCode;
    }
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getIm() {
        return im;
    }
    public void setIm(String im) {
        this.im = im;
    }
}