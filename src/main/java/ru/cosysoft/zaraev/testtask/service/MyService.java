package ru.cosysoft.zaraev.testtask.service;

import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * Интерейс Сервиса для обработки web запросов
 */
@Service
public interface MyService {

    String getMap(String q);

    String getMapByState(String state);

    String getMapByCity(String city);

    Map<String, String> getMyResponse(String string);

}