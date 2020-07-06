
package com.example.concurrency.tools.forkjoin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @Description: 利用Fork/Join框架实现单词计数
 * @Author: zhh
 * @Date: 2020/3/24
 */
public class WordCountTask extends RecursiveTask<Map<String, Long>> {

    private String[] lines;
    int start;
    int end;

    public WordCountTask(String[] lines, int start, int end){
        this.lines = lines;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Map<String, Long> compute() {
        if((end - start) == 0){
            return map(lines[start]);
        }
        int middle = (start + end) / 2;
        WordCountTask task1 = new WordCountTask(lines, start, middle);
        WordCountTask task2 = new WordCountTask(lines, middle + 1, end);

        invokeAll(task1, task2);
        return reduce(task1.join(), task2.join());
    }

    private Map<String, Long> map(String line){
        String[] words = line.split(" ");
        Map<String, Long> wordsMap = new HashMap<>();
        for (String word : words) {
            Long count;
            if ((count = wordsMap.get(word)) != null){
                wordsMap.put(word, count + 1);
            }else{
                wordsMap.put(word, 1L);
            }
        }
        System.out.println(wordsMap);
        return wordsMap;
    }

    private Map<String, Long> reduce(Map<String, Long> wc1, Map<String, Long> wc2){
        for (String word : wc2.keySet()) {
            Long count;
            if((count = wc1.get(word)) != null){
                wc1.put(word, count + wc2.get(word));
            }else {
                wc1.put(word, wc2.get(word));
            }
        }
        return wc1;
    }

    public static void main(String[] args) {
        String[] lines = {
                "hello java idea hello",
                "hello python idea",
                "hello java idea static",
                "hello world idea public",
        };
        ForkJoinPool forkJoinPool = new ForkJoinPool(4);
        WordCountTask wordCountTask = new WordCountTask(lines, 0, lines.length - 1);
        Map<String, Long> result = forkJoinPool.invoke(wordCountTask);
        System.out.println(result);
    }

}
