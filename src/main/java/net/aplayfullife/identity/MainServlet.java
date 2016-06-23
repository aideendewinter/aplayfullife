import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet("/")
public class MainServlet extends HttpServlet {
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.getWriter().write("<html><body>This was written by a servlet. Amazing, no?</body></html>");
  }
}
