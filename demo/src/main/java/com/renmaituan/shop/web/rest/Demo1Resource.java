package com.renmaituan.shop.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.renmaituan.shop.domain.Demo1;

import com.renmaituan.shop.repository.Demo1Repository;
import com.renmaituan.shop.web.rest.util.HeaderUtil;
import com.renmaituan.shop.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * REST controller for managing Demo1.
 */
@RestController
@RequestMapping("/api")
public class Demo1Resource {

    private final Logger log = LoggerFactory.getLogger(Demo1Resource.class);
        
    @Inject
    private Demo1Repository demo1Repository;

    /**
     * POST  /demo-1-s : Create a new demo1.
     *
     * @param demo1 the demo1 to create
     * @return the ResponseEntity with status 201 (Created) and with body the new demo1, or with status 400 (Bad Request) if the demo1 has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/demo-1-s",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Demo1> createDemo1(@RequestBody Demo1 demo1) throws URISyntaxException {
        log.debug("REST request to save Demo1 : {}", demo1);
        if (demo1.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("demo1", "idexists", "A new demo1 cannot already have an ID")).body(null);
        }
        Demo1 result = demo1Repository.save(demo1);
        return ResponseEntity.created(new URI("/api/demo-1-s/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("demo1", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /demo-1-s : Updates an existing demo1.
     *
     * @param demo1 the demo1 to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated demo1,
     * or with status 400 (Bad Request) if the demo1 is not valid,
     * or with status 500 (Internal Server Error) if the demo1 couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/demo-1-s",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Demo1> updateDemo1(@RequestBody Demo1 demo1) throws URISyntaxException {
        log.debug("REST request to update Demo1 : {}", demo1);
        if (demo1.getId() == null) {
            return createDemo1(demo1);
        }
        Demo1 result = demo1Repository.save(demo1);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("demo1", demo1.getId().toString()))
            .body(result);
    }

    /**
     * GET  /demo-1-s : get all the demo1S.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of demo1S in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/demo-1-s",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Demo1>> getAllDemo1S(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Demo1S");
        Page<Demo1> page = demo1Repository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/demo-1-s");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /demo-1-s/:id : get the "id" demo1.
     *
     * @param id the id of the demo1 to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the demo1, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/demo-1-s/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Demo1> getDemo1(@PathVariable Long id) {
        log.debug("REST request to get Demo1 : {}", id);
        Demo1 demo1 = demo1Repository.findOne(id);
        return Optional.ofNullable(demo1)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /demo-1-s/:id : delete the "id" demo1.
     *
     * @param id the id of the demo1 to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/demo-1-s/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDemo1(@PathVariable Long id) {
        log.debug("REST request to delete Demo1 : {}", id);
        demo1Repository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("demo1", id.toString())).build();
    }

}
