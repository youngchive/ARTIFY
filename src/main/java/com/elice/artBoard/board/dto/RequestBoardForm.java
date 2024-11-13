package com.elice.artBoard.board.dto;

import com.elice.artBoard.board.validator.AttachFileCheck;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestBoardForm {

    @Size(max = 100)
    @NotBlank
    private String title;

    @Size(max = 200)
    @NotBlank
    private String description;

    @AttachFileCheck
    private MultipartFile image;
}
