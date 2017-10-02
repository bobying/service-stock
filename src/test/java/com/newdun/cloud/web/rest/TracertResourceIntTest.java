package com.newdun.cloud.web.rest;

import com.newdun.cloud.SourceApp;

import com.newdun.cloud.config.SecurityBeanOverrideConfiguration;

import com.newdun.cloud.domain.Tracert;
import com.newdun.cloud.repository.TracertRepository;
import com.newdun.cloud.service.TracertService;
import com.newdun.cloud.repository.search.TracertSearchRepository;
import com.newdun.cloud.service.dto.TracertDTO;
import com.newdun.cloud.service.mapper.TracertMapper;
import com.newdun.cloud.web.rest.errors.ExceptionTranslator;
import com.newdun.cloud.service.dto.TracertCriteria;
import com.newdun.cloud.service.TracertQueryService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TracertResource REST controller.
 *
 * @see TracertResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SourceApp.class, SecurityBeanOverrideConfiguration.class})
public class TracertResourceIntTest {

    private static final Integer DEFAULT_DAYS = 0;
    private static final Integer UPDATED_DAYS = 1;

    private static final Float DEFAULT_INCREASE_DAY = 1F;
    private static final Float UPDATED_INCREASE_DAY = 2F;

    private static final Float DEFAULT_INCREASE_TOTAL = 1F;
    private static final Float UPDATED_INCREASE_TOTAL = 2F;

    private static final Float DEFAULT_AMPLITUDE_DAY = 1F;
    private static final Float UPDATED_AMPLITUDE_DAY = 2F;

    private static final Float DEFAULT_HIGHEST = 1F;
    private static final Float UPDATED_HIGHEST = 2F;

    private static final Float DEFAULT_LOWEST = 1F;
    private static final Float UPDATED_LOWEST = 2F;

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    @Autowired
    private TracertRepository tracertRepository;

    @Autowired
    private TracertMapper tracertMapper;

    @Autowired
    private TracertService tracertService;

    @Autowired
    private TracertSearchRepository tracertSearchRepository;

    @Autowired
    private TracertQueryService tracertQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTracertMockMvc;

    private Tracert tracert;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TracertResource tracertResource = new TracertResource(tracertService, tracertQueryService);
        this.restTracertMockMvc = MockMvcBuilders.standaloneSetup(tracertResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tracert createEntity(EntityManager em) {
        Tracert tracert = new Tracert()
            .days(DEFAULT_DAYS)
            .increase_day(DEFAULT_INCREASE_DAY)
            .increase_total(DEFAULT_INCREASE_TOTAL)
            .amplitude_day(DEFAULT_AMPLITUDE_DAY)
            .highest(DEFAULT_HIGHEST)
            .lowest(DEFAULT_LOWEST)
            .date(DEFAULT_DATE);
        return tracert;
    }

    @Before
    public void initTest() {
        tracertSearchRepository.deleteAll();
        tracert = createEntity(em);
    }

    @Test
    @Transactional
    public void createTracert() throws Exception {
        int databaseSizeBeforeCreate = tracertRepository.findAll().size();

        // Create the Tracert
        TracertDTO tracertDTO = tracertMapper.toDto(tracert);
        restTracertMockMvc.perform(post("/api/tracerts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracertDTO)))
            .andExpect(status().isCreated());

        // Validate the Tracert in the database
        List<Tracert> tracertList = tracertRepository.findAll();
        assertThat(tracertList).hasSize(databaseSizeBeforeCreate + 1);
        Tracert testTracert = tracertList.get(tracertList.size() - 1);
        assertThat(testTracert.getDays()).isEqualTo(DEFAULT_DAYS);
        assertThat(testTracert.getIncrease_day()).isEqualTo(DEFAULT_INCREASE_DAY);
        assertThat(testTracert.getIncrease_total()).isEqualTo(DEFAULT_INCREASE_TOTAL);
        assertThat(testTracert.getAmplitude_day()).isEqualTo(DEFAULT_AMPLITUDE_DAY);
        assertThat(testTracert.getHighest()).isEqualTo(DEFAULT_HIGHEST);
        assertThat(testTracert.getLowest()).isEqualTo(DEFAULT_LOWEST);
        assertThat(testTracert.getDate()).isEqualTo(DEFAULT_DATE);

        // Validate the Tracert in Elasticsearch
        Tracert tracertEs = tracertSearchRepository.findById(testTracert.getId()).get();
        assertThat(tracertEs).isEqualToComparingFieldByField(testTracert);
    }

    @Test
    @Transactional
    public void createTracertWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = tracertRepository.findAll().size();

        // Create the Tracert with an existing ID
        tracert.setId(1L);
        TracertDTO tracertDTO = tracertMapper.toDto(tracert);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTracertMockMvc.perform(post("/api/tracerts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracertDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Tracert> tracertList = tracertRepository.findAll();
        assertThat(tracertList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllTracerts() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList
        restTracertMockMvc.perform(get("/api/tracerts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tracert.getId().intValue())))
            .andExpect(jsonPath("$.[*].days").value(hasItem(DEFAULT_DAYS)))
            .andExpect(jsonPath("$.[*].increase_day").value(hasItem(DEFAULT_INCREASE_DAY.doubleValue())))
            .andExpect(jsonPath("$.[*].increase_total").value(hasItem(DEFAULT_INCREASE_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].amplitude_day").value(hasItem(DEFAULT_AMPLITUDE_DAY.doubleValue())))
            .andExpect(jsonPath("$.[*].highest").value(hasItem(DEFAULT_HIGHEST.doubleValue())))
            .andExpect(jsonPath("$.[*].lowest").value(hasItem(DEFAULT_LOWEST.doubleValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    public void getTracert() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get the tracert
        restTracertMockMvc.perform(get("/api/tracerts/{id}", tracert.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(tracert.getId().intValue()))
            .andExpect(jsonPath("$.days").value(DEFAULT_DAYS))
            .andExpect(jsonPath("$.increase_day").value(DEFAULT_INCREASE_DAY.doubleValue()))
            .andExpect(jsonPath("$.increase_total").value(DEFAULT_INCREASE_TOTAL.doubleValue()))
            .andExpect(jsonPath("$.amplitude_day").value(DEFAULT_AMPLITUDE_DAY.doubleValue()))
            .andExpect(jsonPath("$.highest").value(DEFAULT_HIGHEST.doubleValue()))
            .andExpect(jsonPath("$.lowest").value(DEFAULT_LOWEST.doubleValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    public void getAllTracertsByDaysIsEqualToSomething() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where days equals to DEFAULT_DAYS
        defaultTracertShouldBeFound("days.equals=" + DEFAULT_DAYS);

        // Get all the tracertList where days equals to UPDATED_DAYS
        defaultTracertShouldNotBeFound("days.equals=" + UPDATED_DAYS);
    }

    @Test
    @Transactional
    public void getAllTracertsByDaysIsInShouldWork() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where days in DEFAULT_DAYS or UPDATED_DAYS
        defaultTracertShouldBeFound("days.in=" + DEFAULT_DAYS + "," + UPDATED_DAYS);

        // Get all the tracertList where days equals to UPDATED_DAYS
        defaultTracertShouldNotBeFound("days.in=" + UPDATED_DAYS);
    }

    @Test
    @Transactional
    public void getAllTracertsByDaysIsNullOrNotNull() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where days is not null
        defaultTracertShouldBeFound("days.specified=true");

        // Get all the tracertList where days is null
        defaultTracertShouldNotBeFound("days.specified=false");
    }

    @Test
    @Transactional
    public void getAllTracertsByDaysIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where days greater than or equals to DEFAULT_DAYS
        defaultTracertShouldBeFound("days.greaterOrEqualThan=" + DEFAULT_DAYS);

        // Get all the tracertList where days greater than or equals to UPDATED_DAYS
        defaultTracertShouldNotBeFound("days.greaterOrEqualThan=" + UPDATED_DAYS);
    }

    @Test
    @Transactional
    public void getAllTracertsByDaysIsLessThanSomething() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where days less than or equals to DEFAULT_DAYS
        defaultTracertShouldNotBeFound("days.lessThan=" + DEFAULT_DAYS);

        // Get all the tracertList where days less than or equals to UPDATED_DAYS
        defaultTracertShouldBeFound("days.lessThan=" + UPDATED_DAYS);
    }


    @Test
    @Transactional
    public void getAllTracertsByIncrease_dayIsEqualToSomething() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where increase_day equals to DEFAULT_INCREASE_DAY
        defaultTracertShouldBeFound("increase_day.equals=" + DEFAULT_INCREASE_DAY);

        // Get all the tracertList where increase_day equals to UPDATED_INCREASE_DAY
        defaultTracertShouldNotBeFound("increase_day.equals=" + UPDATED_INCREASE_DAY);
    }

    @Test
    @Transactional
    public void getAllTracertsByIncrease_dayIsInShouldWork() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where increase_day in DEFAULT_INCREASE_DAY or UPDATED_INCREASE_DAY
        defaultTracertShouldBeFound("increase_day.in=" + DEFAULT_INCREASE_DAY + "," + UPDATED_INCREASE_DAY);

        // Get all the tracertList where increase_day equals to UPDATED_INCREASE_DAY
        defaultTracertShouldNotBeFound("increase_day.in=" + UPDATED_INCREASE_DAY);
    }

    @Test
    @Transactional
    public void getAllTracertsByIncrease_dayIsNullOrNotNull() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where increase_day is not null
        defaultTracertShouldBeFound("increase_day.specified=true");

        // Get all the tracertList where increase_day is null
        defaultTracertShouldNotBeFound("increase_day.specified=false");
    }

    @Test
    @Transactional
    public void getAllTracertsByIncrease_totalIsEqualToSomething() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where increase_total equals to DEFAULT_INCREASE_TOTAL
        defaultTracertShouldBeFound("increase_total.equals=" + DEFAULT_INCREASE_TOTAL);

        // Get all the tracertList where increase_total equals to UPDATED_INCREASE_TOTAL
        defaultTracertShouldNotBeFound("increase_total.equals=" + UPDATED_INCREASE_TOTAL);
    }

    @Test
    @Transactional
    public void getAllTracertsByIncrease_totalIsInShouldWork() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where increase_total in DEFAULT_INCREASE_TOTAL or UPDATED_INCREASE_TOTAL
        defaultTracertShouldBeFound("increase_total.in=" + DEFAULT_INCREASE_TOTAL + "," + UPDATED_INCREASE_TOTAL);

        // Get all the tracertList where increase_total equals to UPDATED_INCREASE_TOTAL
        defaultTracertShouldNotBeFound("increase_total.in=" + UPDATED_INCREASE_TOTAL);
    }

    @Test
    @Transactional
    public void getAllTracertsByIncrease_totalIsNullOrNotNull() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where increase_total is not null
        defaultTracertShouldBeFound("increase_total.specified=true");

        // Get all the tracertList where increase_total is null
        defaultTracertShouldNotBeFound("increase_total.specified=false");
    }

    @Test
    @Transactional
    public void getAllTracertsByAmplitude_dayIsEqualToSomething() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where amplitude_day equals to DEFAULT_AMPLITUDE_DAY
        defaultTracertShouldBeFound("amplitude_day.equals=" + DEFAULT_AMPLITUDE_DAY);

        // Get all the tracertList where amplitude_day equals to UPDATED_AMPLITUDE_DAY
        defaultTracertShouldNotBeFound("amplitude_day.equals=" + UPDATED_AMPLITUDE_DAY);
    }

    @Test
    @Transactional
    public void getAllTracertsByAmplitude_dayIsInShouldWork() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where amplitude_day in DEFAULT_AMPLITUDE_DAY or UPDATED_AMPLITUDE_DAY
        defaultTracertShouldBeFound("amplitude_day.in=" + DEFAULT_AMPLITUDE_DAY + "," + UPDATED_AMPLITUDE_DAY);

        // Get all the tracertList where amplitude_day equals to UPDATED_AMPLITUDE_DAY
        defaultTracertShouldNotBeFound("amplitude_day.in=" + UPDATED_AMPLITUDE_DAY);
    }

    @Test
    @Transactional
    public void getAllTracertsByAmplitude_dayIsNullOrNotNull() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where amplitude_day is not null
        defaultTracertShouldBeFound("amplitude_day.specified=true");

        // Get all the tracertList where amplitude_day is null
        defaultTracertShouldNotBeFound("amplitude_day.specified=false");
    }

    @Test
    @Transactional
    public void getAllTracertsByHighestIsEqualToSomething() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where highest equals to DEFAULT_HIGHEST
        defaultTracertShouldBeFound("highest.equals=" + DEFAULT_HIGHEST);

        // Get all the tracertList where highest equals to UPDATED_HIGHEST
        defaultTracertShouldNotBeFound("highest.equals=" + UPDATED_HIGHEST);
    }

    @Test
    @Transactional
    public void getAllTracertsByHighestIsInShouldWork() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where highest in DEFAULT_HIGHEST or UPDATED_HIGHEST
        defaultTracertShouldBeFound("highest.in=" + DEFAULT_HIGHEST + "," + UPDATED_HIGHEST);

        // Get all the tracertList where highest equals to UPDATED_HIGHEST
        defaultTracertShouldNotBeFound("highest.in=" + UPDATED_HIGHEST);
    }

    @Test
    @Transactional
    public void getAllTracertsByHighestIsNullOrNotNull() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where highest is not null
        defaultTracertShouldBeFound("highest.specified=true");

        // Get all the tracertList where highest is null
        defaultTracertShouldNotBeFound("highest.specified=false");
    }

    @Test
    @Transactional
    public void getAllTracertsByLowestIsEqualToSomething() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where lowest equals to DEFAULT_LOWEST
        defaultTracertShouldBeFound("lowest.equals=" + DEFAULT_LOWEST);

        // Get all the tracertList where lowest equals to UPDATED_LOWEST
        defaultTracertShouldNotBeFound("lowest.equals=" + UPDATED_LOWEST);
    }

    @Test
    @Transactional
    public void getAllTracertsByLowestIsInShouldWork() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where lowest in DEFAULT_LOWEST or UPDATED_LOWEST
        defaultTracertShouldBeFound("lowest.in=" + DEFAULT_LOWEST + "," + UPDATED_LOWEST);

        // Get all the tracertList where lowest equals to UPDATED_LOWEST
        defaultTracertShouldNotBeFound("lowest.in=" + UPDATED_LOWEST);
    }

    @Test
    @Transactional
    public void getAllTracertsByLowestIsNullOrNotNull() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where lowest is not null
        defaultTracertShouldBeFound("lowest.specified=true");

        // Get all the tracertList where lowest is null
        defaultTracertShouldNotBeFound("lowest.specified=false");
    }

    @Test
    @Transactional
    public void getAllTracertsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where date equals to DEFAULT_DATE
        defaultTracertShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the tracertList where date equals to UPDATED_DATE
        defaultTracertShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllTracertsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where date in DEFAULT_DATE or UPDATED_DATE
        defaultTracertShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the tracertList where date equals to UPDATED_DATE
        defaultTracertShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllTracertsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where date is not null
        defaultTracertShouldBeFound("date.specified=true");

        // Get all the tracertList where date is null
        defaultTracertShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllTracertsByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where date greater than or equals to DEFAULT_DATE
        defaultTracertShouldBeFound("date.greaterOrEqualThan=" + DEFAULT_DATE);

        // Get all the tracertList where date greater than or equals to UPDATED_DATE
        defaultTracertShouldNotBeFound("date.greaterOrEqualThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllTracertsByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);

        // Get all the tracertList where date less than or equals to DEFAULT_DATE
        defaultTracertShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the tracertList where date less than or equals to UPDATED_DATE
        defaultTracertShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }


    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultTracertShouldBeFound(String filter) throws Exception {
        restTracertMockMvc.perform(get("/api/tracerts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tracert.getId().intValue())))
            .andExpect(jsonPath("$.[*].days").value(hasItem(DEFAULT_DAYS)))
            .andExpect(jsonPath("$.[*].increase_day").value(hasItem(DEFAULT_INCREASE_DAY.doubleValue())))
            .andExpect(jsonPath("$.[*].increase_total").value(hasItem(DEFAULT_INCREASE_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].amplitude_day").value(hasItem(DEFAULT_AMPLITUDE_DAY.doubleValue())))
            .andExpect(jsonPath("$.[*].highest").value(hasItem(DEFAULT_HIGHEST.doubleValue())))
            .andExpect(jsonPath("$.[*].lowest").value(hasItem(DEFAULT_LOWEST.doubleValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultTracertShouldNotBeFound(String filter) throws Exception {
        restTracertMockMvc.perform(get("/api/tracerts?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingTracert() throws Exception {
        // Get the tracert
        restTracertMockMvc.perform(get("/api/tracerts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTracert() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);
        tracertSearchRepository.save(tracert);
        int databaseSizeBeforeUpdate = tracertRepository.findAll().size();

        // Update the tracert
        Tracert updatedTracert = tracertRepository.findById(tracert.getId()).get();
        updatedTracert
            .days(UPDATED_DAYS)
            .increase_day(UPDATED_INCREASE_DAY)
            .increase_total(UPDATED_INCREASE_TOTAL)
            .amplitude_day(UPDATED_AMPLITUDE_DAY)
            .highest(UPDATED_HIGHEST)
            .lowest(UPDATED_LOWEST)
            .date(UPDATED_DATE);
        TracertDTO tracertDTO = tracertMapper.toDto(updatedTracert);

        restTracertMockMvc.perform(put("/api/tracerts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracertDTO)))
            .andExpect(status().isOk());

        // Validate the Tracert in the database
        List<Tracert> tracertList = tracertRepository.findAll();
        assertThat(tracertList).hasSize(databaseSizeBeforeUpdate);
        Tracert testTracert = tracertList.get(tracertList.size() - 1);
        assertThat(testTracert.getDays()).isEqualTo(UPDATED_DAYS);
        assertThat(testTracert.getIncrease_day()).isEqualTo(UPDATED_INCREASE_DAY);
        assertThat(testTracert.getIncrease_total()).isEqualTo(UPDATED_INCREASE_TOTAL);
        assertThat(testTracert.getAmplitude_day()).isEqualTo(UPDATED_AMPLITUDE_DAY);
        assertThat(testTracert.getHighest()).isEqualTo(UPDATED_HIGHEST);
        assertThat(testTracert.getLowest()).isEqualTo(UPDATED_LOWEST);
        assertThat(testTracert.getDate()).isEqualTo(UPDATED_DATE);

        // Validate the Tracert in Elasticsearch
        Tracert tracertEs = tracertSearchRepository.findById(testTracert.getId()).get();
        assertThat(tracertEs).isEqualToComparingFieldByField(testTracert);
    }

    @Test
    @Transactional
    public void updateNonExistingTracert() throws Exception {
        int databaseSizeBeforeUpdate = tracertRepository.findAll().size();

        // Create the Tracert
        TracertDTO tracertDTO = tracertMapper.toDto(tracert);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restTracertMockMvc.perform(put("/api/tracerts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(tracertDTO)))
            .andExpect(status().isCreated());

        // Validate the Tracert in the database
        List<Tracert> tracertList = tracertRepository.findAll();
        assertThat(tracertList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteTracert() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);
        tracertSearchRepository.save(tracert);
        int databaseSizeBeforeDelete = tracertRepository.findAll().size();

        // Get the tracert
        restTracertMockMvc.perform(delete("/api/tracerts/{id}", tracert.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean tracertExistsInEs = tracertSearchRepository.existsById(tracert.getId());
        assertThat(tracertExistsInEs).isFalse();

        // Validate the database is empty
        List<Tracert> tracertList = tracertRepository.findAll();
        assertThat(tracertList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchTracert() throws Exception {
        // Initialize the database
        tracertRepository.saveAndFlush(tracert);
        tracertSearchRepository.save(tracert);

        // Search the tracert
        restTracertMockMvc.perform(get("/api/_search/tracerts?query=id:" + tracert.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tracert.getId().intValue())))
            .andExpect(jsonPath("$.[*].days").value(hasItem(DEFAULT_DAYS)))
            .andExpect(jsonPath("$.[*].increase_day").value(hasItem(DEFAULT_INCREASE_DAY.doubleValue())))
            .andExpect(jsonPath("$.[*].increase_total").value(hasItem(DEFAULT_INCREASE_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].amplitude_day").value(hasItem(DEFAULT_AMPLITUDE_DAY.doubleValue())))
            .andExpect(jsonPath("$.[*].highest").value(hasItem(DEFAULT_HIGHEST.doubleValue())))
            .andExpect(jsonPath("$.[*].lowest").value(hasItem(DEFAULT_LOWEST.doubleValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tracert.class);
        Tracert tracert1 = new Tracert();
        tracert1.setId(1L);
        Tracert tracert2 = new Tracert();
        tracert2.setId(tracert1.getId());
        assertThat(tracert1).isEqualTo(tracert2);
        tracert2.setId(2L);
        assertThat(tracert1).isNotEqualTo(tracert2);
        tracert1.setId(null);
        assertThat(tracert1).isNotEqualTo(tracert2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TracertDTO.class);
        TracertDTO tracertDTO1 = new TracertDTO();
        tracertDTO1.setId(1L);
        TracertDTO tracertDTO2 = new TracertDTO();
        assertThat(tracertDTO1).isNotEqualTo(tracertDTO2);
        tracertDTO2.setId(tracertDTO1.getId());
        assertThat(tracertDTO1).isEqualTo(tracertDTO2);
        tracertDTO2.setId(2L);
        assertThat(tracertDTO1).isNotEqualTo(tracertDTO2);
        tracertDTO1.setId(null);
        assertThat(tracertDTO1).isNotEqualTo(tracertDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(tracertMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(tracertMapper.fromId(null)).isNull();
    }
}
