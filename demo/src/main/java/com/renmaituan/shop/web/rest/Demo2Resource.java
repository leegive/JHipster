package com.renmaituan.shop.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.renmaituan.shop.domain.Demo2;

import com.renmaituan.shop.repository.Demo2Repository;
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
 * REST controller for managing Demo2.
 */
@RestController
@RequestMapping("/api")
public class Demo2Resource {

    private final Logger log = LoggerFactory.getLogger(Demo2Resource.class);
        
    @Inject
    private Demo2Repository demo2Repository;

    /**
     * POST  /demo-2-s : Create a new demo2.
     *
     * @param demo2 the demo2 to create
     * @return the ResponseEntity with status 201 (Created) and with body the new demo2, or with status 400 (Bad Request) if the demo2 has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/demo-2-s",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Demo2> createDemo2(@RequestBody Demo2 demo2) throws URISyntaxException {
        log.debug("REST request to save Demo2 : {}", demo2);
        if (demo2.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("demo2", "idexists", "A new demo2 cannot already have an ID")).body(null);
        }
        Demo2 result = demo2Repository.save(demo2);
        return ResponseEntity.created(new URI("/api/demo-2-s/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("demo2", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /demo-2-s : Updates an existing demo2.
     *
     * @param demo2 the demo2 to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated demo2,
     * or with status 400 (Bad Request) if the demo2 is not valid,
     * or with status 500 (Internal Server Error) if the demo2 couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/demo-2-s",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Demo2> updateDemo2(@RequestBody Demo2 demo2) throws URISyntaxException {
        log.debug("REST request to update Demo2 : {}", demo2);
        if (demo2.getId() == null) {
            return createDemo2(demo2);
        }
        Demo2 result = demo2Repository.save(demo2);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("demo2", demo2.getId().toString()))
            .body(result);
    }

    /**
     * GET  /demo-2-s : get all the demo2S.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of demo2S in body
     */
    @RequestMapping(value = "/demo-2-s",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Demo2> getAllDemo2S() {
        log.debug("REST request to get all Demo2S");
        List<Demo2> demo2S = demo2Repository.findAll();
        return demo2S;
    }

    /**
     * GET  /demo-2-s/:id : get the "id" demo2.
     *
     * @param id the id of the demo2 to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the demo2, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/demo-2-s/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Demo2> getDemo2(@PathVariable Long id) {
        log.debug("REST request to get Demo2 : {}", id);
        Demo2 demo2 = demo2Repository.findOne(id);
        return Optional.ofNullable(demo2)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /demo-2-s/:id : delete the "id" demo2.
     *
     * @param id the id of the demo2 to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/demo-2-s/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDemo2(@PathVariable Long id) {
        log.debug("REST request to delete Demo2 : {}", id);
        demo2Repository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("demo2", id.toString())).build();
    }

}
