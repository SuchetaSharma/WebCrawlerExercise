package com.webcrawler;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.Test;

public class LinkVisitorTest {
	private final WebCrawler mockCrawler = mock(WebCrawler.class);
	private final LinkFinder mockLinkFinder = mock(LinkFinder.class);
	
	@Test
	public void shouldVisitAllUrlsFoundByLinkFinder() {
		when(mockLinkFinder.findAllLinks("someurl")).thenReturn(asList("link1InSomeUrl", "link2InSomeUrl"));
		when(mockCrawler.isVisited(anyString())).thenReturn(false);
		
		LinkVisitor linkVisitor = new LinkVisitor("someurl", mockCrawler, mockLinkFinder);
		linkVisitor.run();
		
		verify(mockCrawler, times(1)).markVisited("someurl");
		verify(mockCrawler, times(1)).isVisited("link1InSomeUrl");
		verify(mockCrawler, times(1)).isVisited("link2InSomeUrl");
		verify(mockCrawler, times(1)).visit("link1InSomeUrl");
		verify(mockCrawler, times(1)).visit("link2InSomeUrl");
	}
	
	@Test
	public void shouldSkipAlreadyVisitedUrls() {
		when(mockLinkFinder.findAllLinks("someurl")).thenReturn(asList("link1InSomeUrl", "link2InSomeUrl"));
		when(mockCrawler.isVisited("link1InSomeUrl")).thenReturn(false);
		when(mockCrawler.isVisited("link2InSomeUrl")).thenReturn(true);
		
		LinkVisitor linkVisitor = new LinkVisitor("someurl", mockCrawler, mockLinkFinder);
		linkVisitor.run();
		
		verify(mockCrawler, times(1)).markVisited("someurl");
		verify(mockCrawler, times(1)).isVisited("link1InSomeUrl");
		verify(mockCrawler, times(1)).isVisited("link2InSomeUrl");
		verify(mockCrawler, times(1)).visit("link1InSomeUrl");
		verifyNoMoreInteractions(mockCrawler);
	}
}
