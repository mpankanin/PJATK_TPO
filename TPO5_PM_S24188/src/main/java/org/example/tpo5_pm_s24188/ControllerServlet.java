package org.example.tpo5_pm_s24188;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "controllerServlet", value = "/controller")
public class ControllerServlet extends HttpServlet {

    private Repository repository;

    @Override
    public void init() throws ServletException {
        repository = new Repository();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("TYPE", req.getParameter("type"));
        parameters.put("BRAND", req.getParameter("brand"));
        parameters.put("MODEL", req.getParameter("model"));
        parameters.put("PRODUCTION_YEAR", req.getParameter("productionYear"));
        parameters.put("CONSUMPTION", req.getParameter("fuelConsumption"));

        List<String> vehicles = repository.getVehicles(parameters);

        req.setAttribute("vehicles", vehicles);
        req.getRequestDispatcher("/displayVehicles.jsp").forward(req, resp);
    }
}
