package com.example.ArcadiaHub_v1.GameState;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class GameLoop {

    private final MatchRegistry matchRegistry;
    private final SimpMessagingTemplate messagingTemplate;

    private final double gravity = 0.7;

    public GameLoop(MatchRegistry matchRegistry, SimpMessagingTemplate messagingTemplate) {
        this.matchRegistry = matchRegistry;
        this.messagingTemplate = messagingTemplate;
    }

    @Scheduled(fixedRate = 16)
    public void tick() {
        for (MatchState match : matchRegistry.getActiveMatches().values()) {

            if (match.getPlayer1().getHealth() <= 0 || match.getPlayer2().getHealth() <= 0) {
                messagingTemplate.convertAndSend("/topic/match/" + match.getId() + "/state", match);
                continue;
            }


            updatePlayer(match.getPlayer1());
            updatePlayer(match.getPlayer2());
            checkCollisions(match);

            // Broadcast full match state to clients
            messagingTemplate.convertAndSend(
                    "/topic/match/" + match.getId() + "/state",
                    match
            );
        }
    }

    private void updatePlayer(PlayerState player) {
        player.setX(player.getX() + player.getVx());
        player.setY(player.getY() + player.getVy());
        player.setVy(player.getVy() + gravity);

        if (player.getY() > 0) {
            player.setY(0);
            player.setVy(0);
        }
    }

    /*
    private void checkCollisions(MatchState match) {
        PlayerState p1 = match.getPlayer1();
        PlayerState p2 = match.getPlayer2();

        if (p1.isAttacking() && calculateHit(p1, p2)) {
            p2.setHealth(p2.getHealth() - (int)p1.getDamage());
            p1.setAttacking(false);
        }

        if (p2.isAttacking() && calculateHit(p2, p1)) {
            p1.setHealth(p1.getHealth() - (int)p2.getDamage());
            p2.setAttacking(false);
        }
    }
     */

    private void checkCollisions(MatchState match) {
        PlayerState p1 = match.getPlayer1();
        PlayerState p2 = match.getPlayer2();
        if (p1.isAttacking() && calculateHit(p1, p2)) {
            p2.setHealth(p2.getHealth() - (int)p1.getDamage());
            p2.setHit(true);
            p1.setAttacking(false);

            new Thread(() -> {
                try { Thread.sleep(150); } catch (InterruptedException e) {}
                p2.setHit(false);
            }).start();
        }

        if (p2.isAttacking() && calculateHit(p2, p1)) {
            p1.setHealth(p1.getHealth() - (int)p2.getDamage());
            p1.setHit(true);
            p2.setAttacking(false);

            new Thread(() -> {
                try { Thread.sleep(150); } catch (InterruptedException e) {}
                p1.setHit(false);
            }).start();
        }
    }

    private boolean calculateHit(PlayerState attacker, PlayerState target) {
        double attackerAttackX = attacker.getX() + attacker.getAttackOffsetX();
        double attackerAttackY = attacker.getY();

        return attackerAttackX < target.getX() + 50 &&
                attackerAttackX + attacker.getAttackWidth() > target.getX() &&
                attackerAttackY < target.getY() + 150 &&
                attackerAttackY + attacker.getAttackHeight() > target.getY();
    }
}

