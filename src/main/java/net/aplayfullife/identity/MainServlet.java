package net.aplayfullife.identity;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;

@WebServlet("/")
public class MainServlet extends HttpServlet {
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.getOutputStream().print("<html><body>This was written by a servlet. Amazing, no?</body></html>");
  }
}
