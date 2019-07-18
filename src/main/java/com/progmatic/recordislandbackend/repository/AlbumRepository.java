/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.repository;

import com.progmatic.recordislandbackend.domain.Album;
import com.progmatic.recordislandbackend.domain.Artist;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author balza
 */
public interface AlbumRepository extends JpaRepository<Album, Integer>, CustomAlbumRepository   {
}
