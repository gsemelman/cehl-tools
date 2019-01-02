package org.cehl.dao.impl;

import java.util.List;

import org.cehl.model.cehl.player.Player;
import org.cehl.orm.hibernate.AbstractHibernateDaoImpl;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PlayerDao extends AbstractHibernateDaoImpl<Player, Long> {
	
	@Transactional
	public List<Player> getPlayerByTeamId(Long teamId){

		Session session = this.getCurrentSession();

		String hql = "from Player p where p.teamId = :teamId";

		List<Player> result = session.createQuery(hql)
				.setParameter("teamId", teamId).list();

		return result;

	}
}
