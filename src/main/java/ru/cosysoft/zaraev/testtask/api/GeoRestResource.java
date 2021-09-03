package ru.cosysoft.zaraev.testtask.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


/**
 * Интерфейс для работы с контроллером MyResourceImpl
 */
public interface GeoRestResource {

    @RequestMapping(value = "/", method = RequestMethod.GET, params = "query")
    Map<String, String> getMap(@RequestParam String query);

    @RequestMapping(value = "/", method = RequestMethod.GET, params = "state")
    Map<String, String> getMapByState(@RequestParam String state);

    @RequestMapping(value = "/", method = RequestMethod.GET, params = "city")
    Map<String, String> getMapByCity(@RequestParam String city);

}