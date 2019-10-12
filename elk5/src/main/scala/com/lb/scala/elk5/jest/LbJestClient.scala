package com.lb.scala.elk5.jest

import com.google.gson.GsonBuilder
import io.searchbox.client.{JestClient, JestClientFactory}
import io.searchbox.client.config.HttpClientConfig

class LbJestClient {

  private def   getJestClient(): JestClient = {
    val factory : JestClientFactory = new JestClientFactory()

    factory.setHttpClientConfig(new HttpClientConfig
    .Builder("http://127.0.0.1:9200")
      // .defaultCredentials(username,password)
      .gson(new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create())
      .multiThreaded(true)
      .readTimeout(10000)
      .build())
    factory.getObject
  }

}
