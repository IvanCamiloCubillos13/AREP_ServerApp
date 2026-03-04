package edu.eci.MicroSpringBoot;

@RestController
public class HelloController {

	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}
	@GetMapping("/pi")
	public String getPi() {
		return "Pi =" + Math.PI;
	}
	@GetMapping("/hello")
	public String hello() {
		return "Hello world";
	}
}
