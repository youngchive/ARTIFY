package com.elice.artBoard.board.service;

import com.elice.artBoard.board.domain.Board;
import com.elice.artBoard.board.domain.BoardImage;
import com.elice.artBoard.board.dto.RequestBoardForm;
import com.elice.artBoard.board.repository.BoardImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.elice.artBoard.board.constants.DefaultImgConst.DEFAULT_IMAGE_NAME;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BoardImageService {

    private final BoardImageRepository boardImageRepository;

    @Value("${board.file.dir}")
    private String fileDir;

    public BoardImage save(RequestBoardForm form, Board board) {

        MultipartFile formImage = form.getImage();

        BoardImage image = null;
        if (formImage.isEmpty()) {
            image = BoardImage.createDefaultImage(board);

        } else {
            String imagePath = getImagePath(formImage.getOriginalFilename());
            saveImage(formImage, imagePath);
            image = BoardImage.create(formImage.getOriginalFilename(), imagePath, board);
        }

        return boardImageRepository.save(image);
    }

    public void delete(Long boardId) {

        Optional<BoardImage> optionalBoardImage = boardImageRepository.findByBoardId(boardId);

        if (optionalBoardImage.isEmpty()) {
            return;
        }

        BoardImage boardImage = optionalBoardImage.get();

        //이미지가 기본 이미지가 아닌 경우 저장되어 있는 이미지 삭제
        if (!boardImage.getImageName().equals(DEFAULT_IMAGE_NAME)) {
            File file = new File(boardImage.getImagePath());
            file.delete();
        }
        boardImageRepository.delete(boardImage);
    }

    public void update(Board board, RequestBoardForm form) {

        if (form.getImage().isEmpty()) {
            return;
        }
        delete(board.getId());
        save(form, board);
    }

    public List<BoardImage> findImagesByBoardId(List<Board> boards) {
        return boards.stream()
                .map(board -> findByBordId(board.getId())).toList();
    }

    public BoardImage findByImgId(Long boardImageId) {
        return boardImageRepository.findById(boardImageId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사진이 없습니다"));
    }

    public BoardImage findByBordId(Long bordId) {
        return boardImageRepository.findByBoardId(bordId)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 게시물이 없습니다"));

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
