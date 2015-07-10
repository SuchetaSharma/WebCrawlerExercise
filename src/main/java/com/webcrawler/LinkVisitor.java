package com.webcrawler;

import java.util.List;

public class LinkVisitor implements Runnable {

	private final WebCrawler crawler;
	private final String url;
	private final LinkFinder linkFinder;

	public LinkVisitor(String url, WebCrawler crawler, LinkFinder linkFinder) {
		this.url = url;
		this.crawler = crawler;
		this.linkFinder = linkFinder;
	}

	public void run() {
		System.out.println("Crawling : " + url);
		this.crawler.markVisited(url);
		List<String> urls = linkFinder.findAllLinks(url);
		for (String url : urls) {
			if (!this.crawler.isVisited(url)) {
				crawler.visit(url);
			}
		}
	}
}
