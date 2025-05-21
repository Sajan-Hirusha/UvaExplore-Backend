package com.Resource.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextDto {

    private String first_two_text;
    private String all_text;
    private String summarize_text;
    private String status;
    private String request_id;
}
