package com.game.controller;

/*
public class PlayerController {
}
*/

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//import com.game.entity.Profession;
//import com.game.entity.Race;
import com.game.entity.Profession;
import com.game.entity.Race;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

//@CrossOrigin(origins = "http://localhost:8080") // атавизм откуда-то
@RestController // Говорим что это REST контроллер
@RequestMapping("/") // зрим в корень
public class PlayerController {

    @Autowired
    PlayerRepository playerRepository;

    // ОСНОВНАЯ ОБРАБОТКА GET
    @GetMapping("/rest/players")
    public ResponseEntity<List<Player>> getAllPlayers(@RequestParam Map<String, String> allParams) {
        // allParams - if view sends us parameters like sort, order, etc.
        try {
            // DECLARATIONS
            List<Player> players = new ArrayList<>(); // 4 all players
            Stream<Player> playerStream;
            List<Player> outputList = new ArrayList<>(); // for result that we return
            String order;
            String name;
            String title;
            Race race = null;
            Profession profession = null;
            Long after;
            Long before;
            Boolean banned = false;
            Integer minExperience;
            Integer maxExperience;
            Integer minLevel;
            Integer maxLevel;
            Integer pageNumber = 0;
            Integer pageSize = 0;

            System.out.println("Allparams: " + allParams.entrySet()); // some debug
//            System.out.println("after: " + new Date(1041372000000L) + " , before: " + new Date(1136066400000L)); // some debug

            players.addAll(playerRepository.findAll()); // collect all records


            // FILTERING
            // according to optional received params from allParams

            if (allParams.get("pageNumber") != null) pageNumber = Integer.parseInt(allParams.get("pageNumber"));
            else pageNumber = 0;
            if (allParams.get("pageSize") != null) pageSize = Integer.parseInt(allParams.get("pageSize"));
            else pageSize = 3;

            playerStream = players.stream();
            if ((name = allParams.get("name")) != null) {
                playerStream = playerStream.filter(p -> p.getName().contains(name));
            }
            if ((title = allParams.get("title")) != null) {
                playerStream = playerStream.filter(p -> p.getTitle().contains(title));
            }
            if (allParams.get("race") != null) {
                Race finalRace = Race.valueOf(allParams.get("race"));
                playerStream = playerStream.filter(p -> p.getRace().equals(finalRace));
            }
            if (allParams.get("profession") != null) {
                Profession finalProfession = Profession.valueOf(allParams.get("profession"));
                playerStream = playerStream.filter(p -> p.getProfession().equals(finalProfession));
            }
            if (allParams.get("after") != null) {
                String tmpString = allParams.get("after");
                Date tmpDate = new Date(Long.parseLong(tmpString) + 1000);
                System.out.println("after: " + tmpDate);
                playerStream = playerStream.filter(p -> p.getBirthday().after(tmpDate));
            }
            if (allParams.get("before") != null) {
                String tmpString = allParams.get("before");
                Date tmpDate = new Date(Long.parseLong(tmpString) + 1000);
                System.out.println("before: " + tmpDate);
                playerStream = playerStream.filter(p -> p.getBirthday().before(tmpDate));
            }
            if (allParams.get("banned") != null) playerStream = playerStream.filter(p -> p.isBanned() == banned);
            if (allParams.get("minExperience") != null) playerStream = playerStream
                    .filter(p -> p.getExperience() >= Integer.parseInt(allParams.get("minExperience")));
            if (allParams.get("maxExperience") != null) playerStream = playerStream
                    .filter(p -> p.getExperience() <= Integer.parseInt(allParams.get("maxExperience")));

            if (allParams.get("minLevel") != null) {
                playerStream = playerStream.filter(p -> p.getLevel() > Integer.parseInt(allParams.get("minLevel")));
            }
            if (allParams.get("maxLevel") != null) {
                playerStream = playerStream.filter(p -> p.getLevel() < Integer.parseInt(allParams.get("maxLevel")));
            }


            // SORTING RESULT
            order = allParams.get("order"); // get sorting order
            if (order != null) // sort according to order received
            {
                switch (order) {
                    case "ID":
                        Collections.sort(players, Comparator.comparing(Player::getId));
                        break;
                    case "NAME":
                        Collections.sort(players, Comparator.comparing(Player::getName));
                        break;
                    case "EXPERIENCE":
                        Collections.sort(players, Comparator.comparing(Player::getExperience));
                        break;
                    case "BIRTHDAY":
                        Collections.sort(players, Comparator.comparing(Player::getBirthday));
                        break;
                }
            }

            // PAGING
            // split results on pages
            playerStream = playerStream.skip((long) (pageNumber) * pageSize);
            playerStream = playerStream.limit(pageSize);

            players = playerStream.collect(Collectors.toList());

            if (players.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(players, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // ОБРАБОТКА GET id
    @GetMapping("/rest/players/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable("id") long id) {
        Optional<Player> playerData = playerRepository.findById(id);

        if (id <1)  return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (playerData.isPresent()) {
            return new ResponseEntity<>(playerData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // ОБРАБОТКА POST
    @PostMapping("/rest/players")
    public ResponseEntity<Player> createPlayer(@RequestBody Player player) {
        Date dateLow = new Date(100, 01, 01);
        Date dateHi = new Date(1100, 01, 01);


        try {
            if (player.getId() == null && player.getName() == null && player.getTitle() == null
                    && player.getRace() == null && player.getProfession() == null && player.getBirthday() == null
                    && player.isBanned() == false && player.getExperience() == null && player.getLevel() == null
                    && player.getUntilNextLevel() == null) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            if (player == null || player.getBirthday().before(dateLow) || dateHi.before(player.getBirthday())
                    || player.getTitle().length() > 12)
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            if ((player.getExperience() < 0) || (player.getExperience() > 10000000))
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            if (player.getName().equals("")) return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            Long res = playerRepository.count();
//            Long id = player.getId();
//            player.setId(res + 1);
            Player _player = playerRepository
//                    .save(new Player(player.getTitle(), player.getName(), false));
                    .save(new Player(res + 1, player.getName(), player.getTitle(), player.getRace(),
                            player.getProfession(), player.getBirthday(), player.isBanned(), player.getExperience()));
            return new ResponseEntity<>(_player, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // УДАЛЕНИЕ ПО id
    @PutMapping("/rest/players/{id}")
    public ResponseEntity<Player> updatePlayer(@PathVariable("id") long id, @RequestBody Player player) {
        Optional<Player> playerData = playerRepository.findById(id);

        if (id <1)  return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (playerData.isPresent()) {
            Player _player = playerData.get();
            _player.setTitle(player.getTitle());
            _player.setName(player.getName());
            _player.setBanned(player.isBanned());
            return new ResponseEntity<>(playerRepository.save(_player), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/rest/players/{id}")
    public ResponseEntity<HttpStatus> deletePlayer(@PathVariable("id") long id) {
        try {
            if (id < 1 )return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            if (!playerRepository.existsById(id))return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            playerRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

/*    @DeleteMapping("/players")
    public ResponseEntity<HttpStatus> deleteAllPlayers() {
        try {
            playerRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }*/

    // ПОДСЧЁТ КОЛИЧЕСТВА ЗАПИСЕЙ
    @GetMapping("/rest/players/count")
    public ResponseEntity<Integer> getSpecifiedPlayersCount(@RequestParam Map<String, String> allParams) {
        try {
            Integer res;


            List<Player> players = new ArrayList<>(); // 4 all players
            Stream<Player> playerStream;
            List<Player> outputList = new ArrayList<>(); // for result that we return
            String name;
            String title;
            Boolean banned = false;

            System.out.println("Allparams from count: " + allParams.entrySet()); // some debug

            players.addAll(playerRepository.findAll()); // collect all records

            // FILTERING
            // according to optional received params from allParams

            playerStream = players.stream();
            if ((name = allParams.get("name")) != null) {
                playerStream = playerStream.filter(p -> p.getName().contains(name));
            }
            if ((title = allParams.get("title")) != null) {
                playerStream = playerStream.filter(p -> p.getTitle().contains(title));
            }
            if (allParams.get("race") != null) {
                Race finalRace = Race.valueOf(allParams.get("race"));
                playerStream = playerStream.filter(p -> p.getRace().equals(finalRace));
            }
            if (allParams.get("profession") != null) {
                Profession finalProfession = Profession.valueOf(allParams.get("profession"));
                playerStream = playerStream.filter(p -> p.getProfession().equals(finalProfession));
            }
            if (allParams.get("after") != null) {
                String tmpString = allParams.get("after");
                Date tmpDate = new Date(Long.parseLong(tmpString) + 1000);
                System.out.println("after: " + tmpDate);
                playerStream = playerStream.filter(p -> p.getBirthday().after(tmpDate));
            }
            if (allParams.get("before") != null) {
                String tmpString = allParams.get("before");
                Date tmpDate = new Date(Long.parseLong(tmpString) + 1000);
                System.out.println("before: " + tmpDate);
                playerStream = playerStream.filter(p -> p.getBirthday().before(tmpDate));
            }
            if ( allParams.get("banned") != null) playerStream = playerStream.filter(p -> p.isBanned() == Boolean.parseBoolean(allParams.get("banned")));
            if (allParams.get("minExperience") != null) playerStream = playerStream
                    .filter(p -> p.getExperience() >= Integer.parseInt(allParams.get("minExperience")));
            if (allParams.get("maxExperience") != null) playerStream = playerStream
                    .filter(p -> p.getExperience() <= Integer.parseInt(allParams.get("maxExperience")));

            if (allParams.get("minLevel") != null) {
                playerStream = playerStream.filter(p -> p.getLevel() > Integer.parseInt(allParams.get("minLevel")));
            }
            if (allParams.get("maxLevel") != null) {
                playerStream = playerStream.filter(p -> p.getLevel() < Integer.parseInt(allParams.get("maxLevel")));
            }

            players = playerStream.collect(Collectors.toList());


            res = (int) players.size();


            if (res.equals(0)) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(res, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
