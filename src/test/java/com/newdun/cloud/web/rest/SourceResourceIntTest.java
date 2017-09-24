package com.newdun.cloud.web.rest;

import com.newdun.cloud.SourceApp;

import com.newdun.cloud.config.SecurityBeanOverrideConfiguration;

import com.newdun.cloud.domain.Source;
import com.newdun.cloud.repository.SourceRepository;
import com.newdun.cloud.service.SourceService;
import com.newdun.cloud.repository.search.SourceSearchRepository;
import com.newdun.cloud.service.dto.SourceDTO;
import com.newdun.cloud.service.mapper.SourceMapper;
import com.newdun.cloud.web.rest.errors.ExceptionTranslator;
import com.newdun.cloud.service.dto.SourceCriteria;
import com.newdun.cloud.service.SourceQueryService;

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

import static com.newdun.cloud.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the SourceResource REST controller.
 *
 * @see SourceResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SourceApp.class, SecurityBeanOverrideConfiguration.class})
public class SourceResourceIntTest {

    private static final ZonedDateTime DEFAULT_DATE = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_DATE = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_MEDIA = "AAAAAAAAAA";
    private static final String UPDATED_MEDIA = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_STOCK = "AAAAAAAAAA";
    private static final String UPDATED_STOCK = "BBBBBBBBBB";

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private SourceMapper sourceMapper;

    @Autowired
    private SourceService sourceService;

    @Autowired
    private SourceSearchRepository sourceSearchRepository;

    @Autowired
    private SourceQueryService sourceQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restSourceMockMvc;

    private Source source;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SourceResource sourceResource = new SourceResource(sourceService, sourceQueryService);
        this.restSourceMockMvc = MockMvcBuilders.standaloneSetup(sourceResource)
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
    public static Source createEntity(EntityManager em) {
        Source source = new Source()
            .date(DEFAULT_DATE)
            .title(DEFAULT_TITLE)
            .desc(DEFAULT_DESC)
            .media(DEFAULT_MEDIA)
            .url(DEFAULT_URL)
            .stock(DEFAULT_STOCK);
        return source;
    }

    @Before
    public void initTest() {
        sourceSearchRepository.deleteAll();
        source = createEntity(em);
    }

    @Test
    @Transactional
    public void createSource() throws Exception {
        int databaseSizeBeforeCreate = sourceRepository.findAll().size();

        // Create the Source
        SourceDTO sourceDTO = sourceMapper.toDto(source);
        restSourceMockMvc.perform(post("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isCreated());

        // Validate the Source in the database
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeCreate + 1);
        Source testSource = sourceList.get(sourceList.size() - 1);
        assertThat(testSource.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testSource.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testSource.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testSource.getMedia()).isEqualTo(DEFAULT_MEDIA);
        assertThat(testSource.getUrl()).isEqualTo(DEFAULT_URL);
        assertThat(testSource.getStock()).isEqualTo(DEFAULT_STOCK);

        // Validate the Source in Elasticsearch
        Source sourceEs = sourceSearchRepository.findOne(testSource.getId());
        assertThat(sourceEs).isEqualToComparingFieldByField(testSource);
    }

    @Test
    @Transactional
    public void createSourceWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = sourceRepository.findAll().size();

        // Create the Source with an existing ID
        source.setId(1L);
        SourceDTO sourceDTO = sourceMapper.toDto(source);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSourceMockMvc.perform(post("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllSources() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList
        restSourceMockMvc.perform(get("/api/sources?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(source.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].media").value(hasItem(DEFAULT_MEDIA.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].stock").value(hasItem(DEFAULT_STOCK.toString())));
    }

    @Test
    @Transactional
    public void getSource() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get the source
        restSourceMockMvc.perform(get("/api/sources/{id}", source.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(source.getId().intValue()))
            .andExpect(jsonPath("$.date").value(sameInstant(DEFAULT_DATE)))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE.toString()))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC.toString()))
            .andExpect(jsonPath("$.media").value(DEFAULT_MEDIA.toString()))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL.toString()))
            .andExpect(jsonPath("$.stock").value(DEFAULT_STOCK.toString()));
    }

    @Test
    @Transactional
    public void getAllSourcesByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where date equals to DEFAULT_DATE
        defaultSourceShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the sourceList where date equals to UPDATED_DATE
        defaultSourceShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllSourcesByDateIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where date in DEFAULT_DATE or UPDATED_DATE
        defaultSourceShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the sourceList where date equals to UPDATED_DATE
        defaultSourceShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllSourcesByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where date is not null
        defaultSourceShouldBeFound("date.specified=true");

        // Get all the sourceList where date is null
        defaultSourceShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourcesByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where date greater than or equals to DEFAULT_DATE
        defaultSourceShouldBeFound("date.greaterOrEqualThan=" + DEFAULT_DATE);

        // Get all the sourceList where date greater than or equals to UPDATED_DATE
        defaultSourceShouldNotBeFound("date.greaterOrEqualThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    public void getAllSourcesByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where date less than or equals to DEFAULT_DATE
        defaultSourceShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the sourceList where date less than or equals to UPDATED_DATE
        defaultSourceShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }


    @Test
    @Transactional
    public void getAllSourcesByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where title equals to DEFAULT_TITLE
        defaultSourceShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the sourceList where title equals to UPDATED_TITLE
        defaultSourceShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllSourcesByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultSourceShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the sourceList where title equals to UPDATED_TITLE
        defaultSourceShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    public void getAllSourcesByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where title is not null
        defaultSourceShouldBeFound("title.specified=true");

        // Get all the sourceList where title is null
        defaultSourceShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourcesByMediaIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where media equals to DEFAULT_MEDIA
        defaultSourceShouldBeFound("media.equals=" + DEFAULT_MEDIA);

        // Get all the sourceList where media equals to UPDATED_MEDIA
        defaultSourceShouldNotBeFound("media.equals=" + UPDATED_MEDIA);
    }

    @Test
    @Transactional
    public void getAllSourcesByMediaIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where media in DEFAULT_MEDIA or UPDATED_MEDIA
        defaultSourceShouldBeFound("media.in=" + DEFAULT_MEDIA + "," + UPDATED_MEDIA);

        // Get all the sourceList where media equals to UPDATED_MEDIA
        defaultSourceShouldNotBeFound("media.in=" + UPDATED_MEDIA);
    }

    @Test
    @Transactional
    public void getAllSourcesByMediaIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where media is not null
        defaultSourceShouldBeFound("media.specified=true");

        // Get all the sourceList where media is null
        defaultSourceShouldNotBeFound("media.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourcesByUrlIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where url equals to DEFAULT_URL
        defaultSourceShouldBeFound("url.equals=" + DEFAULT_URL);

        // Get all the sourceList where url equals to UPDATED_URL
        defaultSourceShouldNotBeFound("url.equals=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllSourcesByUrlIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where url in DEFAULT_URL or UPDATED_URL
        defaultSourceShouldBeFound("url.in=" + DEFAULT_URL + "," + UPDATED_URL);

        // Get all the sourceList where url equals to UPDATED_URL
        defaultSourceShouldNotBeFound("url.in=" + UPDATED_URL);
    }

    @Test
    @Transactional
    public void getAllSourcesByUrlIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where url is not null
        defaultSourceShouldBeFound("url.specified=true");

        // Get all the sourceList where url is null
        defaultSourceShouldNotBeFound("url.specified=false");
    }

    @Test
    @Transactional
    public void getAllSourcesByStockIsEqualToSomething() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where stock equals to DEFAULT_STOCK
        defaultSourceShouldBeFound("stock.equals=" + DEFAULT_STOCK);

        // Get all the sourceList where stock equals to UPDATED_STOCK
        defaultSourceShouldNotBeFound("stock.equals=" + UPDATED_STOCK);
    }

    @Test
    @Transactional
    public void getAllSourcesByStockIsInShouldWork() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where stock in DEFAULT_STOCK or UPDATED_STOCK
        defaultSourceShouldBeFound("stock.in=" + DEFAULT_STOCK + "," + UPDATED_STOCK);

        // Get all the sourceList where stock equals to UPDATED_STOCK
        defaultSourceShouldNotBeFound("stock.in=" + UPDATED_STOCK);
    }

    @Test
    @Transactional
    public void getAllSourcesByStockIsNullOrNotNull() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);

        // Get all the sourceList where stock is not null
        defaultSourceShouldBeFound("stock.specified=true");

        // Get all the sourceList where stock is null
        defaultSourceShouldNotBeFound("stock.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultSourceShouldBeFound(String filter) throws Exception {
        restSourceMockMvc.perform(get("/api/sources?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(source.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].media").value(hasItem(DEFAULT_MEDIA.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].stock").value(hasItem(DEFAULT_STOCK.toString())));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultSourceShouldNotBeFound(String filter) throws Exception {
        restSourceMockMvc.perform(get("/api/sources?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingSource() throws Exception {
        // Get the source
        restSourceMockMvc.perform(get("/api/sources/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSource() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);
        sourceSearchRepository.save(source);
        int databaseSizeBeforeUpdate = sourceRepository.findAll().size();

        // Update the source
        Source updatedSource = sourceRepository.findOne(source.getId());
        updatedSource
            .date(UPDATED_DATE)
            .title(UPDATED_TITLE)
            .desc(UPDATED_DESC)
            .media(UPDATED_MEDIA)
            .url(UPDATED_URL)
            .stock(UPDATED_STOCK);
        SourceDTO sourceDTO = sourceMapper.toDto(updatedSource);

        restSourceMockMvc.perform(put("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isOk());

        // Validate the Source in the database
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeUpdate);
        Source testSource = sourceList.get(sourceList.size() - 1);
        assertThat(testSource.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testSource.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testSource.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testSource.getMedia()).isEqualTo(UPDATED_MEDIA);
        assertThat(testSource.getUrl()).isEqualTo(UPDATED_URL);
        assertThat(testSource.getStock()).isEqualTo(UPDATED_STOCK);

        // Validate the Source in Elasticsearch
        Source sourceEs = sourceSearchRepository.findOne(testSource.getId());
        assertThat(sourceEs).isEqualToComparingFieldByField(testSource);
    }

    @Test
    @Transactional
    public void updateNonExistingSource() throws Exception {
        int databaseSizeBeforeUpdate = sourceRepository.findAll().size();

        // Create the Source
        SourceDTO sourceDTO = sourceMapper.toDto(source);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restSourceMockMvc.perform(put("/api/sources")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(sourceDTO)))
            .andExpect(status().isCreated());

        // Validate the Source in the database
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteSource() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);
        sourceSearchRepository.save(source);
        int databaseSizeBeforeDelete = sourceRepository.findAll().size();

        // Get the source
        restSourceMockMvc.perform(delete("/api/sources/{id}", source.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean sourceExistsInEs = sourceSearchRepository.exists(source.getId());
        assertThat(sourceExistsInEs).isFalse();

        // Validate the database is empty
        List<Source> sourceList = sourceRepository.findAll();
        assertThat(sourceList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchSource() throws Exception {
        // Initialize the database
        sourceRepository.saveAndFlush(source);
        sourceSearchRepository.save(source);

        // Search the source
        restSourceMockMvc.perform(get("/api/_search/sources?query=id:" + source.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(source.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(sameInstant(DEFAULT_DATE))))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE.toString())))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC.toString())))
            .andExpect(jsonPath("$.[*].media").value(hasItem(DEFAULT_MEDIA.toString())))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL.toString())))
            .andExpect(jsonPath("$.[*].stock").value(hasItem(DEFAULT_STOCK.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Source.class);
        Source source1 = new Source();
        source1.setId(1L);
        Source source2 = new Source();
        source2.setId(source1.getId());
        assertThat(source1).isEqualTo(source2);
        source2.setId(2L);
        assertThat(source1).isNotEqualTo(source2);
        source1.setId(null);
        assertThat(source1).isNotEqualTo(source2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SourceDTO.class);
        SourceDTO sourceDTO1 = new SourceDTO();
        sourceDTO1.setId(1L);
        SourceDTO sourceDTO2 = new SourceDTO();
        assertThat(sourceDTO1).isNotEqualTo(sourceDTO2);
        sourceDTO2.setId(sourceDTO1.getId());
        assertThat(sourceDTO1).isEqualTo(sourceDTO2);
        sourceDTO2.setId(2L);
        assertThat(sourceDTO1).isNotEqualTo(sourceDTO2);
        sourceDTO1.setId(null);
        assertThat(sourceDTO1).isNotEqualTo(sourceDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(sourceMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(sourceMapper.fromId(null)).isNull();
    }
}
