const canvas = document.querySelector('canvas')
const c = canvas.getContext('2d')

let stompClient = null;
let localFighter = null;
let opponentFighter = null;
let gameStarted = false;

canvas.width = 1024
canvas.height = 576

c.fillRect(0, 0, canvas.width, canvas.height)

const gravity = 0.7

const background = new Sprite({
    position: {
        x: 0,
        y: 0
    },
    imageSrc: '/images/background.png'
})

const shop = new Sprite({
    position: {
        x: 600,
        y: 128
    },
    imageSrc: '/images/shop.png',
    scale: 2.75,
    framesMax: 6
})

const player = new Fighter({
    position: { x: 0, y: 0 },
    velocity: { x: 0, y: 0 },
    offset: { x: 0, y: 0 },
    imageSrc: '/images/samuraiMack/Idle.png',
    framesMax: 8,
    scale: 2.5,
    offset: { x: 215, y: 157 },

    damage: 25,
    health: 100,
    runSpeed: 7,
    framesHold: 5,

    sprites: {
        idle: {
            imageSrc: '/images/samuraiMack/Idle.png',
            framesMax: 8
        },
        run: {
            imageSrc: '/images/samuraiMack/Run.png',
            framesMax: 8
        },
        jump: {
            imageSrc: '/images/samuraiMack/Jump.png',
            framesMax: 2
        },
        fall: {
            imageSrc: '/images/samuraiMack/Fall.png',
            framesMax: 2
        },
        attack1: {
            imageSrc: '/images/samuraiMack/Attack1.png',
            framesMax: 6
        },
        takeHit: {
            imageSrc: '/images/samuraiMack/Take Hit - white silhouette.png',
            framesMax: 4
        },
        death: {
            imageSrc: '/images/samuraiMack/Death.png',
            framesMax: 6
        }
    },
    attackBox: {
        offset: { x: 100, y: 50 },
        width: 160,
        height: 50
    }
})

const enemy = new Fighter({
    position: { x: 400, y: 100 },
    velocity: { x: 0, y: 0 },
    color: 'blue',
    offset: { x: -50, y: 0 },
    imageSrc: '/images/kenji/Idle.png',
    framesMax: 4,
    scale: 2.5,
    offset: { x: 215, y: 167 },

    damage: 10,
    health: 150,
    runSpeed: 4,
    framesHold: 10,

    sprites: {
        idle: {
            imageSrc: '/images/kenji/Idle.png',
            framesMax: 4
        },
        run: {
            imageSrc: '/images/kenji/Run.png',
            framesMax: 8
        },
        jump: {
            imageSrc: '/images/kenji/Jump.png',
            framesMax: 2
        },
        fall: {
            imageSrc: '/images/kenji/Fall.png',
            framesMax: 2
        },
        attack1: {
            imageSrc: '/images/kenji/Attack1.png',
            framesMax: 4
        },
        takeHit: {
            imageSrc: '/images/kenji/Take hit.png',
            framesMax: 3
        },
        death: {
            imageSrc: '/images/kenji/Death.png',
            framesMax: 7
        }
    },
    attackBox: {
        offset: { x: -170, y: 50 },
        width: 170,
        height: 50
    }
})

console.log(player)

const keys = {
    a: {
        pressed: false
    },
    d: {
        pressed: false
    },
    ArrowRight: {
        pressed: false
    },
    ArrowLeft: {
        pressed: false
    }
}

decreaseTimer()

/*
window.addEventListener('keydown', (event) => {
    if (!localFighter) return; // ignore until fighter is ready
    handleKeys(event, localFighter, keys, playerNumber === 1 ? {...} : {...});
    if (playerNumber === 1 && !player.dead) {
        handleKeys(event, player, keys, {
            left: 'a',
            right: 'd',
            jump: 'w',
            attack: ' '
        });
    } else if (playerNumber === 2 && !enemy.dead) {
        handleKeys(event, enemy, keys, {
            left: 'ArrowLeft',
            right: 'ArrowRight',
            jump: 'ArrowUp',
            attack: 'ArrowDown'
        });
    }
});
 */

window.addEventListener('keydown', (event) => {
    if (!localFighter || localFighter.dead) return;

    const map = playerNumber === 1
        ? { left: 'a', right: 'd', jump: 'w', attack: ' ' }
        : { left: 'ArrowLeft', right: 'ArrowRight', jump: 'ArrowUp', attack: 'ArrowDown' };

    handleKeys(event, localFighter, keys, map);
});


/*
window.addEventListener('keyup', (event) => {
    if (playerNumber === 1) {
        handleKeyUp(event, keys, ['a', 'd']);
    } else if (playerNumber === 2) {
        handleKeyUp(event, keys, ['ArrowLeft', 'ArrowRight']);
    }
});
 */


window.addEventListener('keyup', (event) => {
    if (!localFighter) return;

    const keysArray = playerNumber === 1 ? ['a', 'd'] : ['ArrowLeft', 'ArrowRight'];
    handleKeyUp(event, keys, keysArray);
});

/*
function handleKeys(event, keysObj, map) {
    switch(event.key) {
        case map.left:
            keysObj[map.left].pressed = true;
            localFighter.lastKey = map.left;
            sendMovement(event.key, 'DOWN');
            break;
        case map.right:
            keysObj[map.right].pressed = true;
            localFighter.lastKey = map.right;
            sendMovement(event.key, 'DOWN');
            break;
        case map.jump:
            localFighter.velocity.y = -20;
            sendMovement(event.key, 'DOWN');
            break;
        case map.attack:
            localFighter.attack();
            sendMovement(event.key, 'DOWN');
            break;
    }
}
 */


function handleKeys(event, fighter, keysObj, map) {
    switch (event.key) {
        case map.left:
            keysObj[map.left].pressed = true;
            fighter.lastKey = map.left;
            sendMovement(event.key, 'DOWN');
            break;
        case map.right:
            keysObj[map.right].pressed = true;
            fighter.lastKey = map.right;
            sendMovement(event.key, 'DOWN');
            break;
        case map.jump:
            fighter.velocity.y = -20;
            sendMovement(event.key, 'DOWN');
            break;
        case map.attack:
            fighter.attack();
            sendMovement(event.key, 'DOWN');
            break;
    }
}

/*
function handleKeys(event, fighter, keysObj, map) {
    switch (event.key) {
        case map.left:
            keysObj[map.left].pressed = true;
            fighter.lastKey = map.left;
            sendInput(event.key, 'DOWN');
            break;
        case map.right:
            keysObj[map.right].pressed = true;
            fighter.lastKey = map.right;
            sendInput(event.key, 'DOWN');
            break;
        case map.jump:
            fighter.velocity.y = -20;
            sendInput(event.key, 'DOWN');
            break;
        case map.attack:
            fighter.attack();
            sendInput(event.key, 'DOWN');
            break;
    }
}

function handleKeyUp(event, keysObj, keysArray) {
    switch (event.key) {
        case keysArray[0]:
            keysObj[keysArray[0]].pressed = false;
            sendInput(event.key, 'UP');
            break;
        case keysArray[1]:
            keysObj[keysArray[1]].pressed = false;
            sendInput(event.key, 'UP');
            break;
    }
}
 */


/*
function sendInput(key, state) {
    //if (!stompClient || stompClient.state !== StompJs.StompState.OPEN) return;

    stompClient.publish({
        destination: `/app/move`,
        body: JSON.stringify({
            matchId: matchId,
            playerNumber: playerNumber,
            key: key,
            state: state
        })
    });
}
 */


function handleKeyUp(event, keysObj, keysArray) {
    switch (event.key) {
        case keysArray[0]:
            keysObj[keysArray[0]].pressed = false;
            localFighter.velocity.x = 0;
            sendMovement(event.key, "UP");
            break;
        case keysArray[1]:
            keysObj[keysArray[1]].pressed = false;
            localFighter.velocity.x = 0;
            sendMovement(event.key, "UP");
            break;
    }
}




function sendMovement(key, state) {
    if (!stompClient || !stompClient.connected) return;

    stompClient.publish({
        destination: `/app/match/${matchId}/move`,
        body: JSON.stringify({
            playerId: playerNumber,
            key: key,
            state: state
        })
    });
}


function connectWebSocket() {
    stompClient = new StompJs.Client({
        webSocketFactory: () => new SockJS('/ws'),
        debug: str => console.log(str),
        onConnect: () => {
            console.log('WS connected');

            // assign fighters ONCE
            if (playerNumber === 1) {
                localFighter = player;
                opponentFighter = enemy;
            } else {
                localFighter = enemy;
                opponentFighter = player;
            }

            stompClient.subscribe(`/topic/match/${matchId}/move`, message => {
                const data = JSON.parse(message.body);
                if (data.playerId === playerNumber) return;
                handleOpponentMovement(data);
            });

            stompClient.subscribe(`/topic/match/${matchId}/state`, message => {
                const state = JSON.parse(message.body);

                if (!opponentFighter) return; // safety check

                // Determine which player is the opponent
                const opponentState = playerNumber === 1 ? state.player2 : state.player1;

                // Smoothly interpolate opponent position for smooth animation
                opponentFighter.position.x += (opponentState.x - opponentFighter.position.x) * 0.2;
                opponentFighter.position.y += (opponentState.y - opponentFighter.position.y) * 0.2;

                // Update velocities for physics
                opponentFighter.velocity.x = opponentState.vx;
                opponentFighter.velocity.y = opponentState.vy;

                // Update health
                opponentFighter.health = opponentState.health;

                // Update animation based on velocity
                switchSpriteBasedOnVelocity(opponentFighter);
            });

            /*
            stompClient.subscribe(`/topic/match/${matchId}/state`, message => {
                const state = JSON.parse(message.body);

                // Determine opponent state based on playerNumber
                const opponentState = playerNumber === 1 ? state.player2 : state.player1;

                // Smooth interpolation
                opponentFighter.position.x += (opponentState.x - opponentFighter.position.x) * 0.2;
                opponentFighter.position.y += (opponentState.y - opponentFighter.position.y) * 0.2;
                opponentFighter.velocity.x = opponentState.vx;
                opponentFighter.velocity.y = opponentState.vy;

                // Optional: sync keys for smooth left/right
                opponentKeys.left = opponentState.keys.left;
                opponentKeys.right = opponentState.keys.right;

                // Update animation
                switchSpriteBasedOnVelocity(opponentFighter);
            });
             */

            /*
            stompClient.subscribe(`topic/match/${matchId}/move/state`,message=>{
                const state = JSON.parse(message.body);

                const opponentState = playerNumber === 1 ? state.player2 : state.player1;

                opponentFighter.position.x = opponentState.x;
                opponentFighter.position.y = opponentState.y;
                opponentFighter.velocity.x = opponentState.vx;
                opponentFighter.velocity.y = opponentState.vy;
                opponentFighter.health = opponentState.health;
                opponentFighter.switchSprite(opponentState.sprite);

                // Optional: health bar update
                if (playerNumber === 1) {
                    gsap.to('#enemyHealth', { width: opponentState.health + '%' });
                } else {
                    gsap.to('#playerHealth', { width: opponentState.health + '%' });
                }
            })
             */

            gameStarted = true;
            animate(); // START LOOP ONCE
        }
    });

    stompClient.activate();
}



/*
function onConnected() {
    subscribeToMovement();
}
 */

const opponentKeys = {
    left: false,
    right: false
};

//subscribe
//opp logic
function handleOpponentMovement(data) {
    if (data.key === 'a' || data.key === 'ArrowLeft') {
        opponentKeys.left = data.state === 'DOWN';
    }

    if (data.key === 'd' || data.key === 'ArrowRight') {
        opponentKeys.right = data.state === 'DOWN';
    }

    if ((data.key === 'w' || data.key === 'ArrowUp') && data.state === 'DOWN') {
        opponentFighter.velocity.y = -20;
    }

    if ((data.key === ' ' || data.key === 'ArrowDown') && data.state === 'DOWN') {
        opponentFighter.attack();
    }
}


function switchSpriteBasedOnVelocity(fighter) {
    if (fighter.velocity.y < 0) {
        fighter.switchSprite('jump');
    } else if (fighter.velocity.y > 0) {
        fighter.switchSprite('fall');
    } else if (fighter.velocity.x !== 0) {
        fighter.switchSprite('run');
    } else {
        fighter.switchSprite('idle');
    }
}

//animate() se izveduva pred stompClientot uspesno da se povrze
//sto znaci toa? Toa znaci animacijata zapocnuva uste pred da se definira i stomp i playerNumber i se
//zatoa vadi greska(pretpostavuvam)


function animate() {
    window.requestAnimationFrame(animate);

    // Clear screen
    c.fillStyle = 'black';
    c.fillRect(0, 0, canvas.width, canvas.height);

    background.update();
    shop.update();

    // Overlay effect
    c.fillStyle = 'rgba(255, 255, 255, 0.15)';
    c.fillRect(0, 0, canvas.width, canvas.height);

    // Update both fighters
    localFighter.update();
    opponentFighter.update();

    // Local movement based on keys
    if (keys.a?.pressed && localFighter === player) localFighter.velocity.x = -localFighter.runSpeed;
    if (keys.d?.pressed && localFighter === player) localFighter.velocity.x = localFighter.runSpeed;
    if (keys.ArrowLeft?.pressed && localFighter === enemy) localFighter.velocity.x = -localFighter.runSpeed;
    if (keys.ArrowRight?.pressed && localFighter === enemy) localFighter.velocity.x = localFighter.runSpeed;

    if (opponentKeys.left) {
        opponentFighter.velocity.x = -opponentFighter.runSpeed;
    } else if (opponentKeys.right) {
        opponentFighter.velocity.x = opponentFighter.runSpeed;
    } else {
        opponentFighter.velocity.x = 0;
    }

    // Switch sprites based on velocity
    switchSpriteBasedOnVelocity(localFighter);
    switchSpriteBasedOnVelocity(opponentFighter);

    // Handle collisions & attacks
    // handleCombat(localFighter, opponentFighter);

    // End game
    if (localFighter.health <= 0 || opponentFighter.health <= 0) {
        determineWinner({ player, enemy, timerId });
    }
}

connectWebSocket()


//connectWebSocket()
// animate()
