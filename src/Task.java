public class Task implements Runnable{
    private Thread t;
    private int upperBound;
    private int lowerBound;
    int[][] colors;
    int[][] energy;
    boolean isJobDone = false;

//    Task(int _lower,int _upper){
//        lowerBound = _lower;
//        upperBound = _upper;
//    }
    public void setUpperBound(int _upperBound){
        upperBound = _upperBound;
    }
    public void setLowerBound(int _lowerBound){
        lowerBound = _lowerBound;
    }
    public void run(){
        for(int i=lowerBound; i < upperBound; i++)
            for(int j=0; j< width(); j++)
                energy[i][j] = energyCalc(j,i); //energy[1][0]
        isJobDone = true;
    }
    public int energyCalc(int x, int y) {
        if (x < 0 || x > this.width() - 1 || y < 0 || y > this.height() - 1) {
            throw new IndexOutOfBoundsException();
        }
        if (x == 0 || x == this.width() - 1 || y == 0 || y == this.height() - 1) {
            return 1000000;
        } else {
            int deltaXRed = red(colors[x - 1][y]) -
                    red(colors[x + 1][y]);
            int deltaXGreen = green(colors[x - 1][y]) -
                    green(colors[x + 1][y]);
            int deltaXBlue = blue(colors[x - 1][y]) -
                    blue(colors[x + 1][y]);

            int deltaYRed = red(colors[x][y - 1]) - red(colors[x][y + 1]);
            int deltaYGreen = green(colors[x][y - 1]) - green(colors[x][y + 1]);
            int deltaYBlue = blue(colors[x][y - 1]) - blue(colors[x][y + 1]);

            return deltaXRed * deltaXRed + deltaXBlue * deltaXBlue + deltaXGreen * deltaXGreen + deltaYRed * deltaYRed + deltaYBlue * deltaYBlue + deltaYGreen * deltaYGreen;
        }

    }

    private int red(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    private int green(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    private int blue(int rgb) {
        return (rgb >> 0) & 0xFF;
    }

    public void start(int[][] _colors, int[][] _energy){
        colors = _colors;
        energy = _energy;
        if (t == null) {
            t = new Thread(this);
            t.start ();
        }
    }
    public int getRGB(int x, int y) {
        return colors[x][y];
    }

    public int width() {
        return this.colors.length;
    }

    public int height() {
        return this.colors[0].length;
    }

}
