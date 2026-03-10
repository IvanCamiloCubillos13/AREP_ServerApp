package edu.eci.MicroSpringBoot;

public interface WebMethod {
    public String execute(HttpRequest req, HttpResponse res);
}
