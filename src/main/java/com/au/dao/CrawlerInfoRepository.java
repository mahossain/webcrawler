package com.au.dao;

import com.au.model.CrawlerInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CrawlerInfoRepository extends CrudRepository<CrawlerInfo, String> {
    Optional<CrawlerInfo> findByUrl(String url);
}
