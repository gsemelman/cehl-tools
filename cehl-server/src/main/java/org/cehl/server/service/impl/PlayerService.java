package org.cehl.server.service.impl;

import org.cehl.model.cehl.player.Player;
import org.cehl.orm.dao.IGenericDao;
import org.cehl.orm.service.AbtractPersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("playerService")
public class PlayerService extends AbtractPersistenceService<Player, Long> {

	@Autowired
	private IGenericDao<Player, Long> playerDao;

	@Override
	protected IGenericDao<Player, Long> getDao() {
		return playerDao;
	}

}
