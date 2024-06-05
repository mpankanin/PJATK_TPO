package org.example.tpo5_pm_s24188;

import java.io.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");

        // Hello
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>" + message + "</h1>");
        out.println("<form action=\"/request\" method=\"post\">");
        out.println("Type: <select name=\"type\">");
        out.println("<option value=\"\">Select type</option>");
        out.println("<option value=\"car\">Car</option>");
        out.println("<option value=\"truck\">Truck</option>");
        out.println("<option value=\"f1\">F1</option>");
        out.println("<option value=\"pickup\">Pickup</option>");
        out.println("</select><br>");
        out.println("Brand: <input type=\"text\" name=\"brand\"><br>");
        out.println("Model: <input type=\"text\" name=\"model\"><br>");
        out.println("Production Year: <select name=\"productionYear\">");
        out.println("<option value=\"\">Select year</option>");
        for (int year = 1980; year <= 2024; year++) {
            out.println("<option value=\"" + year + "\">" + year + "</option>");
        }
        out.println("</select><br>");
        out.println("Fuel Consumption: <input type=\"number\" name=\"fuelConsumption\" min=\"1.0\" max=\"100.0\" step=\"0.1\"><br>");
        out.println("<input type=\"submit\" value=\"Submit\">");
        out.println("</form>");
        out.println("</body></html>");
    }

    public void destroy() {
    }
}