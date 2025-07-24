package com.drajer.ecrapp.controller;

import com.drajer.ecrapp.util.ScheduledTaskUtil;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduled-tasks")
public class SchedulerUtilityController {

  @Autowired private ScheduledTaskUtil scheduledTaskUtil;

  private static final Logger logger = LoggerFactory.getLogger(SchedulerUtilityController.class);

  @PostMapping("/export")
  public ResponseEntity<String> exportScheduledTasks() {
    try {
      String filePath = scheduledTaskUtil.exportScheduledTasks();
      return ResponseEntity.ok("Scheduled tasks exported successfully to file: " + filePath);
    } catch (IOException e) {
      logger.error("Error while exporting scheduled tasks to file", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error exporting scheduled tasks.");
    }
  }

  @PostMapping("/import")
  public ResponseEntity<String> importScheduledTasks() {
    try {
      String filePath = scheduledTaskUtil.importScheduledTasks();
      return ResponseEntity.ok("Scheduled tasks imported successfully from file: " + filePath);
    } catch (IOException e) {
      logger.error("Error while importing scheduled tasks from file", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Error importing scheduled tasks.");
    }
  }
}
