package com.example.parking_hub.model;

import java.time.LocalDateTime;

/**
 * 주차장 리뷰 정보를 담는 모델 클래스
 */
public class Review {
    
    private Long id;                  // 리뷰 ID (자동 생성)
    private String prkCenterId;       // 주차장 ID
    private String userId;            // 사용자 ID (작성자)
    private String username;          // 사용자 이름 (작성자 이름)
    private int rating;               // 평점 (1-5)
    private String content;           // 리뷰 내용
    private LocalDateTime createdAt;  // 작성일시
    private LocalDateTime updatedAt;  // 수정일시
    
    // 기본 생성자
    public Review() {
    }
    
    // 모든 필드 포함 생성자
    public Review(Long id, String prkCenterId, String userId, String username, int rating, 
                 String content, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.prkCenterId = prkCenterId;
        this.userId = userId;
        this.username = username;
        this.rating = rating;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // 리뷰 생성 시 필요한 필드만 포함한 생성자
    public Review(String prkCenterId, String userId, String username, int rating, String content) {
        this.prkCenterId = prkCenterId;
        this.userId = userId;
        this.username = username;
        this.rating = rating;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getter/Setter 메서드
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getPrkCenterId() {
        return prkCenterId;
    }
    
    public void setPrkCenterId(String prkCenterId) {
        this.prkCenterId = prkCenterId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public int getRating() {
        return rating;
    }
    
    public void setRating(int rating) {
        this.rating = rating;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", prkCenterId='" + prkCenterId + '\'' +
                ", userId='" + userId + '\'' +
                ", username='" + username + '\'' +
                ", rating=" + rating +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
