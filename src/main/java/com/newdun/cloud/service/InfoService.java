package com.newdun.cloud.service;

import com.newdun.cloud.domain.Info;
import com.newdun.cloud.repository.InfoRepository;
import com.newdun.cloud.repository.search.InfoSearchRepository;
import com.newdun.cloud.service.dto.InfoDTO;
import com.newdun.cloud.service.mapper.InfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Info.
 */
@Service
@Transactional
public class InfoService {

    private final Logger log = LoggerFactory.getLogger(InfoService.class);

    private final InfoRepository infoRepository;

    private final InfoMapper infoMapper;

    private final InfoSearchRepository infoSearchRepository;
    public InfoService(InfoRepository infoRepository, InfoMapper infoMapper, InfoSearchRepository infoSearchRepository) {
        this.infoRepository = infoRepository;
        this.infoMapper = infoMapper;
        this.infoSearchRepository = infoSearchRepository;
    }

    /**
     * Save a info.
     *
     * @param infoDTO the entity to save
     * @return the persisted entity
     */
    public InfoDTO save(InfoDTO infoDTO) {
        log.debug("Request to save Info : {}", infoDTO);
        Info info = infoMapper.toEntity(infoDTO);
        info = infoRepository.save(info);
        InfoDTO result = infoMapper.toDto(info);
        infoSearchRepository.save(info);
        return result;
    }

    /**
     *  Get all the infos.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<InfoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Infos");
        return infoRepository.findAll(pageable)
            .map(infoMapper::toDto);
    }


    /**
     *  get all the infos where Judge is null.
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public List<InfoDTO> findAllWhereJudgeIsNull() {
        log.debug("Request to get all infos where Judge is null");
        return StreamSupport
            .stream(infoRepository.findAll().spliterator(), false)
            .filter(info -> info.getJudge() == null)
            .map(infoMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     *  Get one info by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true)
    public InfoDTO findOne(Long id) {
        log.debug("Request to get Info : {}", id);
        Info info = infoRepository.getOne(id);
        return infoMapper.toDto(info);
    }

    /**
     *  Delete the  info by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Info : {}", id);
        infoRepository.deleteById(id);
        infoSearchRepository.deleteById(id);
    }

    /**
     * Search for the info corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true)
    public Page<InfoDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Infos for query {}", query);
        Page<Info> result = infoSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(infoMapper::toDto);
    }
}
