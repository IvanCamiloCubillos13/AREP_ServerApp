package edu.eci.MicroSpringBoot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import static org.junit.jupiter.api.Assertions.*;

class MicroSpringBootTest {

    @BeforeEach
    void resetServer() {
        HttpServer.endPoints.clear();
        HttpServer.staticFilesPath = null;
    }

    @Test
    void testRestControllerAnnotation_isPresentOnHelloController() {
        assertTrue(HelloController.class.isAnnotationPresent(RestController.class));
    }

    @Test
    void testRestControllerAnnotation_isPresentOnGreetingController() {
        assertTrue(GreetingController.class.isAnnotationPresent(RestController.class));
    }

    @Test
    void testRestControllerAnnotation_isNotPresentOnPlainClass() {
        assertFalse(HttpRequest.class.isAnnotationPresent(RestController.class));
    }

    @Test
    void testGetMapping_helloControllerHasIndexMethod() throws NoSuchMethodException {
        Method method = HelloController.class.getDeclaredMethod("index");
        assertTrue(method.isAnnotationPresent(GetMapping.class));
        assertEquals("/", method.getAnnotation(GetMapping.class).value());
    }

    @Test
    void testGetMapping_helloControllerHasPiMethod() throws NoSuchMethodException {
        Method method = HelloController.class.getDeclaredMethod("getPi");
        assertTrue(method.isAnnotationPresent(GetMapping.class));
        assertEquals("/pi", method.getAnnotation(GetMapping.class).value());
    }

    @Test
    void testGetMapping_greetingControllerHasGreetingMethod() throws NoSuchMethodException {
        Method method = GreetingController.class.getDeclaredMethod("greeting", String.class);
        assertTrue(method.isAnnotationPresent(GetMapping.class));
        assertEquals("/greeting", method.getAnnotation(GetMapping.class).value());
    }

    @Test
    void testRequestParam_isPresentOnGreetingParameter() throws NoSuchMethodException {
        Method method = GreetingController.class.getDeclaredMethod("greeting", String.class);
        Parameter param = method.getParameters()[0];
        assertTrue(param.isAnnotationPresent(RequestParam.class));
        assertEquals("name", param.getAnnotation(RequestParam.class).value());
        assertEquals("World", param.getAnnotation(RequestParam.class).defaultValue());
    }

    @Test
    void testHttpServer_registerEndpoint() {
        HttpServer.get("/App/test", (req, res) -> "ok");
        assertTrue(HttpServer.endPoints.containsKey("/App/test"));
    }

    @Test
    void testHttpServer_endpointExecutesCorrectly() {
        HttpServer.get("/App/test", (req, res) -> "ok");
        String result = HttpServer.endPoints.get("/App/test")
                .execute(new HttpRequest(""), new HttpResponse());
        assertEquals("ok", result);
    }

    @Test
    void testHttpRequest_extractsQueryParam() {
        HttpRequest req = new HttpRequest("name=Pedro");
        assertEquals("Pedro", req.getValue("name"));
    }

    @Test
    void testHttpRequest_missingParam_returnsEmpty() {
        HttpRequest req = new HttpRequest("name=Pedro");
        assertEquals("", req.getValue("age"));
    }

    @Test
    void testHttpRequest_nullQuery_returnsEmpty() {
        HttpRequest req = new HttpRequest(null);
        assertEquals("", req.getValue("name"));
    }

    @Test
    void testReflection_invokeIndexMethod() throws Exception {
        Method method = HelloController.class.getDeclaredMethod("index");
        Object instance = HelloController.class.getDeclaredConstructor().newInstance();
        assertEquals("Greetings from Spring Boot!", method.invoke(instance));
    }

    @Test
    void testReflection_invokePiMethod() throws Exception {
        Method method = HelloController.class.getDeclaredMethod("getPi");
        Object instance = HelloController.class.getDeclaredConstructor().newInstance();
        assertEquals("Pi =" + Math.PI, method.invoke(instance));
    }

    @Test
    void testReflection_invokeGreetingWithParam() throws Exception {
        Method method = GreetingController.class.getDeclaredMethod("greeting", String.class);
        Object instance = GreetingController.class.getDeclaredConstructor().newInstance();
        assertEquals("Hola Pedro", method.invoke(instance, "Pedro"));
    }
}