package jp.gr.java_conf.frontier.nobreath
import jp.gr.java_conf.frontier.utauplug.UtauElement
import jp.gr.java_conf.frontier.utauplug.UtauPlug
import jp.gr.java_conf.frontier.utauplug.Node
import scala.util.control.Exception._

class BreathSetter(
  //追加ノート
  val elm: UtauElement = new UtauElement(Map("Lyric" -> "息1", "Length" -> "240")),

  //一定以上の長さならこちらの設定を使う(0で無効)
  val useElm2Length: Int = 0,
  val elm2: UtauElement = new UtauElement(Map("Lyric" -> "息1", "Length" -> "240")),

  //条件になる休符の長さ
  val condLength: Int = 240,

  //調整休符の最大長
  val restLength: Int = 240,

  //無視するノート
  val ignoreLyrics: String = "息1",

  //この長さ以上の休符なら無視(0で無効)
  val ignoreLength: Int = 1920) {

  /*
   *--nextが休符以外、無視ノート以外かつ
   * --条件の長さ以上の休符があるとき
   * --追加ノート＋調整休符以上の長さなら、
   * --　その休符の長さをマイナス（追加ノート＋調整休符）
   * --　追加ノート、調整休符を追加
   * --追加ノート以上の長さなら
   * --　その休符を削除
   * --　追加ノート、
   * --　その休符の長さ-追加ノート長さが0以上なら
   * --　　その長さの休符を追加
   *無視するノート長
   *追加ノートが一定以上の場合は別の歌詞
   */
  def isTarget(n: Node): Boolean = {
    n.get.isRest &&
      (n.next match {
        case Some(n) => !n.get.isRest
        case None => false
      }) &&
      condLength <= n.get.length &&
      (ignoreLength == 0 || n.get.length < ignoreLength)
  }

  def exec(plug: UtauPlug): UtauPlug = {
    def restNote(len: Int): UtauElement = new UtauElement.Builder {
      lyric = "R"
      length = len
    }.build

    plug.flatMap { n =>
      if (isTarget(n)) {
        n.get.length match {
          case m if m == elm.length => List(UtauElement.delete, elm)
          case m if elm.length + restLength < m => {
            val b = n.get.builder
            b.length = n.get.length - (elm.length + restLength)
            List(b.build, elm, restNote(restLength))
          }
          case m => List(UtauElement.delete, elm, restNote(m - elm.length))
        }
      } else List(n.get)
    }
  }
}

object BreathSetter {
  def fromXml(n: scala.xml.Node): BreathSetter = {
    var elm1 = Map[String, String]()
    for (e <- (n \\ "element1")(0).child) { elm1 += (e.label -> e.text) }

    var elm2 = Map[String, String]()
    for (e <- (n \\ "element2")(0).child) { elm2 += (e.label -> e.text) }

    new BreathSetter(
      elm = new UtauElement(elm1),
      elm2 = new UtauElement(elm2),
      useElm2Length = allCatch opt (n \\ "useElm2Length")(0).text.toInt getOrElse 0,
      condLength = allCatch opt (n \\ "condLength")(0).text.toInt getOrElse 240,
      restLength = allCatch opt (n \\ "restLength")(0).text.toInt getOrElse 240,
      ignoreLength = allCatch opt (n \\ "ignoreLength")(0).text.toInt getOrElse 1920,
      ignoreLyrics = (n \\ "ignoreLyrics")(0).text)
  }
}