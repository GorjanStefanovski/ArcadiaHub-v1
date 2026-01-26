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

    const displayEl = document.querySelector('#displayText');
    const winStatusEl = document.querySelector('#winStatus');

    if (player.health === enemy.health) {
        winStatusEl.innerHTML = 'TIE';
        winStatusEl.style.color = 'white';
    } else if (player.health > enemy.health) {
        if (playerNumber === 1) {
            winStatusEl.innerHTML = 'YOU WON!';
            winStatusEl.style.color = '#81b214';
        } else {
            winStatusEl.innerHTML = 'YOU LOST!';
            winStatusEl.style.color = '#f05454';
        }
    } else if (enemy.health > player.health) {
        if (playerNumber === 2) {
            winStatusEl.innerHTML = 'YOU WON!';
            winStatusEl.style.color = '#81b214';
        } else {
            winStatusEl.innerHTML = 'YOU LOST!';
            winStatusEl.style.color = '#f05454';
        }
    }

    setTimeout(() => {
        displayEl.style.display = 'flex';

        gsap.fromTo('#displayText',
            { opacity: 0, scale: 0.8 },
            { opacity: 1, scale: 1, duration: 0.6, ease: "back.out(1.7)" }
        );

        gsap.from('#backHomeBtn', {
            y: 30,
            opacity: 0,
            delay: 0.4,
            duration: 0.4
        });
    }, 1500);
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

function leaveMatchAndGoHome() {
    fetch('/fight/leave-match', { method: 'POST' })
        .then(() => {
            window.location.href = '/home/welcomeUser';
        });
}