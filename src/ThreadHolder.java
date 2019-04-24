import java.util.concurrent.*;

import static java.lang.Thread.sleep;

public class ThreadHolder {
    int cores;
    TaskEnergy[] tasks;
    TaskMinCost[] costs;
    int[][] colors;
    int[][] energy;
    int[] distTo;
    int[] nodeTo;
    ExecutorService execEnergy;
    ExecutorService execMin;
    CyclicBarrier cb;
    private CountDownLatch countDownLatch;
    private CountDownLatch countDownLatch1;

    public ThreadHolder() {
        cores = Runtime.getRuntime().availableProcessors();
        cores = 3;
        System.out.println("CORES: " + cores);
        execEnergy = Executors.newFixedThreadPool(cores);
        execMin = Executors.newFixedThreadPool(cores);
        tasks = new TaskEnergy[cores];
        costs = new TaskMinCost[cores];
    }

    private void setTasksEnergy() {
        int section = colors.length / cores;

        for (int i = 0; i < cores; i++)
            tasks[i] = new TaskEnergy(countDownLatch, colors, energy);

        for (int i = 0; i < cores; i++) {
            tasks[i].setLowerBound(i * section);
            tasks[i].setUpperBound((i + 1) * section);
        }
        tasks[cores - 1].setUpperBound(colors.length);
    }

    private void setTasksMin() {
        int section = colors.length / cores;
        for (int i = 0; i < cores; i++)
            costs[i] = new TaskMinCost(colors, energy, nodeTo, distTo, cb, countDownLatch1);

        for (int i = 0; i < cores; i++) {
            costs[i].setLowerBound(i * section);
            costs[i].setUpperBound((i + 1) * section);
        }
        costs[cores - 1].setUpperBound(colors.length);
    }


    void calculateEnergy(int[][] _colors, int[][] _energy) {
        colors = _colors;
        energy = _energy;
        countDownLatch = new CountDownLatch(cores);
        setTasksEnergy();

        for (int i = 0; i < cores; i++) {
            execEnergy.execute(tasks[i]);
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
        }
    }

    void calculateCostTable(int[][] colors, int[][] energy, int[] distTO, int[] nodeTO) {
        this.colors = colors;
        this.energy = energy;
        this.distTo = distTO;
        this.nodeTo = nodeTO;
        countDownLatch1 = new CountDownLatch(cores);
        cb = new CyclicBarrier(cores);
        setTasksMin();
        for (int i = 0; i < cores; i++) {
            execMin.execute(costs[i]);
        }
        try {
            countDownLatch1.await();
        } catch (InterruptedException e) {
        }
    }

    void shutDown() {
        execEnergy.shutdown();
        execMin.shutdown();
    }

}
