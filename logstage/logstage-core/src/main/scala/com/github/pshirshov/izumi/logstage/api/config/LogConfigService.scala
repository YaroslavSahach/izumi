package com.github.pshirshov.izumi.logstage.api.config

import com.github.pshirshov.izumi.logstage.api.Log


trait LogConfigService extends AutoCloseable {
  def threshold(e: Log.LoggerId): Log.Level

  def config(e: Log.Entry): LogEntryConfig

}
