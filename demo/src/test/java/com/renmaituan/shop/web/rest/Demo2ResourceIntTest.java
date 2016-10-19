package com.renmaituan.shop.web.rest;

import com.renmaituan.shop.DemoApp;

import com.renmaituan.shop.config.SecurityBeanOverrideConfiguration;

import com.renmaituan.shop.domain.Demo2;
import com.renmaituan.shop.repository.Demo2Repository;

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
 * Test class for the Demo2Resource REST controller.
 *
 * @see Demo2Resource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoApp.class, SecurityBeanOverrideConfiguration.class})
public class Demo2ResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private Demo2Repository demo2Repository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restDemo2MockMvc;

    private Demo2 demo2;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Demo2Resource demo2Resource = new Demo2Resource();
        ReflectionTestUtils.setField(demo2Resource, "demo2Repository", demo2Repository);
        this.restDemo2MockMvc = MockMvcBuilders.standaloneSetup(demo2Resource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Demo2 createEntity(EntityManager em) {
        Demo2 demo2 = new Demo2()
                .name(DEFAULT_NAME);
        return demo2;
    }

    @Before
    public void initTest() {
        demo2 = createEntity(em);
    }

    @Test
    @Transactional
    public void createDemo2() throws Exception {
        int databaseSizeBeforeCreate = demo2Repository.findAll().size();

        // Create the Demo2

        restDemo2MockMvc.perform(post("/api/demo-2-s")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(demo2)))
                .andExpect(status().isCreated());

        // Validate the Demo2 in the database
        List<Demo2> demo2S = demo2Repository.findAll();
        assertThat(demo2S).hasSize(databaseSizeBeforeCreate + 1);
        Demo2 testDemo2 = demo2S.get(demo2S.size() - 1);
        assertThat(testDemo2.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void getAllDemo2S() throws Exception {
        // Initialize the database
        demo2Repository.saveAndFlush(demo2);

        // Get all the demo2S
        restDemo2MockMvc.perform(get("/api/demo-2-s?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(demo2.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getDemo2() throws Exception {
        // Initialize the database
        demo2Repository.saveAndFlush(demo2);

        // Get the demo2
        restDemo2MockMvc.perform(get("/api/demo-2-s/{id}", demo2.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(demo2.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDemo2() throws Exception {
        // Get the demo2
        restDemo2MockMvc.perform(get("/api/demo-2-s/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDemo2() throws Exception {
        // Initialize the database
        demo2Repository.saveAndFlush(demo2);
        int databaseSizeBeforeUpdate = demo2Repository.findAll().size();

        // Update the demo2
        Demo2 updatedDemo2 = demo2Repository.findOne(demo2.getId());
        updatedDemo2
                .name(UPDATED_NAME);

        restDemo2MockMvc.perform(put("/api/demo-2-s")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDemo2)))
                .andExpect(status().isOk());

        // Validate the Demo2 in the database
        List<Demo2> demo2S = demo2Repository.findAll();
        assertThat(demo2S).hasSize(databaseSizeBeforeUpdate);
        Demo2 testDemo2 = demo2S.get(demo2S.size() - 1);
        assertThat(testDemo2.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteDemo2() throws Exception {
        // Initialize the database
        demo2Repository.saveAndFlush(demo2);
        int databaseSizeBeforeDelete = demo2Repository.findAll().size();

        // Get the demo2
        restDemo2MockMvc.perform(delete("/api/demo-2-s/{id}", demo2.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Demo2> demo2S = demo2Repository.findAll();
        assertThat(demo2S).hasSize(databaseSizeBeforeDelete - 1);
    }
}
