package com.lb.scala.akka.http

import akka.Done
import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.server.HttpApp
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Route
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer

import scala.io.StdIn
import scala.language.postfixOps
import scala.util.Try

case class HttpMessageLB(s: String)

class HelloAkkaHttp  extends  Actor{

  /**
    * 处理业务逻辑
    * @return
    */
  override def receive: Receive = {
    case  hm: HttpMessageLB => println(hm.s)
  }

}

object HelloAkkaHttp extends HttpApp {

  implicit val system = ActorSystem("HelloAkkaHttp")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val ref = system.actorOf(Props[HelloAkkaHttp].withRouter(new RoundRobinPool(10)))

  def main(args: Array[String]): Unit = {

    val bindingFuture = Http().bindAndHandle(routes, "localhost", 8080)

    println(s"Server online at http://localhost:8080/ \n Press RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done

  }

  override protected def postServerShutdown(attempt: Try[Done], system: ActorSystem): Unit = {
    super.postServerShutdown(attempt, system)
  }

  override protected def routes: Route  =  {
    path("demo"){
      get {
        parameter("system".as[String]) { system =>
          ref ! HttpMessageLB(system)
          complete(s"http://127.0.0.1:8080/hello,http://127.0.0.1:8080/abc")
        }
      }
    } ~
    path("hello") {
      put {
        parameter("name".as[String], "age".as[Int]) { (name, age) =>
          println(s"""$name, $age""")
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Say hello to akka-http</h1>"))
        }
      } ~
      get {
        ref ! HttpMessageLB("akka actor")
        println("hello")
        // complete("a1,b2")
        complete("a1,b2, 第一层测试系统返回")
      } ~
      post{
        parameter("name".as[String], "age".as[Int])

        println("sfsadfsfdsf--=-=-=")
        complete("")
      }~
      delete{
        parameter("name"){ a => println(a)
          complete("delete ok")
        }

      }
    }~
    path("abc"){
      get {
        println("abc")
        complete("第二层测试系统返回")
      }
    }
  }
}
