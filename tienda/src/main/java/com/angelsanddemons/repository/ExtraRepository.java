package com.angelsanddemons.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.angelsanddemons.model.Extra;

public interface ExtraRepository extends JpaRepository<Extra, Long> {
    List<Extra> findByActivoTrue();
}
