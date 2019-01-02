package org.cehl.orm.hibernate;

import java.io.Serializable;

import org.cehl.orm.dao.IGenericDao;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class GenericHibernateDao<E, PK extends Serializable> extends AbstractHibernateDaoImpl<E, PK> implements IGenericDao<E, PK> {

}