package net.aplayfullife.home;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.io.IOUtils;
import org.wikipedia.*;

public class WikiBlock implements ContentBlock {
  Wiki wiki;
  String title;
  public WikiBlock(String title) {
    wiki = new Wiki();
    this.title = title;
  }
  public String GetHTML() throws IOException {
    String intro = (wiki.getSummaryText(title, 0));
    return intro;
  }
}
