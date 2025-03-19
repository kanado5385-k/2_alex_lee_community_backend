package org.community.backend.service;

import jakarta.transaction.Transactional;
import org.community.backend.domain.entity.Post;
import org.community.backend.domain.entity.PostImage;
import org.community.backend.dto.request.post.PostCreateRequestDTO;
import org.community.backend.dto.response.post.PostCreateResponseDTO;
import org.community.backend.repository.JpaPostImageRepository;
import org.community.backend.repository.JpaPostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private final JpaPostRepository jpaPostRepository;
    private final JpaPostImageRepository jpaPostImageRepository;

    public PostService(JpaPostRepository jpaPostRepository, JpaPostImageRepository jpaPostImageRepository) {
        this.jpaPostRepository = jpaPostRepository;
        this.jpaPostImageRepository = jpaPostImageRepository;
    }

    @Transactional
    public ResponseEntity<? super PostCreateResponseDTO> createPost(PostCreateRequestDTO postCreateRequestDTO) {
        try {
            Post newPost = new Post(postCreateRequestDTO.getUser_id(), postCreateRequestDTO.getPost_title(), postCreateRequestDTO.getPost_content());
            jpaPostRepository.save(newPost);

            if (postCreateRequestDTO.getPost_image() != null && !postCreateRequestDTO.getPost_image().isEmpty()) {
                PostImage postImage = new PostImage(newPost, postCreateRequestDTO.getPost_image());
                jpaPostImageRepository.save(postImage);
            }

            return PostCreateResponseDTO.success();
        } catch (Exception e) {
            return PostCreateResponseDTO.databaseError();
        }
    }
}
