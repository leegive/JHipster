package com.renmaituan.shop.repository;

import com.renmaituan.shop.domain.Demo2;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Demo2 entity.
 */
@SuppressWarnings("unused")
public interface Demo2Repository extends JpaRepository<Demo2,Long> {

}
