package ru.cosysoft.zaraev.testtask.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.cosysoft.zaraev.testtask.api.MyFeignClient;
import ru.cosysoft.zaraev.testtask.service.MyService;

import java.util.HashMap;
import java.util.Map;

/**
 * Сервисный класс для обработки web запросов
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MyServiceImpl implements MyService {

    private final MyFeignClient myFeignClient;

    /**
     * Обработка запроса с q параметром
     * @param q - значение q параметра
     * @return - ответ от сервиса OSM
     */
    @Cacheable
    @Override
    public String getMap(String q) {
        String responseFromService = myFeignClient.getMap(q);
        log.debug("getMap() Запрос: {} успешно вернул ответ {}", q, responseFromService);
        return responseFromService;
    }

    /**
     * Обработка запроса с state параметром
     * @param state - значение state параметра
     * @return - ответ от сервиса OSM
     */
    @Cacheable
    @Override
    public String getMapByState(String state) {
        String responseFromService = myFeignClient.getMapByState(state);
        log.debug("getMap() Запрос: {} успешно вернул ответ {}", state, responseFromService);
        return responseFromService;
    }

    /**
     * Обработка city с q параметром
     * @param city - значение city параметра
     * @return - ответ от сервиса OSM
     */
    @Cacheable
    @Override
    public String getMapByCity(String city) {
        String responseFromService = myFeignClient.getMapByCity(city);
        log.debug("getMap() Запрос: {} успешно вернул ответ {}", city, responseFromService);
        return responseFromService;
    }

    /**
     * Метод нахождение географичесвого центра массива координат
     * @param jsonArrayCoordinates - массив координат
     * @return - рассчитанные координаты в виде строки
     */
    @Override
    public String getGeographicCenter(JSONArray jsonArrayCoordinates) {
        StringBuilder stringBuilderCoordinates = new StringBuilder();
        for (int i = 0; i < jsonArrayCoordinates.length(); i++) {
            stringBuilderCoordinates.append(jsonArrayCoordinates.get(i).toString().replaceAll("\\[", "").replaceAll("]", "").replaceAll(" ", "")).append(",");
        }
        String string = new String(stringBuilderCoordinates);
        String[] stringArrayCoordinates = string.split(",");
        double resultX = 0.0;
        double resultY = 0.0;
        int i = 1;
        for (String resultString : stringArrayCoordinates) {
            if (i % 2 != 0) {
                resultX = resultX + Double.parseDouble(resultString.trim());
            } else {
                resultY = resultY + Double.parseDouble(resultString.trim());
            }
            i++;
        }
        resultX = resultX / i * 2;
        resultY = resultY / i * 2;
        log.debug("getGeographicCenter() Расчитан географический центр массива координат {} {}", resultX, resultY);
        return resultX + ", " + resultY;
    }

    /**
     * Парсинг строки в Мар
     * @param string - исходная строка
     * @return - полученная мапа
     */
    @Override
    public Map<String, String> getMyResponse(String string) {
        JSONArray JsonObj = new JSONArray(string);
        JSONArray jsonArrayCoordinates = JsonObj.getJSONObject(0).getJSONObject("geojson").getJSONArray("coordinates");
        Map<String, String> myResponse = new HashMap<>();
        myResponse.put("coordinates", jsonArrayCoordinates.toString());
        myResponse.put("geographicCenter", getGeographicCenter(jsonArrayCoordinates));
        log.debug("getGeographicCenter() Получена Мар из строки {} ", myResponse);
        return myResponse;
    }

}