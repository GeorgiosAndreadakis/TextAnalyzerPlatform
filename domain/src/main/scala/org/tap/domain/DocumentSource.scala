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
package org.tap.domain

import java.io.InputStream

/**
  * Informations about the source of a document to be parsed.
  *
  * @author Georgios Andreadakis (georgios@andreadakis-consulting.de)
  */
trait DocumentSource {
  def inputStream: InputStream
  def name: String
  def sourceType: String
}

class DocumentSourceInfo(nameInfo: String, typeInfo:String) extends DocumentSource {
  override def inputStream: InputStream = null
  override def name: String = nameInfo
  override def sourceType: String = typeInfo
}
