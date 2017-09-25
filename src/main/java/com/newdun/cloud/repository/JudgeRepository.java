package com.newdun.cloud.repository;

import com.newdun.cloud.domain.Judge;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Judge entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JudgeRepository extends JpaRepository<Judge, Long>, JpaSpecificationExecutor<Judge> {

}
