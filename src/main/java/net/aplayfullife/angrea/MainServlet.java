package net.aplayfullife.angrea;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import net.aplayfullife.identity.IdentityTemplate;

@WebServlet("/angrea/*")
public class MainServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		IdentityTemplate template = new IdentityTemplate("angrea_main", this);
		
		// Content readers.
		InputStream resourceContent;
		StringWriter writer = new StringWriter();
		
		// Page Navigation
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		resourceContent = classLoader.getResourceAsStream("/content/angrea_pages");
		writer.getBuffer().setLength(0);
		IOUtils.copy(resourceContent, writer, "UTF-8");
		ArrayList<String> pages = new ArrayList<String>(Arrays.asList(writer.toString().split("\\r?\\n")));
		IOUtils.closeQuietly(resourceContent);
		String pageNav = "";
		for (String page : pages) {
			String[] pageInfo = page.split(",");
			pageNav += "<a class=\"nav_level"+ pageInfo[0] +"\" href=\"./" + pageInfo[2] + ".html\">" + pageInfo[1] + "</a>";
		}
		template.SetPageNavigation(pageNav);
		
		// Page Content
		String path = request.getPathInfo();
		if (StringUtils.isBlank(path))
			path = "/";
		path = path.replace(".html", "");
		if (path.equals("/")) {
			resourceContent = classLoader.getResourceAsStream("/content/angrea_main");
		} else
			resourceContent = classLoader.getResourceAsStream("/content/angrea-pages" + path);
		
		writer.getBuffer().setLength(0);
		IOUtils.copy(resourceContent, writer, "UTF-8");
		ArrayList<String> blockIds = new ArrayList<String>(Arrays.asList(writer.toString().split(",")));
		IOUtils.closeQuietly(resourceContent);
		template.SetPageHeader(blockIds.remove(0));
		if (path.equals("/")) {
			parseBlocks("angrea", blockIds, template);
		} else {
			parseBlocks(path.replace("/", ""), blockIds, template);
		}
		template.SetStyle("/stylesheets/angrea.css");
		
		// Output
		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().print(template.GetPage());
	}
	
	protected void parseBlocks(String page, List<String> blockIds, IdentityTemplate template) {
		String content = "";
		for(String blockId : blockIds) {
			blockId = blockId.trim();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			InputStream resourceContent = classLoader.getResourceAsStream("/content/angrea-blocks/" + blockId);
			StringWriter writer = new StringWriter();
			
			try {
				IOUtils.copy(resourceContent, writer, "UTF-8");
				String blockContent = writer.toString();
				if (blockId.contains("text")) {
					content += "<p class=\"block text\">" + blockContent + "</p>";
				} else if (blockId.contains("list")) {
					String[] lines = blockContent.split("\\r?\\n");
					content += "<div class=\"block list\"><h1>" + lines[0] + "</h1><ul>";
					for (int i=1; i<lines.length; i++) {
						content += "<li>" + lines[i] + "</li>";
					}
					content += "</ul></div>";
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
