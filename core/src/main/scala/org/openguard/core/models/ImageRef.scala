package org.openguard.core.models

import java.sql.Timestamp

case class ImageRef(path: String, uploadTime: Timestamp, year: Int, month: Int, day: Int, hour: Int)