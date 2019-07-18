/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

/**
 *
 * @author balza
 */
@Repository
public class CustomAlbumRepositoryImpl implements CustomAlbumRepository {
    
    @PersistenceContext
    private EntityManager em;
}
