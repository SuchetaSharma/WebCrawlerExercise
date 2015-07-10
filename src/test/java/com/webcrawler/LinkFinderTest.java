package com.webcrawler;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.io.Files;

public class LinkFinderTest {
	private final LinkFinder linkFinder = spy(new LinkFinder());
	
	@Test
	public void shouldReturnEmptyListForBlankUrl() {
		assertThat(linkFinder.findAllLinks("").size(), equalTo(0));
		assertThat(linkFinder.findAllLinks(null).size(), equalTo(0));
	}
	
	@Test
	public void shouldReturnEmptyListForMalFormedUrl() {
		assertThat(linkFinder.findAllLinks("something").size(), equalTo(0));
	}
	
	@Test
	public void shouldReturnAllLinksOnUrl() throws IOException {
		doReturn(Optional.of(readFile("someLinkSource.txt"))).when(linkFinder).toDocument("http://www.google.com");
		List<String> findAllLinks = linkFinder.findAllLinks("http://www.google.com");
		assertThat(findAllLinks.size(), equalTo(3));
	}
	
	@Test
	public void shouldReturnEmptyListIfUnableToHitAUrl() throws IOException {
		doReturn(Optional.<String>absent()).when(linkFinder).toDocument("http://www.google.com");
		List<String> findAllLinks = linkFinder.findAllLinks("http://www.google.com");
		assertThat(findAllLinks.size(), equalTo(0));
	}
	
	private String readFile(String fileName) throws IOException {
		return Files.toString(new File(this.getClass().getClassLoader().getResource(fileName).getFile()), Charsets.UTF_8);
	}
}