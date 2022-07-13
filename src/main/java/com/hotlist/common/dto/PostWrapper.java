package com.hotlist.common.dto;

import com.hotlist.utils.HotUtil;
import lombok.Data;

import java.util.Map;
import java.util.stream.Collectors;

@Data
@Deprecated
public class PostWrapper {

    private PostTestDto postTestDto;

    public PostWrapper(PostTestDto postTestDto) {
        this.postTestDto = postTestDto;
    }

    public String preKey() {
        return HotUtil.stringJoin("hot", postTestDto.getRdsName(), postTestDto.getHotRankList(), HotUtil.categoryJoin(postTestDto.getHotRankListCategory()), "pc");
    }

    public String getUrlKey() {
        return HotUtil.stringJoin(preKey(), "url");
    }

    public String getUrl() {
        return postTestDto.getUrl();
    }

    public String getArrayKey() {
        return HotUtil.stringJoin(preKey(), "arrayKey");
    }

    public String getHeaderKey() {
        return HotUtil.stringJoin(preKey(), "headers");
    }

    public String getParseContentKey() {
        return HotUtil.stringJoin(preKey(), "parseContent");
    }

    public String getParseTypeKey() {
        return HotUtil.stringJoin(preKey(), "parseType");
    }

    public Map<String, String> getHeader() {
        return postTestDto.getUserAgent().stream().collect(Collectors.toMap(PostTestDto.Header::getK, PostTestDto.Header::getV));
    }

    // TODO 临时使用

    public Map<String, String> getParseContent() {
        return postTestDto.getParseContent().stream().collect(Collectors.toMap(PostTestDto.Format::getTag, PostTestDto.Format::getValue));
    }

    public String getArrayParseKey() {
        return postTestDto.getKeyObj();
    }

    public String getParseType() {
        return postTestDto.getParseType();
    }
}
