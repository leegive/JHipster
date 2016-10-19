package com.renmaituan.shop.web.rest;

import com.renmaituan.shop.config.DefaultProfileUtil;
import com.renmaituan.shop.config.JHipsterProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TestResource {

    @RequestMapping(value = "/test",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public String getActiveProfiles() {
        return "Hello Test";
    }

}
