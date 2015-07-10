package com.webcrawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

public class LinkFinder {
	private static final Pattern LINK_TAG_PATTERN = Pattern.compile("(?i)<a([^>]+)>(.+?)</a>");
	private static final Pattern HREF_TAG_PATTERN = Pattern.compile("\\s*(?i)href\\s*=\\s*(\"([^\"]*\")|'[^']*'|([^'\">\\s]+))");
 
	public List<String> findAllLinks(String url) {
		Optional<String> doc = toDocument(url);
		if (!doc.isPresent()) {
			return Collections.emptyList();
		}
		return links(doc.get());
	}
	
	private static List<String> links(String s) {
		List<String> links = new ArrayList<String>();
		Matcher linkMatcher = LINK_TAG_PATTERN.matcher(s);
		while (linkMatcher.find()) {
			String link = linkMatcher.group(1);
			Matcher hrefMatcher = HREF_TAG_PATTERN.matcher(link);
			while (hrefMatcher.find()) {
				links.add(hrefMatcher.group(1));
			}
		}
		return links;
	}
		
	@VisibleForTesting
	protected Optional<String> toDocument(String url) {
		Optional<URL> u = toURL(url);
		if (!u.isPresent()) {
			return Optional.absent();
		}
		try {
			String line;
			StringBuilder doc = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader((u.get().openStream())));
			while ((line = br.readLine()) != null) {
				doc.append(line + "\n");
			}
			return Optional.of(doc.toString());
		} catch (IOException e) {
			System.err.println("ERROR: Unable to retrieve URL: " + url);
			e.printStackTrace();
			return Optional.absent();
		}
	}

	private static Optional<URL> toURL(String url) {
		try {
			return Optional.of(new URL(url));
		} catch (MalformedURLException e) {
			//ignore bad urls
		}
		return Optional.absent();
	}
}