//import edu.princeton.cs.algs4.*;
//import java.awt.Color;
//import java.lang.*;
//
//public class SeamCarver {
//    private Picture pic, newPic;
//    //    private int [][] blocked = new int[pic.width()][pic.height()];
//    private int [] ways;
//    private double [][] energy;
//
//    public SeamCarver(Picture picture)                // create a seam carver object based on the given picture
//    {
//        pic = picture;
//        newPic = picture;
//        int w = picture.width();
//        int [] W = {w, w - 1, w + 1};
//        ways = W;
//        energy = new double[pic.width()][pic.height()];
//    }
//    public Picture picture()                          // current picture
//    {
//        return pic;
//    }
//    public int width()                            // width of current picture
//    {
//        return  pic.width();
//    }
//    public int height()                           // height of current picture
//    {
//        return pic.height();
//    }
//    public void calcEnergy(){
//        for(int x = 0; x < width(); x++) for(int y = 0; y < height(); y++) energy[x][y] = energy(x,y);
//    }
//    public int energy(int x, int y)               // energy of pixel at column x and row y
//    {
//        // x:
//        Color right = pic.get(Math.min(x + 1,width() - 1) ,y);
//        Color left = pic.get(Math.max(x - 1,0) ,y);
//        int diffRed = right.getRed() - left.getRed();
//        int diffGreen = right.getGreen() - left.getGreen();
//        int diffBlue = right.getBlue() - left.getBlue();
//        int diffColorX = diffBlue * diffBlue + diffGreen * diffGreen + diffRed * diffRed;
//        // y:
//        Color up = pic.get(x,Math.max(y - 1, 0));
//        Color down = pic.get(x, Math.min(y + 1, height() - 1));
//        diffRed = up.getRed() - down.getRed();
//        diffGreen = up.getGreen() - down.getGreen();
//        diffBlue = up.getBlue() - down.getBlue();
//        int diffColorY = diffBlue * diffBlue + diffGreen * diffGreen + diffRed * diffRed;
//
//        int energy = diffColorY + diffColorX;
//        return  energy;
//
//    }
//    public   int[] findVerticalSeam() {                 // sequence of indices for vertical seam
//        int start = height() * width();
//        int finish = height() * width() + 1;
//        Digraph g = new Digraph(width()*height() + 2);
//        for(int j = 0;j < height();j++) {
//            for (int i = 0;i < width();i++) {
//                if(j == height() - 1){
//                    g.addEdge(i + (height() - 1) * width(), finish);
//                }else{
//                    if(j == 0) g.addEdge(start, i);
//                    int v = i + j * width();
//                    for(int k = 0;k < 3;k++){
//                        int w = ways[k] + v;
//                        if(Math.abs(v % width() - w % width()) > 1)continue;
//                        g.addEdge(v,w);
//                    }
//                }
//            }
//        }
//
//        DSP dsp = new DSP(g,height()*width());
//        int[] prevVertex = dsp.prevVertexInShPath;
//
//        int[] vseam = new int[height()];
//
//        for(int i = 0; i < height(); i++) {
//            int prev = prevVertex[finish];
//            int prevX = prev % width(); // % width()
//            int prevY = prev / width(); // == height()-1
//            vseam[prevY] = prevX;
//            finish = prev;
//        }
//        return vseam;
//    }
//
//    public class DSP {
//        public int[] prevVertexInShPath;
//        private double[] distTo;
//        private IndexMinPQ<Double> pq;
//
//        public DSP(Digraph g,int s) {
//            prevVertexInShPath = new int[g.V()];
//            distTo = new double[g.V()];
//            pq = new IndexMinPQ<>(g.V());
//            for(int v = 0 ; v < g.V() ; v++)  distTo[v] = Double.POSITIVE_INFINITY;
//            distTo[s] = 0.0;
//            pq.insert(s, 0.0);
//            while (!pq.isEmpty()) {
//                int v = pq.delMin();
//                for ( int w : g.adj(v) ){
//                    relax(v, w);
//                }
//            }
//        }
//
//        private void relax(int v, int w) {
//            double weight = 0;
//            if(w < height() * width()){
//                int x = w % width();
//                int y = w / width();
//                weight = energy[x][y];
//            }
//            if(distTo[w] > distTo[v] + weight) {
//                distTo[w] = distTo[v] + weight;
//                prevVertexInShPath[w] = v;
//                if(pq.contains(w)) pq.decreaseKey(w, distTo[w]);
//                else pq.insert (w, distTo[w]);
//            }
//        }
//    }
//
//    public void removeVerticalSeam(int[] seam)     // remove vertical seam from current picture
//    {
//        Picture temp = new Picture(width() - 1, height());
//
//        for(int y = 0; y < height(); y++){
//            int x = 0;
//            for(;x < seam[y]; x++) temp.set(x, y, pic.get(x, y));
//            x++;
//            for(;x < width(); x++) temp.set(x - 1, y, pic.get(x, y));
//        }
//        pic = temp;
//        ways[0] = width();
//        ways[1] = width() - 1;
//        ways[2] = width() + 1;
//        calcEnergy();
//    }
//    public static void main(String[] args) {        // unit testing
//        long startTime = System.nanoTime();
//        String picName = "chameleon";
//        Picture p = new Picture(picName + ".png");
//        SeamCarver sc = new SeamCarver(p);
//        sc.calcEnergy();
//        int times = sc.width() - sc.height();
//        if(times > 0){
//            System.out.println("times :" + times);
//            while(times-- > 0){
//                int [] seam = sc.findVerticalSeam();
//                sc.removeVerticalSeam(seam);
//                System.out.println("Size :" + sc.width() + " x " + sc.height());
//            }
//        }
//        sc.picture().save(picName + "_edited.png");
//        long endTime   = System.nanoTime();
//        long totalTime = (endTime - startTime)/1000000000;
//        System.out.println(totalTime);
//        sc.picture().show();
//    }
//}
//
