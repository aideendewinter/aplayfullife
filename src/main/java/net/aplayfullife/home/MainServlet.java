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
    resourceContent = context.getResourceAsStream("/WEB-INF/content/home_main");
    writer.getBuffer().setLength(0);
    IOUtils.copy(resourceContent, writer, "UTF-8");
    template = template.replace("{page_content}", writer.toString());
    IOUtils.closeQuietly(resourceContent);
    // Output
    response.setContentType("text/html; charset=UTF-8");
    response.getOutputStream().print(template);
  }
}
