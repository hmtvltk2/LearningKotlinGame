package com.hmtvltk2.learningkotlingame.screens

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.TimeUtils
import com.hmtvltk2.learningkotlingame.MainGame
import ktx.app.KtxScreen

class GameScreen(val game: MainGame) : KtxScreen {
    // load the images for the droplet and the bucket, 64x64 pixels each
    private val dropImage = Texture(Gdx.files.internal("images/drop.png"))
    private val bucketImage = Texture(Gdx.files.internal("images/bucket.png"))

    // load the drop sound effect and the rain background "music"
    private val dropSound = Gdx.audio.newSound(Gdx.files.internal("sounds/drop.wav"))
    private val rainMusic =
        Gdx.audio.newMusic(Gdx.files.internal("music/rain.mp3")).apply { isLooping = true }

    // Sử dụng OrthographicCamera để tự động scale hình theo kích thước màn hình
    private val camera = OrthographicCamera().apply { setToOrtho(false, 800f, 480f) }

    // Lưu vị trí của cái xô
    private val bucket = Rectangle(800f / 2f - 64f / 2f, 20f, 64f, 64f)
    private val touchPos = Vector3()

    // Lưu mảng vị trí của những giọt nước
    private val raindrops = Array<Rectangle>()

    // Thời gian tạo giọt nước cuối cùng
    private var lastDropTime = 0L

    // Số lượng giọt nước hứng được
    private var dropGathered = 0

    override fun show() {
        rainMusic.play()
    }

    override fun render(delta: Float) {
        // generally good practice to update the camera's matrices once per frame
        camera.update()

        // tell the SpriteBatch to render in the coordinate system specified by the camera.
        game.batch.projectionMatrix = camera.combined

        // begin a new batch and draw the bucket and all drops
        game.batch.begin()
        game.font.draw(game.batch, "Drops Collected: $dropGathered", 0f, 480f)
        game.batch.draw(bucketImage, bucket.x, bucket.y)
        for (raindrop in raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y)
        }
        game.batch.end()

        // Di chuyển cái xô để hứng nước
        if (Gdx.input.isTouched) {
            touchPos.set(Gdx.input.x.toFloat(), Gdx.input.y.toFloat(), 0f)
            camera.unproject(touchPos)
            bucket.x = touchPos.x - 64 / 2
        }

        // kiểm tra cứ mỗi 1s thì tạo một giọt nước
        if (TimeUtils.nanoTime() - lastDropTime > 1_000_000_000L) {
            spawnRaindrop()
        }

        // move the raindrops, remove any that are beneath the bottom edge of the
        //    screen or that hit the bucket.  In the latter case, play back a sound
        //    effect also
        val iter = raindrops.iterator()
        while (iter.hasNext()) {
            val raindrop = iter.next()
            raindrop.y -= 200 * delta

            if (raindrop.y + 64 < 0) {
                iter.remove()
            }

            // Kiểm tra nếu giọt nước chạm vào xô thì tăng số giọt nước hứng được
            // và phát âm thanh giọt nước rơi
            if (raindrop.overlaps(bucket)) {
                dropGathered++
                dropSound.play()
                iter.remove()
            }
        }
    }

    /**
     * Hàm tạo giọt nước tại vị trí ngẫu nhiên phía trên cùng màn hình
     */
    private fun spawnRaindrop() {
        raindrops.add(Rectangle(MathUtils.random(0f, 800f - 64f), 480f, 64f, 64f))
        lastDropTime = TimeUtils.nanoTime()
    }

    override fun dispose() {
        // Dọn dẹp tài nguyên
        dropImage.dispose()
        bucketImage.dispose()
        dropSound.dispose()
        rainMusic.dispose()
    }
}