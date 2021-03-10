package com.hmtvltk2.learningkotlingame

import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.hmtvltk2.learningkotlingame.screens.GameScreen
import ktx.app.KtxGame
import ktx.app.KtxScreen

class MainGame : KtxGame<KtxScreen>(){
    // SpriteBatch dùng để hiển thị hình ảnh
    val batch by lazy { SpriteBatch() }

    // use LibGDX's default Arial font
    // BitmapFont để hiển thị text
    val font by lazy { BitmapFont() }

    override fun create() {
        // Thêm screen GameScreen vào game
        addScreen(GameScreen(this))
        // Set screen hiện tại là GameScreen
        setScreen<GameScreen>()
        super.create()
    }

    override fun dispose() {
        // Dọn dẹp tài nguyên trước khi đóng game
        batch.dispose()
        font.dispose()
        super.dispose()
    }
}