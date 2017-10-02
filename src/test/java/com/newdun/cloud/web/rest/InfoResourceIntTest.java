package com.newdun.cloud.web.rest;

import com.newdun.cloud.SourceApp;

import com.newdun.cloud.config.SecurityBeanOverrideConfiguration;

import com.newdun.cloud.domain.Info;
import com.newdun.cloud.repository.InfoRepository;
import com.newdun.cloud.service.InfoService;
import com.newdun.cloud.repository.search.InfoSearchRepository;
import com.newdun.cloud.service.dto.InfoDTO;
import com.newdun.cloud.service.mapper.InfoMapper;
import com.newdun.cloud.web.rest.errors.ExceptionTranslator;
import com.newdun.cloud.service.dto.InfoCriteria;
import com.newdun.cloud.service.InfoQueryService;

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
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static com.newdun.cloud.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the InfoResource REST controller.
 *
 * @see InfoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SourceApp.class, SecurityBeanOverrideConfiguration.class})
public class InfoResourceIntTest {

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_STOCK = "AAAAAAAAAA";
    private static final String UPDATED_STOCK = "BBBBBBBBBB";

    @Autowired
    private InfoRepository infoRepository;

    @Autowired
    private InfoMapper infoMapper;

    @Autowired
    private InfoService infoService;

    @Autowired
    private InfoSearchRepository infoSearchRepository;

    @Autowired
    private InfoQueryService infoQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restInfoMockMvc;

    private Info info;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final InfoResource infoResource = new InfoResource(infoService, infoQueryService);
        this.restInfoMockMvc = MockMvcBuilders.standaloneSetup(infoResource)
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
    public static Info createEntity(EntityManager em) {
        Info info = new Info()
            .date(DEFAULT_DATE)
            .title(DEFAULT_TITLE)
            .desc(DEFAULT_DESC)
            .stock(DEFAULT_STOCK);
        return info;
    }

    @Before
    public void initTest() {
        infoSearchRepository.deleteAll();
        info = createEntity(em);
    }

    @Test
    @Transactional
    public void createInfo() throws Exception {
        int databaseSizeBeforeCreate = infoRepository.findAll().size();

        // Create the Info
        InfoDTO infoDTO = infoMapper.toDto(info);
        restInfoMockMvc.perform(post("/api/infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(infoDTO)))
            .andExpect(status().isCreated());

        // Validate the Info in the database
        List<Info> infoList = infoRepository.findAll();
        assertThat(infoList).hasSize(databaseSizeBeforeCreate + 1);
        Info testInfo = infoList.get(infoList.size() - 1);
        assertThat(testInfo.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testInfo.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testInfo.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testInfo.getStock()).isEqualTo(DEFAULT_STOCK);

        // Validate the Info in Elasticsearch
        Optional<Info> infoEs = infoSearchRepository.findById(testInfo.getId());
        assertThat(infoEs.get()).isEqualToComparingFieldByField(testInfo);
    }

    @Test
    @Transactional
    public void createInfoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = infoRepository.findAll().size();

        // Create the Info with an existing ID
        info.setId(1L);
        InfoDTO infoDTO = infoMapper.toDto(info);

        // An entity with an existing ID cannot be created, so this API call must fail
        restInfoMockMvc.perform(post("/api/infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(infoDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Info> infoList = infoRepository.findAll();
        assertThat(infoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllInfos() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);

        // Get all the infoList
        restInfoMockMvc.perform(get("/api/infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(info.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].stock").value(hasItem(DEFAULT_STOCK.toString())));
    }

    @Test
    @Transactional
    public void getInfo() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);

        // Get the info
        restInfoMockMvc.perform(get("/api/infos/{id}", info.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(info.getId().intValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()))
            .andExpect(jsonPath("$.stock").value(DEFAULT_STOCK.toString()));
    }

    @Test
    @Transactional
    public void getAllInfosByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);

        // Get all the infoList where date equals to DEFAULT_DATE
        defaultInfoShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the infoList where date equals to UPDATED_DATE
        defaultInfoShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllInfosByDateIsInShouldWork() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);

        // Get all the infoList where date in DEFAULT_DATE or UPDATED_DATE
        defaultInfoShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the infoList where date equals to UPDATED_DATE
        defaultInfoShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllInfosByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);

        // Get all the infoList where date is not null
        defaultInfoShouldBeFound("date.specified=true");

        // Get all the infoList where date is null
        defaultInfoShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllInfosByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);

        // Get all the infoList where date greater than or equals to DEFAULT_DATE
        defaultInfoShouldBeFound("date.greaterOrEqualThan=" + DEFAULT_DATE);

        // Get all the infoList where date greater than or equals to UPDATED_DATE
        defaultInfoShouldNotBeFound("date.greaterOrEqualThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllInfosByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);

        // Get all the infoList where date less than or equals to DEFAULT_DATE
        defaultInfoShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the infoList where date less than or equals to UPDATED_DATE
        defaultInfoShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }


    @Test
    @Transactional
    public void getAllInfosByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);

        // Get all the infoList where title equals to DEFAULT_TITLE
        defaultInfoShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the infoList where title equals to UPDATED_TITLE
        defaultInfoShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllInfosByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);

        // Get all the infoList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultInfoShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the infoList where title equals to UPDATED_TITLE
        defaultInfoShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllInfosByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);

        // Get all the infoList where title is not null
        defaultInfoShouldBeFound("title.specified=true");

        // Get all the infoList where title is null
        defaultInfoShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    public void getAllInfosByStockIsEqualToSomething() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);

        // Get all the infoList where stock equals to DEFAULT_STOCK
        defaultInfoShouldBeFound("stock.equals=" + DEFAULT_STOCK);

        // Get all the infoList where stock equals to UPDATED_STOCK
        defaultInfoShouldNotBeFound("stock.equals=" + UPDATED_STOCK);
    }

    @Test
    @Transactional
    public void getAllInfosByStockIsInShouldWork() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);

        // Get all the infoList where stock in DEFAULT_STOCK or UPDATED_STOCK
        defaultInfoShouldBeFound("stock.in=" + DEFAULT_STOCK + "," + UPDATED_STOCK);

        // Get all the infoList where stock equals to UPDATED_STOCK
        defaultInfoShouldNotBeFound("stock.in=" + UPDATED_STOCK);
    }

    @Test
    @Transactional
    public void getAllInfosByStockIsNullOrNotNull() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);

        // Get all the infoList where stock is not null
        defaultInfoShouldBeFound("stock.specified=true");

        // Get all the infoList where stock is null
        defaultInfoShouldNotBeFound("stock.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultInfoShouldBeFound(String filter) throws Exception {
        restInfoMockMvc.perform(get("/api/infos?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(info.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].stock").value(hasItem(DEFAULT_STOCK.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultInfoShouldNotBeFound(String filter) throws Exception {
        restInfoMockMvc.perform(get("/api/infos?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingInfo() throws Exception {
        // Get the info
        restInfoMockMvc.perform(get("/api/infos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateInfo() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);
        infoSearchRepository.save(info);
        int databaseSizeBeforeUpdate = infoRepository.findAll().size();

        // Update the info
        Info updatedInfo = infoRepository.getOne(info.getId());
        updatedInfo
            .date(UPDATED_DATE)
            .title(UPDATED_TITLE)
            .desc(UPDATED_DESC)
            .stock(UPDATED_STOCK);
        InfoDTO infoDTO = infoMapper.toDto(updatedInfo);

        restInfoMockMvc.perform(put("/api/infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(infoDTO)))
            .andExpect(status().isOk());

        // Validate the Info in the database
        List<Info> infoList = infoRepository.findAll();
        assertThat(infoList).hasSize(databaseSizeBeforeUpdate);
        Info testInfo = infoList.get(infoList.size() - 1);
        assertThat(testInfo.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testInfo.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testInfo.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testInfo.getStock()).isEqualTo(UPDATED_STOCK);

        // Validate the Info in Elasticsearch
        Optional<Info> infoEs = infoSearchRepository.findById(testInfo.getId());
        assertThat(infoEs.get()).isEqualToComparingFieldByField(testInfo);
    }

    @Test
    @Transactional
    public void updateNonExistingInfo() throws Exception {
        int databaseSizeBeforeUpdate = infoRepository.findAll().size();

        // Create the Info
        InfoDTO infoDTO = infoMapper.toDto(info);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restInfoMockMvc.perform(put("/api/infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(infoDTO)))
            .andExpect(status().isCreated());

        // Validate the Info in the database
        List<Info> infoList = infoRepository.findAll();
        assertThat(infoList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteInfo() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);
        infoSearchRepository.save(info);
        int databaseSizeBeforeDelete = infoRepository.findAll().size();

        // Get the info
        restInfoMockMvc.perform(delete("/api/infos/{id}", info.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean infoExistsInEs = infoSearchRepository.existsById(info.getId());
        assertThat(infoExistsInEs).isFalse();

        // Validate the database is empty
        List<Info> infoList = infoRepository.findAll();
        assertThat(infoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchInfo() throws Exception {
        // Initialize the database
        infoRepository.saveAndFlush(info);
        infoSearchRepository.save(info);

        // Search the info
        restInfoMockMvc.perform(get("/api/_search/infos?query=id:" + info.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(info.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].stock").value(hasItem(DEFAULT_STOCK.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Info.class);
        Info info1 = new Info();
        info1.setId(1L);
        Info info2 = new Info();
        info2.setId(info1.getId());
        assertThat(info1).isEqualTo(info2);
        info2.setId(2L);
        assertThat(info1).isNotEqualTo(info2);
        info1.setId(null);
        assertThat(info1).isNotEqualTo(info2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(InfoDTO.class);
        InfoDTO infoDTO1 = new InfoDTO();
        infoDTO1.setId(1L);
        InfoDTO infoDTO2 = new InfoDTO();
        assertThat(infoDTO1).isNotEqualTo(infoDTO2);
        infoDTO2.setId(infoDTO1.getId());
        assertThat(infoDTO1).isEqualTo(infoDTO2);
        infoDTO2.setId(2L);
        assertThat(infoDTO1).isNotEqualTo(infoDTO2);
        infoDTO1.setId(null);
        assertThat(infoDTO1).isNotEqualTo(infoDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(infoMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(infoMapper.fromId(null)).isNull();
    }
}
