/*
 * Copyright (c) 2019 Georgios Andreadakis
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
package org.tap.framework.persistence.elastic

import org.tap.domain.{Document, DocumentRepository}

/**
  * An implementation of the [[DocumentRepository]] running against EleastiSearch.
  *
  * @author Georgios Andreadakis (georgios@andreadakis-consulting.de)
  */
class DocumentRepositoryForElastic extends DocumentRepository {
  def findDoc(docId: String): Either[Exception,Document] = {
    val operation = new ReadSingleDocOperation(docId)
    (new PersistenceContext).execute(operation)
    operation.getResult
  }


  override def allDocs: Either[Exception,List[Document]] = {
    val operation = new ReadAllDocsOperation
    (new PersistenceContext).execute(operation)
    operation.getResult
  }

  override def deleteDoc(docId: String): Unit = {
    (new PersistenceContext).execute(new DeleteDocOperation(docId))
  }

  override def save(document: Document): Unit = {
    (new PersistenceContext).execute(new SaveDocOperation(document))
  }

}


