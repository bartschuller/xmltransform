package org.smop.xmltransform.example

import org.smop.xmltransform.runtime.Transform
import com.codecommit.antixml._

class Example1 extends Transform {
  template() {
      case Text(t) => t.toUpperCase
  }
  
  template() {
    case root @ Elem(None, "root", _, _, cs) =>
    <html>
      <head>
        <title>
          {root \ "title" \ text}
        </title>
      </head>
      <body>
        {applyTemplates(cs).convert}
      </body>
    </html>
  }

  template() {
    case Elem(None, "title", _, _, cs) =>
      Elem(None, "h1", Attributes(), Map(), applyTemplates(cs))
  }

  template(2) {
    case Elem(None, "para", m, _, cs) if m.get("important") == Some("true") =>
      <p><b>{applyTemplates(cs).convert}</b></p>
  }
  
  template() {
    case Elem(None, "para", _, _, cs) =>
      <p>{applyTemplates(cs).convert}</p>
  }
}

object Example1 extends App {
  val input =
<root>
  <title>Hello, World!</title>
  <para>Hey, how ya doin'?</para>
  <para important="true">Watch out.</para>
</root>.convert

  val trans = new Example1
  println("---- input ----")
  println(input)

  val output = trans.transform(input)
  println("---- output ----")
  println(output)

//<html>
//      <head>
//        <title>
//          Hello, World!
//        </title>
//      </head>
//      <body>
//
//  <h1>HELLO, WORLD!</h1>
//  <p>HEY, HOW YA DOIN'?</p>
//  <p><b>WATCH OUT.</b></p>
//
//      </body>
//    </html>
}
