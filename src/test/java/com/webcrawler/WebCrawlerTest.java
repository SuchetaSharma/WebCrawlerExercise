package com.webcrawler;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class WebCrawlerTest {
	
	@Test
	public void shouldThrowIfStartingUrlIsNull() {
		try {
			new WebCrawler(10).startCrawling(null);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), containsString("Starting URL cannot be blank"));
		}
	}
	
	@Test
	public void shouldThrowIfStartingUrlIsBlank() {
		try {
			new WebCrawler(10).startCrawling("");
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), containsString("Starting URL cannot be blank"));
		}
	}
	
	@Test
	public void shouldThrowIfMaximumUrlsToBeVisitedIsLessThanOrEqualToZero() {
		try {
			new WebCrawler(0);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), containsString("Maximum links to be visted must be greater than 0"));
		}
		
		try {
			new WebCrawler(-1);
			fail();
		} catch (IllegalArgumentException e) {
			assertThat(e.getMessage(), containsString("Maximum links to be visted must be greater than 0"));
		}
	}
	
	@Test
	public void isVisitedShouldReturnTrueIfUrlIsMarkedAsVisited() {
		WebCrawler crawler = new WebCrawler(10);
		crawler.markVisited("someurl1");
		assertThat(crawler.isVisited("someurl1"), equalTo(true));
		assertThat(crawler.isVisited("someurl2"), equalTo(false));
	}
	
	@Test
	public void shouldNotDoAnythingIfMaxUrlaAreAlreadyVisited() {
		WebCrawler crawler = new WebCrawler(10);
		for (int i = 1; i <= 10; i++) {
			crawler.markVisited("someurl" + i);
		}
		for (int i = 1; i <= 10; i++) {
			assertThat(crawler.isVisited("someurl" + i), equalTo(true));
		}
		crawler.markVisited("someurl11");
		assertThat(crawler.isVisited("someurl11"), equalTo(false));
	}
	
	@Test
	public void shouldCreateThreadsForEachUrlToBeVisited() {
		WebCrawler crawler = new WebCrawler(10);
		ThreadPoolExecutor executorService = (ThreadPoolExecutor)crawler.executorService();
		assertThat(executorService.getPoolSize(), equalTo(0));
		for (int i = 1; i <= 10; i++) {
			crawler.visit("someurl" + i);
		}
		assertThat(executorService.getPoolSize(), equalTo(10));
	}
	
	@Test
	public void shouldStopOnceMaximumUrlsToBeVisited() throws InterruptedException {
		WebCrawler crawler = new WebCrawler(10);
		ThreadPoolExecutor executorService = (ThreadPoolExecutor)crawler.executorService();
		assertThat(executorService.getPoolSize(), equalTo(0));
		for (int i = 1; i <= 10; i++) {
			crawler.visit("someurl" + i);
		}
		sleepForFiveSeconds();
		assertThat(executorService.getPoolSize(), lessThanOrEqualTo(10));
		
		crawler.visit("someurl11");
		assertThat(executorService.getActiveCount(), lessThan(10));
		assertThat(executorService.isShutdown(), equalTo(true));
	}

	private void sleepForFiveSeconds() throws InterruptedException {
		TimeUnit.SECONDS.sleep(5);
	}
}