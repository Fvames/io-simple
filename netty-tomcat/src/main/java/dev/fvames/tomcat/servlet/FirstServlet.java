package dev.fvames.tomcat.servlet;

import dev.fvames.tomcat.http.Request;
import dev.fvames.tomcat.http.Response;
import dev.fvames.tomcat.http.Servlet;

/**
 * @version 2019/7/1 13:43
 */

public class FirstServlet extends Servlet {

    @Override
    protected void doPost(Request request, Response response) {
        this.doPost(request, response);
    }

    @Override
    protected void doGet(Request request, Response response) {
        response.write("First Servlet");
    }
}
