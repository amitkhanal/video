package com.acme.video.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Autowired;

import com.acme.video.Configuration;

public abstract class AbstractService {

	@Autowired
	protected Configuration configuration;

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	protected void moveFileToProcessed(Path source, Path destination) throws IOException{
		Files.move(source, destination.resolve(source.getFileName()));
	}
}
