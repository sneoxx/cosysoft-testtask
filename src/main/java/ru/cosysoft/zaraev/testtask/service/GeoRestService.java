package ru.cosysoft.zaraev.testtask.service;

import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * Интерфейс сервиса GeoRestService для обработки запросов от контроллеров
 */
@Service
public interface GeoRestService {

    String getMap(String query);

    String getMapByState(String state);

    String getMapByCity(String city);

    Map<String, String> getMyResponse(String string);

}