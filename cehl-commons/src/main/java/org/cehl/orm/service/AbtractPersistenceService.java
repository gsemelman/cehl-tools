package org.cehl.orm.service;

import java.io.Serializable;
import java.util.List;

import org.cehl.orm.dao.IGenericDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public abstract class AbtractPersistenceService<E, PK extends Serializable> implements PersistenceService<E, PK> {

	@Override
	@Transactional
	public void update(E entity) {
		getDao().update(entity);
	}

	@Override
	@Transactional
	public PK save(E entity) {
		return getDao().save(entity);
	}

	@Override
	@Transactional
	public void delete(E entity) {
		getDao().delete(entity);
	}

	@Override
	@Transactional(readOnly = true)
	public E find(PK id) {
		return getDao().find(id);
	}	

	@Override
	@Transactional(readOnly = true)
	public List<E> find() {
		return getDao().find();
	}

	@Override
	@Transactional
	public void saveOrUpdate(E entity) {
		getDao().saveOrUpdate(entity);
	}

	protected abstract IGenericDao<E, PK> getDao();

}
