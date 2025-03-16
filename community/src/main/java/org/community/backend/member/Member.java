package org.community.backend.member;

public class Member {
    private int id;
    private String email;
    private String password;
    private String nickname;


    public Member(int id, String email, String password, String nickname) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
