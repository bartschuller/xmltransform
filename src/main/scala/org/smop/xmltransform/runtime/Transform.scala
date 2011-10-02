package org.smop.xmltransform.runtime

import com.codecommit.antixml._

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
   * Bogus. Needs added priorities and not pick the first but the one with the highest priority which matches
   */
  def applyTemplate(node: Node): Group[Node] = {
    templates.find(_.isDefinedAt(node)).get.apply(node) match {
      case g: Group[_] => g
      case n: Node => Group(n)
      case ns: scala.xml.NodeSeq => ns.convert
      case a => Group(Text(a.toString))
    }
  }

  var templates: List[PartialFunction[Node, Any]] = Nil

  /**
   * Registers a template with the transform. Last one matching wins.
   */
  def template(priority: Double = 1)(pf: PartialFunction[Node, Any]) {
    templates = pf :: templates
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
