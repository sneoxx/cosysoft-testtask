package ru.cosysoft.zaraev.testtask.api;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * Интерфейс получающий данные с сервиса OSM (Open Street Map)
 *
 */
@FeignClient(name = "OpenMap", url = "https://nominatim.openstreetmap.org/search?format=json&polygon_geojson=1")
public interface MyFeignClient {

    @GetMapping(value = "/{q}")
    String getMap(@RequestParam String q);

    @GetMapping(value = "/{state}")
    String getMapByState(@RequestParam String state);

    @GetMapping(value = "/{city}")
    String getMapByCity(@RequestParam String city);

}