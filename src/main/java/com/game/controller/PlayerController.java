package com.game.controller;

/*
public class PlayerController {
}
*/

import java.util.ArrayList;
//import java.util.Date;
import java.util.List;
import java.util.Optional;

//import com.game.entity.Profession;
//import com.game.entity.Race;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/")
public class PlayerController {

    @Autowired
    PlayerRepository playerRepository;

    @GetMapping("/rest/players")
    public ResponseEntity<List<Player>> getAllPlayers() {
        try {
            List<Player> players = new ArrayList<>();

            playerRepository.findAll().forEach(players::add);

            if (players.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(players, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/players/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable("id") long id) {
        Optional<Player> playerData = playerRepository.findById(id);

        if (playerData.isPresent()) {
            return new ResponseEntity<>(playerData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        try {
            Player _player = playerRepository
//                    .save(new Player(player.getTitle(), player.getName(), false));
                    .save(new Player(player.getName(), player.getTitle(), player.getRace(),
                            player.getProfession(), player.getBirthday(), player.isBanned(), player.getExperience()));
            return new ResponseEntity<>(_player, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") long id, @RequestBody Player player) {
        Optional<Player> playerData = playerRepository.findById(id);

        if (playerData.isPresent()) {
            Player _player = playerData.get();
            _player.setTitle(player.getTitle());
            _player.setName(player.getName());
            _player.setBanned(player.isBanned());
            return new ResponseEntity<>(playerRepository.save(_player), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/players/{id}")
    public ResponseEntity<HttpStatus> deletePlayer(@PathVariable("id") long id) {
        try {
            playerRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/players")
    public ResponseEntity<HttpStatus> deleteAllPlayers() {
        try {
            playerRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/rest/players/count")
    public ResponseEntity<Integer> getSpecifiedPlayersCount(@RequestParam(required = false) String title) {
        try {
            Integer res = (int)playerRepository.count();

            if (res.equals(0)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
