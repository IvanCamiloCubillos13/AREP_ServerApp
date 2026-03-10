package edu.eci.MicroSpringBoot;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MicroSpringBootApplication {

    public static void main(String[] args) throws Exception {
        start();
    }

    public static void start() throws Exception {
        System.out.println("Iniciando MicroSpringBoot...");

        List<Class<?>> controllers = scanControllers();

        for (Class<?> controller : controllers) {
            Object instance = controller.getDeclaredConstructor().newInstance();
            for (Method method : controller.getDeclaredMethods()) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping annotation = method.getAnnotation(GetMapping.class);
                    String path = "/App" + annotation.value();
                    HttpServer.get(path, (req, res) -> {
                        try {
                            Object[] methodArgs = getMethodArgs(method, req);
                            if (methodArgs.length == 0) {
                                return (String) method.invoke(instance);
                            } else {
                                return (String) method.invoke(instance, methodArgs);
                            }
                        } catch (Exception e) {
                            return "Error: " + e.getMessage();
                        }
                    });
                    System.out.println("Registrado endpoint: " + path);
                }
            }
        }

        HttpServer.main(new String[] {});
    }

    private static Object[] getMethodArgs(Method method, HttpRequest req) {
        if (method.getParameterCount() == 0)
            return new Object[] {};

        Object[] args = new Object[method.getParameterCount()];
        Parameter[] params = method.getParameters();
        for (int i = 0; i < params.length; i++) {
            System.out.println("Parametro " + i + ": " + params[i].getName());
            System.out.println("Tiene @RequestParam: " + params[i].isAnnotationPresent(RequestParam.class));
            if (params[i].isAnnotationPresent(RequestParam.class)) {
                RequestParam rp = params[i].getAnnotation(RequestParam.class);
                String val = req.getValue(rp.value());
                System.out.println("Valor extraido para '" + rp.value() + "': '" + val + "'");
                args[i] = val.isEmpty() ? rp.defaultValue() : val;
                System.out.println("Argumento final: " + args[i]);
            }
        }
        return args;
    }

    private static List<Class<?>> scanControllers() throws Exception {
        List<Class<?>> result = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL root = classLoader.getResource("edu/eci/MicroSpringBoot");
        File directory = new File(root.toURI());

        for (File file : directory.listFiles()) {
            if (file.getName().endsWith(".class")) {
                String className = "edu.eci.MicroSpringBoot."
                        + file.getName().replace(".class", "");
                Class<?> c = Class.forName(className);
                if (c.isAnnotationPresent(RestController.class)) {
                    System.out.println("Componente encontrado: " + className);
                    result.add(c);
                }
            }
        }
        return result;
    }
}