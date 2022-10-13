package ru.itis.dnf_spring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.itis.dnf_spring.models.DNF;
import ru.itis.dnf_spring.services.DnfService;

import java.util.List;

@Controller
public class MainController {

    @GetMapping("/")
    public String helloWorld() {
        return "dnf-hello";
    }

    @PostMapping("/process")
    public String getResult(String vec, Model model) {
        model.addAttribute("vec", vec);

        try {
            // Строим СДНФ по вектору
            DNF sdnf = DnfService.getSdnfByVector(vec);
            model.addAttribute("sdnf", sdnf);

            // Строим СокрДНФ методом Квайна
            DNF sokrDnf = DnfService.getSorkDnfBySdnf(sdnf);
            model.addAttribute("sokrdnf", sokrDnf);

            // Получаем все тупиковые ДНФ по СокрДНФ:
            List<DNF> deadend = DnfService.getAllDeadEnd(sokrDnf);
            model.addAttribute("deadend", deadend);

            List<DNF> shortest = DnfService.getShortest(deadend);
            model.addAttribute("shortest", shortest);
            List<DNF> minimum = DnfService.getMinimum(deadend);
            model.addAttribute("minimum", minimum);
        }catch (Exception e) {
            model.addAttribute("err", e.getLocalizedMessage());
            return "dnf-error";
        }

        return "dnf-result";
    }

}
