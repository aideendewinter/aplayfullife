package net.aplayfullife.home;

import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import net.aplayfullife.identity.*;
import org.apache.commons.io.IOUtils;

@WebServlet(urlPatterns = {"/sitemap.html"})
public class SiteMapServlet extends HttpServlet {
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
      String output = "";
      
      InputStream resourceContent;
      StringWriter writer = new StringWriter();
      
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      resourceContent = classLoader.getResourceAsStream("/content/home_pages");
      IOUtils.copy(resourceContent, writer, "UTF-8");
      
      output += "<a href=\"/\">A Playful Life : Home</a>";
      output += "<a href=\"/angrea/\">Angrea RPG</a>";
      output += "<a href=\"/identity/\">Identity</a>";
      
      response.setContentType("text/html; charset=UTF-8");
      response.getWriter().print(output);
  }
}
