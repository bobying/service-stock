package com.newdun.cloud.repository.search;

import com.newdun.cloud.domain.Source;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Source entity.
 */
public interface SourceSearchRepository extends ElasticsearchRepository<Source, Long> {
}
