package net.aplayfullife.identity;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;

@WebServlet("/")
public class MainServlet extends HttpServlet {
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.getWriter().write("<html><body>This was written by a servlet. Amazing, no?</body></html>");
  }
}
