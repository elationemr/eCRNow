package com.drajer.bsa.controller;

import com.drajer.bsa.service.TimeZoneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TimeZoneController {

  @Autowired private TimeZoneService timeZoneService;

  private static final Logger logger = LoggerFactory.getLogger(TimeZoneController.class);

  @GetMapping("/api/timezone")
  public String getDatabaseTimezone() {
    return timeZoneService.getDatabaseTimezone();
  }

  @PostMapping("/api/timezone")
  public ResponseEntity<String> setTimeZone(@RequestParam String timeZone) {
    try {
      timeZoneService.setDatabaseTimezone(timeZone);
      return ResponseEntity.ok("Time zone updated successfully.");
    } catch (Exception e) {
      logger.error("Error updating time zone", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error updating time zone.");
    }
  }
}
