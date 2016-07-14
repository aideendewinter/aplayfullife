package net.aplayfullife.home;

import java.io.*;
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
    // Page Content
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    resourceContent = classLoader.getResourceAsStream("/content/home_main");
    writer.getBuffer().setLength(0);
    IOUtils.copy(resourceContent, writer, "UTF-8");
    String content = parseBlocks(writer.toString());
    IOUtils.closeQuietly(resourceContent);
    // Output
    template = template.replace("{page_body}", content);
    response.setContentType("text/html; charset=UTF-8");
    response.getWriter().print(template);
  }
  
  protected String parseBlocks(String src) {
    String[] blockIds = src.split(" ");
    String blockOutput = "";
    for(String blockId : blockIds) {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      InputStream resourceContent = classLoader.getResourceAsStream("/content/home-blocks/" + blockId);
      StringWriter writer = new StringWriter();
      try {
        IOUtils.copy(resourceContent, writer, "UTF-8");
        String blockContent = writer.toString();
        if (blockId.contains("text")) {
          blockOutput += blockContent;
        } else if (blockId.contains("wiki")) {
          try {
            WikiBlock wBlock = new WikiBlock(blockContent);
            blockOutput += wBlock.GetHTML();
          }
          catch (IOException e) {
            blockOutput += "<h1>Bad Wiki Block<h1>";
          }
        }
      }
      catch (IOException e) {
        blockOutput += "<h1>Bad Block<h1>";
      }
      IOUtils.closeQuietly(resourceContent);
    }
    return blockOutput;
  }
}
