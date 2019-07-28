package com.progmatic.recordislandbackend;

import java.time.LocalDate;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 *
 * @author balza
 */
@SpringBootApplication
@ComponentScan("com.progmatic.recordislandbackend")
public class RecordIslandApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecordIslandApplication.class, args);
        
    }
}
