const canvas = document.querySelector('canvas')
const c = canvas.getContext('2d')
const gravity = 0.7

const selectedGame = document.body.getAttribute('data-game-type') || 'default';
const assetPath = `/images/default-game`;

let stompClient = null;
let localFighter = null;
let opponentFighter = null;
let player = null;
let enemy = null;
let gameStarted = false;

const groundLevel = 330;
canvas.width = 1024
canvas.height = 576

c.fillRect(0, 0, canvas.width, canvas.height)

const background = new Sprite({
    position: { x: 0, y: 0 },
    imageSrc: `${assetPath}/background.png`
})

const shop = new Sprite({
    position: { x: 600, y: 128 },
    imageSrc: `${assetPath}/shop.png`,
    scale: 2.75,
    framesMax: 6
})

const characterConfigs = {
    1: {
        imageSrc: `${assetPath}/samuraiMack/Idle.png`,
        framesMax: 8, scale: 2.5, offset: { x: 215, y: 157 },
        framesHold: 8,
        sprites: {
            idle: { imageSrc: `${assetPath}/samuraiMack/Idle.png`, framesMax: 8 },
            run: { imageSrc: `${assetPath}/samuraiMack/Run.png`, framesMax: 8 },
            jump: { imageSrc: `${assetPath}/samuraiMack/Jump.png`, framesMax: 2 },
            fall: { imageSrc: `${assetPath}/samuraiMack/Fall.png`, framesMax: 2 },
            attack1: { imageSrc: `${assetPath}/samuraiMack/Attack1.png`, framesMax: 6 },
            takeHit: { imageSrc: `${assetPath}/samuraiMack/Take Hit - white silhouette.png`, framesMax: 4 },
            death: { imageSrc: `${assetPath}/samuraiMack/Death.png`, framesMax: 6 }
        },
        attackBox: { offset: { x: 100, y: 50 }, width: 160, height: 50 }
    },
    2: {
        imageSrc: `${assetPath}/kenji/Idle.png`,
        framesMax: 4, scale: 2.5, offset: { x: 215, y: 167 },
        framesHold: 8,
        sprites: {
            idle: { imageSrc: `${assetPath}/kenji/Idle.png`, framesMax: 4 },
            run: { imageSrc: `${assetPath}/kenji/Run.png`, framesMax: 8 },
            jump: { imageSrc: `${assetPath}/kenji/Jump.png`, framesMax: 2 },
            fall: { imageSrc: `${assetPath}/kenji/Fall.png`, framesMax: 2 },
            attack1: { imageSrc: `${assetPath}/kenji/Attack1.png`, framesMax: 4 },
            takeHit: { imageSrc: `${assetPath}/kenji/Take hit.png`, framesMax: 3 },
            death: { imageSrc: `${assetPath}/kenji/Death.png`, framesMax: 7 }
        },
        attackBox: { offset: { x: -170, y: 50 }, width: 170, height: 50 }
    }
}

const keys = {
    a: { pressed: false }, d: { pressed: false },
    ArrowRight: { pressed: false }, ArrowLeft: { pressed: false }
}

function sendMovement(key, state) {
    if (!stompClient || !stompClient.connected) return;
    stompClient.publish({
        destination: `/app/match/${matchId}/move`,
        body: JSON.stringify({ playerId: playerNumber, key: key, state: state })
    });
}

// ОВАА ФУНКЦИЈА ЈА ПОВИКУВА ТВОЈАТА JAVA handleMatchEnd ФУНКЦИЈА
function finalizeMatch(winnerDbId) {
    if (!stompClient || !stompClient.connected) return;
    stompClient.publish({
        destination: `/app/match/${matchId}/end`,
        body: JSON.stringify({ winnerId: winnerDbId })
    });
}

window.addEventListener('keydown', (event) => {
    if (!localFighter || localFighter.dead || !gameStarted) return;
    const map = playerNumber === 1
        ? { left: 'a', right: 'd', jump: 'w', attack: ' ' }
        : { left: 'ArrowLeft', right: 'ArrowRight', jump: 'ArrowUp', attack: 'ArrowDown' };

    if (Object.values(map).includes(event.key)) {
        if (event.key === map.left) { keys.a.pressed = true; sendMovement(event.key, 'DOWN'); }
        if (event.key === map.right) { keys.d.pressed = true; sendMovement(event.key, 'DOWN'); }
        if (event.key === map.jump) sendMovement(event.key, 'DOWN');
        if (event.key === map.attack) {
            localFighter.attack();
            sendMovement(event.key, 'DOWN');
        }
    }
});

window.addEventListener('keyup', (event) => {
    const keysArray = playerNumber === 1 ? ['a', 'd'] : ['ArrowLeft', 'ArrowRight'];
    if (keysArray.includes(event.key)) {
        sendMovement(event.key, "UP");
    }
});

function connectWebSocket() {
    stompClient = new StompJs.Client({
        webSocketFactory: () => new SockJS('/ws'),
        onConnect: () => {
            console.log('WS Connected');

            stompClient.subscribe(`/topic/match/${matchId}/redirect`, message => {
                console.log("Match data processed on server.");
            });

            stompClient.subscribe(`/topic/match/${matchId}/state`, message => {
                const state = JSON.parse(message.body);

                if (!player || !enemy) {
                    const p1Conf = characterConfigs[state.player1.classId];
                    const p2Conf = characterConfigs[state.player2.classId];

                    player = new Fighter({
                        position: { x: state.player1.x, y: state.player1.y + groundLevel },
                        velocity: { x: 0, y: 0 },
                        health: state.player1.health,
                        playerId: state.player1.playerId,
                        ...p1Conf,
                        framesHold: p1Conf.framesHold
                    });

                    enemy = new Fighter({
                        position: { x: state.player2.x, y: state.player2.y + groundLevel },
                        velocity: { x: 0, y: 0 },
                        health: state.player2.health,
                        playerId: state.player2.playerId,
                        ...p2Conf,
                        framesHold: p2Conf.framesHold
                    });

                    localFighter = (playerNumber === 1) ? player : enemy;
                    opponentFighter = (playerNumber === 1) ? enemy : player;

                    animate();
                    startMatchCountdown();
                    return;
                }

                if (!gameStarted) return;

                player.position.x = state.player1.x;
                player.position.y = state.player1.y + groundLevel;
                enemy.position.x = state.player2.x;
                enemy.position.y = state.player2.y + groundLevel;
                player.health = state.player1.health;
                enemy.health = state.player2.health;

                updateFighterAnimation(player, state.player1);
                updateFighterAnimation(enemy, state.player2);


                if (player.health <= 0 || enemy.health <= 0) {
                    if (gameStarted) {
                        gameStarted = false;

                        const winnerId = player.health <= 0 ? enemy.playerId : player.playerId;
                        if (playerNumber === 1) {
                            console.log("Player 1 sending final results to server...");
                            finalizeMatch(winnerId);
                        }
                        determineWinner({ player, enemy, timerId });
                        if (player.health <= 0) player.switchSprite('death');
                        else enemy.switchSprite('death');
                    }
                }
            });
        }
    });
    stompClient.activate();
}

function startMatchCountdown() {
    const countdownEl = document.querySelector('#countdownText');
    const overlayEl = document.querySelector('#countdown-overlay');
    const beep = document.querySelector('#beepSound');
    const fightSound = document.querySelector('#fightSound');

    let count = 5;

    const countInterval = setInterval(() => {
        if (count > 0) {
            countdownEl.innerHTML = count;
            beep.play().catch(e => console.log("Audio play blocked"));
            gsap.fromTo('#countdownText', { scale: 5, opacity: 0 }, { scale: 1, opacity: 1, duration: 0.5, ease: "back.out(1.7)" });
            count--;
        } else if (count === 0) {
            countdownEl.innerHTML = 'FIGHT!';
            countdownEl.style.color = '#ff0000';
            fightSound.play().catch(e => console.log("Audio play blocked"));
            gsap.fromTo('#countdownText', { scale: 0, opacity: 0 }, { scale: 1.5, opacity: 1, duration: 0.4, ease: "expo.out" });
            count--;
        } else {
            clearInterval(countInterval);
            overlayEl.style.display = 'none';
            gameStarted = true;
            decreaseTimer();
        }
    }, 1000);
}

function updateFighterAnimation(fighter, remoteState) {
    if (fighter.dead) return;
    if (remoteState.hit) { fighter.switchSprite('takeHit'); return; }
    if (remoteState.attacking) { fighter.attack(); return; }
    const isOnGround = remoteState.y >= 0;
    if (remoteState.vy < -1) fighter.switchSprite('jump');
    else if (remoteState.vy > 5 && !isOnGround) fighter.switchSprite('fall');
    else if (Math.abs(remoteState.vx) > 0.5) fighter.switchSprite('run');
    else fighter.switchSprite('idle');
}

function animate() {
    window.requestAnimationFrame(animate);
    c.fillStyle = 'black';
    c.fillRect(0, 0, canvas.width, canvas.height);
    background.update();
    shop.update();

    if (player && enemy) {
        player.update();
        enemy.update();
        const p1Width = (player.health / 100) * 100;
        const p2Width = (enemy.health / 100) * 100;
        document.querySelector('#playerHealth').style.width = Math.max(0, p1Width) + '%';
        document.querySelector('#enemyHealth').style.width = Math.max(0, p2Width) + '%';
    }
}

connectWebSocket();