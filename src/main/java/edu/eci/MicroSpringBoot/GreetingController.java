package edu.eci.MicroSpringBoot;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private long counter = 0;

    @GetMapping("/greeting")
    public String greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hola " + name;
    }
}