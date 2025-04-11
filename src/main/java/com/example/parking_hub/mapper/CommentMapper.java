package com.example.parking_hub.mapper;

import com.example.parking_hub.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentMapper {
    
    /**
     * 게시글 ID로 댓글 목록 조회
     */
    List<Comment> selectCommentsByPostId(@Param("postId") Long postId);
    
    /**
     * 사용자 ID로 댓글 목록 조회
     */
    List<Comment> selectCommentsByUserId(@Param("userId") Long userId);
    
    /**
     * 댓글 ID로 댓글 조회
     */
    Comment selectCommentById(@Param("id") Long id);
    
    /**
     * 댓글 등록
     */
    void insertComment(Comment comment);
    
    /**
     * 댓글 수정
     */
    void updateComment(Comment comment);
    
    /**
     * 댓글 삭제
     */
    void deleteComment(@Param("id") Long id);
    
    /**
     * 게시글의 모든 댓글 삭제
     */
    void deleteAllCommentsByPostId(@Param("postId") Long postId);
} 