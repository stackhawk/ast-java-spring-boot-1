package com.example.ast.service;

import com.example.ast.domain.Continent;
import com.example.ast.domain.Hotel;
import com.example.ast.dao.jpa.HotelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Sample service to demonstrate what the API would use to get things done
 */
@Service
public class HotelService {

    private static final Logger log = LoggerFactory.getLogger(HotelService.class);

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    CounterService counterService;

    @Autowired
    GaugeService gaugeService;

    public HotelService() {
    }

    public Hotel createHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    public Hotel getHotel(long id) {
        return hotelRepository.findOne(id);
    }

    public void updateHotel(Hotel hotel) {
        hotelRepository.save(hotel);
    }

    public void deleteHotel(Long id) {
        hotelRepository.delete(id);
    }

    //http://goo.gl/7fxvVf
    public Page<Hotel> getAllHotels(Integer page, Integer size) {
        Page<Hotel> pageOfHotels = hotelRepository.findAll(new PageRequest(page, size));
        // example of adding to the /metrics
        if (size > 50) {
            counterService.increment("Khoubyari.HotelService.getAll.largePayload");
        }
        return pageOfHotels;
    }

    public Hotel randomHotel() {
        Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
        List<Hotel> hotels = new ArrayList<>(hotelRepository.findAll(pageable).getContent());
        Collections.shuffle(hotels);
        return hotels.isEmpty() ? null : hotels.get(0);
    }

    public Map<Continent, List<Hotel>> hotelsByLocation() {
        Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
        List<Hotel> hotels = new ArrayList<>(hotelRepository.findAll(pageable).getContent());
        Collections.shuffle(hotels);

        Map<Continent, List<Hotel>> hotelsByLocation = new HashMap<>();

        for (Hotel hotel : hotels) {
            Continent[] continents = Continent.values();
            String rnd = hotel.getName() + hotel.getCity() + hotel.getDescription() + hotel.getId();
            int i = rnd.length() % continents.length;
            Continent continent = continents[i];

            hotelsByLocation.computeIfAbsent(continent, k -> new ArrayList<>());
            hotelsByLocation.get(continent).add(hotel);
        }
        return hotelsByLocation;
    }
}
