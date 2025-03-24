package com.example.parking_hub.mapper;

import com.example.parking_hub.model.Post;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PostMapper {
    // 모든 게시글 조회
    List<Post> selectAllPosts();

    // 게시글 페이징 조회
    List<Post> selectPostsWithPaging(@Param("offset") int offset, @Param("limit") int limit);

    // 특정 게시글 조회
    Post selectPostById(@Param("id") int id);

    // 특정 사용자의 게시글 조회
    List<Post> selectPostsByUserId(@Param("userId") int userId);

    // 게시글 제목 검색
    List<Post> searchPostsByTitle(@Param("keyword") String keyword);

    // 게시글 등록
    int insertPost(Post post);

    // 게시글 수정
    int updatePost(Post post);

    // 게시글 조회수 증가
    int increaseViewCount(@Param("id") int id);

    // 게시글 삭제
    int deletePost(@Param("id") int id);
}
