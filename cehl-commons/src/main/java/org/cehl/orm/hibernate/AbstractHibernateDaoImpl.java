package org.cehl.orm.hibernate;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.cehl.orm.dao.IGenericDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("dao")
@SuppressWarnings("unchecked")
@Transactional
public abstract class AbstractHibernateDaoImpl<E, PK extends Serializable> implements IGenericDao<E, PK> {

	private Class<E> entityClass;

	@Autowired
	private SessionFactory sessionFactory;

	protected Class<E> getEntityClass() {
		if (entityClass == null) {
			entityClass = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		}
		return entityClass;
	}

	@Override
	public void update(E entity) {
		getCurrentSession().update(entity);
	}

	@Override
	public E merge(E entity) {
		return (E) getCurrentSession().merge(entity);
	}

	@Override
	public PK save(E entity) {
		return (PK) getCurrentSession().save(entity);
	}

	@Override
	public void delete(E entity) {
		getCurrentSession().delete(entity);
	}

	@Override
	public E find(PK id) {
		return (E) getCurrentSession().get(getEntityClass(), id);
	}

	@Override
	public List<E> find() {
		return getCurrentSession().createQuery("from " + getEntityClass().getName()).list();
	}

	@Override
	public void saveOrUpdate(E entity) {
		getCurrentSession().saveOrUpdate(entity);
	}
	
	protected final Session getCurrentSession(){
	      return sessionFactory.getCurrentSession();
	}
}
