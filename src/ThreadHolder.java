import static java.lang.Thread.sleep;

public class ThreadHolder {
    Task[] tasks;
    int cores;
    int[][] colors;
    int[][] energy;
    public ThreadHolder(){
        cores = Runtime.getRuntime().availableProcessors();
        tasks = new Task[cores];
        for(int i=0; i < cores; i++)
            tasks[i] = new Task();
    }

    private void setTasks(){
        int height = colors[0].length;
        int section = height / cores;

        for(int i=0; i < cores; i++)
            tasks[i] = new Task();
        for(int i=0; i < cores; i++)
        {
            tasks[i].setLowerBound(i*section);
            tasks[i].setUpperBound(i*section+section);
        }
        tasks[cores-1].setUpperBound(height);
    }
    void calculate(int[][] _colors, int[][] _energy){
        colors = _colors;
        energy = _energy;
        setTasks();
        for(int i=0; i < cores; i++) {
            tasks[i].start(_colors, _energy);
        }
        boolean status = true;
        while(true){
            status = true;
            for(int i=0; i < cores; i++)
                if(tasks[i].isJobDone == false)
                    status = false;
            if(status == true)
                break;
            try{sleep(10);}
            catch(InterruptedException e){}
        }
    }

}
