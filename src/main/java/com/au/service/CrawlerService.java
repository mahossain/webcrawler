package com.au.service;


import com.au.model.CrawlerInfo;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

public interface CrawlerService {
    boolean isValidURL(final String URL) throws IOException;
    Optional<Document> findHmlDocument(String URL, String correlationID);
    Set<String> findLinks(Document document);
    String findTitle(Document document);
    Optional<CrawlerInfo> createCrawler(final String url, String correlationID);
}
