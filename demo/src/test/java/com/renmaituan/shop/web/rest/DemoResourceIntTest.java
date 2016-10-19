package com.renmaituan.shop.web.rest;

import com.renmaituan.shop.DemoApp;

import com.renmaituan.shop.config.SecurityBeanOverrideConfiguration;

import com.renmaituan.shop.domain.Demo;
import com.renmaituan.shop.repository.DemoRepository;
import com.renmaituan.shop.service.DemoService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DemoResource REST controller.
 *
 * @see DemoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoApp.class, SecurityBeanOverrideConfiguration.class})
public class DemoResourceIntTest {

    private static final String DEFAULT_TITLE = "AAAAA";
    private static final String UPDATED_TITLE = "BBBBB";

    private static final Integer DEFAULT_LEVEL = 1;
    private static final Integer UPDATED_LEVEL = 2;

    @Inject
    private DemoRepository demoRepository;

    @Inject
    private DemoService demoService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restDemoMockMvc;

    private Demo demo;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DemoResource demoResource = new DemoResource();
        ReflectionTestUtils.setField(demoResource, "demoService", demoService);
        this.restDemoMockMvc = MockMvcBuilders.standaloneSetup(demoResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Demo createEntity(EntityManager em) {
        Demo demo = new Demo()
                .title(DEFAULT_TITLE)
                .level(DEFAULT_LEVEL);
        return demo;
    }

    @Before
    public void initTest() {
        demo = createEntity(em);
    }

    @Test
    @Transactional
    public void createDemo() throws Exception {
        int databaseSizeBeforeCreate = demoRepository.findAll().size();

        // Create the Demo

        restDemoMockMvc.perform(post("/api/demos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(demo)))
                .andExpect(status().isCreated());

        // Validate the Demo in the database
        List<Demo> demos = demoRepository.findAll();
        assertThat(demos).hasSize(databaseSizeBeforeCreate + 1);
        Demo testDemo = demos.get(demos.size() - 1);
        assertThat(testDemo.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testDemo.getLevel()).isEqualTo(DEFAULT_LEVEL);
    }

    @Test
    @Transactional
    public void getAllDemos() throws Exception {
        // Initialize the database
        demoRepository.saveAndFlush(demo);

        // Get all the demos
        restDemoMockMvc.perform(get("/api/demos?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(demo.getId().intValue())))
                .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
                .andExpect(jsonPath("$.[*].level").value(hasItem(DEFAULT_LEVEL)));
    }

    @Test
    @Transactional
    public void getDemo() throws Exception {
        // Initialize the database
        demoRepository.saveAndFlush(demo);

        // Get the demo
        restDemoMockMvc.perform(get("/api/demos/{id}", demo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(demo.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.level").value(DEFAULT_LEVEL));
    }

    @Test
    @Transactional
    public void getNonExistingDemo() throws Exception {
        // Get the demo
        restDemoMockMvc.perform(get("/api/demos/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDemo() throws Exception {
        // Initialize the database
        demoService.save(demo);

        int databaseSizeBeforeUpdate = demoRepository.findAll().size();

        // Update the demo
        Demo updatedDemo = demoRepository.findOne(demo.getId());
        updatedDemo
                .title(UPDATED_TITLE)
                .level(UPDATED_LEVEL);

        restDemoMockMvc.perform(put("/api/demos")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDemo)))
                .andExpect(status().isOk());

        // Validate the Demo in the database
        List<Demo> demos = demoRepository.findAll();
        assertThat(demos).hasSize(databaseSizeBeforeUpdate);
        Demo testDemo = demos.get(demos.size() - 1);
        assertThat(testDemo.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testDemo.getLevel()).isEqualTo(UPDATED_LEVEL);
    }

    @Test
    @Transactional
    public void deleteDemo() throws Exception {
        // Initialize the database
        demoService.save(demo);

        int databaseSizeBeforeDelete = demoRepository.findAll().size();

        // Get the demo
        restDemoMockMvc.perform(delete("/api/demos/{id}", demo.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Demo> demos = demoRepository.findAll();
        assertThat(demos).hasSize(databaseSizeBeforeDelete - 1);
    }
}
