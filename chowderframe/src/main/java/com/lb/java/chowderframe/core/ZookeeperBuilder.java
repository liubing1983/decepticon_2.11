package com.lb.java.chowderframe.core;

import com.lb.zookeeper.curator.ZkConnection;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperBuilder {

    static Logger log = LoggerFactory.getLogger(ZookeeperBuilder.class);

    private  ZkConnection zkc = null;
    private  CuratorFramework client = null;

    // 命名空间
    private String namespace;
    // IP:port
    private String ip;

    private ZookeeperBuilder(Builder builder) {
        namespace = builder.namespace;
        ip = builder.ip;
    }

    /**
     * 得到zk客户端
     * @return
     */
    public CuratorFramework getCuratorFrameworkClient(){
        if(client == null){
            zkc = new ZkConnection(namespace, ip);
            client = zkc.getZKConnection();
            log.info("Init  Zookeeper.");

            client.start();
            log.info("Zookeeper Curator Framework Start.");
        }
        return client;
    }




    public static final class Builder {
        private String namespace = "lb/guavacache";
        private String ip = "127.0.0.1:2181";

        public Builder() {
        }

        public Builder namespace(String val) {
            namespace = val;
            return this;
        }

        public Builder ip(String val) {
            ip = val;
            return this;
        }

        public ZookeeperBuilder build() {
            return new ZookeeperBuilder(this);
        }
    }
}
