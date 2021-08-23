package ru.cosysoft.zaraev.testtask.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.cosysoft.zaraev.testtask.api.MyFeignClient;
import ru.cosysoft.zaraev.testtask.service.MyService;

import java.util.*;


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
     *
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
     *
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
     *
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
     * Парсинг строки в Мар-у, возвращающаю массив координат наибольшей части гео-объекта и координаты его географического центра
     *
     * @param string - исходная строка
     * @return - полученная мапа
     */
    @Override
    public Map<String, String> getMyResponse(String string) {
        JSONArray JsonObj = new JSONArray(string);
        JSONArray jsonArrayCoordinates = JsonObj.getJSONObject(0).getJSONObject("geojson").getJSONArray("coordinates");
        JSONArray largestArrayOfCoordinates = getLargestArrayOfCoordinates(jsonArrayCoordinates);
        Map<String, String> myResponse = new HashMap<>();
        myResponse.put("coordinates", largestArrayOfCoordinates.toString());
        myResponse.put("geographicCenter", getGeographicCenter(largestArrayOfCoordinates));
        log.debug("getGeographicCenter() Получена Мар из строки {} ", myResponse);
        return myResponse;
    }

    /**
     * Метод нахождение географичесвого центра массива координат
     *
     * @param jsonArrayCoordinates - массив координат
     * @return - рассчитанные координаты в виде строки
     */
    private String getGeographicCenter(JSONArray jsonArrayCoordinates) {
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
     * Конвертация координат из JSONArray в коллекцию List<String> с устранением вложенности
     *
     * @param jsonArrayCoordinates - исходный JSONArray
     * @return - коллекция List<String> с координатами
     */
    public List<String> getMyResponse(JSONArray jsonArrayCoordinates) {
        StringBuilder stringBuilderCoordinates = new StringBuilder();
        for (int i = 0; i < jsonArrayCoordinates.length(); i++) {
            stringBuilderCoordinates.append(jsonArrayCoordinates.get(i).toString().replaceAll("\\[", "").replaceAll("]", "").replaceAll(" ", "")).append(",");
        }
        String string1 = new String(stringBuilderCoordinates);
        String[] stringArrayCoordinates = string1.split(",");
        log.debug("getMyResponse() JSONArray успешно скновертирован в коллекцию List<String>");
        return Arrays.asList(stringArrayCoordinates);
    }

    /**
     * Найти площадь коллекции List с координатами
     *
     * @param stringList - коллекция List<String> с координатами
     * @return - площадь
     */
    private double getCoordinateListArea(List<String> stringList) {
        List<String> stringListCoordinates1X = new ArrayList<>();
        List<String> stringListCoordinates1Y = new ArrayList<>();
        for (int i = 0; i < stringList.size(); i++) {
            if (i % 2 == 0) {
                stringListCoordinates1X.add(stringList.get(i));
            } else {
                stringListCoordinates1Y.add(stringList.get(i));
            }
        }
        double sum = 0;
        int numberOfVertices = stringListCoordinates1X.size();
        for (int i = 0; i < numberOfVertices - 1; i++) {
            sum = sum + Double.parseDouble(stringListCoordinates1X.get(i)) * Double.parseDouble(stringListCoordinates1Y.get(i + 1)) % numberOfVertices - Double.parseDouble(stringListCoordinates1Y.get(i)) * Double.parseDouble(stringListCoordinates1X.get(i + 1)) % numberOfVertices;
        }
        sum = Math.abs(sum / 2);
        log.debug("getCoordinateListArea() Найдена площадь коллекции List: {}", sum);
        return sum;
    }

    /**
     * Найти массив координат c наибольшей площадью
     *
     * @param jsonArrayCoordinates - JSONArray с массивами координат
     * @return - наибольший массив координат
     */
    private JSONArray getLargestArrayOfCoordinates(JSONArray jsonArrayCoordinates) {
        double maxArea = 0;
        int indexLargestArray = 0;
        for (int i = 0; i < jsonArrayCoordinates.length(); i++) {
            List<String> stringList = getMyResponse(jsonArrayCoordinates.getJSONArray(i));
            double coordinateArea = getCoordinateListArea(stringList);
            if (maxArea < coordinateArea) {
                maxArea = coordinateArea;
                indexLargestArray = i;
            }
        }
        JSONArray maxJsonArray = jsonArrayCoordinates.getJSONArray(indexLargestArray);
        log.debug("getLargestArrayOfCoordinates() Найден массив координат c наибольшей площадью {} ", maxJsonArray);
        return maxJsonArray;
    }
}