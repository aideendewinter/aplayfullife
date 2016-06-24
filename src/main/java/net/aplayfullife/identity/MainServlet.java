package net.aplayfullife.identity;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.io.IOUtils;

@WebServlet("/")
public class MainServlet extends HttpServlet {
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    ServletContext context = getServletContext();
    InputStream resourceContent = context.getResourceAsStream("/resources/templates/identity_main.html");
    StringWriter writer = new StringWriter();
    IOUtils.copy(inputStream, writer, "UTF-8");
    String template = writer.toString();
    IOUtils.closeQuietly(inputStream);
    response.setContentType("text/html; charset=UTF-8");
    response.getOutputStream().print(template);
  }
}
