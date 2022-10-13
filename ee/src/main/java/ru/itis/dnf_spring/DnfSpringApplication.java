package ru.itis.dnf_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class DnfSpringApplication {

    public static final Character[] VARIABLES = {'X', 'Y', 'Z', 'W', 'A', 'B'};
    public static Map<Integer, Integer> NUM_VARS_BY_VEC_LEN = new HashMap<>();

    public static void main(String[] args) {
        for(int i = 2; i <= 6; i++) NUM_VARS_BY_VEC_LEN.put((int) Math.pow(2, i), i);
        SpringApplication.run(DnfSpringApplication.class, args);
    }

}
