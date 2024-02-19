package org.example;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DataServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // Получение данных из параметров запроса
        try {
            String inputData = request.getParameter("data");

            // Обработка данных
            Controller controller = new Controller();
            inputData = controller.getSecureText(inputData).getValue();

            // Отправка обработанных данных обратно клиенту
            response.setContentType("text/plain");
            response.getWriter().write("Processed data: " + inputData);
        }
        catch (Exception e){
            e.getMessage();
        }
    }
}
