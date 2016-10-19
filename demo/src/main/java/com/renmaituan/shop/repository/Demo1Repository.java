package com.renmaituan.shop.repository;

import com.renmaituan.shop.domain.Demo1;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Demo1 entity.
 */
@SuppressWarnings("unused")
public interface Demo1Repository extends JpaRepository<Demo1,Long> {

}
