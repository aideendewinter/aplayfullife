package net.aplayfullife.identity;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.io.IOUtils;

public class IdentityTemplate {
  protected String templateName;
  protected HttpServlet myServlet;
  protected String pageNav, pageHeader, pageContent, pageLeft, pageRight, stylesheet;
  
  public IdentityTemplate(String name, HttpServlet servlet) {
    templateName = name;
    myServlet = servlet;
  }
  
  public void SetPageNavigation(String nav) {
    pageNav = nav;
  }
  public void SetPageHeader(String header) {
    pageHeader = header;
  }
  public void SetPageContent(String content) {
    pageContent = content;
  }
  public void SetPageLeft(String content) {
    pageLeft = content;
  }
  public void SetPageRight(String content) {
    pageRight = content;
  }
  public void SetStyle(String content) {
    stylesheet = content;
  }
  
  public String GetPage() throws IOException {
    ServletContext context = myServlet.getServletContext();
    InputStream resourceContent = context.getResourceAsStream("/WEB-INF/templates/" + templateName + ".html");
    StringWriter writer = new StringWriter();
    IOUtils.copy(resourceContent, writer, "UTF-8");
    String template = writer.toString();
    IOUtils.closeQuietly(resourceContent);
    // Header
    resourceContent = context.getResourceAsStream("/WEB-INF/templates/" + templateName + "_header.html");
    writer.getBuffer().setLength(0);
    IOUtils.copy(resourceContent, writer, "UTF-8");
    template = template.replace("{site_header}", writer.toString());
    IOUtils.closeQuietly(resourceContent);
    // Navigation
    resourceContent = context.getResourceAsStream("/WEB-INF/templates/site_navigation.html");
    writer.getBuffer().setLength(0);
    IOUtils.copy(resourceContent, writer, "UTF-8");
    template = template.replace("{site_navigation}", writer.toString());
    IOUtils.closeQuietly(resourceContent);
    // Page
    template = template.replace("{stylesheet}", stylesheet);
    template = template.replace("{page_navigation}", pageNav);
    template = template.replace("{page_title}", pageHeader);
    template = template.replace("{page_header}", pageHeader);
    template = template.replace("{page_body}", pageContent);
    template = template.replace("{page_left}", pageLeft);
    template = template.replace("{page_right}", pageRight);
    
    return template;
  }
}
