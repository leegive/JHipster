package com.renmaituan.shop.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.renmaituan.shop.domain.Demo;
import com.renmaituan.shop.service.DemoService;
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
 * REST controller for managing Demo.
 */
@RestController
@RequestMapping("/api")
public class DemoResource {

    private final Logger log = LoggerFactory.getLogger(DemoResource.class);
        
    @Inject
    private DemoService demoService;

    /**
     * POST  /demos : Create a new demo.
     *
     * @param demo the demo to create
     * @return the ResponseEntity with status 201 (Created) and with body the new demo, or with status 400 (Bad Request) if the demo has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/demos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Demo> createDemo(@RequestBody Demo demo) throws URISyntaxException {
        log.debug("REST request to save Demo : {}", demo);
        if (demo.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("demo", "idexists", "A new demo cannot already have an ID")).body(null);
        }
        Demo result = demoService.save(demo);
        return ResponseEntity.created(new URI("/api/demos/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("demo", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /demos : Updates an existing demo.
     *
     * @param demo the demo to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated demo,
     * or with status 400 (Bad Request) if the demo is not valid,
     * or with status 500 (Internal Server Error) if the demo couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/demos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Demo> updateDemo(@RequestBody Demo demo) throws URISyntaxException {
        log.debug("REST request to update Demo : {}", demo);
        if (demo.getId() == null) {
            return createDemo(demo);
        }
        Demo result = demoService.save(demo);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("demo", demo.getId().toString()))
            .body(result);
    }

    /**
     * GET  /demos : get all the demos.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of demos in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/demos",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Demo>> getAllDemos(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Demos");
        Page<Demo> page = demoService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/demos");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /demos/:id : get the "id" demo.
     *
     * @param id the id of the demo to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the demo, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/demos/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Demo> getDemo(@PathVariable Long id) {
        log.debug("REST request to get Demo : {}", id);
        Demo demo = demoService.findOne(id);
        return Optional.ofNullable(demo)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /demos/:id : delete the "id" demo.
     *
     * @param id the id of the demo to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/demos/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteDemo(@PathVariable Long id) {
        log.debug("REST request to delete Demo : {}", id);
        demoService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("demo", id.toString())).build();
    }

}
