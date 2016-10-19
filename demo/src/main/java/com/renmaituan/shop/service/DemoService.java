package com.renmaituan.shop.service;

import com.renmaituan.shop.domain.Demo;
import com.renmaituan.shop.repository.DemoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Demo.
 */
@Service
@Transactional
public class DemoService {

    private final Logger log = LoggerFactory.getLogger(DemoService.class);
    
    @Inject
    private DemoRepository demoRepository;

    /**
     * Save a demo.
     *
     * @param demo the entity to save
     * @return the persisted entity
     */
    public Demo save(Demo demo) {
        log.debug("Request to save Demo : {}", demo);
        Demo result = demoRepository.save(demo);
        return result;
    }

    /**
     *  Get all the demos.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Demo> findAll(Pageable pageable) {
        log.debug("Request to get all Demos");
        Page<Demo> result = demoRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one demo by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Demo findOne(Long id) {
        log.debug("Request to get Demo : {}", id);
        Demo demo = demoRepository.findOne(id);
        return demo;
    }

    /**
     *  Delete the  demo by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Demo : {}", id);
        demoRepository.delete(id);
    }
}
