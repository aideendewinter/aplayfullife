package net.aplayfullife.identity;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

@WebServlet("/identity/*")
public class MainServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		IdentityTemplate template = new IdentityTemplate("identity_main", this);
		MindMap mindMap = new MindMap("/content/identity-pages", this);
		
		// Content readers.
		InputStream resourceContent;
		StringWriter writer = new StringWriter();
		
		// Page Navigation
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		resourceContent = classLoader.getResourceAsStream("/content/identity_pages");
		writer.getBuffer().setLength(0);
		IOUtils.copy(resourceContent, writer, "UTF-8");
		ArrayList<String> pages = new ArrayList<String>(Arrays.asList(writer.toString().split("\\r?\\n")));
		IOUtils.closeQuietly(resourceContent);
		String pageNav = "";
		for (String page : pages) {
			String[] pageInfo = page.split(",");
			if (pageInfo.length < 2)
				continue;
			pageNav += "<a href=\"./" + pageInfo[1] + ".html\">" + pageInfo[0] + "</a>";
		}
		template.SetPageNavigation(pageNav);
		
		// Page Content
		String path = request.getPathInfo().replace(".html", "");
		if (path.equals("/") || StringUtils.isBlank(path)) {
			resourceContent = classLoader.getResourceAsStream("/content/identity_main");
		} else
			resourceContent = classLoader.getResourceAsStream("/content/identity-pages" + path);
		
		writer.getBuffer().setLength(0);
		IOUtils.copy(resourceContent, writer, "UTF-8");
		ArrayList<String> blockIds = new ArrayList<String>(Arrays.asList(writer.toString().split(",")));
		IOUtils.closeQuietly(resourceContent);
		template.SetPageHeader(blockIds.remove(0));
		if (path.equals("/") || StringUtils.isBlank(path)) {
			parseBlocks("identity", blockIds, template, mindMap);
		} else {
			parseBlocks(path.replace("/", ""), blockIds, template, mindMap);
		}
		template.SetStyle("/stylesheets/identity.css");
		
		// Output
		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().print(template.GetPage());
	}
	
	protected void parseBlocks(String page, List<String> blockIds, IdentityTemplate template, MindMap mindMap) {
		String content = "";
		for(String blockId : blockIds) {
			blockId = blockId.trim();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream resourceContent = classLoader.getResourceAsStream("/content/identity-blocks/" + blockId);
			StringWriter writer = new StringWriter();
			
			try {
				IOUtils.copy(resourceContent, writer, "UTF-8");
				String blockContent = writer.toString();
				if (blockId.contains("mindtext")) {
					try {
						MindTextBlock mindTBlock = new MindTextBlock(blockContent);
						content += "<div class=\"block mindtext\">" + mindTBlock.GetHTML(page, blockId, mindMap)
							+ "</div>";
					} catch (IOException e) {
						content += "<h1>Bad Mind Text Block<h1>";
					}
				} else if (blockId.contains("text")) {
					content += "<p class=\"block text\">" + blockContent + "</p>";
				} else if (blockId.contains("wiki")) {
					try {
						WikiBlock wBlock = new WikiBlock(blockContent);
						content += "<div class=\"block wiki\">" + wBlock.GetHTML() + "</div>";
					} catch (IOException e) {
						content += "<h1>Bad Wiki Block<h1>";
					}
				} else if (blockId.contains("html")) {
					content += "<div class=\"block html\">" + blockContent + "</div>";
				}
			} catch (IOException | java.lang.NullPointerException ex) {
				content += "Bad Block : " + blockId + ".";
			}
			IOUtils.closeQuietly(resourceContent);
		}
		
		template.SetPageContent(content);
	}
}
