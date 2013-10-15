package dao

import model.MailMessage
import java.util.Properties
import javax.mail.{BodyPart, Multipart, Folder, Session}
import org.joda.time.DateTime
import helpers.Formatting
import org.jsoup.Jsoup
import org.jsoup.safety.Whitelist

object MailDao {

  val max = 10

  def getMessages(host: String, username: String, password: String, port: Int, ssl: Boolean): Seq[MailMessage] = {
    val proto = ssl match {
      case true => "imaps"
      case false => "imap"
    }

    val props = new Properties
    props.setProperty("mail.store.protocol", proto)

    val store = Session.getInstance(props).getStore(proto)
    store.connect(host, port, username, password)
    val inbox = store.getFolder("Inbox")
    inbox.open(Folder.READ_ONLY)

    val total = inbox.getMessageCount
    val messages = inbox.getMessages(total - max + 1, total).reverse

    val msgs = for (message <- messages) yield {
      MailMessage(
        message.getFrom()(0).toString,
        message.getSubject,
        Formatting.print(new DateTime(message.getReceivedDate)),
        parseContent(message.getContent)
      )
    }
    inbox.close(true)
    store.close

    msgs
  }

  def parseContent(x: Any): String = {
    // parse the various message types
    val s = x match {
      case x: String => x
      case x: Multipart => {
        var text: BodyPart = null
        var html: BodyPart = null
        for (i <- 0 until x.getCount) {
          val part = x.getBodyPart(i)
          if (part.isMimeType("text/plain")) {
            text = part
          } else if (part.isMimeType("text/html")) {
            html = part
          }
        }

        if (text != null) {
          text.getContent.asInstanceOf[String]
        } else if (html != null) {
          html.getContent.asInstanceOf[String]
        } else {
          ""
        }
      }
      case _ => ""
    }

    val t = Jsoup.clean(s, Whitelist.none)

    // chop down the string
    if (t.length > 200) {
      t.substring(0, 200)
    } else {
      t
    }
  }
}
