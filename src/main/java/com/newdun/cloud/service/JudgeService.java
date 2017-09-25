package com.newdun.cloud.service;

import com.newdun.cloud.domain.Judge;
import com.newdun.cloud.repository.JudgeRepository;
import com.newdun.cloud.repository.search.JudgeSearchRepository;
import com.newdun.cloud.service.dto.JudgeDTO;
import com.newdun.cloud.service.mapper.JudgeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Judge.
 */
@Service
@Transactional
public class JudgeService {

    private final Logger log = LoggerFactory.getLogger(JudgeService.class);

    private final JudgeRepository judgeRepository;

    private final JudgeMapper judgeMapper;

    private final JudgeSearchRepository judgeSearchRepository;
    public JudgeService(JudgeRepository judgeRepository, JudgeMapper judgeMapper, JudgeSearchRepository judgeSearchRepository) {
        this.judgeRepository = judgeRepository;
        this.judgeMapper = judgeMapper;
        this.judgeSearchRepository = judgeSearchRepository;
    }

    /**
     * Save a judge.
     *
     * @param judgeDTO the entity to save
     * @return the persisted entity
     */
    public JudgeDTO save(JudgeDTO judgeDTO) {
        log.debug("Request to save Judge : {}", judgeDTO);
        Judge judge = judgeMapper.toEntity(judgeDTO);
        judge = judgeRepository.save(judge);
        JudgeDTO result = judgeMapper.toDto(judge);
        judgeSearchRepository.save(judge);
        return result;
    }

    /**
     *  Get all the judges.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<JudgeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Judges");
        return judgeRepository.findAll(pageable)
            .map(judgeMapper::toDto);
    }

    /**
     *  Get one judge by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public JudgeDTO findOne(Long id) {
        log.debug("Request to get Judge : {}", id);
        Judge judge = judgeRepository.findOne(id);
        return judgeMapper.toDto(judge);
    }

    /**
     *  Delete the  judge by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Judge : {}", id);
        judgeRepository.delete(id);
        judgeSearchRepository.delete(id);
    }

    /**
     * Search for the judge corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<JudgeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Judges for query {}", query);
        Page<Judge> result = judgeSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(judgeMapper::toDto);
    }
}
