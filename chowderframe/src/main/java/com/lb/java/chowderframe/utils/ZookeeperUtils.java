package com.lb.java.chowderframe.utils;

import com.lb.java.chowderframe.core.ZookeeperBuilder;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZookeeperUtils {

    static Logger log = LoggerFactory.getLogger(ZookeeperUtils.class);

    private CuratorFramework client;

    public ZookeeperUtils(){
         client = new ZookeeperBuilder.Builder().build().getCuratorFrameworkClient();
    }

    /**
     * 根据路径从zk中查找数据
     *
     * @param key
     * @param <V>
     * @return
     * @throws Exception
     */
    public <V> V findZkPath(String key) throws Exception {
        log.info("Namespace/path:  /" + client.getNamespace() + "/" + key);
        return (V) new String(client.getData().forPath("/" + key));
    }


    public void close(CuratorFramework client) {
        client.close();
    }


    /**
     *
     * @param cacheUtils
     * @throws Exception
     */
    public void PathChildrenCacheWatcher(GuavaCacheUtils cacheUtils)  {

        // 注册监控
        PathChildrenCache cache = new PathChildrenCache(client, "/", true);
        // 启动监控
        try {
            cache.start();
        }catch (Exception e){
            log.error(e.getMessage());
        }

        // 监控回调函数
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) {
                PathChildrenCacheEvent.Type type = event.getType();

                if (type == PathChildrenCacheEvent.Type.CHILD_ADDED || type == PathChildrenCacheEvent.Type.CHILD_UPDATED) {
                    //  监控到新增或修改节点
                    log.info("--------------------");
                    log.info("key: "+ event.getData().getPath().replace("/", ""));
                    log.info("value: "+ new String(event.getData().getData()));
                    log.info("--------------------");
                    cacheUtils.putCallableCache(event.getData().getPath().replace("/", ""), new String(event.getData().getData()) );
                } else if (type == PathChildrenCacheEvent.Type.CHILD_REMOVED) {
                    // 监控到删除节点
                    cacheUtils.deleteCallableCache(event.getData().getPath().replace("/", ""));
                } else {
                    log.info(type.name());
                }
            }
        });

        try {
            while (true){
                Thread.sleep(1000 * 60);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }

    }
}




