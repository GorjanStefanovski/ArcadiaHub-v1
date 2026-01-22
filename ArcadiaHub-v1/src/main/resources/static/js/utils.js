function rectangularCollision({ rectangle1, rectangle2 }) {
    return (
        rectangle1.attackBox.position.x + rectangle1.attackBox.width >=
        rectangle2.position.x &&
        rectangle1.attackBox.position.x <=
        rectangle2.position.x + rectangle2.width &&
        rectangle1.attackBox.position.y + rectangle1.attackBox.height >=
        rectangle2.position.y &&
        rectangle1.attackBox.position.y <= rectangle2.position.y + rectangle2.height
    )
}

function determineWinner({ player, enemy, timerId }) {
    clearTimeout(timerId);
    gameStarted = false;

    const displayElement = document.querySelector('#displayText');
    displayElement.style.display = 'flex';

    let winnerId = null;

    if (player.health === enemy.health) {
        displayElement.innerHTML = 'Tie';
    } else if (player.health > enemy.health) {
        displayElement.innerHTML = 'Player 1 Wins';
        winnerId = player.playerId;
    } else if (player.health < enemy.health) {
        displayElement.innerHTML = 'Player 2 Wins';
        winnerId = enemy.playerId;
    }

    // Повикување на функцијата од index.js за зачувување во база
    if (winnerId) {
        finalizeMatch(winnerId);
    }
}

let timer = 90;
let timerId;

function decreaseTimer() {
    if (timer > 0 && gameStarted) {
        timerId = setTimeout(decreaseTimer, 1000);
        timer--;
        document.querySelector('#timer').innerHTML = timer;
    }

    if (timer === 0 && gameStarted) {
        determineWinner({ player, enemy, timerId });
    }
}