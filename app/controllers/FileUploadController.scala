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
package controllers

import java.nio.file.{Files, Paths}

import com.google.inject.Inject
import models.DocumentViewModel
import org.tap.framework.DocumentPathSource
import org.tap.framework.filesystem.FileHandling.FileReference
import org.tap.framework.parser.tika.DocumentParserTika
import play.api.mvc.{BaseController, ControllerComponents}

class FileUploadController @Inject() (val controllerComponents: ControllerComponents) extends BaseController {

  def upload = Action(parse.multipartFormData) { request =>
    request.body.file("fileToImport").map { fileToImport =>
      val filename = fileToImport.filename
      val physicalFilename = System.currentTimeMillis() + "__" + fileToImport.filename
      if (Option(physicalFilename).exists(_.trim.nonEmpty)) {
        val tmpFolder = FileReference.findTempFolder().toFile.getAbsolutePath
        val path = Paths.get(s"$tmpFolder/$physicalFilename")
        fileToImport.ref.moveTo(path.toFile, replace = true)
        fileToImport.ref.deleteOnExit()
        val pathSource = new DocumentPathSource(path)
        val doc = (new DocumentParserTika).parse(pathSource)

        Files.deleteIfExists(path)
        Ok(views.html.index(filename, DocumentViewModel(doc)))
      } else {
        Redirect(routes.FileUploadController.error(s"Missing file named '$filename'"))
      }
    }.get
  }

  def error(msg: String) = Action {
    Ok(views.html.error(s"TAP error: $msg"))
  }
}
