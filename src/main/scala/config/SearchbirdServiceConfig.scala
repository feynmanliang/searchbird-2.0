package com.feynmanliang.searchbird
package config

import com.twitter.finagle.tracing.{Tracer, NullTracer}

class SearchbirdServiceConfig {
  var thriftPort: Int = 9999
  var tracerFactory: Tracer.Factory = NullTracer.factory
}
