package com.lb.scala.akka.business.pipeline.eventbus

import akka.event.{EventBus, SubchannelClassification}
import akka.util.Subclassification

/**
  * @ClassName QuotesEventBus
  * @Description @TODO
  * @Author liubing
  * @Date 2019/11/30 12:05
  * @Version 1.0
  **/

case class Money(amount: BigDecimal){
  def this(amount: String)  = this (new java.math.BigDecimal(amount))

  amount.setScale(4, BigDecimal.RoundingMode.HALF_UP)
}

case class Market(name: String)
case class PriceQuoted(market: Market, ticker : Symbol, price: Money)


//object QuotesEventBus extends EventBus with SubchannelClassification{
//
//  type Classifier = Market
//  override type Event = this.type
//  override type Subscriber = this.type
//
//  override protected implicit def subclassification: Subclassification[Market] = ???
//
// override protected def classify(event: QuotesEventBus): Market = {}
//
//  override protected def publish(event: QuotesEventBus, subscriber: QuotesEventBus): Unit = {}
//}
