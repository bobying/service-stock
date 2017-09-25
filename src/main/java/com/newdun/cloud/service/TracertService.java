package com.newdun.cloud.service;

import com.newdun.cloud.domain.Tracert;
import com.newdun.cloud.repository.TracertRepository;
import com.newdun.cloud.repository.search.TracertSearchRepository;
import com.newdun.cloud.service.dto.TracertDTO;
import com.newdun.cloud.service.mapper.TracertMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Tracert.
 */
@Service
@Transactional
public class TracertService {

    private final Logger log = LoggerFactory.getLogger(TracertService.class);

    private final TracertRepository tracertRepository;

    private final TracertMapper tracertMapper;

    private final TracertSearchRepository tracertSearchRepository;
    public TracertService(TracertRepository tracertRepository, TracertMapper tracertMapper, TracertSearchRepository tracertSearchRepository) {
        this.tracertRepository = tracertRepository;
        this.tracertMapper = tracertMapper;
        this.tracertSearchRepository = tracertSearchRepository;
    }

    /**
     * Save a tracert.
     *
     * @param tracertDTO the entity to save
     * @return the persisted entity
     */
    public TracertDTO save(TracertDTO tracertDTO) {
        log.debug("Request to save Tracert : {}", tracertDTO);
        Tracert tracert = tracertMapper.toEntity(tracertDTO);
        tracert = tracertRepository.save(tracert);
        TracertDTO result = tracertMapper.toDto(tracert);
        tracertSearchRepository.save(tracert);
        return result;
    }

    /**
     *  Get all the tracerts.
     *
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<TracertDTO> findAll() {
        log.debug("Request to get all Tracerts");
        return tracertRepository.findAll().stream()
            .map(tracertMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get one tracert by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public TracertDTO findOne(Long id) {
        log.debug("Request to get Tracert : {}", id);
        Tracert tracert = tracertRepository.findOne(id);
        return tracertMapper.toDto(tracert);
    }

    /**
     *  Delete the  tracert by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Tracert : {}", id);
        tracertRepository.delete(id);
        tracertSearchRepository.delete(id);
    }

    /**
     * Search for the tracert corresponding to the query.
     *
     *  @param query the query of the search
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public List<TracertDTO> search(String query) {
        log.debug("Request to search Tracerts for query {}", query);
        return StreamSupport
            .stream(tracertSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(tracertMapper::toDto)
            .collect(Collectors.toList());
    }
}
