package com.au.controller;

import com.au.dao.CrawlerInfoRepository;
import com.au.model.CrawlerInfo;
import com.au.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.StringUtils.isBlank;


@RestController
@ControllerAdvice
@RequestMapping("/crawler")
public class WebCrawlerController {
    private Map<String, Integer> depthTree = new ConcurrentHashMap<>();
    private final CrawlerService crawlerService;
    private final CrawlerInfoRepository crawlerInfoRepository;

    @Autowired
    public WebCrawlerController(final CrawlerService crawlerService, final CrawlerInfoRepository crawlerInfoRepository) {
        this.crawlerService = crawlerService;
        this.crawlerInfoRepository = crawlerInfoRepository;
    }

    // crawler/rest/api/2?url=http://google.com
    @RequestMapping(value = "rest/api/{limit}")
    public @ResponseBody CrawlerInfo crawl(@PathVariable(value = "limit") int limit,
                                           @RequestParam(value = "url", required = true) String url) throws Exception {

        if (!crawlerService.isValidURL(url)) {
            throw new ConnectException(url + " is not accessible or invalid");
        }

        final String correlationID = UUID.randomUUID().toString();
        crawlerService.initVisitLinks(correlationID);

        Optional<CrawlerInfo> fromDB = crawlerInfoRepository.findByUrl(url);
        if (fromDB.isPresent()) {
            return fromDB.get();
        }

        Optional<CrawlerInfo> crawlerInfoOptional = crawlerService.createCrawler(url, correlationID);
        int currentDepth = 1;
        depthTree.put(correlationID, currentDepth);
        if (crawlerInfoOptional.isPresent()) {
            CrawlerInfo crawlerInfo = crawlerInfoOptional.get();
            if (currentDepth < limit)
                travers(crawlerInfo, limit, correlationID);

            crawlerInfo.setId(correlationID);
            crawlerInfoRepository.save(crawlerInfo);
            reset(correlationID);
            return crawlerInfo;
        }
        return new CrawlerInfo(url);
    }

    private void travers(final CrawlerInfo root, final int limit, final String correlationID) {

        if (root == null || root.getNodes() == null || root.getNodes().size() == 0) {
            return;
        }

        root.getNodes()
                .parallelStream()
                .filter(node -> !isBlank(node.getUrl()))
                .forEach(node -> {
                            depthTree.put(correlationID, depthTree.get(correlationID).intValue() + 1);
                            Optional<CrawlerInfo> childOptional = crawlerService.createCrawler(node.getUrl(), correlationID);

                            childOptional.ifPresent(child -> {
                                node.addChild(child);
                            });
                            Integer depth = depthTree.get(correlationID);
                            if (depth != null && depth <= limit) {
                                travers(node, limit, correlationID);
                            }
                        }
                );
    }

    private void reset(final String correlationID) {
        crawlerService.resetVisitedLinksFor(correlationID);
        depthTree.remove(correlationID);
    }

    @ExceptionHandler(value = {UnknownHostException.class, ConnectException.class})
    @ResponseBody
    public ResponseEntity<String> exceptionHandler(Exception e) {
        return new ResponseEntity<>(e.getMessage() + ".check the URL and Internet Connection", HttpStatus.BAD_REQUEST);
    }
}
