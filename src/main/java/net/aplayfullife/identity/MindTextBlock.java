package net.aplayfullife.identity;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import org.apache.commons.io.IOUtils;

public class MindTextBlock implements ContentBlock {
  String text;
  public MindTextBlock(String text) {
    this.text = text;
  }
  public String GetHTML() throws IOException {
    ArrayList<String> paragraphs = new ArrayList<String>(Arrays.asList(writer.toString().split("\\r?\\n\\r?\\n")));
    String parsed = "";
		for (String paragraph : paragraphs) {
			parsed += "<p>" + paragraph + "</p>";
		}
		
		return parsed;
  }
}
