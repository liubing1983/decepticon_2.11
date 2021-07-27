package com.lb.java.hive.udaf;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;

/**
 * @ClassName HiveUdafDemo
 * @Description @TODO
 * @Author liubing
 * @Date 2020/4/27 15:33
 * @Version 1.0
 **/
public class HiveUdafDemo extends UDAF {
    public static class Evaluator implements UDAFEvaluator {

        private int mSum;
        public Evaluator() {
            super();
            init();
        }
        //初始化，初始值为0
        public void init() {
            mSum = 0;

        }
        //map阶段，返回值为boolean
        public boolean iterate(String o) {
            if (o != null) {
                mSum += 1;
            }
            return true;
        }
        // 类似于Combiner，不过只传给merge结果
        public int terminatePartial() {
            // This is SQL standard - sum of zero items should be null.
            return mSum;
        }
        // reduce 阶段，返回值为boolean
        public boolean merge(int o) {
            mSum +=o;

            return true;
        }
        // 返回聚合结果
        public int terminate() {
            // This is SQL standard - sum of zero items should be null.
            return mSum;
        }
    }
}
