package org.cehl.server.controller;

import java.util.List;

import org.cehl.model.cehl.player.Player;
import org.cehl.orm.service.PersistenceService;
import org.cehl.server.service.impl.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/player")
public class PlayerController {

	@Autowired
	private PersistenceService<Player, Long> playerService;

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public List<Player> all() {
		return playerService.find();
	}

	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
	public Player get(@PathVariable Long id) {
		return playerService.find(id);
	}

}
