package com.newdun.cloud.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.newdun.cloud.service.TracertService;
import com.newdun.cloud.web.rest.util.HeaderUtil;
import com.newdun.cloud.service.dto.TracertDTO;
import com.newdun.cloud.service.dto.TracertCriteria;
import com.newdun.cloud.service.TracertQueryService;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Tracert.
 */
@RestController
@RequestMapping("/api")
public class TracertResource {

    private final Logger log = LoggerFactory.getLogger(TracertResource.class);

    private static final String ENTITY_NAME = "tracert";

    private final TracertService tracertService;
    private final TracertQueryService tracertQueryService;

    public TracertResource(TracertService tracertService, TracertQueryService tracertQueryService) {
        this.tracertService = tracertService;
        this.tracertQueryService = tracertQueryService;
    }

    /**
     * POST  /tracerts : Create a new tracert.
     *
     * @param tracertDTO the tracertDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new tracertDTO, or with status 400 (Bad Request) if the tracert has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/tracerts")
    @Timed
    public ResponseEntity<TracertDTO> createTracert(@Valid @RequestBody TracertDTO tracertDTO) throws URISyntaxException {
        log.debug("REST request to save Tracert : {}", tracertDTO);
        if (tracertDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new tracert cannot already have an ID")).body(null);
        }
        TracertDTO result = tracertService.save(tracertDTO);
        return ResponseEntity.created(new URI("/api/tracerts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /tracerts : Updates an existing tracert.
     *
     * @param tracertDTO the tracertDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated tracertDTO,
     * or with status 400 (Bad Request) if the tracertDTO is not valid,
     * or with status 500 (Internal Server Error) if the tracertDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/tracerts")
    @Timed
    public ResponseEntity<TracertDTO> updateTracert(@Valid @RequestBody TracertDTO tracertDTO) throws URISyntaxException {
        log.debug("REST request to update Tracert : {}", tracertDTO);
        if (tracertDTO.getId() == null) {
            return createTracert(tracertDTO);
        }
        TracertDTO result = tracertService.save(tracertDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, tracertDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /tracerts : get all the tracerts.
     *
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of tracerts in body
     */
    @GetMapping("/tracerts")
    @Timed
    public ResponseEntity<List<TracertDTO>> getAllTracerts(TracertCriteria criteria) {
        log.debug("REST request to get Tracerts by criteria: {}", criteria);
        List<TracertDTO> entityList = tracertQueryService.findByCriteria(criteria);
        return ResponseEntity.ok().body(entityList);
    }

    /**
     * GET  /tracerts/:id : get the "id" tracert.
     *
     * @param id the id of the tracertDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the tracertDTO, or with status 404 (Not Found)
     */
    @GetMapping("/tracerts/{id}")
    @Timed
    public ResponseEntity<TracertDTO> getTracert(@PathVariable Long id) {
        log.debug("REST request to get Tracert : {}", id);
        TracertDTO tracertDTO = tracertService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(tracertDTO));
    }

    /**
     * DELETE  /tracerts/:id : delete the "id" tracert.
     *
     * @param id the id of the tracertDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/tracerts/{id}")
    @Timed
    public ResponseEntity<Void> deleteTracert(@PathVariable Long id) {
        log.debug("REST request to delete Tracert : {}", id);
        tracertService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/tracerts?query=:query : search for the tracert corresponding
     * to the query.
     *
     * @param query the query of the tracert search
     * @return the result of the search
     */
    @GetMapping("/_search/tracerts")
    @Timed
    public List<TracertDTO> searchTracerts(@RequestParam String query) {
        log.debug("REST request to search Tracerts for query {}", query);
        return tracertService.search(query);
    }

}
