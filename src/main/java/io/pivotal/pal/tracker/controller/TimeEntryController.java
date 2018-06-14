package io.pivotal.pal.tracker.controller;

import io.pivotal.pal.tracker.TimeEntry;
import io.pivotal.pal.tracker.TimeEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TimeEntryController {

    private TimeEntryRepository timeEntryRepository;
    private CounterService counterService;
    private GaugeService gaugeService;


    //@Autowired
    public TimeEntryController(
            TimeEntryRepository timeEntryRepository,
            CounterService counterService,
            GaugeService gaugeService
    ) {
        this.timeEntryRepository = timeEntryRepository;
        this.counterService = counterService;
        this.gaugeService = gaugeService;
    }


    @RequestMapping(path = "/time-entries", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntryToCreate) {

        TimeEntry createdTimeEntry = timeEntryRepository.create(timeEntryToCreate);
        counterService.increment("TimeEntry.create");
        gaugeService.submit("timeEntries.count",timeEntryRepository.list().size());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTimeEntry);

    }

    @RequestMapping(path = "/time-entries/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TimeEntry> read(@PathVariable long id) {
        TimeEntry timeEntry = this.timeEntryRepository.find(id);

        if(null == timeEntry){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        counterService.increment("TimeEntry.read");
        return ResponseEntity.ok(timeEntry);
    }

    @RequestMapping(path = "/time-entries", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TimeEntry>> list() {
        List<TimeEntry> timeEntries = this.timeEntryRepository.list();
        counterService.increment("TimeEntry.list");
        return ResponseEntity.ok(timeEntries);
    }

    @RequestMapping(path = "/time-entries/{id}", method = RequestMethod.PUT,consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity update(@PathVariable long id, @RequestBody TimeEntry timeEntry) {

        TimeEntry updatedTimeEntry = this.timeEntryRepository.update(id, timeEntry);
        if(null == updatedTimeEntry){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        counterService.increment("TimeEntry.update");
        return ResponseEntity.ok(updatedTimeEntry);


    }

    @RequestMapping(path = "/time-entries/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TimeEntry> delete(@PathVariable long id) {
        counterService.increment("TimeEntry.delete");
        this.timeEntryRepository.delete(id);
        gaugeService.submit("timeEntries.count",timeEntryRepository.list().size());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
