package org.smop.xmltransform.runtime

import com.codecommit.antixml._
import collection.SortedMap
import math.round

/**
 * Base class for XML transforms.
 */
abstract class Transform {
  /**
   * Main entry point. Transform XML into other XML.
   * 
   * It's not possible yet to specify a template for /, the root element will always be processed.
   */
  def transform(root: Elem): Group[Node] = {
    applyTemplates(Group(root))
  }

  /**
   * Apply templates to selection. Roughly equivalent to <xsl:apply-templates select="selection"/>
   */
  def applyTemplates(selection: Group[Node]): Group[Node] = {
    selection.flatMap(node=>applyTemplate(node))
  }

  /**
   * Picks the highest priority template which matches and executes it.
   * The result is converted to a Group[Node]
   */
  def applyTemplate(node: Node): Group[Node] = {
    orderedTemplates.find(_.isDefinedAt(node)).get.apply(node) match {
      case g: Group[_] => g
      case n: Node => Group(n)
      case ns: scala.xml.NodeSeq => ns.convert
      case a => Group(Text(a.toString))
    }
  }

  def orderedTemplates = templates.values.flatten

  var templates = SortedMap.empty[Int, List[PartialFunction[Node, Any]]](Ordering.Int.reverse)

  /**
   * Registers a template with the transform. Last one matching wins.
   */
  def template(priority: Float = 1)(pf: PartialFunction[Node, Any]) {
    val prio: Int = round(1000F*priority)
    val list = templates.getOrElse(prio, Nil)
    templates += (prio -> (pf :: list))
  }
  
  /**
   * Default template: recursive copy/apply-templates.
   */
  template() {
    case e@ Elem(_, _, _, _, cs) => Group(e.copy(children=applyTemplates(cs)))
    case n => Group(n)
  }

  /**
   * Less work than reversing the 50 lines in anti-xml's conversion.scala
   */
  implicit val anti2Scala = new XMLConvertable[Group[Node], scala.xml.NodeSeq] {
    def apply(g: Group[Node]) = {scala.xml.XML.loadString("<root>"+g.toString+"</root>").child}
  }
}
