package com.example.ArcadiaHub_v1.Friends;

import com.example.ArcadiaHub_v1.Player.Player;
import com.example.ArcadiaHub_v1.Player.PlayerDto;
import com.example.ArcadiaHub_v1.Player.PlayerRepository;
import com.example.ArcadiaHub_v1.Player.PlayerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FriendsService {

    private final PlayerRepository repository;

    public FriendsService(PlayerRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void addFriend(Long currentPlayerId, Long friendId) {
        if (currentPlayerId.equals(friendId)) {
            throw new IllegalArgumentException("Cannot add yourself as friend");
        }

        Player current = repository.findById(currentPlayerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        Player friend = repository.findById(friendId)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        if (current.getFriends().contains(friend)) {
            throw new IllegalArgumentException("Already friends");
        }
        current.addFriend(friend);
        repository.save(current);
    }

    @Transactional
    public void removeFriend(Long currentPlayerId, Long friendId) {
        Player current = repository.findById(currentPlayerId).orElseThrow();
        Player friend = repository.findById(friendId).orElseThrow();

        current.removeFriend(friend);
        repository.save(current);
    }
}
