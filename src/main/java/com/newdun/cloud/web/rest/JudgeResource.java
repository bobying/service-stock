package com.newdun.cloud.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.newdun.cloud.service.JudgeService;
import com.newdun.cloud.web.rest.util.HeaderUtil;
import com.newdun.cloud.web.rest.util.PaginationUtil;
import com.newdun.cloud.service.dto.JudgeDTO;
import com.newdun.cloud.service.dto.JudgeCriteria;
import com.newdun.cloud.service.JudgeQueryService;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Judge.
 */
@RestController
@RequestMapping("/api")
public class JudgeResource {

    private final Logger log = LoggerFactory.getLogger(JudgeResource.class);

    private static final String ENTITY_NAME = "judge";

    private final JudgeService judgeService;
    private final JudgeQueryService judgeQueryService;

    public JudgeResource(JudgeService judgeService, JudgeQueryService judgeQueryService) {
        this.judgeService = judgeService;
        this.judgeQueryService = judgeQueryService;
    }

    /**
     * POST  /judges : Create a new judge.
     *
     * @param judgeDTO the judgeDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new judgeDTO, or with status 400 (Bad Request) if the judge has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/judges")
    @Timed
    public ResponseEntity<JudgeDTO> createJudge(@RequestBody JudgeDTO judgeDTO) throws URISyntaxException {
        log.debug("REST request to save Judge : {}", judgeDTO);
        if (judgeDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new judge cannot already have an ID")).body(null);
        }
        JudgeDTO result = judgeService.save(judgeDTO);
        return ResponseEntity.created(new URI("/api/judges/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /judges : Updates an existing judge.
     *
     * @param judgeDTO the judgeDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated judgeDTO,
     * or with status 400 (Bad Request) if the judgeDTO is not valid,
     * or with status 500 (Internal Server Error) if the judgeDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/judges")
    @Timed
    public ResponseEntity<JudgeDTO> updateJudge(@RequestBody JudgeDTO judgeDTO) throws URISyntaxException {
        log.debug("REST request to update Judge : {}", judgeDTO);
        if (judgeDTO.getId() == null) {
            return createJudge(judgeDTO);
        }
        JudgeDTO result = judgeService.save(judgeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, judgeDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /judges : get all the judges.
     *
     * @param pageable the pagination information
     * @param criteria the criterias which the requested entities should match
     * @return the ResponseEntity with status 200 (OK) and the list of judges in body
     */
    @GetMapping("/judges")
    @Timed
    public ResponseEntity<List<JudgeDTO>> getAllJudges(JudgeCriteria criteria,@ApiParam Pageable pageable) {
        log.debug("REST request to get Judges by criteria: {}", criteria);
        Page<JudgeDTO> page = judgeQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/judges");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /judges/:id : get the "id" judge.
     *
     * @param id the id of the judgeDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the judgeDTO, or with status 404 (Not Found)
     */
    @GetMapping("/judges/{id}")
    @Timed
    public ResponseEntity<JudgeDTO> getJudge(@PathVariable Long id) {
        log.debug("REST request to get Judge : {}", id);
        JudgeDTO judgeDTO = judgeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(judgeDTO));
    }

    /**
     * DELETE  /judges/:id : delete the "id" judge.
     *
     * @param id the id of the judgeDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/judges/{id}")
    @Timed
    public ResponseEntity<Void> deleteJudge(@PathVariable Long id) {
        log.debug("REST request to delete Judge : {}", id);
        judgeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/judges?query=:query : search for the judge corresponding
     * to the query.
     *
     * @param query the query of the judge search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/judges")
    @Timed
    public ResponseEntity<List<JudgeDTO>> searchJudges(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Judges for query {}", query);
        Page<JudgeDTO> page = judgeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/judges");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
