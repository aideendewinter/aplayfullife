package net.aplayfullife.identity;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import java.util.list;

@WebServlet("/")
public class MainServlet extends HttpServlet {
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    ServletContext context = getServletContext();
    InputStream resourceContent = context.getResourceAsStream("/resources/templates/identity_main.html");
    String template = "";
    byte input = resourceContent.read();
    while (input != -1) {
      template = template.concat(input.toChar());
      input = resourceContent.read();
    }
    response.setContentType("text/html; charset=UTF-8");
    response.getOutputStream().print("<html><body>This was written by a servlet. Amazing, no?</body></html>");
  }
}
