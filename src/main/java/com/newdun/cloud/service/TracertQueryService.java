package com.newdun.cloud.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.newdun.cloud.domain.Tracert;
import com.newdun.cloud.domain.*; // for static metamodels
import com.newdun.cloud.repository.TracertRepository;
import com.newdun.cloud.repository.search.TracertSearchRepository;
import com.newdun.cloud.service.dto.TracertCriteria;

import com.newdun.cloud.service.dto.TracertDTO;
import com.newdun.cloud.service.mapper.TracertMapper;

/**
 * Service for executing complex queries for Tracert entities in the database.
 * The main input is a {@link TracertCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {%link TracertDTO} or a {@link Page} of {%link TracertDTO} which fullfills the criterias
 */
@Service
@Transactional(readOnly = true)
public class TracertQueryService extends QueryService<Tracert> {

    private final Logger log = LoggerFactory.getLogger(TracertQueryService.class);


    private final TracertRepository tracertRepository;

    private final TracertMapper tracertMapper;

    private final TracertSearchRepository tracertSearchRepository;
    public TracertQueryService(TracertRepository tracertRepository, TracertMapper tracertMapper, TracertSearchRepository tracertSearchRepository) {
        this.tracertRepository = tracertRepository;
        this.tracertMapper = tracertMapper;
        this.tracertSearchRepository = tracertSearchRepository;
    }

    /**
     * Return a {@link List} of {%link TracertDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<TracertDTO> findByCriteria(TracertCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Tracert> specification = createSpecification(criteria);
        return tracertMapper.toDto(tracertRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {%link TracertDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<TracertDTO> findByCriteria(TracertCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Tracert> specification = createSpecification(criteria);
        final Page<Tracert> result = tracertRepository.findAll(specification, page);
        return result.map(tracertMapper::toDto);
    }

    /**
     * Function to convert TracertCriteria to a {@link Specifications}
     */
    private Specifications<Tracert> createSpecification(TracertCriteria criteria) {
        Specifications<Tracert> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Tracert_.id));
            }
            if (criteria.getDays() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDays(), Tracert_.days));
            }
            if (criteria.getIncrease_day() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getIncrease_day(), Tracert_.increase_day));
            }
            if (criteria.getIncrease_total() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getIncrease_total(), Tracert_.increase_total));
            }
            if (criteria.getSourceId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getSourceId(), Tracert_.source, Source_.id));
            }
        }
        return specification;
    }

}
