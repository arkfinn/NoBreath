package jp.gr.java_conf.frontier.nobreath

import java.io.File
import scala.io.Source
import scala.swing.event.ButtonClicked
import scala.swing.BoxPanel
import scala.swing.Button
import scala.swing.Label
import scala.swing.MainFrame
import scala.swing.Orientation
import scala.swing.SimpleSwingApplication
import scala.xml.parsing.ConstructingParser
import jp.gr.java_conf.frontier.utauplug.UtauPlug
import scala.swing.Component

object Main extends SimpleSwingApplication {
  var a: String = ""
  override def startup(args: Array[String]) {
    if (0 < args.length) a = args(0)
    super.startup(args)
  }
  def top = new MainFrame {
    title = a
    contents = new BoxPanel(Orientation.Vertical) {
      contents += new Label("OKボタン押下でNoBreathを実行します")
      contents += new Button("OK") {
        reactions += {
          case ButtonClicked(s) => {
            val u = UtauPlug.fromFile(a)
            val u2 = setter.exec(u)
            u2.output(a)
            quit()
          }
        }
      }
    }
  }

  def setter: BreathSetter = {
    val f = new File("preset/")
    if (f.exists()) {
      var list = f.listFiles().filter { _.getPath.endsWith(".xml") }
      if (0 < list.size) {
        //xmlから読み込む
        val doc = ConstructingParser.fromSource(Source.fromFile(list(0), "UTF-8"), false).document()
        BreathSetter.fromXml(doc.docElem)
      } else new BreathSetter()
    } else new BreathSetter()
  }

}