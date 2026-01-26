package com.example.ArcadiaHub_v1.Friends;

import com.example.ArcadiaHub_v1.Player.Player;
import com.example.ArcadiaHub_v1.Player.PlayerRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate; // НОВО
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FriendRequestService {
    private final FriendRequestRepository requestRepository;
    private final PlayerRepository playerRepository;
    private final SimpMessagingTemplate messagingTemplate; // НОВО

    public FriendRequestService(FriendRequestRepository requestRepository,
                                PlayerRepository playerRepository,
                                SimpMessagingTemplate messagingTemplate) {
        this.requestRepository = requestRepository;
        this.playerRepository = playerRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public void sendRequest(Player sender, String receiverUsername) {
        Player receiver = playerRepository.findByUsername(receiverUsername);

        if (receiver == null) throw new RuntimeException("User '" + receiverUsername + "' not found!");
        if (sender.equals(receiver)) throw new RuntimeException("You cannot add yourself!");
        if (sender.getFriends().contains(receiver)) throw new RuntimeException("Already friends!");
        if (requestRepository.findBySenderAndReceiver(sender, receiver).isPresent()) {
            throw new RuntimeException("Request is already pending!");
        }

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(RequestStatus.PENDING);
        requestRepository.save(request);
        messagingTemplate.convertAndSendToUser(
                receiver.getGoogleSub(),
                "/queue/friend-notifications",
                "New friend request from " + sender.getUsername()
        );
    }

    @Transactional
    public void processRequest(Long requestId, String action, Player currentUser) {
        FriendRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Player sender = request.getSender();
        Player receiver = request.getReceiver();

        if ("ACCEPT".equals(action)) {
            request.setStatus(RequestStatus.ACCEPTED);
            sender.getFriends().add(receiver);
            receiver.getFriends().add(sender);

            playerRepository.save(sender);
            playerRepository.save(receiver);
            requestRepository.delete(request);
            messagingTemplate.convertAndSendToUser(
                    sender.getGoogleSub(),
                    "/queue/friend-notifications",
                    currentUser.getUsername() + " accepted your alliance request!"
            );
        } else if ("REJECT".equals(action)) {
            requestRepository.delete(request);
        }
    }
}