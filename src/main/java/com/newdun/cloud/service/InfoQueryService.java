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

import com.newdun.cloud.domain.Info;
import com.newdun.cloud.domain.*; // for static metamodels
import com.newdun.cloud.repository.InfoRepository;
import com.newdun.cloud.repository.search.InfoSearchRepository;
import com.newdun.cloud.service.dto.InfoCriteria;

import com.newdun.cloud.service.dto.InfoDTO;
import com.newdun.cloud.service.mapper.InfoMapper;

/**
 * Service for executing complex queries for Info entities in the database.
 * The main input is a {@link InfoCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {%link InfoDTO} or a {@link Page} of {%link InfoDTO} which fullfills the criterias
 */
@Service
@Transactional(readOnly = true)
public class InfoQueryService extends QueryService<Info> {

    private final Logger log = LoggerFactory.getLogger(InfoQueryService.class);


    private final InfoRepository infoRepository;

    private final InfoMapper infoMapper;

    private final InfoSearchRepository infoSearchRepository;
    public InfoQueryService(InfoRepository infoRepository, InfoMapper infoMapper, InfoSearchRepository infoSearchRepository) {
        this.infoRepository = infoRepository;
        this.infoMapper = infoMapper;
        this.infoSearchRepository = infoSearchRepository;
    }

    /**
     * Return a {@link List} of {%link InfoDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<InfoDTO> findByCriteria(InfoCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Info> specification = createSpecification(criteria);
        return infoMapper.toDto(infoRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {%link InfoDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<InfoDTO> findByCriteria(InfoCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Info> specification = createSpecification(criteria);
        final Page<Info> result = infoRepository.findAll(specification, page);
        return result.map(infoMapper::toDto);
    }

    /**
     * Function to convert InfoCriteria to a {@link Specifications}
     */
    private Specifications<Info> createSpecification(InfoCriteria criteria) {
        Specifications<Info> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Info_.id));
            }
            if (criteria.getDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDate(), Info_.date));
            }
            if (criteria.getTitle() != null) {
                specification = specification.and(buildStringSpecification(criteria.getTitle(), Info_.title));
            }
            if (criteria.getStock() != null) {
                specification = specification.and(buildStringSpecification(criteria.getStock(), Info_.stock));
            }
            if (criteria.getSourceId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getSourceId(), Info_.source, Source_.id));
            }
            if (criteria.getJudgeId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getJudgeId(), Info_.judge, Judge_.id));
            }
        }
        return specification;
    }

}
