package com.game.repository;
//import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.game.entity.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
//    List<Player> findByPublished(boolean published);
//    List<Player> findByTitleContaining(String title);
}

