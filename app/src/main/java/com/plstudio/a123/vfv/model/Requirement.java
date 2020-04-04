package com.plstudio.a123.vfv.model;

public class Requirement {


    private String sex, group, name, a_12, a_13, a_14, a_15, a_16, a_17, img;
    private String current_a, status;
    private String[] requirement;

    public Requirement(String s){
        if(s != null)
        requirement = s.split("\\s+");

        if(requirement.length > 9) {
            sex = requirement[0];
            group = requirement[1];
            name = requirement[2];
            a_12 = requirement[3];
            a_13 = requirement[4];
            a_14 = requirement[5];
            a_15 = requirement[6];
            a_16 = requirement[7];
            a_17 = requirement[8];
            img = requirement[9];
        }
    }

    public Requirement(String name, String current_a, String status, String img){
        this.name = name.replace('_',' ');
        this.current_a = current_a;
        this.status = status;
        this.img = img;

    }
    public Requirement(String sex, String group, String name, String current_a, String a_12,
                       String a_13,  String a_14,  String a_15,  String a_16,  String a_17, String img, String status){

        this.sex = sex;
        this.group = group;
        this.name = name;
        this.current_a = current_a;
        this.a_12 = a_12;
        this.a_13 = a_13;
        this.a_14 = a_14;
        this.a_15 = a_15;
        this.a_16 = a_16;
        this.a_17 = a_17;
        this.status = status;
        this.img = img;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getCurrent_a() {
        return current_a;
    }
    public String getSex() {
        return sex;
    }

    public String getGroup() {
        return group;
    }

    public String getName() {
        return name;
    }

    public String getA_12() {
        return a_12;
    }

    public String getA_13() {
        return a_13;
    }

    public String getA_14() {
        return a_14;
    }

    public String getA_15() {
        return a_15;
    }

    public String getA_16() {
        return a_16;
    }

    public String getA_17() {
        return a_17;
    }

    public String getImg() { return img; }

    public void setName(String name) {
        this.name = name;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setCurrent_a(String current_a) {
        this.current_a = current_a;
    }


    public String toString(){
        return sex + group + name + a_12 + a_13 + a_14 + a_15 + a_16 + a_17 + img;
    }
}
