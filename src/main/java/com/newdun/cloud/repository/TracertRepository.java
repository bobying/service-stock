package com.newdun.cloud.repository;

import com.newdun.cloud.domain.Tracert;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Tracert entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TracertRepository extends JpaRepository<Tracert, Long>, JpaSpecificationExecutor<Tracert> {

}
