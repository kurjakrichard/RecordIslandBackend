/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend;

import com.progmatic.recordislandbackend.domain.Artist;
import java.util.HashSet;
import java.util.Set;
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
