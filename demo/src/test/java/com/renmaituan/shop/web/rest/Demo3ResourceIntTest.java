package com.renmaituan.shop.web.rest;

import com.renmaituan.shop.DemoApp;

import com.renmaituan.shop.config.SecurityBeanOverrideConfiguration;

import com.renmaituan.shop.domain.Demo3;
import com.renmaituan.shop.repository.Demo3Repository;

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
 * Test class for the Demo3Resource REST controller.
 *
 * @see Demo3Resource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoApp.class, SecurityBeanOverrideConfiguration.class})
public class Demo3ResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private Demo3Repository demo3Repository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restDemo3MockMvc;

    private Demo3 demo3;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Demo3Resource demo3Resource = new Demo3Resource();
        ReflectionTestUtils.setField(demo3Resource, "demo3Repository", demo3Repository);
        this.restDemo3MockMvc = MockMvcBuilders.standaloneSetup(demo3Resource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Demo3 createEntity(EntityManager em) {
        Demo3 demo3 = new Demo3()
                .name(DEFAULT_NAME);
        return demo3;
    }

    @Before
    public void initTest() {
        demo3 = createEntity(em);
    }

    @Test
    @Transactional
    public void createDemo3() throws Exception {
        int databaseSizeBeforeCreate = demo3Repository.findAll().size();

        // Create the Demo3

        restDemo3MockMvc.perform(post("/api/demo-3-s")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(demo3)))
                .andExpect(status().isCreated());

        // Validate the Demo3 in the database
        List<Demo3> demo3S = demo3Repository.findAll();
        assertThat(demo3S).hasSize(databaseSizeBeforeCreate + 1);
        Demo3 testDemo3 = demo3S.get(demo3S.size() - 1);
        assertThat(testDemo3.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void getAllDemo3S() throws Exception {
        // Initialize the database
        demo3Repository.saveAndFlush(demo3);

        // Get all the demo3S
        restDemo3MockMvc.perform(get("/api/demo-3-s?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(demo3.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getDemo3() throws Exception {
        // Initialize the database
        demo3Repository.saveAndFlush(demo3);

        // Get the demo3
        restDemo3MockMvc.perform(get("/api/demo-3-s/{id}", demo3.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(demo3.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDemo3() throws Exception {
        // Get the demo3
        restDemo3MockMvc.perform(get("/api/demo-3-s/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDemo3() throws Exception {
        // Initialize the database
        demo3Repository.saveAndFlush(demo3);
        int databaseSizeBeforeUpdate = demo3Repository.findAll().size();

        // Update the demo3
        Demo3 updatedDemo3 = demo3Repository.findOne(demo3.getId());
        updatedDemo3
                .name(UPDATED_NAME);

        restDemo3MockMvc.perform(put("/api/demo-3-s")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDemo3)))
                .andExpect(status().isOk());

        // Validate the Demo3 in the database
        List<Demo3> demo3S = demo3Repository.findAll();
        assertThat(demo3S).hasSize(databaseSizeBeforeUpdate);
        Demo3 testDemo3 = demo3S.get(demo3S.size() - 1);
        assertThat(testDemo3.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteDemo3() throws Exception {
        // Initialize the database
        demo3Repository.saveAndFlush(demo3);
        int databaseSizeBeforeDelete = demo3Repository.findAll().size();

        // Get the demo3
        restDemo3MockMvc.perform(delete("/api/demo-3-s/{id}", demo3.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Demo3> demo3S = demo3Repository.findAll();
        assertThat(demo3S).hasSize(databaseSizeBeforeDelete - 1);
    }
}
