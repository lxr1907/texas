package com.lxrtalk.texas.texas.robot;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RobotManager {
    private static final Logger logger = LogManager.getLogger(RobotManager.class);

    /**
     * 机器人列表
     */
    private static final List<RobotWsClient> robotClientList = new CopyOnWriteArrayList<>();
    /**
     * 最多允许的机器人个数
     */
    public static final int MAX_ROBOT_COUNT = 2;
    public static ExecutorService executorService = Executors.newSingleThreadExecutor();

    public static void start(int number) {
        // 创建一个单线程的线程池
        executorService.submit(() -> {
            if (robotClientList.size() < MAX_ROBOT_COUNT) {
                for (int i = 0; i < number; i++) {
                    if (robotClientList.size() >= MAX_ROBOT_COUNT) {
                        break;
                    }
                    RobotWsClient client = new RobotWsClient(true);
                    robotClientList.add(client);
                    try {
                        // 修正 long 字面量，将 100l 改为 100L
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        // 建议使用日志记录异常
                        // 这里只是示例，实际使用时需要导入日志库并初始化 logger
                        // logger.error("Thread sleep interrupted", e);
                        logger.error("",e);
                    }
                }
            }
            // 关闭线程池
            executorService.shutdown();
        });
    }

}
