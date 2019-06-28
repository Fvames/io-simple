package dev.fvames.tomcat.http;

/**
 * @version 2019/6/28 17:28
 */

public abstract class Servlet {

    public void service(Request request, Response response) {
        if ("GET".equalsIgnoreCase(request.getMethodName())) {

            doGet(request, response);
        } else if ("POST".equalsIgnoreCase(request.getMethodName())) {

            doPost(request, response);
        }
    }

    protected abstract void doPost(Request request, Response response);

    protected abstract void doGet(Request request, Response response);


}
