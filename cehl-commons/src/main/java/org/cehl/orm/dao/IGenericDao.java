package org.cehl.orm.dao;

import java.io.Serializable;
import java.util.List;


public interface IGenericDao<E, PK extends Serializable> {
	void update(E entity);

	E merge(E entity);

	PK save(E entity);

	void delete(E entity);

	E find(PK id);	
	
	List<E> find();

	void saveOrUpdate(E entity);

}