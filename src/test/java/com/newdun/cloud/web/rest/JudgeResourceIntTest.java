package com.newdun.cloud.web.rest;

import com.newdun.cloud.SourceApp;

import com.newdun.cloud.config.SecurityBeanOverrideConfiguration;

import com.newdun.cloud.domain.Judge;
import com.newdun.cloud.repository.JudgeRepository;
import com.newdun.cloud.service.JudgeService;
import com.newdun.cloud.repository.search.JudgeSearchRepository;
import com.newdun.cloud.service.dto.JudgeDTO;
import com.newdun.cloud.service.mapper.JudgeMapper;
import com.newdun.cloud.web.rest.errors.ExceptionTranslator;
import com.newdun.cloud.service.dto.JudgeCriteria;
import com.newdun.cloud.service.JudgeQueryService;

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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the JudgeResource REST controller.
 *
 * @see JudgeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SourceApp.class, SecurityBeanOverrideConfiguration.class})
public class JudgeResourceIntTest {

    private static final Integer DEFAULT_SCORE = 1;
    private static final Integer UPDATED_SCORE = 2;

    private static final Float DEFAULT_INCREASE_TOTAL = 1F;
    private static final Float UPDATED_INCREASE_TOTAL = 2F;

    private static final Integer DEFAULT_INCREASE_DAYS = 1;
    private static final Integer UPDATED_INCREASE_DAYS = 2;

    private static final Float DEFAULT_DAY_5 = 1F;
    private static final Float UPDATED_DAY_5 = 2F;

    private static final Float DEFAULT_DAY_10 = 1F;
    private static final Float UPDATED_DAY_10 = 2F;

    private static final Float DEFAULT_DAY_30 = 1F;
    private static final Float UPDATED_DAY_30 = 2F;

    @Autowired
    private JudgeRepository judgeRepository;

    @Autowired
    private JudgeMapper judgeMapper;

    @Autowired
    private JudgeService judgeService;

    @Autowired
    private JudgeSearchRepository judgeSearchRepository;

    @Autowired
    private JudgeQueryService judgeQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restJudgeMockMvc;

    private Judge judge;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final JudgeResource judgeResource = new JudgeResource(judgeService, judgeQueryService);
        this.restJudgeMockMvc = MockMvcBuilders.standaloneSetup(judgeResource)
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
    public static Judge createEntity(EntityManager em) {
        Judge judge = new Judge()
            .score(DEFAULT_SCORE)
            .increase_total(DEFAULT_INCREASE_TOTAL)
            .increase_days(DEFAULT_INCREASE_DAYS)
            .day5(DEFAULT_DAY_5)
            .day10(DEFAULT_DAY_10)
            .day30(DEFAULT_DAY_30);
        return judge;
    }

    @Before
    public void initTest() {
        judgeSearchRepository.deleteAll();
        judge = createEntity(em);
    }

    @Test
    @Transactional
    public void createJudge() throws Exception {
        int databaseSizeBeforeCreate = judgeRepository.findAll().size();

        // Create the Judge
        JudgeDTO judgeDTO = judgeMapper.toDto(judge);
        restJudgeMockMvc.perform(post("/api/judges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(judgeDTO)))
            .andExpect(status().isCreated());

        // Validate the Judge in the database
        List<Judge> judgeList = judgeRepository.findAll();
        assertThat(judgeList).hasSize(databaseSizeBeforeCreate + 1);
        Judge testJudge = judgeList.get(judgeList.size() - 1);
        assertThat(testJudge.getScore()).isEqualTo(DEFAULT_SCORE);
        assertThat(testJudge.getIncrease_total()).isEqualTo(DEFAULT_INCREASE_TOTAL);
        assertThat(testJudge.getIncrease_days()).isEqualTo(DEFAULT_INCREASE_DAYS);
        assertThat(testJudge.getDay5()).isEqualTo(DEFAULT_DAY_5);
        assertThat(testJudge.getDay10()).isEqualTo(DEFAULT_DAY_10);
        assertThat(testJudge.getDay30()).isEqualTo(DEFAULT_DAY_30);

        // Validate the Judge in Elasticsearch
        Judge judgeEs = judgeSearchRepository.findOne(testJudge.getId());
        assertThat(judgeEs).isEqualToComparingFieldByField(testJudge);
    }

    @Test
    @Transactional
    public void createJudgeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = judgeRepository.findAll().size();

        // Create the Judge with an existing ID
        judge.setId(1L);
        JudgeDTO judgeDTO = judgeMapper.toDto(judge);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJudgeMockMvc.perform(post("/api/judges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(judgeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Judge> judgeList = judgeRepository.findAll();
        assertThat(judgeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllJudges() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList
        restJudgeMockMvc.perform(get("/api/judges?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(judge.getId().intValue())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.[*].increase_total").value(hasItem(DEFAULT_INCREASE_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].increase_days").value(hasItem(DEFAULT_INCREASE_DAYS)))
            .andExpect(jsonPath("$.[*].day5").value(hasItem(DEFAULT_DAY_5.doubleValue())))
            .andExpect(jsonPath("$.[*].day10").value(hasItem(DEFAULT_DAY_10.doubleValue())))
            .andExpect(jsonPath("$.[*].day30").value(hasItem(DEFAULT_DAY_30.doubleValue())));
    }

    @Test
    @Transactional
    public void getJudge() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get the judge
        restJudgeMockMvc.perform(get("/api/judges/{id}", judge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(judge.getId().intValue()))
            .andExpect(jsonPath("$.score").value(DEFAULT_SCORE))
            .andExpect(jsonPath("$.increase_total").value(DEFAULT_INCREASE_TOTAL.doubleValue()))
            .andExpect(jsonPath("$.increase_days").value(DEFAULT_INCREASE_DAYS))
            .andExpect(jsonPath("$.day5").value(DEFAULT_DAY_5.doubleValue()))
            .andExpect(jsonPath("$.day10").value(DEFAULT_DAY_10.doubleValue()))
            .andExpect(jsonPath("$.day30").value(DEFAULT_DAY_30.doubleValue()));
    }

    @Test
    @Transactional
    public void getAllJudgesByScoreIsEqualToSomething() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where score equals to DEFAULT_SCORE
        defaultJudgeShouldBeFound("score.equals=" + DEFAULT_SCORE);

        // Get all the judgeList where score equals to UPDATED_SCORE
        defaultJudgeShouldNotBeFound("score.equals=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    public void getAllJudgesByScoreIsInShouldWork() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where score in DEFAULT_SCORE or UPDATED_SCORE
        defaultJudgeShouldBeFound("score.in=" + DEFAULT_SCORE + "," + UPDATED_SCORE);

        // Get all the judgeList where score equals to UPDATED_SCORE
        defaultJudgeShouldNotBeFound("score.in=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    public void getAllJudgesByScoreIsNullOrNotNull() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where score is not null
        defaultJudgeShouldBeFound("score.specified=true");

        // Get all the judgeList where score is null
        defaultJudgeShouldNotBeFound("score.specified=false");
    }

    @Test
    @Transactional
    public void getAllJudgesByScoreIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where score greater than or equals to DEFAULT_SCORE
        defaultJudgeShouldBeFound("score.greaterOrEqualThan=" + DEFAULT_SCORE);

        // Get all the judgeList where score greater than or equals to UPDATED_SCORE
        defaultJudgeShouldNotBeFound("score.greaterOrEqualThan=" + UPDATED_SCORE);
    }

    @Test
    @Transactional
    public void getAllJudgesByScoreIsLessThanSomething() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where score less than or equals to DEFAULT_SCORE
        defaultJudgeShouldNotBeFound("score.lessThan=" + DEFAULT_SCORE);

        // Get all the judgeList where score less than or equals to UPDATED_SCORE
        defaultJudgeShouldBeFound("score.lessThan=" + UPDATED_SCORE);
    }


    @Test
    @Transactional
    public void getAllJudgesByIncrease_totalIsEqualToSomething() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where increase_total equals to DEFAULT_INCREASE_TOTAL
        defaultJudgeShouldBeFound("increase_total.equals=" + DEFAULT_INCREASE_TOTAL);

        // Get all the judgeList where increase_total equals to UPDATED_INCREASE_TOTAL
        defaultJudgeShouldNotBeFound("increase_total.equals=" + UPDATED_INCREASE_TOTAL);
    }

    @Test
    @Transactional
    public void getAllJudgesByIncrease_totalIsInShouldWork() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where increase_total in DEFAULT_INCREASE_TOTAL or UPDATED_INCREASE_TOTAL
        defaultJudgeShouldBeFound("increase_total.in=" + DEFAULT_INCREASE_TOTAL + "," + UPDATED_INCREASE_TOTAL);

        // Get all the judgeList where increase_total equals to UPDATED_INCREASE_TOTAL
        defaultJudgeShouldNotBeFound("increase_total.in=" + UPDATED_INCREASE_TOTAL);
    }

    @Test
    @Transactional
    public void getAllJudgesByIncrease_totalIsNullOrNotNull() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where increase_total is not null
        defaultJudgeShouldBeFound("increase_total.specified=true");

        // Get all the judgeList where increase_total is null
        defaultJudgeShouldNotBeFound("increase_total.specified=false");
    }

    @Test
    @Transactional
    public void getAllJudgesByIncrease_daysIsEqualToSomething() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where increase_days equals to DEFAULT_INCREASE_DAYS
        defaultJudgeShouldBeFound("increase_days.equals=" + DEFAULT_INCREASE_DAYS);

        // Get all the judgeList where increase_days equals to UPDATED_INCREASE_DAYS
        defaultJudgeShouldNotBeFound("increase_days.equals=" + UPDATED_INCREASE_DAYS);
    }

    @Test
    @Transactional
    public void getAllJudgesByIncrease_daysIsInShouldWork() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where increase_days in DEFAULT_INCREASE_DAYS or UPDATED_INCREASE_DAYS
        defaultJudgeShouldBeFound("increase_days.in=" + DEFAULT_INCREASE_DAYS + "," + UPDATED_INCREASE_DAYS);

        // Get all the judgeList where increase_days equals to UPDATED_INCREASE_DAYS
        defaultJudgeShouldNotBeFound("increase_days.in=" + UPDATED_INCREASE_DAYS);
    }

    @Test
    @Transactional
    public void getAllJudgesByIncrease_daysIsNullOrNotNull() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where increase_days is not null
        defaultJudgeShouldBeFound("increase_days.specified=true");

        // Get all the judgeList where increase_days is null
        defaultJudgeShouldNotBeFound("increase_days.specified=false");
    }

    @Test
    @Transactional
    public void getAllJudgesByIncrease_daysIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where increase_days greater than or equals to DEFAULT_INCREASE_DAYS
        defaultJudgeShouldBeFound("increase_days.greaterOrEqualThan=" + DEFAULT_INCREASE_DAYS);

        // Get all the judgeList where increase_days greater than or equals to UPDATED_INCREASE_DAYS
        defaultJudgeShouldNotBeFound("increase_days.greaterOrEqualThan=" + UPDATED_INCREASE_DAYS);
    }

    @Test
    @Transactional
    public void getAllJudgesByIncrease_daysIsLessThanSomething() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where increase_days less than or equals to DEFAULT_INCREASE_DAYS
        defaultJudgeShouldNotBeFound("increase_days.lessThan=" + DEFAULT_INCREASE_DAYS);

        // Get all the judgeList where increase_days less than or equals to UPDATED_INCREASE_DAYS
        defaultJudgeShouldBeFound("increase_days.lessThan=" + UPDATED_INCREASE_DAYS);
    }


    @Test
    @Transactional
    public void getAllJudgesByDay5IsEqualToSomething() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where day5 equals to DEFAULT_DAY_5
        defaultJudgeShouldBeFound("day5.equals=" + DEFAULT_DAY_5);

        // Get all the judgeList where day5 equals to UPDATED_DAY_5
        defaultJudgeShouldNotBeFound("day5.equals=" + UPDATED_DAY_5);
    }

    @Test
    @Transactional
    public void getAllJudgesByDay5IsInShouldWork() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where day5 in DEFAULT_DAY_5 or UPDATED_DAY_5
        defaultJudgeShouldBeFound("day5.in=" + DEFAULT_DAY_5 + "," + UPDATED_DAY_5);

        // Get all the judgeList where day5 equals to UPDATED_DAY_5
        defaultJudgeShouldNotBeFound("day5.in=" + UPDATED_DAY_5);
    }

    @Test
    @Transactional
    public void getAllJudgesByDay5IsNullOrNotNull() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where day5 is not null
        defaultJudgeShouldBeFound("day5.specified=true");

        // Get all the judgeList where day5 is null
        defaultJudgeShouldNotBeFound("day5.specified=false");
    }

    @Test
    @Transactional
    public void getAllJudgesByDay10IsEqualToSomething() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where day10 equals to DEFAULT_DAY_10
        defaultJudgeShouldBeFound("day10.equals=" + DEFAULT_DAY_10);

        // Get all the judgeList where day10 equals to UPDATED_DAY_10
        defaultJudgeShouldNotBeFound("day10.equals=" + UPDATED_DAY_10);
    }

    @Test
    @Transactional
    public void getAllJudgesByDay10IsInShouldWork() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where day10 in DEFAULT_DAY_10 or UPDATED_DAY_10
        defaultJudgeShouldBeFound("day10.in=" + DEFAULT_DAY_10 + "," + UPDATED_DAY_10);

        // Get all the judgeList where day10 equals to UPDATED_DAY_10
        defaultJudgeShouldNotBeFound("day10.in=" + UPDATED_DAY_10);
    }

    @Test
    @Transactional
    public void getAllJudgesByDay10IsNullOrNotNull() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where day10 is not null
        defaultJudgeShouldBeFound("day10.specified=true");

        // Get all the judgeList where day10 is null
        defaultJudgeShouldNotBeFound("day10.specified=false");
    }

    @Test
    @Transactional
    public void getAllJudgesByDay30IsEqualToSomething() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where day30 equals to DEFAULT_DAY_30
        defaultJudgeShouldBeFound("day30.equals=" + DEFAULT_DAY_30);

        // Get all the judgeList where day30 equals to UPDATED_DAY_30
        defaultJudgeShouldNotBeFound("day30.equals=" + UPDATED_DAY_30);
    }

    @Test
    @Transactional
    public void getAllJudgesByDay30IsInShouldWork() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where day30 in DEFAULT_DAY_30 or UPDATED_DAY_30
        defaultJudgeShouldBeFound("day30.in=" + DEFAULT_DAY_30 + "," + UPDATED_DAY_30);

        // Get all the judgeList where day30 equals to UPDATED_DAY_30
        defaultJudgeShouldNotBeFound("day30.in=" + UPDATED_DAY_30);
    }

    @Test
    @Transactional
    public void getAllJudgesByDay30IsNullOrNotNull() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);

        // Get all the judgeList where day30 is not null
        defaultJudgeShouldBeFound("day30.specified=true");

        // Get all the judgeList where day30 is null
        defaultJudgeShouldNotBeFound("day30.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultJudgeShouldBeFound(String filter) throws Exception {
        restJudgeMockMvc.perform(get("/api/judges?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(judge.getId().intValue())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.[*].increase_total").value(hasItem(DEFAULT_INCREASE_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].increase_days").value(hasItem(DEFAULT_INCREASE_DAYS)))
            .andExpect(jsonPath("$.[*].day5").value(hasItem(DEFAULT_DAY_5.doubleValue())))
            .andExpect(jsonPath("$.[*].day10").value(hasItem(DEFAULT_DAY_10.doubleValue())))
            .andExpect(jsonPath("$.[*].day30").value(hasItem(DEFAULT_DAY_30.doubleValue())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultJudgeShouldNotBeFound(String filter) throws Exception {
        restJudgeMockMvc.perform(get("/api/judges?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingJudge() throws Exception {
        // Get the judge
        restJudgeMockMvc.perform(get("/api/judges/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJudge() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);
        judgeSearchRepository.save(judge);
        int databaseSizeBeforeUpdate = judgeRepository.findAll().size();

        // Update the judge
        Judge updatedJudge = judgeRepository.findOne(judge.getId());
        updatedJudge
            .score(UPDATED_SCORE)
            .increase_total(UPDATED_INCREASE_TOTAL)
            .increase_days(UPDATED_INCREASE_DAYS)
            .day5(UPDATED_DAY_5)
            .day10(UPDATED_DAY_10)
            .day30(UPDATED_DAY_30);
        JudgeDTO judgeDTO = judgeMapper.toDto(updatedJudge);

        restJudgeMockMvc.perform(put("/api/judges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(judgeDTO)))
            .andExpect(status().isOk());

        // Validate the Judge in the database
        List<Judge> judgeList = judgeRepository.findAll();
        assertThat(judgeList).hasSize(databaseSizeBeforeUpdate);
        Judge testJudge = judgeList.get(judgeList.size() - 1);
        assertThat(testJudge.getScore()).isEqualTo(UPDATED_SCORE);
        assertThat(testJudge.getIncrease_total()).isEqualTo(UPDATED_INCREASE_TOTAL);
        assertThat(testJudge.getIncrease_days()).isEqualTo(UPDATED_INCREASE_DAYS);
        assertThat(testJudge.getDay5()).isEqualTo(UPDATED_DAY_5);
        assertThat(testJudge.getDay10()).isEqualTo(UPDATED_DAY_10);
        assertThat(testJudge.getDay30()).isEqualTo(UPDATED_DAY_30);

        // Validate the Judge in Elasticsearch
        Judge judgeEs = judgeSearchRepository.findOne(testJudge.getId());
        assertThat(judgeEs).isEqualToComparingFieldByField(testJudge);
    }

    @Test
    @Transactional
    public void updateNonExistingJudge() throws Exception {
        int databaseSizeBeforeUpdate = judgeRepository.findAll().size();

        // Create the Judge
        JudgeDTO judgeDTO = judgeMapper.toDto(judge);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restJudgeMockMvc.perform(put("/api/judges")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(judgeDTO)))
            .andExpect(status().isCreated());

        // Validate the Judge in the database
        List<Judge> judgeList = judgeRepository.findAll();
        assertThat(judgeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteJudge() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);
        judgeSearchRepository.save(judge);
        int databaseSizeBeforeDelete = judgeRepository.findAll().size();

        // Get the judge
        restJudgeMockMvc.perform(delete("/api/judges/{id}", judge.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean judgeExistsInEs = judgeSearchRepository.exists(judge.getId());
        assertThat(judgeExistsInEs).isFalse();

        // Validate the database is empty
        List<Judge> judgeList = judgeRepository.findAll();
        assertThat(judgeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchJudge() throws Exception {
        // Initialize the database
        judgeRepository.saveAndFlush(judge);
        judgeSearchRepository.save(judge);

        // Search the judge
        restJudgeMockMvc.perform(get("/api/_search/judges?query=id:" + judge.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(judge.getId().intValue())))
            .andExpect(jsonPath("$.[*].score").value(hasItem(DEFAULT_SCORE)))
            .andExpect(jsonPath("$.[*].increase_total").value(hasItem(DEFAULT_INCREASE_TOTAL.doubleValue())))
            .andExpect(jsonPath("$.[*].increase_days").value(hasItem(DEFAULT_INCREASE_DAYS)))
            .andExpect(jsonPath("$.[*].day5").value(hasItem(DEFAULT_DAY_5.doubleValue())))
            .andExpect(jsonPath("$.[*].day10").value(hasItem(DEFAULT_DAY_10.doubleValue())))
            .andExpect(jsonPath("$.[*].day30").value(hasItem(DEFAULT_DAY_30.doubleValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Judge.class);
        Judge judge1 = new Judge();
        judge1.setId(1L);
        Judge judge2 = new Judge();
        judge2.setId(judge1.getId());
        assertThat(judge1).isEqualTo(judge2);
        judge2.setId(2L);
        assertThat(judge1).isNotEqualTo(judge2);
        judge1.setId(null);
        assertThat(judge1).isNotEqualTo(judge2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(JudgeDTO.class);
        JudgeDTO judgeDTO1 = new JudgeDTO();
        judgeDTO1.setId(1L);
        JudgeDTO judgeDTO2 = new JudgeDTO();
        assertThat(judgeDTO1).isNotEqualTo(judgeDTO2);
        judgeDTO2.setId(judgeDTO1.getId());
        assertThat(judgeDTO1).isEqualTo(judgeDTO2);
        judgeDTO2.setId(2L);
        assertThat(judgeDTO1).isNotEqualTo(judgeDTO2);
        judgeDTO1.setId(null);
        assertThat(judgeDTO1).isNotEqualTo(judgeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(judgeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(judgeMapper.fromId(null)).isNull();
    }
}
