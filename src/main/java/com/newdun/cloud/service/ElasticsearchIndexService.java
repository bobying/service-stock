package com.newdun.cloud.service;

import com.codahale.metrics.annotation.Timed;
import com.newdun.cloud.domain.*;
import com.newdun.cloud.repository.*;
import com.newdun.cloud.repository.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

@Service
public class ElasticsearchIndexService {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexService.class);

    private final InfoRepository infoRepository;

    private final InfoSearchRepository infoSearchRepository;

    private final JudgeRepository judgeRepository;

    private final JudgeSearchRepository judgeSearchRepository;

    private final SourceRepository sourceRepository;

    private final SourceSearchRepository sourceSearchRepository;

    private final TracertRepository tracertRepository;

    private final TracertSearchRepository tracertSearchRepository;

    private final ElasticsearchTemplate elasticsearchTemplate;

    public ElasticsearchIndexService(
        InfoRepository infoRepository,
        InfoSearchRepository infoSearchRepository,
        JudgeRepository judgeRepository,
        JudgeSearchRepository judgeSearchRepository,
        SourceRepository sourceRepository,
        SourceSearchRepository sourceSearchRepository,
        TracertRepository tracertRepository,
        TracertSearchRepository tracertSearchRepository,
        ElasticsearchTemplate elasticsearchTemplate) {
        this.infoRepository = infoRepository;
        this.infoSearchRepository = infoSearchRepository;
        this.judgeRepository = judgeRepository;
        this.judgeSearchRepository = judgeSearchRepository;
        this.sourceRepository = sourceRepository;
        this.sourceSearchRepository = sourceSearchRepository;
        this.tracertRepository = tracertRepository;
        this.tracertSearchRepository = tracertSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Async
    @Timed
    public void reindexAll() {
        reindexForClass(Info.class, infoRepository, infoSearchRepository);
        reindexForClass(Judge.class, judgeRepository, judgeSearchRepository);
        reindexForClass(Source.class, sourceRepository, sourceSearchRepository);
        reindexForClass(Tracert.class, tracertRepository, tracertSearchRepository);

        log.info("Elasticsearch: Successfully performed reindexing");
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    private <T, ID extends Serializable> void reindexForClass(Class<T> entityClass, JpaRepository<T, ID> jpaRepository,
                                                              ElasticsearchRepository<T, ID> elasticsearchRepository) {
        elasticsearchTemplate.deleteIndex(entityClass);
        try {
            elasticsearchTemplate.createIndex(entityClass);
        } catch (Exception e) {
            // Do nothing. Index was already concurrently recreated by some other service.
        }
        elasticsearchTemplate.putMapping(entityClass);
        if (jpaRepository.count() > 0) {
            try {
                Method m = jpaRepository.getClass().getMethod("findAllWithEagerRelationships");
                elasticsearchRepository.saveAll((List<T>) m.invoke(jpaRepository));
            } catch (Exception e) {
                elasticsearchRepository.saveAll(jpaRepository.findAll());
            }
        }
        log.info("Elasticsearch: Indexed all rows for " + entityClass.getSimpleName());
    }
}
