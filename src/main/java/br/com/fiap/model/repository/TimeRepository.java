package br.com.fiap.model.repository;

import br.com.fiap.model.entity.Time;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeRepository extends JpaRepository<Time, Long> { }
