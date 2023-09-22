package com.yvan.williwork;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class DataGouvService {
    public DataGouvService() {
    }

    Logger logger = Logger.getLogger(DataGouvService.class.getName());

    @GetMapping("/zones")
    public ResponseEntity<HolidayResponse> calendrierByZone(@RequestParam("zone") String zone) {
        // Logic to fetch holidays based on zone and year
        // For demonstration purposes, let's assume we have retrieved the JSON response as a string
        String jsonResponse = "{\"2025-01-01\": \"1er janvier\", \"2025-04-21\": \"Lundi de Pâques\", \"2025-05-01\": \"1er mai\"}";

        ObjectMapper objectMapper = new ObjectMapper();
        HolidayResponse response;
        try {
            response = objectMapper.readValue(jsonResponse, HolidayResponse.class);
        } catch (JsonProcessingException e) {
            // Handle JSON parsing exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<HolidayResponse> calendrierByZoneAndYears(@RequestParam("zone") String zone, @RequestParam("year") int year) {
        //fetch holidays based on zone and year by calling another API
        final JsonNode byZoneAndYears = getByZoneAndYears(zone, year);
        logger.info("byZoneAndYears" + byZoneAndYears);
        // For demonstration purposes, let's assume we have retrieved the JSON response as a string
        //String jsonResponse = "{\"2025-01-01\": \"1er janvier\", \"2025-04-21\": \"Lundi de Pâques\", \"2025-05-01\": \"1er mai\"}";
        // Parse the JSON into a Map
        ObjectMapper objectMapper = new ObjectMapper();
        HolidayResponse response = new HolidayResponse();
        Set<Holiday> holidays;
        try {
            Map<String, String> holidayMap = objectMapper.readValue(byZoneAndYears.toString(), Map.class);
            // Create a set of Holiday objects
            holidays = holidayMap.entrySet().stream()
                    .map(entry -> {
                        Holiday holiday = new Holiday();
                        holiday.setDate(StringToDate(entry.getKey()));
                        holiday.setNom(entry.getValue());
                        return holiday;
                    })
                    .collect(Collectors.toSet());

        } catch (JsonProcessingException e) {
            // Handle JSON parsing exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        response.setHolidays(holidays);
        return ResponseEntity.ok(response);
    }
    private Date StringToDate(String date) {
        // Logic to convert a string to a date
        return new Date();
    }
    private JsonNode getByZoneAndYears( String zone, int years) {
        JsonNode response = null;
        try {
            String apiUrl = "https://calendrier.api.gouv.fr/jours-feries/metropole/2025.json";

            // Make a GET request to the API
            response = Unirest.get(apiUrl)
                    .header("accept", "application/json")
                    .asJson()
                    .getBody();

            // Print the JSON response
            System.out.println(response.toString());
        } catch (UnirestException e) {
            e.printStackTrace();
        }
        return response;
    }
}
