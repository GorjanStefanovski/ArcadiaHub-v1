package com.example.ArcadiaHub_v1.Friends;

import com.example.ArcadiaHub_v1.Player.Player;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Player sender;

    @ManyToOne
    private Player receiver;

    @Enumerated(EnumType.STRING)
    private RequestStatus status; // PENDING, ACCEPTED, REJECTED

    private LocalDateTime createdAt;

    public void setId(Long id) {
        this.id = id;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setSender(Player sender) {
        this.sender = sender;
    }

    public void setReceiver(Player receiver) {
        this.receiver = receiver;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public Player getReceiver() {
        return receiver;
    }

    public Player getSender() {
        return sender;
    }

}
