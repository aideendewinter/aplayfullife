package net.aplayfullife.identity;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.io.IOUtils;

public class MindTextBlock {
	String text;
	
	public MindTextBlock(String text) {
		this.text = text;
	}
	
	public String GetHTML(String page, String blockId, MindMap mindMap) throws IOException {
		String mapped = text;
		int pageIndex = 0;
		while (!page.equals(mindMap.pages[pageIndex]))
			pageIndex++;
		
		for (int i=0; i<mindMap.mapMatrix[pageIndex].length; i++) {
			if (i==pageIndex)
				continue;
			int weight = (mindMap.mapMatrix[pageIndex][i]+mindMap.mapMatrix[i][pageIndex]);
			String linkWeight="normal-weight";
			if (weight > 50)
				linkWeight="strong-weight";
			
			if (mindMap.bestLinkRank[pageIndex][i] >= 0) {
				if (mindMap.bestLinkBlock[pageIndex][i].equals(blockId)) {
					mapped = mapped.replaceFirst(mindMap.bestLinkWord[pageIndex][i],
						"<a class=" + linkWeight + " href=\"/identity/"
						+ mindMap.pages[i] + ".html\">" 
						+ mindMap.pages[i] + "</a>");
				}
			}
		}
		
		ArrayList<String> paragraphs = new ArrayList<String>(Arrays.asList(mapped.split("\\r?\\n\\r?\\n")));
		String parsed = "";
		for (String paragraph : paragraphs) {
			parsed += "<p>" + paragraph + "</p>";
		}
		
		return parsed;
	}
}
