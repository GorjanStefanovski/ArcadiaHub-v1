const tintCanvas = document.createElement('canvas');
const tintC = tintCanvas.getContext('2d');

class Sprite {
    constructor({ position, imageSrc, scale = 1, framesMax = 1, offset = { x: 0, y: 0 }, framesHold = 5 }) {
        this.position = position
        this.image = new Image()
        this.image.src = imageSrc
        this.scale = scale
        this.framesMax = framesMax
        this.framesCurrent = 0
        this.framesElapsed = 0
        this.framesHold = framesHold
        this.offset = offset
    }

    draw() {
        if (!this.image || !this.image.complete || this.image.naturalWidth === 0) return;

        const crop = {
            position: { x: this.framesCurrent * (this.image.width / this.framesMax), y: 0 },
            width: this.image.width / this.framesMax,
            height: this.image.height
        }

        const pos = {
            x: this.position.x - this.offset.x,
            y: this.position.y - this.offset.y,
            width: (this.image.width / this.framesMax) * this.scale,
            height: this.image.height * this.scale
        }

        c.drawImage(
            this.image,
            crop.position.x, crop.position.y, crop.width, crop.height,
            pos.x, pos.y, pos.width, pos.height
        )

        const gameType = document.body.getAttribute('data-game-type');

        if (gameType === 'boxing-game') {
            tintCanvas.width = crop.width;
            tintCanvas.height = crop.height;

            tintC.clearRect(0, 0, crop.width, crop.height);

            tintC.drawImage(
                this.image,
                crop.position.x, crop.position.y, crop.width, crop.height,
                0, 0, crop.width, crop.height
            );

            tintC.globalCompositeOperation = 'source-in';
            tintC.fillStyle = 'rgba(255, 0, 0, 0.3)';
            tintC.fillRect(0, 0, crop.width, crop.height);

            c.drawImage(
                tintCanvas,
                pos.x, pos.y, pos.width, pos.height
            );
        }

        if (gameType === 'karate-game') {
            tintCanvas.width = crop.width;
            tintCanvas.height = crop.height;

            tintC.clearRect(0, 0, crop.width, crop.height);

            tintC.drawImage(
                this.image,
                crop.position.x, crop.position.y, crop.width, crop.height,
                0, 0, crop.width, crop.height
            );

            tintC.globalCompositeOperation = 'source-in';
            tintC.fillStyle = 'rgba(255, 252, 127, 0.3)';
            tintC.fillRect(0, 0, crop.width, crop.height);

            c.drawImage(
                tintCanvas,
                pos.x, pos.y, pos.width, pos.height
            );
        }
    }

    animateFrames() {
        this.framesElapsed++
        if (this.framesElapsed % this.framesHold === 0) {
            if (this.framesCurrent < this.framesMax - 1) {
                this.framesCurrent++
            } else {
                this.framesCurrent = 0
            }
        }
    }

    update() {
        this.draw()
        if (!this.dead) this.animateFrames()
    }
}

class Fighter extends Sprite {
    constructor({ position, velocity, imageSrc, scale = 1, framesMax = 1, offset = { x: 0, y: 0 }, sprites, health = 100, framesHold = 5, playerId }) {
        super({ position, imageSrc, scale, framesMax, offset, framesHold })
        this.velocity = velocity
        this.width = 50
        this.height = 150
        this.health = health
        this.maxHealth = health
        this.sprites = sprites
        this.originalFramesHold = framesHold
        this.dead = false
        this.playerId = playerId
        this.currentSprite = 'idle'

        for (const sprite in this.sprites) {
            sprites[sprite].image = new Image()
            sprites[sprite].image.src = sprites[sprite].imageSrc
        }
    }

    update() {
        this.draw()
        if (!this.dead) this.animateFrames()
    }

    attack() {
        this.switchSprite('attack1')
    }

    switchSprite(sprite) {
        if (this.image === this.sprites.death.image) {
            if (this.framesCurrent === this.sprites.death.framesMax - 1)
                this.dead = true
            return
        }

        if (
            (this.image === this.sprites.attack1.image && this.framesCurrent < this.sprites.attack1.framesMax - 1) ||
            (this.image === this.sprites.takeHit.image && this.framesCurrent < this.sprites.takeHit.framesMax - 1)
        ) return


        if (this.currentSprite !== sprite) {
            console.log(`Промена од ${this.currentSprite} во ${sprite}`);
            this.image = this.sprites[sprite].image
            this.framesMax = this.sprites[sprite].framesMax
            this.framesCurrent = 0
            this.currentSprite = sprite

            if (sprite === 'takeHit') {
                this.framesHold = 12
            } else {
                this.framesHold = this.originalFramesHold || 8
            }
        }
    }
}