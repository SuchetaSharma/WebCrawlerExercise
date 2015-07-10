package com.webcrawler;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.annotations.VisibleForTesting;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

public class WebCrawler {
	private final ConcurrentLinkedQueue<String> visitedUrls;
	private final ExecutorService executorService;
	private final int maxLinksToBeVisited;
	private final LinkFinder linkFinder = new LinkFinder();
	public WebCrawler(int maxLinksToBeVisited) {
		checkArgument(maxLinksToBeVisited > 0, "Maximum links to be visted must be greater than 0");
		this.maxLinksToBeVisited = maxLinksToBeVisited;
		this.visitedUrls = new ConcurrentLinkedQueue<String>();
		this.executorService = Executors.newCachedThreadPool();
	}
	
	public void startCrawling(String url) {
		checkArgument(!isNullOrEmpty(url), "Starting URL cannot be blank");
		this.visit(url);
	}
	
	public void stop() {
		this.executorService.shutdownNow();
	}
	
	public boolean isVisited(String url) {
		return visitedUrls.contains(url);
	}
	
	public void visit(String url) {
		if (!maximumUrlsYetToBeVisited()) {
			stop();
		} else if (!isVisited(url)) {
			executorService.execute(new LinkVisitor(url, this, linkFinder));
		}
	}
	
	public void markVisited(String url) {
		if (maximumUrlsYetToBeVisited()) {
			this.visitedUrls.add(url);
		}
	}
	
	@VisibleForTesting
	protected ExecutorService executorService() {
		return executorService;
	}
	
	private boolean maximumUrlsYetToBeVisited() {
		return visitedUrls.size() < maxLinksToBeVisited;
	}
	
	
}