
/*
 * Copyright 2020 Lenses.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.lenses.streamreactor.connect.aws.s3.sink.conversion

import org.apache.kafka.connect.sink.SinkRecord

import scala.collection.JavaConverters._

object HeaderToStringConverter {

  def apply(record: SinkRecord): Map[String, String] = record.headers().asScala.map(header => header.key() -> headerValueToString(header.value())).toMap

  def headerValueToString(value: Any): String = {
    value match {
      case stringVal: String => stringVal
      case intVal: Int => String.valueOf(intVal)
      case shortVal: Short => String.valueOf(shortVal)
      case floatVal: Float => String.valueOf(floatVal)
      case doubleVal: Double => String.valueOf(doubleVal)
      case byteVal: Byte => String.valueOf(byteVal)
      case boolVal: Boolean => String.valueOf(boolVal)
      case longVal: Long => String.valueOf(longVal)
      case otherVal => throw new IllegalArgumentException(s"Unsupported header value type $otherVal:${otherVal.getClass.getCanonicalName}. Consider if you need to set the header.converter property in your connector configuration.")
    }
  }

}
