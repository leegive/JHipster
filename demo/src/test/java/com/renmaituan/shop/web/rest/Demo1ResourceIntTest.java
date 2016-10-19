package com.renmaituan.shop.web.rest;

import com.renmaituan.shop.DemoApp;

import com.renmaituan.shop.config.SecurityBeanOverrideConfiguration;

import com.renmaituan.shop.domain.Demo1;
import com.renmaituan.shop.repository.Demo1Repository;

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
 * Test class for the Demo1Resource REST controller.
 *
 * @see Demo1Resource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DemoApp.class, SecurityBeanOverrideConfiguration.class})
public class Demo1ResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";

    @Inject
    private Demo1Repository demo1Repository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restDemo1MockMvc;

    private Demo1 demo1;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        Demo1Resource demo1Resource = new Demo1Resource();
        ReflectionTestUtils.setField(demo1Resource, "demo1Repository", demo1Repository);
        this.restDemo1MockMvc = MockMvcBuilders.standaloneSetup(demo1Resource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Demo1 createEntity(EntityManager em) {
        Demo1 demo1 = new Demo1()
                .name(DEFAULT_NAME);
        return demo1;
    }

    @Before
    public void initTest() {
        demo1 = createEntity(em);
    }

    @Test
    @Transactional
    public void createDemo1() throws Exception {
        int databaseSizeBeforeCreate = demo1Repository.findAll().size();

        // Create the Demo1

        restDemo1MockMvc.perform(post("/api/demo-1-s")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(demo1)))
                .andExpect(status().isCreated());

        // Validate the Demo1 in the database
        List<Demo1> demo1S = demo1Repository.findAll();
        assertThat(demo1S).hasSize(databaseSizeBeforeCreate + 1);
        Demo1 testDemo1 = demo1S.get(demo1S.size() - 1);
        assertThat(testDemo1.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void getAllDemo1S() throws Exception {
        // Initialize the database
        demo1Repository.saveAndFlush(demo1);

        // Get all the demo1S
        restDemo1MockMvc.perform(get("/api/demo-1-s?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(demo1.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getDemo1() throws Exception {
        // Initialize the database
        demo1Repository.saveAndFlush(demo1);

        // Get the demo1
        restDemo1MockMvc.perform(get("/api/demo-1-s/{id}", demo1.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(demo1.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDemo1() throws Exception {
        // Get the demo1
        restDemo1MockMvc.perform(get("/api/demo-1-s/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDemo1() throws Exception {
        // Initialize the database
        demo1Repository.saveAndFlush(demo1);
        int databaseSizeBeforeUpdate = demo1Repository.findAll().size();

        // Update the demo1
        Demo1 updatedDemo1 = demo1Repository.findOne(demo1.getId());
        updatedDemo1
                .name(UPDATED_NAME);

        restDemo1MockMvc.perform(put("/api/demo-1-s")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedDemo1)))
                .andExpect(status().isOk());

        // Validate the Demo1 in the database
        List<Demo1> demo1S = demo1Repository.findAll();
        assertThat(demo1S).hasSize(databaseSizeBeforeUpdate);
        Demo1 testDemo1 = demo1S.get(demo1S.size() - 1);
        assertThat(testDemo1.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteDemo1() throws Exception {
        // Initialize the database
        demo1Repository.saveAndFlush(demo1);
        int databaseSizeBeforeDelete = demo1Repository.findAll().size();

        // Get the demo1
        restDemo1MockMvc.perform(delete("/api/demo-1-s/{id}", demo1.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Demo1> demo1S = demo1Repository.findAll();
        assertThat(demo1S).hasSize(databaseSizeBeforeDelete - 1);
    }
}
