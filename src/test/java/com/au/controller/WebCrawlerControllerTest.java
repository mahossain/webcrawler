package com.au.controller;

import com.au.dao.CrawlerInfoRepository;
import com.au.model.CrawlerInfo;
import com.au.service.CrawlerServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(WebCrawlerController.class)
public class WebCrawlerControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CrawlerServiceImpl crawlerService;

    @MockBean
    private CrawlerInfoRepository crawlerInfoRepository;

    @Test
    public void givenURL_thenReturnWebcrawlerInfoJsonArray() throws Exception {
        CrawlerInfo crawlerInfo = new CrawlerInfo();
        crawlerInfo.setId(UUID.randomUUID().toString());
        crawlerInfo.setTitle("Home Page");
        crawlerInfo.setUrl("http://google.com");
        given(crawlerService.isValidURL(anyString())).willReturn(true);
        given(crawlerInfoRepository.findByUrl(anyString())).willReturn(Optional.of(crawlerInfo));
        mvc.perform(get("/crawler/rest/api/2?url=http://google.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}