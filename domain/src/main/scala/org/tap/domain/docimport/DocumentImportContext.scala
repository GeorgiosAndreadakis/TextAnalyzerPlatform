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
package org.tap.domain.docimport

import org.tap.domain.DocumentRepository

/**
  * Defines the collaborators, a repository and parser, for the document import.
  *
  * @author Georgios Andreadakis (georgios@andreadakis-consulting.de)
  */
trait DocumentImportContext {
  def repository: DocumentRepository
  def parser: DocumentParser
}