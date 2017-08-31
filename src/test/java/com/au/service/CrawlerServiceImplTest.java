package com.au.service;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.au.controller.WebCrawlerController.pageTree;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Jsoup.class)
public class CrawlerServiceImplTest {
    @Mock
    Connection connection;
    @Mock
    Document document;
    private CrawlerService crawlerService = null;

    @Before
    public void setUp() throws Exception {
        this.crawlerService = new CrawlerServiceImpl();
        PowerMockito.mockStatic(Jsoup.class);
    }

    @After
    public void tearDown() throws Exception {
        this.crawlerService = null;
    }

    @Test
    public void testFindHmlDocument() throws Exception {
        PowerMockito.mockStatic(Jsoup.class);
        given(Jsoup.connect(anyString())).willReturn(connection);
        given(connection.timeout(anyInt())).willReturn(connection);
        given(connection.get()).willReturn(document);
        Connection.Response response = mock(Connection.Response.class);
        given(connection.execute()).willReturn(response);
        given(response.statusCode()).willReturn(200);
        given(Jsoup.connect(anyString())).willReturn(connection);
        given(connection.header(anyString(), anyString())).willReturn(connection);
        given(connection.userAgent(anyString())).willReturn(connection);
        final String correlationID = randomUUID().toString();
        pageTree.put(correlationID, new HashSet<String>());
        Optional<Document> documentOptional = crawlerService.findHmlDocument("http://google.com", correlationID);
        assertThat(documentOptional.isPresent(), is(true));
        assertThat(documentOptional.get(), notNullValue());
    }

    @Test
    public void testFindLinks() throws Exception {
        final Element element = Mockito.mock(Element.class);
        given(element.attr("abs:href")).willReturn("http://abcnews.com.au");
        final Elements elements = new Elements(1);
        elements.add(element);
        given(document.select("a[href]")).willReturn(elements);
        final Set<String> links = crawlerService.findLinks(document);
        assertThat(links, notNullValue());
        assertThat(links.size(), is(1));
        assertThat(links.contains("http://abcnews.com.au"), is(true));
    }

    @Test
    public void testFindTitle() throws Exception {
        Document document = Mockito.mock(Document.class);
        given(document.title()).willReturn("Home Page");
        final String title = crawlerService.findTitle(document);
        assertThat(title, is("Home Page"));
    }
}