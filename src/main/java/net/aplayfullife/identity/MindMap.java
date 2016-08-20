package net.aplayfullife.identity;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.*;

public class MindMap {
	private static final Pattern UNDESIRABLES = Pattern.compile("[(){},.;!?<>%]");
	public int[][] mapMatrix;
	public String[][] bestLinkWord;
	public String[][] bestLinkBlock;
	public int[][] bestLinkRank;
	public String[] pages;
	private HttpServlet myServlet;
	
	public MindMap(String directory, HttpServlet myServlet) throws IOException{
		this.myServlet = myServlet;
		// Content readers.
		InputStream resourceContent;
		StringWriter writer = new StringWriter();
		
		// 
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		resourceContent = classLoader.getResourceAsStream("/content/identity-pages");
		writer.getBuffer().setLength(0);
		IOUtils.copy(resourceContent, writer, "UTF-8");
		pages = writer.toString().split("\\r?\\n");
		IOUtils.closeQuietly(resourceContent);
		
		mapMatrix = new int[pages.length][pages.length];
		bestLinkWord = new String[pages.length][pages.length];
		bestLinkBlock = new String[pages.length][pages.length];
		bestLinkRank = new int[pages.length][pages.length];
		
		for (int i=0, n=bestLinkRank.length; i < n; i++)
			for(int j=0; j < n; j++)
				bestLinkRank[i][j] = -1;
		
		for(int i=0; i<pages.length; i++) {
			resourceContent = classLoader.getResourceAsStream("/content/identity-pages/" + pages[i]);
			writer.getBuffer().setLength(0);
			IOUtils.copy(resourceContent, writer, "UTF-8");
			ArrayList<String> blockIds = new ArrayList<String>(Arrays.asList(writer.toString().split(",")));
			IOUtils.closeQuietly(resourceContent);
			blockIds.remove(0);
			for(String blockId : blockIds) {
				blockId = blockId.trim();
				resourceContent = classLoader.getResourceAsStream("/content/identity-blocks/" + blockId);
				writer.getBuffer().setLength(0);
				
				try {
					IOUtils.copy(resourceContent, writer, "UTF-8");
					String blockContent = writer.toString();
					if (blockId.contains("mindtext")) {
						Parse(blockContent, i, blockId);
					}
				} catch (IOException | java.lang.NullPointerException ex) {
					
				}
				IOUtils.closeQuietly(resourceContent);
			}
		}
	}
	
	public void Parse(String blockContent, int i, String blockId) throws IOException {
		String stripped = UNDESIRABLES.matcher(blockContent).replaceAll("");
		stripped = stripped.toLowerCase();
		for(int j=0; j<pages.length; j++) {
			if (j==i)
				continue;
			
			// Content readers.
			StringWriter writer = new StringWriter();
			// 
			ServletContext context = myServlet.getServletContext();
			InputStream resourceContent =
				context.getResourceAsStream("/words/" + pages[j] + ".json");
			IOUtils.copy(resourceContent, writer, "UTF-8");
			String wordJSON = writer.toString();
			IOUtils.closeQuietly(resourceContent);
			
			JSONObject target = (JSONObject) JSONValue.parse(wordJSON);
			
			for (String word : stripped.split(" ")) {
				if (word.equals(target.get("Word"))) {
					mapMatrix[i][j] += 10;
					if (bestLinkRank[i][j] < 3) {
						bestLinkRank[i][j] = 3;
						bestLinkBlock[i][j] = blockId;
						bestLinkWord[i][j] = (String)target.get("Word");
					}
				}
				for (Object targetObj : ((JSONArray)target.get("Variations"))) {
					String targetWord = (String)targetObj;
					if(word.equals(targetWord)) {
						mapMatrix[i][j] += 10;
						if (bestLinkRank[i][j] < 2) {
							bestLinkRank[i][j] = 2;
							bestLinkBlock[i][j] = blockId;
							bestLinkWord[i][j] = targetWord;
						}
					}
				}
				for (Object targetObj : ((JSONArray)target.get("Synonyms"))) {
					String targetWord = (String)targetObj;
					if(word.equals(targetWord)) {
						mapMatrix[i][j] += 5;
						if (bestLinkRank[i][j] < 1) {
							bestLinkRank[i][j] = 1;
							bestLinkBlock[i][j] = blockId;
							bestLinkWord[i][j] = targetWord;
						}
					}
				}
				for (Object targetObj : ((JSONArray)target.get("SynonymVariations"))) {
					String targetWord = (String)targetObj;
					if(word.equals(targetWord)) {
						mapMatrix[i][j] += 1;
						if (bestLinkRank[i][j] < 0) {
							bestLinkRank[i][j] = 0;
							bestLinkBlock[i][j] = blockId;
							bestLinkWord[i][j] = targetWord;
						}
					}
				}
			}
		}
	}
}
