package net.aplayfullife.identity;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class MindMap {
	public int[][] mapMatrix;
	
	public MindMap(String directory, IdentityTemplate template) throws IOException{
		// Content readers.
		InputStream resourceContent;
		StringWriter writer = new StringWriter();
		
		// 
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		resourceContent = classLoader.getResourceAsStream("/content/identity-pages");
		writer.getBuffer().setLength(0);
		IOUtils.copy(resourceContent, writer, "UTF-8");
		String[] pages = writer.toString().split("\\r?\\n");
		IOUtils.closeQuietly(resourceContent);
		
		mapMatrix = new int[pages.length][pages.length]
		
		for(int i=0; i<pages.length; i++) {
			resourceContent = classLoader.getResourceAsStream("/content/identity-pages/" + pages[i]);
			writer.getBuffer().setLength(0);
			IOUtils.copy(resourceContent, writer, "UTF-8");
			ArrayList<String> blockIds = new ArrayList<String>(Arrays.asList(writer.toString().split(",")));
			IOUtils.closeQuietly(resourceContent);
			blockIds.remove(0);
			
			for(int j=0; j<pages.length; j++) {
				mapMatrix[i][j] = 0;
			}
			for(String blockId : blockIds) {
				blockId = blockId.trim();
				resourceContent = classLoader.getResourceAsStream("/content/identity-blocks/" + blockId);
				writer.getBuffer().setLength(0);
				
				try {
					IOUtils.copy(resourceContent, writer, "UTF-8");
					String blockContent = writer.toString();
					if (blockId.contains("mindtext")) {
						for(int j=0; j<pages.length; j++) {
							mapMatrix[i][j] += StringUtils.CountMatches(blockcontent, pages[j]);
						}
					}
				} catch (IOException | java.lang.NullPointerException ex) {
					
				}
				IOUtils.closeQuietly(resourceContent);
			}
		}
	}
}
