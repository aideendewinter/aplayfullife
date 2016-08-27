package net.aplayfullife.identity;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

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
		
		for (int i=0; i<mindMap.pages.length; i++) {
			if (i==pageIndex)
				continue;
			float weight = mindMap.pageRanks[mindMap.RankIndex(pageIndex, i)];
			String linkWeight="normal-weight";
			if (weight > 10)
				linkWeight="strong-weight";
			else if (weight < -10)
				linkWeight="weak-weight";
			
			if (!StringUtils.isEmpty(mindMap.bestLinkWord[pageIndex][i])) {
				if (mindMap.bestLinkBlock[pageIndex][i].equals(blockId)) {
					mapped = mapped.replaceFirst("\\b" + mindMap.bestLinkWord[pageIndex][i] + "\\b",
						"<a class=" + linkWeight + " href=\"/identity/"
						+ mindMap.pages[i] + ".html\">$0</a>");
				}
			}
		}
		mapped.replaceAll("[\\[\\]]", "");
		ArrayList<String> paragraphs = new ArrayList<String>(Arrays.asList(mapped.split("\\r?\\n\\r?\\n")));
		String parsed = "";
		for (String paragraph : paragraphs) {
			parsed += "<p>" + paragraph + "</p>";
		}
		
		return parsed;
	}
}
