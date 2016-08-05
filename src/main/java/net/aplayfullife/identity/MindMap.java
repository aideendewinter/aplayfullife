package net.aplayfullife.identity;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.io.IOUtils;

public class MindMap {
	
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
		for(int i=0; i<pages.length; i++) {
			resourceContent = classLoader.getResourceAsStream("/content/identity-pages/" + pages[i]);
			writer.getBuffer().setLength(0);
			IOUtils.copy(resourceContent, writer, "UTF-8");
			template.DebugPrintLn(i + writer.toString());
		}
		IOUtils.closeQuietly(resourceContent);
	}
}
