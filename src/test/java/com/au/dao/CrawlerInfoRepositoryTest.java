package com.au.dao;

import com.au.model.CrawlerInfo;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataMongoTest
public class CrawlerInfoRepositoryTest {

    @Autowired
    private CrawlerInfoRepository crawlerInfoRepository;

    @Test
    public void whenFindByUrl_thenReturnCrawlerInfo() {
        CrawlerInfo crawlerInfo = new CrawlerInfo();
        crawlerInfo.setId(UUID.randomUUID().toString());
        crawlerInfo.setTitle("Home Page");
        crawlerInfo.setUrl("http://google.com");
        crawlerInfoRepository.save(crawlerInfo);

        Optional<CrawlerInfo> result = crawlerInfoRepository.findByUrl("http://google.com");
        assertThat(result.isPresent(), Matchers.is(true));
        assertThat(result.get().getTitle(), Matchers.is("Home Page"));
        assertThat(result.get().getUrl(), Matchers.is("http://google.com"));
    }
}