/*
 * Copyright (c) 2017 Georgios Andreadakis
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
package org.tap.accepttest.importdoc.api

import java.nio.file.Paths

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers
import org.tap.accepttest.testdata.TestFileReference
import org.tap.application.importdoc.DocImporter
import org.tap.domain.docimport.DocumentParser
import org.tap.domain.{Document, DocumentRepository, DocumentSource, Paragraph}
import org.tap.framework.DocumentPathSource
import org.tap.framework.parser.tika.DocumentParserTika

/**
  * The Cucumber step definitions for the story "import text file with a single passage".
  *
  * @author Georgios Andreadakis (georgios@andreadakis-consulting.de)
  */
class SimpleTextPassageInWordFileStepDefs extends ScalaDsl with EN with Matchers {

  private var source: DocumentPathSource = _
  private val parser: DocumentParser = new DocumentParserTika
  private val docRepo: DocumentRepositoryMock = new DocumentRepositoryMock

  private def testreference = {
    TestFileReference.build.find(TestFileReference.WORD_SINGLE_PARAGRAPH)
  }


  Given("""^a word file which contains a single text passage$"""){ () =>
    val path = testreference.qualifiedPath
    source = new DocumentPathSource(Paths.get(path))
    withClue(s"Source in path '$path' not found: ") {
      source should not be null
    }
  }

  When("""^the user starts the import for the given file$"""){ () =>
    new DocImporter(docRepo, parser).importFile(source)
  }

  Then("""^the file will be imported and the text is in the system available$"""){ () =>
    withClue("Save was called: ") {
      docRepo.saveCalled shouldBe true
    }
    withClue("Document should not be null") {
      docRepo.doc should not be null
    }

    withClue("Document misses a paragraph containing defined text") {
      val text = testreference.expected
      docRepo.documentContainsParagraphStartingWith(text) shouldBe true
    }
  }
}

class DocumentRepositoryMock extends DocumentRepository {
  var saveCalled = false
  var doc: Document = _
  override def save(document: Document): Unit = {
    saveCalled = true
    doc = document
  }
  def documentContainsParagraphStartingWith(text: String): Boolean = {
    doc.filter(_.isInstanceOf[Paragraph]).exists(elem => {
      val p = elem.asInstanceOf[Paragraph]
      p.text.contains(text)
    })
  }
}

class DocumentParserMock extends DocumentParser {
  override def parse(source: DocumentSource): Document = new Document
}