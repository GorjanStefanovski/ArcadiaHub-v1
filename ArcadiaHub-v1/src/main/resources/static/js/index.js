const canvas = document.querySelector('canvas')
const c = canvas.getContext('2d')
const gravity = 0.7

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
    imageSrc: '/images/background.png'
})

const shop = new Sprite({
    position: { x: 600, y: 128 },
    imageSrc: '/images/shop.png',
    scale: 2.75,
    framesMax: 6
})

const characterConfigs = {
    1: {
        imageSrc: '/images/samuraiMack/Idle.png',
        framesMax: 8, scale: 2.5, offset: { x: 215, y: 157 },
        sprites: {
            idle: { imageSrc: '/images/samuraiMack/Idle.png', framesMax: 8 },
            run: { imageSrc: '/images/samuraiMack/Run.png', framesMax: 8 },
            jump: { imageSrc: '/images/samuraiMack/Jump.png', framesMax: 2 },
            fall: { imageSrc: '/images/samuraiMack/Fall.png', framesMax: 2 },
            attack1: { imageSrc: '/images/samuraiMack/Attack1.png', framesMax: 6 },
            takeHit: { imageSrc: '/images/samuraiMack/Take Hit - white silhouette.png', framesMax: 4 },
            death: { imageSrc: '/images/samuraiMack/Death.png', framesMax: 6 }
        },
        attackBox: { offset: { x: 100, y: 50 }, width: 160, height: 50 }
    },
    2: {
        imageSrc: '/images/kenji/Idle.png',
        framesMax: 4, scale: 2.5, offset: { x: 215, y: 167 },
        sprites: {
            idle: { imageSrc: '/images/kenji/Idle.png', framesMax: 4 },
            run: { imageSrc: '/images/kenji/Run.png', framesMax: 8 },
            jump: { imageSrc: '/images/kenji/Jump.png', framesMax: 2 },
            fall: { imageSrc: '/images/kenji/Fall.png', framesMax: 2 },
            attack1: { imageSrc: '/images/kenji/Attack1.png', framesMax: 4 },
            takeHit: { imageSrc: '/images/kenji/Take hit.png', framesMax: 3 },
            death: { imageSrc: '/images/kenji/Death.png', framesMax: 7 }
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
                setTimeout(() => { window.location.href = message.body; }, 4000);
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
                        ...p1Conf
                    });

                    enemy = new Fighter({
                        position: { x: state.player2.x, y: state.player2.y + groundLevel },
                        velocity: { x: 0, y: 0 },
                        health: state.player2.health,
                        playerId: state.player2.playerId,
                        ...p2Conf
                    });

                    localFighter = (playerNumber === 1) ? player : enemy;
                    opponentFighter = (playerNumber === 1) ? enemy : player;
                    gameStarted = true;
                    animate();
                    decreaseTimer();
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
                    gameStarted = false;
                    determineWinner({ player, enemy, timerId });

                    if (player.health <= 0) player.switchSprite('death');
                    else enemy.switchSprite('death');
                }
            });
        }
    });
    stompClient.activate();
}

function updateFighterAnimation(fighter, remoteState) {
    if (fighter.dead) return;
    if (remoteState.attacking) fighter.attack();
    else if (remoteState.vy < 0) fighter.switchSprite('jump');
    else if (remoteState.vy > 0) fighter.switchSprite('fall');
    else if (remoteState.vx !== 0) fighter.switchSprite('run');
    else fighter.switchSprite('idle');
}

function animate() {
    window.requestAnimationFrame(animate);
    c.fillStyle = 'black';
    c.fillRect(0, 0, canvas.width, canvas.height);
    background.update();
    shop.update();
    player.update();
    enemy.update();

    const p1Width = (player.health / player.maxHealth) * 100;
    const p2Width = (enemy.health / enemy.maxHealth) * 100;
    document.querySelector('#playerHealth').style.width = Math.max(0, p1Width) + '%';
    document.querySelector('#enemyHealth').style.width = Math.max(0, p2Width) + '%';
}

connectWebSocket();