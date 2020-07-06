
package com.example.concurrency.tools.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * @Description:
 * @Author: zhh
 * @Date: 2020/3/24
 */
public class SumTask extends RecursiveTask<Integer> {

    public static final int THRESHOLD = 2;
    private int start;
    private int end;

    public SumTask(int start, int end){
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int sum = 0;
        boolean canCompute = (end - start)<=THRESHOLD;
        if(canCompute){
            for (int i = start; i <= end; i++) {
                sum+=i;
            }
        }else{
            //如果任务大于阀值，就分个成两个子任务计算
            int middle = (start+end)/2;
            SumTask leftTask = new SumTask(start, middle);
            SumTask rightTask = new SumTask(middle+1, end);
            //执行子任务
            leftTask.fork();
            rightTask.fork();

            //等待子任务执行完，并得到计算结果
            int rightResult = rightTask.join();
            int leftResult = leftTask.join();
            sum = leftResult + rightResult;
        }
        return sum;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        SumTask sumTask = new SumTask(1, 4);
        ForkJoinTask<Integer> result = forkJoinPool.submit(sumTask);
        System.out.println(result.get());
    }
}
