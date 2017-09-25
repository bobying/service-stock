package com.newdun.cloud.repository.search;

import com.newdun.cloud.domain.Judge;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Judge entity.
 */
public interface JudgeSearchRepository extends ElasticsearchRepository<Judge, Long> {
}
