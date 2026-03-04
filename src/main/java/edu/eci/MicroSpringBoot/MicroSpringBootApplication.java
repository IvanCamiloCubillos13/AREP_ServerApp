package edu.eci.MicroSpringBoot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MicroSpringBootApplication {

	static Map<String, Method> controllerMethods = new HashMap<>();

	public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException {
		SpringApplication.run(MicroSpringBootApplication.class, args);
		System.out.println("Cargando componentes");

		Class c = Class.forName(args[0]);

		if(c.isAnnotationPresent(RestController.class)){
			for(Method m: c.getDeclaredMethods()){
				if(m.isAnnotationPresent(GetMapping.class)){
					GetMapping anotacion = m.getAnnotation(GetMapping.class);
					String path = anotacion.value();
					controllerMethods.put(path, m);
				}
			}
		}

		System.out.println("Invoking Method for path: " + args[1]);
		Method m = controllerMethods.get(args[1]);
		System.out.println(m.invoke(null));
	}

}
