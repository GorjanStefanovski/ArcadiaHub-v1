const canvas = document.querySelector('canvas')
const c = canvas.getContext('2d')
const gravity = 0.7

// 1. Детектирање на играта
const selectedGame = document.body.getAttribute('data-game-type') || 'default';

// 2. Дефинирање на Глобалната Боја (Overlay Color)
let globalOverlayColor = null;

if (selectedGame.includes('boxing')) {
    // Црвен филтер за Бокс (0.3 е транспарентност, колку поголем број - толку поцрвено)
    globalOverlayColor = 'rgba(255, 0, 0, 0.3)';
} else if (selectedGame.includes('karate')) {
    // Жолт/Портокалов филтер за Карате
    globalOverlayColor = 'rgba(255, 200, 0, 0.3)';
}

// Патеките се секогаш default (бидејќи ја користиме истата слика)
const backgroundPath = `/images/default-game`;
const charsPath = `/images/default-game`;

let stompClient = null;
let localFighter = null;
let opponentFighter = null;
let player = null;
let enemy = null;
let gameStarted = false;

// (Timer и determineWinner се во utils.js)

const groundLevel = 330;
canvas.width = 1024
canvas.height = 576

c.fillRect(0, 0, canvas.width, canvas.height)

const background = new Sprite({
    position: { x: 0, y: 0 },
    imageSrc: `${backgroundPath}/background.png`
})

const shop = new Sprite({
    position: { x: 600, y: 128 },
    imageSrc: `${backgroundPath}/shop.png`,
    scale: 2.75,
    framesMax: 6
})

// Конфигурации за MACK и KENJI
const mackConfig = {
    imageSrc: `${charsPath}/samuraiMack/Idle.png`,
    framesMax: 8, scale: 2.5, offset: { x: 215, y: 157 },
    framesHold: 8,
    sprites: {
        idle: { imageSrc: `${charsPath}/samuraiMack/Idle.png`, framesMax: 8 },
        run: { imageSrc: `${charsPath}/samuraiMack/Run.png`, framesMax: 8 },
        jump: { imageSrc: `${charsPath}/samuraiMack/Jump.png`, framesMax: 2 },
        fall: { imageSrc: `${charsPath}/samuraiMack/Fall.png`, framesMax: 2 },
        attack1: { imageSrc: `${charsPath}/samuraiMack/Attack1.png`, framesMax: 6 },
        takeHit: { imageSrc: `${charsPath}/samuraiMack/Take Hit - white silhouette.png`, framesMax: 4 },
        death: { imageSrc: `${charsPath}/samuraiMack/Death.png`, framesMax: 6 }
    },
    attackBox: { offset: { x: 100, y: 50 }, width: 160, height: 50 }
};

const kenjiConfig = {
    imageSrc: `${charsPath}/kenji/Idle.png`,
    framesMax: 4, scale: 2.5, offset: { x: 215, y: 167 },
    framesHold: 8,
    sprites: {
        idle: { imageSrc: `${charsPath}/kenji/Idle.png`, framesMax: 4 },
        run: { imageSrc: `${charsPath}/kenji/Run.png`, framesMax: 8 },
        jump: { imageSrc: `${charsPath}/kenji/Jump.png`, framesMax: 2 },
        fall: { imageSrc: `${charsPath}/kenji/Fall.png`, framesMax: 2 },
        attack1: { imageSrc: `${charsPath}/kenji/Attack1.png`, framesMax: 4 },
        takeHit: { imageSrc: `${charsPath}/kenji/Take hit.png`, framesMax: 3 },
        death: { imageSrc: `${charsPath}/kenji/Death.png`, framesMax: 7 }
    },
    attackBox: { offset: { x: -170, y: 50 }, width: 170, height: 50 }
};

// ID Maps
const characterConfigs = {
    1: mackConfig,
    2: kenjiConfig,
    3: mackConfig,
    4: kenjiConfig,
    5: mackConfig,
    6: kenjiConfig
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

window.finalizeMatch = function(winnerDbId) {
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
                console.log("Match Over. User should click Back to Home.");
            });

            stompClient.subscribe(`/topic/match/${matchId}/state`, message => {
                const state = JSON.parse(message.body);

                if (!player || !enemy) {
                    const p1Conf = characterConfigs[state.player1.classId];
                    const p2Conf = characterConfigs[state.player2.classId];

                    if (!p1Conf || !p2Conf) {
                        console.error("Missing config for IDs:", state.player1.classId, state.player2.classId);
                        return;
                    }

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

                if (!gameStarted && timer > 0) return;

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
            if(beep) beep.play().catch(e => console.log("Audio play blocked"));
            if(window.gsap) gsap.fromTo('#countdownText', { scale: 5, opacity: 0 }, { scale: 1, opacity: 1, duration: 0.5, ease: "back.out(1.7)" });
            count--;
        } else if (count === 0) {
            countdownEl.innerHTML = 'FIGHT!';
            countdownEl.style.color = '#ff0000';
            if(fightSound) fightSound.play().catch(e => console.log("Audio play blocked"));
            if(window.gsap) gsap.fromTo('#countdownText', { scale: 0, opacity: 0 }, { scale: 1.5, opacity: 1, duration: 0.4, ease: "expo.out" });
            count--;
        } else {
            clearInterval(countInterval);
            if(overlayEl) overlayEl.style.display = 'none';
            gameStarted = true;
            decreaseTimer(); // Повик до utils.js
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

// 3. ФУНКЦИЈАТА ЗА ЦРТАЊЕ
function animate() {
    window.requestAnimationFrame(animate);
    c.fillStyle = 'black';
    c.fillRect(0, 0, canvas.width, canvas.height);

    // 1. Цртај ги елементите нормално
    background.update();
    shop.update();

    if (player && enemy) {
        // Полу-проѕирен контраст зад играчите за да се гледаат (опционално, можеш да го тргнеш)
        // c.fillStyle = 'rgba(255, 255, 255, 0.15)';
        // c.fillRect(0, 0, canvas.width, canvas.height);

        player.update();
        enemy.update();

        const p1Width = (player.health / 100) * 100;
        const p2Width = (enemy.health / 100) * 100;
        document.querySelector('#playerHealth').style.width = Math.max(0, p1Width) + '%';
        document.querySelector('#enemyHealth').style.width = Math.max(0, p2Width) + '%';
    }

    // 2. ВАЖНО: АКО ИМАМЕ ГЛОБАЛНА БОЈА, ЦРТАМЕ ПРЕКУ СЕ!
    if (globalOverlayColor) {
        c.fillStyle = globalOverlayColor;
        c.fillRect(0, 0, canvas.width, canvas.height);
    }
}

connectWebSocket();