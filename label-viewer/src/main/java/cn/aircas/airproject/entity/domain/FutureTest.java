package cn.aircas.airproject.entity.domain;

import java.awt.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class FutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {

       // Test.TestBuilder age = Test.builder().age(18);
        //Test test = new Test();

        System.out.println(2.1 % 1);


        FutureTest futureTest = new FutureTest();

        CompletableFuture<Integer> future1 = futureTest.testAppLy();
        System.out.println("-----------------------------------------");
        CompletableFuture<Integer> future = futureTest.testCombine();
    }

    /**
     * 当多个任务连接执行时候，可以使用thenApply方式提示执行效率
     * @return
     */
    public CompletableFuture<Integer> testAppLy() {
        long start = System.currentTimeMillis();
        System.out.println("开始：" + start);
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("我是异步结果获取1");
            return 5;
        });

        CompletableFuture<Integer> future1 = future.thenApply((p) -> {
            System.out.println("我是异步结果获取2");
            return p + 1;
        });

        System.out.println("结果：" + future1.join());
        System.out.println("耗时：" + (System.currentTimeMillis() - start));
        return future;
    }

    /**
     * 当多个任务连接执行时候，任务之间依赖不大，可以使用Combine方式提示执行效率
     * @return
     */
    public CompletableFuture<Integer> testCombine() {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("我是异步结果获取1");
            return 5;
        });

        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("我是异步结果获取2");
            return 6;
        });

        CompletableFuture<Integer> future3 = future.thenCombine(future1, (f1, f2) -> {
            System.out.println("我是联合任务3");
            return f1 + f2;
        });

        System.out.println("结果：" + future3.join());
        return future;
    }
}
