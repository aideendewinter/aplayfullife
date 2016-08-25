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
	private static final Pattern UNDESIRABLES = Pattern.compile("[(){},.;!?<>%\"\']");
	private float t;
	
	public String[] pages;
	public int[] pageRanks;
	public String[][] bestLinkBlock;
	public String[][] bestLinkWord;
	
	public MindMap(String directory, HttpServlet myServlet) throws IOException{
		// Content readers.
		InputStream resourceContent;
		StringWriter writer = new StringWriter();
		
		// 
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		resourceContent = classLoader.getResourceAsStream(directory);
		IOUtils.copy(resourceContent, writer, "UTF-8");
		pages = writer.toString().split("\\r?\\n");
		IOUtils.closeQuietly(resourceContent);
		
		int[][] mapMatrix = new int[pages.length][pages.length];
		bestLinkWord = new String[pages.length][pages.length];
		bestLinkBlock = new String[pages.length][pages.length];
		int[][] bestLinkRank = new int[pages.length][pages.length];
		
		for (int i=0, n=bestLinkRank.length; i < n; i++)
			for(int j=0; j < n; j++)
				bestLinkRank[i][j] = -1;
		
		for(int i=0; i<pages.length; i++) {
			resourceContent = classLoader.getResourceAsStream(directory + "/" + pages[i]);
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
						String[] tokens = Parse(blockContent);
						stripped = stripped.toLowerCase();
						for(int j=0; j<pages.length; j++) {
							if (j==i)
								continue;
			
							// Content readers.
							writer.getBuffer().setLength(0);
							// 
							ServletContext context = myServlet.getServletContext();
							resourceContent =
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
										mapMatrix[i][j] += 5;
										if (bestLinkRank[i][j] < 0) {
											bestLinkRank[i][j] = 0;
											bestLinkBlock[i][j] = blockId;
											bestLinkWord[i][j] = targetWord;
										}
									}
								}
								for (Object targetObj : ((JSONArray)target.get("Related"))) {
									String targetWord = (String)targetObj;
									if(word.equals(targetWord)) {
										mapMatrix[i][j] += 1;
									}
								}
							}
						}
					}
				} catch (IOException | java.lang.NullPointerException ex) {
					
				}
				IOUtils.closeQuietly(resourceContent);
			}
		}
		t = pages.length-1.5f;
		pageRanks = new int[(int)(pages.length * (t+.5) * .5)];
		int x=0;
		int u = (int)((t * x) - (.5 * x * x) - 1);
		for(int z=0; z<pageRanks.length; z++) {
			int y = z - u;
			if (y > (t+.5)) {
				x++;
				u = (int)((t * x) - (.5 * x * x) - 1);
				y = z - u;
			}
			pageRanks[z] = mapMatrix[x][y] + mapMatrix[y][x];
		}
		int average = IntArraySum(pageRanks) / pageRanks.length;
		for(int i=0; i<pageRanks.length; i++) {
			pageRanks[i] -= average;
		}
	}
	
	public int IntArraySum(int[] arr) {
		int sum = 0;
		for(int i : arr) {
			sum += i;
		}
		return sum;
	}
	
	public int RankIndex(int x, int y) {
		if (x > y) {
			int temp = y;
			y=x;
			x=temp;
		}
		return (int)((t * x) - (.5 * x * x) - 1 + y);
	}
	
	private LinkedList<String> Parse(String inp) {
		int state=-1;
		LinkedList<String> tokens = new LinkedList<String>();
		String currentToken;
		for (int i=0; i< inp.length(); i++) {
			Character ch = new Character(inp.charAt(i));
			switch(state) {
				case -1:
					if (ch.isLetter()) {
						currentToken = ch;
						state = 0;
					} else if (ch == '[') {
						currentToken = ch;
						state = 1;
					}
					break;
				case 0:
					if (ch.isLetter()) {
						currentToken += ch;
					} else {
						tokens.add(currentToken);
						state = -1;
					}
					break;
				default:
					break;
			}
		}
	}
}
