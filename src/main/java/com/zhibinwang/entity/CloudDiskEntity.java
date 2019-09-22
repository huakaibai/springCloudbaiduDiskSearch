package com.zhibinwang.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@Document(indexName = "clouddisk", type = "disk")
public class CloudDiskEntity {
	/**
	 * { "name": "2018史上最全SpringBoot", "source": "百度云盘", "describe":
	 * "2018史上最全的SpringBoot课程", "shartime": "2018-10-10", "browsetimes": 10,
	 * "filesize": 4.35, "sharpeople": "余胜军644", "collectiontime": "2018-11-24",
	 * "addres": "address"
	 * 
	 * }
	 * 
	 */
	@Id
	@JSONField(serialize = false)
	private String id;
	// 名称
	private String name;
	// 来源
	private String source;
	// 描述
	private String describe;
	// 分享时间
	private Date shartime;
	// 浏览次数
	private Long browsetimes;
	// 文件大小
	private Double filesize;
	// 分享人
	private String sharpeople;
	// 收录时间
	private String collectiontime;
	// 地址
	private String baiduaddres;

}
