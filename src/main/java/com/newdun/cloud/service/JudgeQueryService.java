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

import com.newdun.cloud.domain.Judge;
import com.newdun.cloud.domain.*; // for static metamodels
import com.newdun.cloud.repository.JudgeRepository;
import com.newdun.cloud.repository.search.JudgeSearchRepository;
import com.newdun.cloud.service.dto.JudgeCriteria;

import com.newdun.cloud.service.dto.JudgeDTO;
import com.newdun.cloud.service.mapper.JudgeMapper;

/**
 * Service for executing complex queries for Judge entities in the database.
 * The main input is a {@link JudgeCriteria} which get's converted to {@link Specifications},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {%link JudgeDTO} or a {@link Page} of {%link JudgeDTO} which fullfills the criterias
 */
@Service
@Transactional(readOnly = true)
public class JudgeQueryService extends QueryService<Judge> {

    private final Logger log = LoggerFactory.getLogger(JudgeQueryService.class);


    private final JudgeRepository judgeRepository;

    private final JudgeMapper judgeMapper;

    private final JudgeSearchRepository judgeSearchRepository;
    public JudgeQueryService(JudgeRepository judgeRepository, JudgeMapper judgeMapper, JudgeSearchRepository judgeSearchRepository) {
        this.judgeRepository = judgeRepository;
        this.judgeMapper = judgeMapper;
        this.judgeSearchRepository = judgeSearchRepository;
    }

    /**
     * Return a {@link List} of {%link JudgeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<JudgeDTO> findByCriteria(JudgeCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specifications<Judge> specification = createSpecification(criteria);
        return judgeMapper.toDto(judgeRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {%link JudgeDTO} which matches the criteria from the database
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<JudgeDTO> findByCriteria(JudgeCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specifications<Judge> specification = createSpecification(criteria);
        final Page<Judge> result = judgeRepository.findAll(specification, page);
        return result.map(judgeMapper::toDto);
    }

    /**
     * Function to convert JudgeCriteria to a {@link Specifications}
     */
    private Specifications<Judge> createSpecification(JudgeCriteria criteria) {
        Specifications<Judge> specification = Specifications.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildSpecification(criteria.getId(), Judge_.id));
            }
            if (criteria.getScore() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getScore(), Judge_.score));
            }
            if (criteria.getIncrease_total() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getIncrease_total(), Judge_.increase_total));
            }
            if (criteria.getIncrease_days() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getIncrease_days(), Judge_.increase_days));
            }
            if (criteria.getDay5() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDay5(), Judge_.day5));
            }
            if (criteria.getDay10() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDay10(), Judge_.day10));
            }
            if (criteria.getDay30() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDay30(), Judge_.day30));
            }
            if (criteria.getDay20() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getDay20(), Judge_.day20));
            }
            if (criteria.getInfoId() != null) {
                specification = specification.and(buildReferringEntitySpecification(criteria.getInfoId(), Judge_.info, Info_.id));
            }
        }
        return specification;
    }

}
