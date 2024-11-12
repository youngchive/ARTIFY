package com.elice.artBoard.post.service;

import com.elice.artBoard.post.entity.Post;
import com.elice.artBoard.post.entity.PostImage;
import com.elice.artBoard.post.entity.PostPostDto;
import com.elice.artBoard.post.repository.PostImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class PostImageService {
    private final PostImageRepository postImageRepository;

    @Value("${post.file.dir}")
    private String fileDir;

    public PostImage save(PostPostDto postPostDto, Post post) {
        MultipartFile formImage = postPostDto.getImage();

        PostImage image = null;
        if (!formImage.isEmpty()) {  // 이미지가 있을 때만 생성
            String imagePath = getImagePath(formImage.getOriginalFilename());
            saveImage(formImage, imagePath);
            image = PostImage.create(formImage.getOriginalFilename(), imagePath, post);
        }

        // 이미지가 없으면 PostImage를 생성하지 않음
        if (image != null) {
            return postImageRepository.save(image);  // 이미지가 있을 경우만 저장
        } else {
            return null;  // 이미지가 없으면 null 반환
        }
    }

    public void delete(Long postId) {
        Optional<PostImage> optionalPostImage = postImageRepository.findByPostId(postId);

        if (optionalPostImage.isEmpty()) {
            return;
        }

        postImageRepository.delete(optionalPostImage.get());
    }

    public void update(Post post, PostPostDto postPostDto) {

        if (postPostDto.getImage().isEmpty()) {
            return;
        }
        delete(post.getId());
        save(postPostDto, post);
    }

    public PostImage findImageByPostId(Post post) {
        return postImageRepository.findByPostId(post.getId()).orElse(null);
    }

    public PostImage findByImgId(Long postImageId) {
        return postImageRepository.findById(postImageId).get();
    }

    private String getImagePath(String originalFilename) {
        String ext = extractExt(originalFilename);
        return createImagePath(ext);
    }

    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    private String createImagePath(String ext) {
        String uuid = UUID.randomUUID().toString();
        return fileDir + uuid + "." + ext;
    }

    private void saveImage(MultipartFile formImage, String imagePath) {
        try {
            formImage.transferTo(new File(imagePath));
        } catch (IOException e) {
            log.error("이미지 저장 예외 발생 = {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
