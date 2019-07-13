package com.example.jankenpon

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    val gu = 0
    val choki = 1
    val pa = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val id = intent.getIntExtra("MY_HAND",0)

        val myHand: Int

        myHand = when(id) {

            R.id.gu -> {
                myHandImage.setImageResource(R.drawable.gu)
                gu

            }

            R.id.choki -> {
                myHandImage.setImageResource(R.drawable.choki)
                choki

            }
            R.id.pa -> {
                myHandImage.setImageResource(R.drawable.pa)
                pa

            }

            else -> gu

        }

       // コンピューターの手を決める
       // val comHand = (Math.random() *3 ).toInt()
       // 修正
        val comHand = getHand()
        when (comHand) {
            gu -> comHandImage.setImageResource(R.drawable.com_gu)
            choki -> comHandImage.setImageResource(R.drawable.com_choki)
            pa -> comHandImage.setImageResource(R.drawable.com_pa)
        }

        // 勝敗を判定
        val gameResult = (comHand - myHand +3) % 3
        when(gameResult) {
            0 -> resultLabel.setText(R.string.result_draw) //ひきわけ
            1 -> resultLabel.setText(R.string.result_win) //勝ち
            2 -> resultLabel.setText(R.string.result_lose) //負け
        }

        backButton.setOnClickListener {finish()}
        // じゃんけんの結果を保存する
        saveData(myHand, comHand, gameResult)
    }

    // 共有プリファレンス saveData メソッド
    private fun saveData(myHand: Int, comHand: Int, gameResult: Int) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt("GAME_COUNT", 0)
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT", 0)
        val lastComHand = pref.getInt("LAST_COM_HAND", 0)
        val lastGameResult = pref.getInt("GAME_RESULT", -1)

        val editor = pref.edit()
        //　何回勝ったか
        editor.putInt("GAME_COUNT", gameCount + 1)
            // コンピュータが連勝したか
            .putInt(
                "WINNING_STREAK_COUNT",
                if (lastGameResult == 2 && gameResult == 2)
                    winningStreakCount + 1

                else
                    0
            )
            // 何回勝ったか、連勝したかを、前回の結果として共有プリファレンスに書き込む
            .putInt("LAST_MY_HAND", myHand)
            .putInt("LAST_COM_HAND", comHand)
            .putInt("BEFORE_LAST_COM_HAND", lastComHand)
            .putInt("GAME_RESULT", gameResult)
            // editorインターフェイスのインスタスを通じて行った変更を保存
            .apply()
    }

    // 心理学的に最強のじゃんけんロジック
    private fun getHand(): Int {
        var hand = (Math.random() *3).toInt()
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt("GAME_COUNT", 0)
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT",0)
        val lastMyHand = pref.getInt("LAST_MY_HAND",0)
        val lastComHand = pref.getInt("LAST_COM_HAND",0)
        val beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND",0)
        val gameResult = pref.getInt("GAME_RESULT",-1)

        if (gameCount == 1) {
            if (gameResult == 2) {
                // 前回の勝者が1回目でコンピュータが勝利
                // コンピュータは次に出す手を変える
                while (lastComHand == hand) {
                    hand = (Math.random() *3).toInt()
                }
            } else if (gameResult == 1) {
                // 前回の勝者が1回目で、コンピュータが負け
                // 相手の出した手に勝つ手を出す
                    hand = (lastMyHand - 1 + 3) % 3
            }
        } else if (winningStreakCount > 0) {
            if (beforeLastComHand == lastComHand) {
                // 同じ手で連勝した時は手を変える
                while (lastComHand == hand) {
                    hand = (Math.random() * 3).toInt()
                }
            }
        }

        return hand
    }
}
