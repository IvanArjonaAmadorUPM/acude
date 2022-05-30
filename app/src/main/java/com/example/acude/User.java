package com.example.acude;

public class User {
    public String email;
    public int idRoleSelected;
    public String password;

    public User(){
    }

    public User(String email, String password, int idRoleSelected){
        this.email = email;
        this.password = password;
        this.idRoleSelected = idRoleSelected;
    }

    public int getRole() {
        return idRoleSelected;
    }

    public void setName(int idRoleSelected) {
        this.idRoleSelected = idRoleSelected;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}