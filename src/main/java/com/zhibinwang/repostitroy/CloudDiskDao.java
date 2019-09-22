package com.zhibinwang.repostitroy;

import com.zhibinwang.entity.CloudDiskEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author 花开
 * @create 2019-09-07 10:04
 * @desc
 **/
public interface CloudDiskDao extends ElasticsearchRepository<CloudDiskEntity,String> {
}
