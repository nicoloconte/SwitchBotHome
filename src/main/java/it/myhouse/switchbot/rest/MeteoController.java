package it.myhouse.switchbot.rest;

import it.myhouse.switchbot.service.MeteoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeteoController {

    private final MeteoService meteoService;

    public MeteoController(MeteoService meteoService) {
        this.meteoService = meteoService;
    }

    @GetMapping("/api/meteo/data")
    public String getMeteoData() throws Exception {
        return meteoService.getMeteoData();
    }
}