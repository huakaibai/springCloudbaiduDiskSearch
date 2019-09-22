package com.zhibinwang.page;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;

import java.util.List;

/**
 * @author 花开
 * @create 2019-09-07 10:49
 * @desc
 **/


@Data
public class MyPage<T>  {

    private Page<T> page;

    private  Long totalPage;

    private Integer pageSize;


    public MyPage(Page<T> page,Integer pageSize){
        this.page = page;
        this.pageSize = pageSize;
    }


    public Long getTotalPage(){
       return  ((page.getTotalElements() - 1) / pageSize + 1);
    }


}
