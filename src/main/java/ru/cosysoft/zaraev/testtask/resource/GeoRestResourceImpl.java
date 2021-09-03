package ru.cosysoft.zaraev.testtask.resource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import ru.cosysoft.zaraev.testtask.api.GeoRestResource;
import ru.cosysoft.zaraev.testtask.service.GeoRestService;
import java.util.Map;


/**
 * Класс RestController для обработки веб запросов
 */
@Slf4j
@RequiredArgsConstructor
@RestController
public class GeoRestResourceImpl implements GeoRestResource {

    private final GeoRestService geoRestService;

    /**
     * Обработка запроса к контроллеру с q параметром
     * @param query - значение q параметра
     * @return - вернет массив координат и его географический центр
     */
    @Override
    public Map<String, String> getMap(String query) {
        String responseFromService = geoRestService.getMap(query);
        Map<String, String> myResponse = geoRestService.getMyResponse(responseFromService);
        log.debug("getMap() Запрос: {} успешно вернул ответ {}", query, myResponse);
        return myResponse;
    }

    /**
     * Обработка запроса к контроллеру с state параметром
     * @param state - значение state параметра
     * @return - вернет массив координат и его географический центр
     */
    @Override
    public Map<String, String> getMapByState(String state) {
        String responseFromService = geoRestService.getMapByState(state);
        Map<String, String> myResponse = geoRestService.getMyResponse(responseFromService);
        log.debug("getMapByState() Запрос: {} успешно вернул ответ {}", state, myResponse);
        return myResponse;
    }

    /**
     * Обработка запроса к контроллеру с city параметром
     * @param city - значение city параметра
     * @return - вернет массив координат и его географический центр
     */
    @Override
    public Map<String, String> getMapByCity(String city) {
        String responseFromService = geoRestService.getMapByCity(city);
        Map<String, String> myResponse = geoRestService.getMyResponse(responseFromService);
        log.debug("getMapByCity() Запрос: {} успешно вернул ответ {}", city, myResponse);
        return myResponse;
    }

}