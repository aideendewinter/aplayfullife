package net.aplayfullife.home;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import net.aplayfullife.identity.*;
import org.apache.commons.io.IOUtils;

@WebServlet(urlPatterns = {"", "*.html"})
public class MainServlet extends HttpServlet {
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    IdentityTemplate template = new IdentityTemplate("home_main", this);
    
    InputStream resourceContent;
    StringWriter writer = new StringWriter();
    // Page Navigation
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    resourceContent = classLoader.getResourceAsStream("/content/home_pages");
    writer.getBuffer().setLength(0);
    IOUtils.copy(resourceContent, writer, "UTF-8");
    ArrayList<String> pages = new ArrayList<String>(Arrays.asList(writer.toString().split("\\r?\\n")));
    IOUtils.closeQuietly(resourceContent);
    String pageNav = "";
    for (String page : pages) {
      String[] pageInfo = page.split(",");
      pageNav += "<a href=\"./" + pageInfo[1] + ".html\">" + pageInfo[0] + "</a>";
    }
    template.SetPageNavigation(pageNav);
    // Page Content
    String path = request.getServletPath().replace(".html", "");
    if (path == "")
      resourceContent = classLoader.getResourceAsStream("/content/home_main");
    else
      resourceContent = classLoader.getResourceAsStream("/content" + path);
    writer.getBuffer().setLength(0);
	try {
		IOUtils.copy(resourceContent, writer, "UTF-8");
		ArrayList<String> blockIds = new ArrayList<String>(Arrays.asList(writer.toString().split(",")));
		IOUtils.closeQuietly(resourceContent);
		template.SetPageHeader(blockIds.remove(0));
		if (path == "") {
		parseBlocks("/home", blockIds, template);
		}
		else {
		parseBlocks(path, blockIds, template);
		}
		template.SetStyle("/stylesheets/identity.css");
		// Output
		response.setContentType("text/html; charset=UTF-8");
		response.getWriter().print(template.GetPage());
	} catch (NullPointerException e) {
		response.sendError(404, "Home servlet is confused.");
	}
  }
	
	protected void parseBlocks(String page, List<String> blockIds, IdentityTemplate template) {
		String content = "";
		for(String blockId : blockIds) {
			blockId = blockId.trim();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      InputStream resourceContent = classLoader.getResourceAsStream("/content"+page+"-blocks/" + blockId);
			StringWriter writer = new StringWriter();
			
			try {
				IOUtils.copy(resourceContent, writer, "UTF-8");
				String blockContent = writer.toString();
				if (blockId.contains("text")) {
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
