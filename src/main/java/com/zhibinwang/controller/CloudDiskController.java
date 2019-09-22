package com.zhibinwang.controller;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhibinwang.entity.CloudDiskEntity;
import com.zhibinwang.page.MyPage;
import com.zhibinwang.repostitroy.CloudDiskDao;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.search.MatchQuery;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsMapper;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;

/**
 * @author 花开
 * @create 2019-09-07 10:05
 * @desc
 **/
@Controller
public class CloudDiskController {


    @Autowired
    private CloudDiskDao cloudDiskDao;


    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @GetMapping("findById/{id}")
    public Optional<CloudDiskEntity> findById(@PathVariable("id") String id){
        Optional<CloudDiskEntity> byId = cloudDiskDao.findById(id);
        return byId;
    }

    /**
     *
     * @param keyword
     * @param pageable
     * @return
     *
     * @PageableDefault(page = 0,size = 2,value = 2)
     * 这里value 和size 的意思都是一样的都是只每页的个数
     */
    @RequestMapping("/search")
    @ResponseBody
    public MyPage<CloudDiskEntity> search(String keyword, @PageableDefault(page = 0,value = 3) Pageable pageable){

        // 组装查询参数
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(keyword)){
            MatchQueryBuilder name = matchQuery("name", keyword);
            boolQuery.must(name);
        }

        //进行分页查询
        Page<CloudDiskEntity> page = cloudDiskDao.search(boolQuery, pageable);




        System.out.println("条目总数="+page.getTotalElements());

        System.out.println("总页数="+page.getTotalPages());
        System.out.println("每页个数="+pageable.getPageSize());

        return new MyPage(page,pageable.getPageSize());



    }

    /**
     * 高亮显示
     * @param keyword
     * @param pageable
     * @return
     */

    @SuppressWarnings("deprecation")
    @RequestMapping("/highLightSearch")
    public String highLightSearch(String keyword, @PageableDefault(page = 0,value = 1) Pageable pageable, HttpServletRequest request){

        long l = System.currentTimeMillis();

        String preTag = "<font color='#dd4b39'>";//google的色值
        String postTag = "</font>";

        SearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(matchQuery("name",keyword))
                .withHighlightFields(new HighlightBuilder.Field("name").preTags(preTag).postTags(postTag)).build();

        searchQuery.setPageable(pageable);


        final AggregatedPage<CloudDiskEntity> cloudDiskEntities = elasticsearchTemplate.queryForPage(searchQuery, CloudDiskEntity.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                List<CloudDiskEntity> list = new ArrayList<>();
                SearchHits hits = searchResponse.getHits();
                ObjectMapper objectMapper = new ObjectMapper();

                for (SearchHit searchHit:hits
                     ) {
                    if (searchResponse.getHits().getHits().length < 0)
                        return null;
                    String sourceAsString = searchHit.getSourceAsString();
                    CloudDiskEntity cloudDiskEntity1 = JSONObject.parseObject(sourceAsString,CloudDiskEntity.class);
                    HighlightField name = searchHit.getHighlightFields().get("name");
                    if (name != null){
                        System.out.println(name.fragments());
                        cloudDiskEntity1.setName(name.fragments()[0].toString());
                    }
                    cloudDiskEntity1.setId(searchHit.getId());
                    list.add(cloudDiskEntity1);
                }
                if (list.size() > 0)
                    return  new AggregatedPageImpl<>((List<T>) list);
                return null;
            }
        });
        System.out.println(cloudDiskEntities.getTotalPages());
        request.setAttribute("time",(System.currentTimeMillis()-l));
        request.setAttribute("total",cloudDiskEntities.getTotalElements());
        request.setAttribute("page",cloudDiskEntities);
        request.setAttribute("keyword", keyword);
        request.setAttribute("totalPage", cloudDiskEntities.getTotalPages());
        int totalPage = (int) ((cloudDiskEntities.getTotalElements() - 1) / pageable.getPageSize() + 1);
        System.out.println(totalPage);
        request.setAttribute("totalPage", totalPage);
        System.out.println(cloudDiskEntities.getTotalElements());
        return "search";

    }
}
