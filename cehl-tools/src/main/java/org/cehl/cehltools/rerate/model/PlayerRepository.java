package org.cehl.cehltools.rerate.model;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Long> {

//	/**
//	 * Returns all accounts belonging to the given {@link Customer}.
//	 *
//	 * @param customer
//	 * @return
//	 */
//	List<Account> findByCustomer(Customer customer);
	public List<Player> findAll(Sort sort);
	
	public Player findByName(String name);
	
	public Player findByNameAndCountry(String name, String country);
	
	public List<Player> findPlayersByName(String name);
}