package net.aplayfullife.identity;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import net.aplayfullife.identity;
import org.apache.commons.io.IOUtils;

public class IdentityTemplate {
  protected String templateName;
  protected HttpServlet myServlet;
  
  public IdentityTemplate(String name, HttpServlet servlet) {
    templateName = name;
    myServlet = servlet;
  }
  
  public String GetTemplate() {
    ServletContext context = myServlet.getServletContext();
    InputStream resourceContent = context.getResourceAsStream("/WEB-INF/templates/" + name + ".html");
    StringWriter writer = new StringWriter();
    IOUtils.copy(resourceContent, writer, "UTF-8");
    String template = writer.toString();
    IOUtils.closeQuietly(resourceContent);
    // Header
    resourceContent = context.getResourceAsStream("/WEB-INF/templates/" + name + "_header.html");
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
  }
}
