package org.example.tpo5_pm_s24188;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "serviceServlet", value = "/request")
public class ServiceServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> vehicles = getVehiclesFromDB(req, resp);
        System.out.println(vehicles);
        //getHTML(req, resp, vehicles);
    }

    private void getHTML(HttpServletRequest req, HttpServletResponse resp, List<String> vehicles) throws ServletException, IOException {
        req.setAttribute("vehicles", vehicles);
        req.getRequestDispatcher("/frontend").forward(req, resp);
    }

    private List<String> getVehiclesFromDB(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/persistence").forward(req, resp);

        return (List<String>) req.getAttribute("result");
    }

}
