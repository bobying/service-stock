package com.newdun.cloud.service;


import java.time.ZonedDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.newdun.cloud.domain.Source;
import com.newdun.cloud.domain.*; // for static metamodels
import com.newdun.cloud.repository.SourceRepository;
import com.newdun.cloud.repository.search.SourceSearchRepository;
import com.newdun.cloud.service.dto.SourceCriteria;

import com.newdun.cloud.service.dto.SourceDTO;
import com.newdun.cloud.service.mapper.SourceMapper;

/**
 * Service for executing complex queries for Source entities in the database.
 * The main input is a {@link SourceCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {%link SourceDTO} or a {@link Page} of {%link SourceDTO} which fullfills the criterias
 */
@Service
@Transactional(readOnly = true)
public class SourceQueryService extends QueryService<Source> {

    private final Logger log = LoggerFactory.getLogger(SourceQueryService.class);


    private final SourceRepository sourceRepository;

    private final SourceMapper sourceMapper;

    private final SourceSearchRepository sourceSearchRepository;
    public SourceQueryService(SourceRepository sourceRepository, SourceMapper sourceMapper, SourceSearchRepository sourceSearchRepository) {
        this.sourceRepository = sourceRepository;
        this.sourceMapper = sourceMapper;
        this.sourceSearchRepository = sourceSearchRepository;
    }

    /**
     * Return a {@link List} of {%link SourceDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<SourceDTO> findByCriteria(SourceCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Source> specification = createSpecification(criteria);
        return sourceMapper.toDto(sourceRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {%link SourceDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<SourceDTO> findByCriteria(SourceCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Source> specification = createSpecification(criteria);
        final Page<Source> result = sourceRepository.findAll(specification, page);
        return result.map(sourceMapper::toDto);
    }

    /**
     * Function to convert SourceCriteria to a {@link Specifications}
     */
    private Specifications<Source> createSpecification(SourceCriteria criteria) {
        Specifications<Source> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Source_.id));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Source_.date));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Source_.title));
            }
            if (criteria.getMedia() != null) {
                specification = specification.and(buildStringSpecification(criteria.getMedia(), Source_.media));
            }
            if (criteria.getUrl() != null) {
                specification = specification.and(buildStringSpecification(criteria.getUrl(), Source_.url));
            }
            if (criteria.getStock() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStock(), Source_.stock));
            }
        }
        return specification;
    }

}
