package com.newdun.cloud.repository.search;

import com.newdun.cloud.domain.Tracert;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Tracert entity.
 */
public interface TracertSearchRepository extends ElasticsearchRepository<Tracert, Long> {
}
