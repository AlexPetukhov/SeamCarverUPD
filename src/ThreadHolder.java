import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class ThreadHolder {
    Task[] tasks;
    int cores;
    int[][] colors;
    int[][] energy;
    ExecutorService exec;
    private CountDownLatch countDownLatch;
    public ThreadHolder(){
        cores = Runtime.getRuntime().availableProcessors();
        exec = Executors.newFixedThreadPool(cores);
        tasks = new Task[cores];
    }

    private void setTasks(){
        int section = colors.length / cores;

        for(int i=0; i < cores; i++)
            tasks[i] = new Task(countDownLatch,colors, energy);

        for(int i=0; i < cores; i++)
        {
            tasks[i].setLowerBound(i*section);
            tasks[i].setUpperBound((i+1)*section);
        }
        tasks[cores-1].setUpperBound(colors.length);
    }
    void calculate(int[][] _colors, int[][] _energy){
        colors = _colors;
        energy = _energy;
        countDownLatch = new CountDownLatch(cores);
        setTasks();

        for(int i=0; i < cores; i++) {
            exec.execute(tasks[i]);
        }
        try{countDownLatch.await();
        }catch (InterruptedException e){}
    }
    void shutDown(){
        exec.shutdown();
    }

}
