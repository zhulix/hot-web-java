package com.hotlist.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ScoreSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsVariant;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.hotlist.common.dto.SearchDto;
import com.hotlist.common.vo.SearchResultVo;
import com.hotlist.config.ElasticSearchConfig;
import com.hotlist.entity.HotResourceESModel;
import com.hotlist.service.SearchService;
import com.hotlist.utils.ESPageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    public void setClient(ElasticsearchClient client) {
        this.client = client;
    }
    private ElasticsearchClient client;

    @Override
    public ESPageUtil searchResult(SearchDto searchDto) throws IOException {
        SearchRequest request = buildSearchRequest(searchDto);
        SearchResponse<HotResourceESModel> result = client.search(request, HotResourceESModel.class);

        HitsMetadata<HotResourceESModel> hitsMatadata = result.hits();
        List<Hit<HotResourceESModel>> hits = hitsMatadata.hits();

        List<SearchResultVo> res = hits.stream().map(hit -> {
            Map<String, List<String>> highlight = hit.highlight();
            SearchResultVo resultVo = new SearchResultVo();
            assert hit.source() != null;
            BeanUtils.copyProperties(hit.source(), resultVo);
            resultVo.setTitle(highlight.get("title").get(0));
//            if (StringUtils.hasText(hit.source().getSource()))
//                resultVo.setResource(JSON.parseObject(hit.source().getSource()));
//            Double score = hit.score();
            return resultVo;
        }).collect(Collectors.toList());

        assert hitsMatadata.total() != null;
        long value = hitsMatadata.total().value();
        return new ESPageUtil(res, value, ElasticSearchConfig.size, searchDto.getPage());
    }

    private SearchRequest buildSearchRequest(SearchDto searchDto) {
        SearchRequest.Builder builder = new SearchRequest.Builder().index(ElasticSearchConfig.INDEXES);

        if (StringUtils.hasText(searchDto.getWord())) {
            builder.query(query -> query.match(q -> q.field("title").query(searchDto.getWord())));
        }
        searchDto.setPage(Objects.isNull(searchDto.getPage()) || searchDto.getPage() == 0 ? 1 : searchDto.getPage());

        builder.size(ElasticSearchConfig.size).from((searchDto.getPage() - 1 ) * ElasticSearchConfig.size);
        builder.highlight(h -> h
                .fields("title", fn -> fn.preTags("<span style='color: #F56C6C;'>").postTags("</span>"))
        );

        // 时间顺序，相关性顺序。二选一
        if (Objects.nonNull(searchDto.getSort()) && searchDto.getSort().equals("related")) {
            builder.sort(fn -> fn.score(new ScoreSort.Builder().build()));
        } else {
            builder.sort(s -> s.field(f -> f.field("timeStamp").order(SortOrder.Desc)));
        }

        return builder.build();
    }


}
