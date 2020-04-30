/*
 * Copyright 2017 Datamountaineer.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datamountaineer.streamreactor.connect.ftp.source

import java.util

import com.datamountaineer.streamreactor.connect.ftp.source.SourceRecordProducers.SourceRecordProducer
import org.apache.kafka.connect.source.SourceRecord
import org.apache.kafka.connect.storage.OffsetStorageReader

import scala.collection.JavaConverters._

/**
  * Simple file converter. Writes the complete file into a single record
  * including the file attributes.
  */
class SimpleFileConverter(props: util.Map[String, String], offsetStorageReader : OffsetStorageReader)
  extends FileConverter(props, offsetStorageReader) {

  val cfg = new FtpSourceConfig(props)
  val metaStore = new ConnectFileMetaDataStore(offsetStorageReader)
  val recordConverter: SourceRecordConverter = cfg.sourceRecordConverter
  val recordMaker: SourceRecordProducer = cfg.keyStyle match {
    case KeyStyle.String => SourceRecordProducers.stringKeyRecord
    case KeyStyle.Struct => SourceRecordProducers.structKeyRecord
  }

  override def convert(topic: String, meta: FileMetaData, body: FileBody): Seq[SourceRecord] = {
    metaStore.set(meta.attribs.path, meta)
    recordConverter.convert(recordMaker(metaStore, topic, meta, body)).asScala
  }

  override def getFileOffset(path: String): Option[FileMetaData] = metaStore.get(path)
}
