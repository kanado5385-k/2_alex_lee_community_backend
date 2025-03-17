package org.community.backend.member;

public class MemberProfileImage {
    private Integer id;
    private Integer memberId;
    private String imageUrl;

    public MemberProfileImage(Integer memberId, String imageUrl) {
        this.memberId = memberId;
        this.imageUrl = imageUrl;
    }

    public Integer getId() {
        return id;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
