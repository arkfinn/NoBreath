package jp.gr.java_conf.frontier.nobreath

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith
import jp.gr.java_conf.frontier.utauplug.UtauPlug
import jp.gr.java_conf.frontier.utauplug.UtauElement

@RunWith(classOf[JUnitRunner])
class BreathSetterSpec extends Specification {

  "exec" should {
    "単純な置き換え" in {
      val plug = new UtauPlug("test", List(
        new UtauElement("#0002", Map("Lyric" -> "あ", "Length" -> "480")),
        new UtauElement("#0003", Map("Lyric" -> "R", "Length" -> "240")),
        new UtauElement("#0004", Map("Lyric" -> "あ", "Length" -> "480"))))
      /*
       * 最初のRが480R 240息 240R
       * 三番目のRが息1に変わるはず
       */
      val setter = new BreathSetter(
        condLength = 240,
        restLength = 240,
        ignoreLength = 0);

      val res = setter.exec(plug)

      res.list(0).lyric must_== "あ"

      res.list(1).blockName must_== "#DELETE"
      res.list(2).lyric must_== "息1"
      res.list(2).length must_== 240

      res.list(3).lyric must_== "あ"

    }
    "単純な置き換え2" in {
      val plug = new UtauPlug("test", List(
        new UtauElement("#0002", Map("Lyric" -> "あ", "Length" -> "480")),
        new UtauElement("#0003", Map("Lyric" -> "R", "Length" -> "480")),
        new UtauElement("#0004", Map("Lyric" -> "あ", "Length" -> "480"))))
      /*
       * 最初のRが480R 240息 240R
       * 三番目のRが息1に変わるはず
       */
      val setter = new BreathSetter(
        condLength = 240,
        restLength = 240,
        ignoreLength = 0);

      val res = setter.exec(plug)

      res.list(0).lyric must_== "あ"

      res.list(1).blockName must_== "#DELETE"
      res.list(2).lyric must_== "息1"
      res.list(2).length must_== 240

            res.list(3).lyric must_== "R"
      res.list(3).length must_== 240

      res.list(4).lyric must_== "あ"

    }
    "分割する" in {
      val plug = new UtauPlug("test", List(
        new UtauElement("#0001", Map("Lyric" -> "R", "Length" -> "960")),
        new UtauElement("#0002", Map("Lyric" -> "あ", "Length" -> "480")),
        new UtauElement("#0003", Map("Lyric" -> "R", "Length" -> "240")),
        new UtauElement("#0004", Map("Lyric" -> "あ", "Length" -> "480"))))
      /*
       * 最初のRが480R 240息 240R
       * 三番目のRが息1に変わるはず
       */
      val setter = new BreathSetter(
        condLength = 240,
        restLength = 240,
        ignoreLength = 0)

      val res = setter.exec(plug)
      res.list(0).lyric must_== "R"
      res.list(0).length must_== 480

      res.list(1).lyric must_== "息1"
      res.list(1).length must_== 240

      res.list(2).lyric must_== "R"
      res.list(2).length must_== 240

      res.list(3).lyric must_== "あ"

      res.list(4).blockName must_== "#DELETE"

      res.list(5).lyric must_== "息1"
      res.list(5).length must_== 240

      res.list(6).lyric must_== "あ"

    }

    "restLengthのテスト" in {
      val plug = new UtauPlug("test", List(
        new UtauElement("#0001", Map("Lyric" -> "R", "Length" -> "360")),
        new UtauElement("#0002", Map("Lyric" -> "あ", "Length" -> "480")),
        new UtauElement("#0003", Map("Lyric" -> "R", "Length" -> "240")),
        new UtauElement("#0004", Map("Lyric" -> "あ", "Length" -> "480"))))
      val setter = new BreathSetter(
        condLength = 240,
        restLength = 240,
        ignoreLength = 480)

      val res = setter.exec(plug)

      res.list(0).blockName must_== "#DELETE"

      res.list(1).lyric must_== "息1"
      res.list(1).length must_== 240

      res.list(2).lyric must_== "R"
      res.list(2).length must_== 120

      res.list(3).lyric must_== "あ"
      res.list(4).blockName must_== "#DELETE"

      res.list(5).lyric must_== "息1"
      res.list(5).length must_== 240

      res.list(6).lyric must_== "あ"
    }
    "ignoreLengthのテスト" in {
      val plug = new UtauPlug("test", List(
        new UtauElement("#0001", Map("Lyric" -> "R", "Length" -> "960")),
        new UtauElement("#0002", Map("Lyric" -> "あ", "Length" -> "480")),
        new UtauElement("#0003", Map("Lyric" -> "R", "Length" -> "240")),
        new UtauElement("#0004", Map("Lyric" -> "あ", "Length" -> "480"))))
      /*
       * 最初のRが480R 240息 240R
       * 三番目のRが息1に変わるはず
       */
      val setter = new BreathSetter(
        condLength = 240,
        restLength = 240,
        ignoreLength = 480)

      val res = setter.exec(plug)
      res.list(0).lyric must_== "R"
      res.list(0).length must_== 960

      res.list(1).lyric must_== "あ"

      res.list(2).blockName must_== "#DELETE"

      res.list(3).lyric must_== "息1"
      res.list(3).length must_== 240

      res.list(4).lyric must_== "あ"
    }

    //elm2のテスト
  }

  "from xml" should {
    "from xml" in {
      val xml =
        <NoBreath>
          <element1>
            <Lyric>息2</Lyric>
            <Length>120</Length>
          </element1>
          <useElm2Length>30</useElm2Length>
          <element2>
            <Lyric>息2</Lyric>
            <Length>140</Length>
          </element2>
          <condLength>340</condLength>
          <restLength>440</restLength>
          <ignoreLyrics>息2</ignoreLyrics>
          <ignoreLength>1720</ignoreLength>
        </NoBreath>
      val b = BreathSetter.fromXml(xml)
      b.useElm2Length must_== 30
      b.condLength must_== 340
      b.restLength must_== 440
      b.ignoreLength must_== 1720
      b.ignoreLyrics must_== "息2"

      b.elm.lyric must_== "息2"
      b.elm.length must_== 120
      b.elm2.lyric must_== "息2"
      b.elm2.length must_== 140
    }
  }
}