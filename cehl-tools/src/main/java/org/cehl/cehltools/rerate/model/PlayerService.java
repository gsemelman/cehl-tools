package org.cehl.cehltools.rerate.model;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class PlayerService {
	@Autowired
	PlayerRepository repository;
	
	@Transactional
	public List<Player> findAll(Sort sort){
		return repository.findAll(sort);
	}
	
	@Transactional
	public Player findByName(String name) {
		return repository.findByName(name);
	}
	
	@Transactional
	public Player findByNameAndCountry(String name, String country) {
		return repository.findByNameAndCountry(name, country);
	}
	
	@Transactional
	public List<Player> findPlayersByName(String name){
		return repository.findPlayersByName(name);
	}
}
