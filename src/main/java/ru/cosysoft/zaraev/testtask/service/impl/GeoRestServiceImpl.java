package ru.cosysoft.zaraev.testtask.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ru.cosysoft.zaraev.testtask.api.FeignClientForOpenMap;
import ru.cosysoft.zaraev.testtask.service.GeoRestService;

import java.util.*;


/**
 * Сервисный класс для обработки запросов от контроллеров
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class GeoRestServiceImpl implements GeoRestService {

    private final FeignClientForOpenMap feignClientForOpenMap;

    /**
     * Обработка запроса с query параметром
     *
     * @param query - значение q параметра
     * @return - ответ от сервиса OSM
     */
    @Cacheable
    @Override
    public String getMap(String query) {
        String responseFromService = feignClientForOpenMap.getMap(query);
        log.debug("getMap() Запрос: {} успешно вернул ответ {}", query, responseFromService);
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
        String responseFromService = feignClientForOpenMap.getMapByState(state);
        log.debug("getMap() Запрос: {} успешно вернул ответ {}", state, responseFromService);
        return responseFromService;
    }

    /**
     * Обработка запроса с city параметром
     *
     * @param city - значение city параметра
     * @return - ответ от сервиса OSM
     */
    @Cacheable
    @Override
    public String getMapByCity(String city) {
        String responseFromService = feignClientForOpenMap.getMapByCity(city);
        log.debug("getMap() Запрос: {} успешно вернул ответ {}", city, responseFromService);
        return responseFromService;
    }

    /**
     * Парсинг строки в коллекцию Мар, возвращающая массив координат наибольшей части гео-объекта и координаты его географического центра
     *
     * @param stringResponse - исходная строка полученная из OpenMap c данными объекта
     * @return - полученная мапа
     */
    @Override
    public Map<String, String> getMyResponse(String stringResponse) {
        JSONArray JsonObj = new JSONArray(stringResponse);
        JSONArray jsonArrayCoordinates = JsonObj.getJSONObject(0).getJSONObject("geojson").getJSONArray("coordinates");
        JSONArray largestArrayOfCoordinates = getLargestArrayOfCoordinates(jsonArrayCoordinates);
        Map<String, String> myResponse = new HashMap<>();
        myResponse.put("coordinates", largestArrayOfCoordinates.toString());
        myResponse.put("geographicCenter", getGeographicCenter(largestArrayOfCoordinates));
        log.debug("getGeographicCenter() Получена Мар из строки {} ", myResponse);
        return myResponse;
    }

    /**
     * Метод нахождение географического центра массива координат
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
        log.debug("getMyResponse() JSONArray успешно сконвертирован в коллекцию List<String>");
        return Arrays.asList(stringArrayCoordinates);
    }

    /**
     * Найти площадь коллекции List координат
     *
     * @param stringListCoordinates - коллекция List<String> с координатами
     * @return - найденная площадь типа double
     */
    private double getCoordinateListArea(List<String> stringListCoordinates) {
        List<String> stringListCoordinatesX = new ArrayList<>();
        List<String> stringListCoordinatesY = new ArrayList<>();
        for (int i = 0; i < stringListCoordinates.size(); i++) {
            if (i % 2 == 0) {
                stringListCoordinatesX.add(stringListCoordinates.get(i));
            } else {
                stringListCoordinatesY.add(stringListCoordinates.get(i));
            }
        }
        double sum = 0;
        int numberOfVertices = stringListCoordinatesX.size();
        for (int i = 0; i < numberOfVertices - 1; i++) {
            sum = sum + Double.parseDouble(stringListCoordinatesX.get(i)) * Double.parseDouble(stringListCoordinatesY.get(i + 1))
                    % numberOfVertices - Double.parseDouble(stringListCoordinatesY.get(i)) * Double.parseDouble(stringListCoordinatesX.get(i + 1)) % numberOfVertices;
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