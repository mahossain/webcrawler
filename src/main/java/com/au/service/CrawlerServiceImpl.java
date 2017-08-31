package com.au.service;

import com.au.model.CrawlerInfo;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

import static com.au.controller.WebCrawlerController.pageTree;

@Service
public class CrawlerServiceImpl implements CrawlerService {
    private static final Logger LOG = Logger.getLogger(CrawlerServiceImpl.class.getName());
    @Value("${web.request.timeout}")
    private int timeout;
    @Value("${web.request.header}")
    private String headerValue;
    @Value("${web.request.userAgent}")
    private String userAgent;

    public CrawlerServiceImpl() {
    }

    @Override
    public boolean isValidURL(final String URL) {
        boolean isValidUrl = false;
        try {
            Connection connection = Jsoup.connect(URL).timeout(timeout);
            Connection.Response response = connection.execute();
            if (response.statusCode() == 200) {
                isValidUrl = true;
            }
        } catch (Exception ex) {
            LOG.warning(ex.getMessage());
        }
        return isValidUrl;
    }

    @Override
    public Optional<Document> findHmlDocument(final String URL, final String correlationID) {
        HashSet<String> pageVisited = pageTree.get(correlationID);
        try {
            if (pageVisited != null && !pageVisited.contains(URL)) {
                LOG.info("visiting: " + URL);
                if (isValidURL(URL)) {
                    final Document document = Jsoup.connect(URL)
                            .header("Content-Type", headerValue)
                            .userAgent(userAgent)
                            .get();
                    pageVisited.add(URL);
                    return Optional.of(document);
                }
            }
        } catch (SocketTimeoutException ste) {
            LOG.warning(ste.getMessage());
        } catch (IOException ex) {
            LOG.warning(ex.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Set<String> findLinks(final Document document) {
        final Set<String> links = new LinkedHashSet<>();
        final Elements linksOnPage = document.select("a[href]");
        linksOnPage.forEach(element -> {
            final String link = element.attr("abs:href");
            links.add(link);
        });
        return links;
    }

    @Override
    public String findTitle(Document document) {
        if (document != null)
            return document.title();
        else return "";
    }

    @Override
    public Optional<CrawlerInfo> createCrawler(final String url, final String correlationID) {
        final CrawlerInfo crawlerInfo = new CrawlerInfo();
        final Optional<Document> documentOptional = findHmlDocument(url, correlationID);

        if (!documentOptional.isPresent()) {
            return Optional.empty();
        }

        final Document document = documentOptional.get();
        crawlerInfo.setTitle(findTitle(document));
        crawlerInfo.setUrl(url);
        HashSet<String> pageVisited = pageTree.get(correlationID);
        Set<String> links = findLinks(document);
        links.parallelStream()
                .filter(link -> !pageVisited.contains(link))
                .forEach(link -> {
                    CrawlerInfo child = new CrawlerInfo(link);
                    Optional<Document> childDocumentOptional = findHmlDocument(link, correlationID);
                    childDocumentOptional.ifPresent(childDocument -> {
                        child.setTitle(findTitle(childDocument));
                    });
                    crawlerInfo.addChild(child);
                });
        return Optional.of(crawlerInfo);
    }
}
