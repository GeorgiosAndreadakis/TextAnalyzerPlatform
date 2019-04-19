/*
 * Copyright (c) 2018 Georgios Andreadakis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tap.framework.parser.tika

import java.util.UUID

import org.tap.domain.{DocElement, Document, Paragraph, Section}

/**
  * Builders for the document elements.
  */
sealed abstract class ElementBuilder {

  var myDocument: Document = _
  var myElement: DocElement = _
  val textBuilder: TextBuilder = new TextBuilder

  def parentBuilder: ElementBuilder
  def endElementEventReceived(): Unit

  def charactersEventReceived(event: CharactersEvent): ElementBuilder = {
    textBuilder.append(event.ch)
    this
  }

  def findDocument: Document = {
    if (myDocument != null) {
      myDocument
    } else {
      parentBuilder.findDocument
    }
  }

  def newChild(docElement: DocElement): Unit = {
    //if (parentBuilder == null) throw new IllegalStateException("#parentBuilder is missing!")
    myElement.asContainer.addChild(docElement)
    findDocument.elementCreated(docElement)
  }

}


////

case class ParagraphBuilder(parentBuilder: ElementBuilder) extends ElementBuilder {
  override def endElementEventReceived(): Unit = {
    val text = textBuilder.build
    if (!text.isEmpty) {
      val paragraph = Paragraph(UUID.randomUUID().toString, text)
      parentBuilder.newChild(paragraph)
      myElement = paragraph
    }
  }
}


////

case class SectionBuilder(level: Int, parentBuilder: ElementBuilder) extends ElementBuilder {
  override def endElementEventReceived(): Unit = {
    val section = Section(UUID.randomUUID().toString, level, textBuilder.build)
    parentBuilder.newChild(section)
    myElement = section
  }
}


////

case class DummyBuilder(parentBuilder: ElementBuilder) extends ElementBuilder {
  override def endElementEventReceived(): Unit = {
  }
}


////

class TextBuilder {
  var stringBuilder: StringBuilder = new StringBuilder
  def append(ch: String):Unit = stringBuilder.append(ch)
  def build: String = {
    val text: String = stringBuilder.toString
    stringBuilder.clear()
    text
  }
}


////

case class RootContainerBuilder(document: Document) extends ElementBuilder() {

  myDocument = document
  myElement = myDocument.bodyElements
  override def parentBuilder: Null = null

  override def newChild(docElement: DocElement): Unit = {
    myElement.asContainer.addChild(docElement)
    myDocument.elementCreated(docElement)
  }

  override def endElementEventReceived(): Unit = {
  }
}

