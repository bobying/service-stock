package com.newdun.cloud.service;

import com.newdun.cloud.domain.Source;
import com.newdun.cloud.repository.SourceRepository;
import com.newdun.cloud.repository.search.SourceSearchRepository;
import com.newdun.cloud.service.dto.SourceDTO;
import com.newdun.cloud.service.mapper.SourceMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Source.
 */
@Service
@Transactional
public class SourceService {

    private final Logger log = LoggerFactory.getLogger(SourceService.class);

    private final SourceRepository sourceRepository;

    private final SourceMapper sourceMapper;

    private final SourceSearchRepository sourceSearchRepository;
    public SourceService(SourceRepository sourceRepository, SourceMapper sourceMapper, SourceSearchRepository sourceSearchRepository) {
        this.sourceRepository = sourceRepository;
        this.sourceMapper = sourceMapper;
        this.sourceSearchRepository = sourceSearchRepository;
    }

    /**
     * Save a source.
     *
     * @param sourceDTO the entity to save
     * @return the persisted entity
     */
    public SourceDTO save(SourceDTO sourceDTO) {
        log.debug("Request to save Source : {}", sourceDTO);
        Source source = sourceMapper.toEntity(sourceDTO);
        source = sourceRepository.save(source);
        SourceDTO result = sourceMapper.toDto(source);
        sourceSearchRepository.save(source);
        return result;
    }

    /**
     *  Get all the sources.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<SourceDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Sources");
        return sourceRepository.findAll(pageable)
            .map(sourceMapper::toDto);
    }

    /**
     *  Get one source by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public SourceDTO findOne(Long id) {
        log.debug("Request to get Source : {}", id);
        Source source = sourceRepository.findOne(id);
        return sourceMapper.toDto(source);
    }

    /**
     *  Delete the  source by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Source : {}", id);
        sourceRepository.delete(id);
        sourceSearchRepository.delete(id);
    }

    /**
     * Search for the source corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<SourceDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Sources for query {}", query);
        Page<Source> result = sourceSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(sourceMapper::toDto);
    }
}
