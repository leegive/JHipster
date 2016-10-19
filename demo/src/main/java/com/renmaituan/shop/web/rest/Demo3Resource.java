package com.renmaituan.shop.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.renmaituan.shop.domain.Demo3;

import com.renmaituan.shop.repository.Demo3Repository;
import com.renmaituan.shop.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Demo3.
 */
@RestController
@RequestMapping("/api")
public class Demo3Resource {

    private final Logger log = LoggerFactory.getLogger(Demo3Resource.class);
        
    @Inject
    private Demo3Repository demo3Repository;

    /**
     * POST  /demo-3-s : Create a new demo3.
     *
     * @param demo3 the demo3 to create
     * @return the ResponseEntity with status 201 (Created) and with body the new demo3, or with status 400 (Bad Request) if the demo3 has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/demo-3-s",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Demo3> createDemo3(@RequestBody Demo3 demo3) throws URISyntaxException {
        log.debug("REST request to save Demo3 : {}", demo3);
        if (demo3.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("demo3", "idexists", "A new demo3 cannot already have an ID")).body(null);
        }
        Demo3 result = demo3Repository.save(demo3);
        return ResponseEntity.created(new URI("/api/demo-3-s/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("demo3", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /demo-3-s : Updates an existing demo3.
     *
     * @param demo3 the demo3 to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated demo3,
     * or with status 400 (Bad Request) if the demo3 is not valid,
     * or with status 500 (Internal Server Error) if the demo3 couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/demo-3-s",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Demo3> updateDemo3(@RequestBody Demo3 demo3) throws URISyntaxException {
        log.debug("REST request to update Demo3 : {}", demo3);
        if (demo3.getId() == null) {
            return createDemo3(demo3);
        }
        Demo3 result = demo3Repository.save(demo3);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("demo3", demo3.getId().toString()))
            .body(result);
    }

    /**
     * GET  /demo-3-s : get all the demo3S.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of demo3S in body
     */
    @RequestMapping(value = "/demo-3-s",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Demo3> getAllDemo3S() {
        log.debug("REST request to get all Demo3S");
        List<Demo3> demo3S = demo3Repository.findAll();
        return demo3S;
    }

    /**
     * GET  /demo-3-s/:id : get the "id" demo3.
     *
     * @param id the id of the demo3 to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the demo3, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/demo-3-s/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Demo3> getDemo3(@PathVariable Long id) {
        log.debug("REST request to get Demo3 : {}", id);
        Demo3 demo3 = demo3Repository.findOne(id);
        return Optional.ofNullable(demo3)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /demo-3-s/:id : delete the "id" demo3.
     *
     * @param id the id of the demo3 to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/demo-3-s/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDemo3(@PathVariable Long id) {
        log.debug("REST request to delete Demo3 : {}", id);
        demo3Repository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("demo3", id.toString())).build();
    }

}
