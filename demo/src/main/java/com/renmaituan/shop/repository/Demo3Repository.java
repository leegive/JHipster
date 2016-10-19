package com.renmaituan.shop.repository;

import com.renmaituan.shop.domain.Demo3;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Demo3 entity.
 */
@SuppressWarnings("unused")
public interface Demo3Repository extends JpaRepository<Demo3,Long> {

}
