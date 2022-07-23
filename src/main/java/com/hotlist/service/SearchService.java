package com.hotlist.service;

import com.hotlist.common.dto.SearchDto;
import com.hotlist.utils.ESPageUtil;

import java.io.IOException;

public interface SearchService {
    ESPageUtil searchResult(SearchDto searchDto) throws IOException;
}
