package net.curso.springbootmvc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.curso.springbootmvc.model.Cargo;

@Repository
@Transactional
public interface CargoRepository extends JpaRepository<Cargo, Long> {

	//@Query("select c from Cargo c where c.nome = 1?")
	//List<Cargo> findCargoByName(String nome);
}
