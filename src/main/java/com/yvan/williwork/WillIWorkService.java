package com.yvan.williwork;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class WillIWorkService {
    @Autowired
    DataGouvService dataGouvService;

    Logger logger = Logger.getLogger(WillIWorkService.class.getName());

    public WillIWorkService() {
        // constructor is empty
    }

    public boolean willIWork(String zone, int days) {
        if (zone == null || zone.isEmpty()) {
            throw new IllegalArgumentException("Zone cannot be null or empty");
        }
        HolidayResponse holidayResponse = new HolidayResponse();
        if (Math.abs(days) > 365) {
            //Create a method to transform numbers of days into numbers of years
            final int years = days / 365;
            final ResponseEntity<HolidayResponse> holidayResponseResponseEntity = dataGouvService.calendrierByZoneAndYears(zone, years);
            holidayResponse = holidayResponseResponseEntity.getBody();
        } else {

            final ResponseEntity<HolidayResponse> holidayResponseResponseEntity = dataGouvService.calendrierByZone(zone);
            holidayResponse = holidayResponseResponseEntity.getBody();
        }
        logger.info("holidayResponse" + holidayResponse);
        Date nowDate = new Date();
        assert holidayResponse != null;
        return isWorkDay(holidayResponse, days, nowDate);
    }

    private boolean isWorkDay(HolidayResponse holidayResponse, int days, Date nowDate) {
        final Date newDate;
        nowDate.setTime(nowDate.getTime() + days * 24 * 60 * 60 * 1000);
        newDate = nowDate;
        final Optional<Holiday> first = holidayResponse
                .getHolidays()
                .stream()
                .filter(holiday -> compareDatesWithoutTime(holiday.getDate(), newDate)).findFirst();
        return first.isEmpty();
    }

    private boolean compareDatesWithoutTime(Date date1, Date date2) {
        // Create Calendar instances to extract year, month, and day components
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(date1);
        cal2.setTime(date2);

        // Extract year, month, and day components
        int year1 = cal1.get(Calendar.YEAR);
        int month1 = cal1.get(Calendar.MONTH);
        int day1 = cal1.get(Calendar.DAY_OF_MONTH);

        int year2 = cal2.get(Calendar.YEAR);
        int month2 = cal2.get(Calendar.MONTH);
        int day2 = cal2.get(Calendar.DAY_OF_MONTH);

        // Compare the year, month, and day components
        return year1 == year2 && month1 == month2 && day1 == day2;
    }
}
