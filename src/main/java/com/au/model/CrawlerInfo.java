package com.au.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.annotation.Generated;
import java.util.LinkedList;
import java.util.List;

@JsonIgnoreProperties(value = {"id"})
@Document(collection = "crawlerInfo")
public class CrawlerInfo {
    @Id
    private String id;
    private String title="";
    private String url="";
    private List<CrawlerInfo> nodes = new LinkedList<>();
    public CrawlerInfo() {
    }

    public CrawlerInfo(final String url) {
        super();
        this.url = url;
    }

    public void addChild(CrawlerInfo crawlerInfo) {
        if(crawlerInfo!=null){
            this.nodes.add(crawlerInfo);
        }
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<CrawlerInfo> getNodes() {
        return nodes;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
