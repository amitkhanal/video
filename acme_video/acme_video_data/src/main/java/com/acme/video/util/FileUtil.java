package com.acme.video.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

/**
 * @author amitkhanal
 * 
 * Handles common file operations
 */
public class FileUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);
	
	public static File[] getFiles(String location){
		if(StringUtils.isEmpty(location)){
			return null;
		}
		File datasetDirectory = new File(location);
		File[] unprocessedfiles = datasetDirectory.listFiles();
		return unprocessedfiles;
	}
	
	public static void addErrorEntry(String file, String entry){
		Calendar calendar = Calendar.getInstance();
		StringBuilder errorFile = new StringBuilder("error_")
										.append(calendar.get(Calendar.MONTH))
										.append("_")
										.append(calendar.get(Calendar.DAY_OF_MONTH))
										.append("_")
										.append(calendar.get(Calendar.YEAR));
		Path filePath = Paths.get(file+errorFile.toString());
		try {
			Files.write(filePath,entry.getBytes(),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
		} catch (IOException e) {
			LOGGER.error("Unable to write error entry for file "+file,e);
		}
	}
	
	public static Stream<String> lines(Path path) {
	    InputStream inputStream = null;
	    BufferedInputStream bufferedInputStream = null;
	    GZIPInputStream gzipInputstream = null;
	    try {
	      inputStream = Files.newInputStream(path);
	      bufferedInputStream = new BufferedInputStream(inputStream, 65535);
	      gzipInputstream = new GZIPInputStream(bufferedInputStream);
	    } catch (IOException e) {
	    	close(gzipInputstream);
	    	close(bufferedInputStream);
	    	close(inputStream);
	      throw new UncheckedIOException(e);
	    }
	    BufferedReader reader = new BufferedReader(new InputStreamReader(gzipInputstream));
	    return reader.lines().onClose(() -> close(reader));
	  }

	  private static void close(Closeable closeable) {
	    if (closeable != null) {
	      try {
	        closeable.close();
	      } catch (IOException e) {
	        // Ignore
	      }
	    }
	  }
}
