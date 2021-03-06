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
package controllers

import com.google.inject.Inject
import javax.inject._
import models.DocumentViewModel
import org.tap.framework.persistence.elastic.DocumentRepositoryForElastic
import play.api.mvc._

@Singleton
class HomeController @Inject() (val controllerComponents: ControllerComponents)
  extends BaseController {

  def index = Action {
    Ok(views.html.index("", null, storedDocuments))
  }

  private def storedDocuments:List[DocumentViewModel] = {
    val repo = new DocumentRepositoryForElastic
    repo.allDocs.right.get.map( DocumentViewModel )
  }
}
