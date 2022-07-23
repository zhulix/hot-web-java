package com.hotlist.common.dto;

import lombok.Data;

/**
 * /s/search?word=hi&pageNum=20&sort=time_asc&t=15987263485324
 */
@Data
public class SearchDto {

    private String word;

    private Integer page;

    private String sort;
}
