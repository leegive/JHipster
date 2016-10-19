package com.renmaituan.shop.repository;

import com.renmaituan.shop.domain.Demo;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Demo entity.
 */
@SuppressWarnings("unused")
public interface DemoRepository extends JpaRepository<Demo,Long> {

}
