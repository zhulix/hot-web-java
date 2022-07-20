package com.hotlist.controller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.hotlist.common.R;
import com.hotlist.entity.HotResourceESModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/s")
public class SearchTestController {

    @Resource
    ElasticsearchClient client;

    @GetMapping("/search")
    public R search(@RequestParam("word") String word) throws IOException {

        SearchRequest.Builder builder = new SearchRequest.Builder();
        builder.index("hot-list-dev0");
        MatchQuery titleQuery = MatchQuery.of(m -> m.field("title").query(word));
        builder.query(query -> query.match(titleQuery));

//        Highlight.Builder highlight = new Highlight.Builder();
//        builder.highlight(h -> h.fields("title", fn -> fn.field("2")).preTags("<").postTags(">"));

//        System.out.println(builder.build().analyzer());
        SearchResponse<HotResourceESModel> result = client.search(builder.build(), HotResourceESModel.class);
        List<HotResourceESModel> res = new ArrayList<>();
        List<Hit<HotResourceESModel>> hits = result.hits().hits();
        for (Hit<HotResourceESModel> hit : hits) {
            res.add(hit.source());
        }
//        SearchResultVo resultVo = new SearchResultVo();


        return R.ok().put("data", res);
    }


}
