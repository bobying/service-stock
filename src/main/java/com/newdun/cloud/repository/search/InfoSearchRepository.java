package com.newdun.cloud.repository.search;

import com.newdun.cloud.domain.Info;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Info entity.
 */
public interface InfoSearchRepository extends ElasticsearchRepository<Info, Long> {
}
