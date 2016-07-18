package net.aplayfullife.home;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.io.IOUtils;

@WebServlet("")
public class MainServlet extends HttpServlet {
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    ServletContext context = getServletContext();
    InputStream resourceContent = context.getResourceAsStream("/WEB-INF/templates/home_main.html");
    StringWriter writer = new StringWriter();
    IOUtils.copy(resourceContent, writer, "UTF-8");
    String template = writer.toString();
    IOUtils.closeQuietly(resourceContent);
    // Header
    resourceContent = context.getResourceAsStream("/WEB-INF/templates/home_header.html");
    writer.getBuffer().setLength(0);
    IOUtils.copy(resourceContent, writer, "UTF-8");
    template = template.replace("{site_header}", writer.toString());
    IOUtils.closeQuietly(resourceContent);
    // Header
    resourceContent = context.getResourceAsStream("/WEB-INF/templates/site_navigation.html");
    writer.getBuffer().setLength(0);
    IOUtils.copy(resourceContent, writer, "UTF-8");
    template = template.replace("{site_navigation}", writer.toString());
    IOUtils.closeQuietly(resourceContent);
    // Page Content
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    resourceContent = classLoader.getResourceAsStream("/content/home_main");
    writer.getBuffer().setLength(0);
    IOUtils.copy(resourceContent, writer, "UTF-8");
    List<String> blockIds = Arrays.asList(writer.toString().split(","));
    template = template.replace("{page_header}", blockIds.remove(0));
    String content = parseBlocks(blockIds);
    IOUtils.closeQuietly(resourceContent);
    // Output
    template = template.replace("{page_body}", content);
    response.setContentType("text/html; charset=UTF-8");
    response.getWriter().print(template);
  }
  
  protected String parseBlocks(List<String> blockIds) {
    String blockOutput = "";
    for(String blockId : blockIds) {
      blockId = blockId.trim();
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      InputStream resourceContent = classLoader.getResourceAsStream("/content/home-blocks/" + blockId);
      StringWriter writer = new StringWriter();
      try {
        IOUtils.copy(resourceContent, writer, "UTF-8");
        String blockContent = writer.toString();
        if (blockId.contains("text")) {
          blockOutput += "<p class=\"block text\">" + blockContent + "</p>";
        } else if (blockId.contains("wiki")) {
          try {
            WikiBlock wBlock = new WikiBlock(blockContent);
            blockOutput += "<p class=\"block wiki\">" + wBlock.GetHTML() + "</p>";
          }
          catch (IOException e) {
            blockOutput += "<h1>Bad Wiki Block<h1>";
          }
        }
      }
      catch (IOException | java.lang.NullPointerException ex) {
        blockOutput += "Bad Block : " + blockId + ".";
      }
      IOUtils.closeQuietly(resourceContent);
    }
    return blockOutput;
  }
}
