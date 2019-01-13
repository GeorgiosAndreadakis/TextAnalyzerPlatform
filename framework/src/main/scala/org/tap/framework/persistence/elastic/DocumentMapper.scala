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

import org.elasticsearch.action.admin.indices.refresh.{RefreshRequest, RefreshResponse}
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.{SearchRequest, SearchResponse}
import org.elasticsearch.client.{RequestOptions, RestHighLevelClient}
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.SearchHit
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.tap.domain.{DocElement, Document, Paragraph}
import org.tap.framework.DocumentStringSource

/**
  * Responsible for mapping {{Document}} instances to the Index instances (aka documents).
  *
  * @author Georgios Andreadakis (georgios@andreadakis-consulting.de)
  */
class DocumentMapper(client: RestHighLevelClient) {

  private val docIndexName = "documents"
  private val elementIndexName = "elements"

  private val docIdAttributName = "docId"
  private val filenameAttributName = "filename"
  private val textAttributeName = "text"


  def readAllDocuments: SearchResponse = {
    val searchSourceBuilder = (new SearchSourceBuilder).query(QueryBuilders.matchAllQuery())
    val searchRequest = new SearchRequest(docIndexName).source(searchSourceBuilder)
    client.search(searchRequest, defaultRequestOptions)
  }

  def convert(hit: SearchHit): Document = {
    val doc = new Document(hit.getId)
    val value = hit.getSourceAsMap.get(filenameAttributName).asInstanceOf[String]
    doc.setSource(new DocumentStringSource(value))
    addElements(hit, doc)
    doc
  }

  private def addElements(docHit: SearchHit, doc: Document): Unit = {

    val docId = doc.getId
    val searchSourceBuilder = (new SearchSourceBuilder).query(QueryBuilders.matchQuery(docIdAttributName, docId))
    val searchRequest = new SearchRequest(elementIndexName).source(searchSourceBuilder)
    val searchResponse = client.search(searchRequest, defaultRequestOptions)

    val hits = searchResponse.getHits
    for (hit <- hits.getHits) {
      addElement(hit, doc)
    }
  }

  private def addElement(elemHit: SearchHit, doc: Document): Unit = {
    val text = elemHit.getSourceAsMap.get(textAttributeName).asInstanceOf[String]
    val paragraph = Paragraph(text)
    doc.elementCreated(paragraph)
  }

  def saveDocument(doc: Document): Unit = {

    val indexRequest = createIndexRequestForDocument(doc)
    client.index(indexRequest, RequestOptions.DEFAULT)

    // refresh
    refreshForDocumentIndex
  }

  private def createIndexRequestForDocument(doc: Document): IndexRequest = {
    val jsonMap = new java.util.HashMap[String, AnyRef]
    jsonMap.put(filenameAttributName, doc.getSource.name)
    val indexRequest = new IndexRequest(docIndexName, "doc", doc.getId)
    indexRequest.source(jsonMap)
    indexRequest
  }

  def saveDocElement(elem:DocElement, doc: Document): Unit = {
    // Paragraph
    // Create index request for storing doc
    val pMap = new java.util.HashMap[String, AnyRef]
    pMap.put(textAttributeName, elem.asInstanceOf[Paragraph].text)
    pMap.put(docIdAttributName, doc.getId)

    val indexRequest = new IndexRequest(elementIndexName, "doc", elem.id)
    indexRequest.source(pMap)

    // Run index request
    client.index(indexRequest, RequestOptions.DEFAULT)

    // refresh
    refreshForDocumentElementIndex
  }

  def refreshForDocumentIndex: RefreshResponse = {
    val refreshRequest = new RefreshRequest(docIndexName)
    client.indices().refresh(refreshRequest, defaultRequestOptions)
  }

  def refreshForDocumentElementIndex: RefreshResponse = {
    val refreshRequest = new RefreshRequest(elementIndexName)
    client.indices().refresh(refreshRequest, RequestOptions.DEFAULT)
  }

  private def defaultRequestOptions = RequestOptions.DEFAULT
}