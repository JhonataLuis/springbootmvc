package net.curso.springbootmvc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import net.curso.springbootmvc.model.Telefone;

@Repository
@Transactional
public interface TelefoneRepository  extends JpaRepository<Telefone, Long>{

	
	@Query("select t from Telefone t where t.pessoa.id = ?1")
	List<Telefone> getTelefones(Long pessoaid);
}
