import com.sun.jdi.IntegerValue;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import java.util.*;
import javax.imageio.ImageIO;

import static java.lang.Thread.sleep;


public class SeamCarver {
    private static int[][] colors;
    private static int[][] energyArr;
    private static ThreadHolder TH;

    public SeamCarver(Picture picture) {
        if (picture == null) throw new NullPointerException();
        colors = new int[picture.width()][picture.height()];
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                colors[i][j] = picture.get(i, j).getRGB();
            }
        }
        TH = new ThreadHolder();
    }

    public Picture picture() {
        Picture picture = new Picture(colors.length, colors[0].length);
        for (int i = 0; i < colors.length; i++) {
            for (int j = 0; j < colors[0].length; j++) {
                Color color = new Color(this.colors[i][j]);
                picture.set(i, j, color);
            }
        }
        return picture;
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

    public int energy(int x, int y) {
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

    public int[] findHorizontalSeam() {
        this.colors = transpose(this.colors);
        int h = colors[0].length;
        int w = colors.length;
        energyArr = new int[h][w];
//        Task task1 = new Task(0,colors[0].length);
//        task1.start(colors,energyArr);
//        while(task1.isJobDone==false){
//            try{sleep(10);}
//            catch(InterruptedException e){}
//        }
        TH.calculate(colors,energyArr);
//        Task task = new Task();
//        task.setLowerBound(0);
//        task.setUpperBound(colors[0].length);
//        task.start(colors,energyArr);
//        while(task.isJobDone==false){
//            try{sleep(10);}
//            catch(InterruptedException e){}
//        }
        int[] seam = findVerticalSeam();
        this.colors = transpose(this.colors);
        return seam;
    }


    public int[] findVerticalSeam() {
        int n = this.width() * this.height();
        int[] seam = new int[this.height()];
        int[] nodeTo = new int[n];
        int[] distTo = new int[n];
        for (int i = 0; i < n; i++) {
            if (i < width())
                distTo[i] = 0;
            else
                distTo[i] = Integer.MAX_VALUE;
        }
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                for (int k = -1; k <= 1; k++) {
                    if (j + k < 0 || j + k > this.width() - 1 || i + 1 < 0 || i + 1 > this.height() - 1) {
                        continue;
                    } else {
                        if (distTo[index(j + k, i + 1)] > distTo[index(j, i)] + energyArr[i][j]) {
                            distTo[index(j + k, i + 1)] = distTo[index(j, i)] + energyArr[i][j];
                            nodeTo[index(j + k, i + 1)] = index(j, i);
                        }
                    }
                }
            }
        }

        // find min dist in the last row
        int min = Integer.MAX_VALUE;
        int index = -1;
        for (int j = 0; j < width(); j++) {
            if (distTo[j + width() * (height() - 1)] < min) {
                index = j + width() * (height() - 1);
                min = distTo[j + width() * (height() - 1)];
            }
        }

        // find seam one by one
        for (int j = 0; j < height(); j++) {
            int y = height() - j - 1;
            int x = index - y * width();
            seam[height() - 1 - j] = x;
            index = nodeTo[index];
        }

        return seam;
    }
    void addVerticalSeam(int num){
        num = 900;
        int[][] updatedColor = new int [width()+num][height()];
        int seam[][] = new int[num][height()];
        int colleft[][] = new int [num][height()];
        int colright[][] = new int [num][height()];
        int r,g,b,rgbNewPixel;
        int tmp1,tmp2;
        for(int i =0; i < height(); i ++)
            for(int j =0; j <width();j++)
                updatedColor[j][i] = colors[j][i];
        for(int k=0; k<num;k++) {
            int h = colors[0].length;
            int w = colors.length;
            energyArr = new int[h][w];
            TH.calculate(colors,energyArr);
            seam[k] = findVerticalSeam();
            for (int i = 0; i < height(); i++) {
                int x = seam[k][i];

                if (x == 0) {
                    rgbNewPixel = (getRGB(x+1,i) + getRGB(x,i)) / 2;
                    r = (red(updatedColor[x + 1][ i]) + red(updatedColor[x][ i])) / 2;
                    r = r << 16;
                    g = (green(updatedColor[x + 1][ i]) + green(updatedColor[x][ i])) / 2;
                    g = g << 8;
                    b = (blue(updatedColor[x + 1][ i]) + blue(updatedColor[x][ i])) / 2;
                    colright[k][i] = r + g + b;
                    colleft[k][i] = updatedColor[x][i];
                } else if (x == width() - 1) {
                    rgbNewPixel = (getRGB(x - 1, i) + getRGB(x, i)) / 2;
                    r = (red(updatedColor[x - 1][ i]) + red(updatedColor[x][ i])) / 2;
                    r = r << 16;
                    g = (green(updatedColor[x - 1][ i]) + green(updatedColor[x][ i])) / 2;
                    g = g << 8;
                    b = (blue(updatedColor[x - 1][ i]) + blue(updatedColor[x][ i])) / 2;
                    colleft[k][i] = r + g + b;
                    colright[k][i] = updatedColor[x][i];
                } else {
                    r = (red(updatedColor[x - 1][ i]) + red(updatedColor[x][ i])) / 2;
                    r = r << 16;
                    g = (green(updatedColor[x - 1][ i]) + green(updatedColor[x][ i])) / 2;
                    g = g << 8;
                    b = (blue(updatedColor[x - 1][ i]) + blue(updatedColor[x][ i])) / 2;
                    colleft[k][i] = r + g + b;
                    rgbNewPixel = (getRGB(x + 1, i) + getRGB(x, i)) / 2;
                    r = (red(updatedColor[x + 1][ i]) + red(updatedColor[x][ i])) / 2;
                    r = r << 16;
                    g = (green(updatedColor[x + 1][ i]) + green(updatedColor[x][ i])) / 2;
                    g = g << 8;
                    b = ((blue(updatedColor[x + 1][ i])) + blue(updatedColor[x][ i])) / 2;
                    colright[k][i] = r + g + b;
                }
                colors[x][i] = rgbNewPixel;

                if(x!=0){
                    tmp2=updatedColor[x][i];
                    updatedColor[x-1][i] = colleft[k][i];
                    updatedColor[x][i] = colright[k][i];
                    for(int j=x+1;j<width()+num-1;j++) {
                        tmp1=updatedColor[j][i];
                        updatedColor[j][i] = tmp2;
                        tmp2=tmp1;
                    }
                    updatedColor[width()+num-1][i]=tmp2;
                    }
                    else{
                    tmp2=updatedColor[x+1][i];
                    updatedColor[x][i] = colleft[k][i];
                    updatedColor[x+1][i] = colright[k][i];
                    for(int j=x+2;j<width()+num-1;j++) {
                        tmp1=updatedColor[j][i];
                        updatedColor[j][i] = tmp2;
                        tmp2=tmp1;
                    }
                updatedColor[width()+num-1][i]=tmp2;
                }

//                    if(x!=width()+num - 1)
//                        tmp2=updatedColor[x+1][i];
//                    else tmp2 =0;
//                    updatedColor[x][i] = colleft[k][i];
//                    updatedColor[x + 1][i] = colright[k][i];
//
//                    for(int j=x+2;j<width()+num-1;j++) {
//                        tmp1=updatedColor[j][i];
//                        updatedColor[j][i] = tmp2;
//                        tmp2=tmp1;
//                    }
//                    updatedColor[width()+num-1][i]=tmp2;


            }
        }
//        for(int k=0; k<num;k++) {
//            for (int i = 0; i < height(); i++) {
//
//                int x = seam[k][i];
//                if(x!=width()+num - 1)
//                    tmp2=updatedColor[x+1][i];
//                else tmp2 =0;
//                updatedColor[x][i] = colleft[k][i];
//                updatedColor[x + 1][i] = colright[k][i];
//
//                for(int j=x+2;j<width()+num-1;j++) {
//                    tmp1=updatedColor[j][i];
//                    updatedColor[j][i] = tmp2;
//                    tmp2=tmp1;
//                }
//                updatedColor[width()+num-1][i]=tmp2;
//            }
//        }

        colors = updatedColor;
        }

    public void removeHorizontalSeam(int[] seam) {
        if (height() <= 1) throw new IllegalArgumentException();
        if (seam == null) throw new NullPointerException();
        if (seam.length != width()) throw new IllegalArgumentException();

        for (int i = 0; i < seam.length; i++) {
            if (seam[i] < 0 || seam[i] > height() - 1)
                throw new IllegalArgumentException();
            if (i < width() - 1 && Math.pow(seam[i] - seam[i + 1], 2) > 1)
                throw new IllegalArgumentException();
        }

        int[][] updatedColor = new int[width()][height() - 1];
        for (int i = 0; i < seam.length; i++) {
            if (seam[i] == 0) {
                System.arraycopy(this.colors[i], seam[i] + 1, updatedColor[i], 0, height() - 1);
            } else if (seam[i] == height() - 1) {
                System.arraycopy(this.colors[i], 0, updatedColor[i], 0, height() - 1);
            } else {
                System.arraycopy(this.colors[i], 0, updatedColor[i], 0, seam[i]);
                System.arraycopy(this.colors[i], seam[i] + 1, updatedColor[i], seam[i], height() - seam[i] - 1);
            }

        }
        this.colors = updatedColor;
    }

    public void removeVerticalSeam(int[] seam) {
        this.colors = transpose(this.colors);
        removeHorizontalSeam(seam);
        this.colors = transpose(this.colors);
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

    private int index(int x, int y) {
        return width() * y + x;
    }

    private int[][] transpose(int[][] origin) {
        if (origin == null) throw new NullPointerException();
        if (origin.length < 1) throw new IllegalArgumentException();
        int[][] result = new int[origin[0].length][origin.length];
        for (int i = 0; i < origin[0].length; i++) {
            for (int j = 0; j < origin.length; j++) {
                result[i][j] = origin[j][i];
            }
        }
        return result;
    }

    public static String createDir(String path, String picName){
        String dirName = picName;
        String dirPath = path + "OUTPUT/" + dirName;
        File theDir = new File(dirPath);
        if (!theDir.exists()) {
            System.out.println("creating directory: " + theDir.getName());
            boolean result = false;
            try{
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if(result) {
                System.out.println("DIR \"" + path + "OUTPUT/" + dirName + "\" created");
            }
        }else{
            System.out.println("DIR \"" + path + "OUTPUT/" + dirName + "\" exists");
        }
        return dirPath;
    }
    public void increaseMode(String path, Picture picture, SeamCarver sc, String ... Pname){}
    public static void ROFLmode(String path, Picture picture, SeamCarver sc, String ... pName){
        // ROFL mode :
        String picName;
        if(pName.length == 0) picName = "A" + System.nanoTime() / 10000000;
        else picName = pName[0] + "_" + System.nanoTime() / 1000000000;
        String dirPath = createDir(path,picName);

        int times = Math.min(picture.height(),picture.width()) - 100;
        times = 1;
        System.out.println("TIMES: " +  times);
        Picture ptmp;
        for (int i = 0; i < times; i++) {
            if(i%2==0) System.out.println(i);
            int h = colors[0].length;
            int w = colors.length;
            energyArr = new int[h][w];
            TH.calculate(colors,energyArr);
//            Task task = new Task();
//            task.setLowerBound(0);
//            task.setUpperBound(colors[0].length);
//            task.start(colors,energyArr);
//            while(task.isJobDone==false){
//                try{sleep(10);}
//                catch(InterruptedException e){}
//            }
//            int[] seam = sc.findVerticalSeam();
            sc.addVerticalSeam(150);
           // sc.removeVerticalSeam(seam);


            //seam = sc.findHorizontalSeam();
            //sc.removeHorizontalSeam(seam);


            if(i%10==0){
                ptmp = new Picture(sc.width(),sc.height());
                for (int k = 0; k < sc.width(); k++) {
                    for (int j = 0; j < sc.height(); j++) {
                        ptmp.setRGB(k,j,sc.getRGB(k,j));
                    }
                }
                long tmp = System.nanoTime()/10000000;
                ptmp.save(dirPath + "/" + picName + "_"+ tmp + ".png");
            }
        }
    }

    public static void normalMode(String path, Picture picture, SeamCarver sc, String picName){ // not finished!!
        int times = Math.abs(picture.width() - picture.height());
        System.out.println("TIMES: " +  times);
        if(picture.width() > picture.height()){
            // go vertical
            for (int i = 0; i < times; i++) {
                if(i%50==0)System.out.println(i);
                int[] verticalSeam = sc.findVerticalSeam();
                sc.removeVerticalSeam(verticalSeam);
            }
        }else{
            // go horizontal
            for (int i = 0; i < times; i++) {
                if(i%50==0)System.out.println(i);
                int[] verticalSeam = sc.findHorizontalSeam();
                sc.removeHorizontalSeam(verticalSeam);
            }
        }


        // saving picture :
        System.out.println(sc.colors.length + "x" + sc.colors[0].length);

        Picture p = new Picture(sc.width(),sc.height());
        for (int i = 0; i < sc.width(); i++) {
            for (int j = 0; j < sc.height(); j++) {
                p.setRGB(i,j,sc.getRGB(i,j));
            }
        }

        long time = System.nanoTime()/10000000;
        String saveFile = path + picName + "_" + time + ".png";
        System.out.println("Saved picture: " + saveFile);
        sc.picture().save(saveFile);
    }


    // array of pictures:
//        for(int z = 1; z <= 13;z++){
//            Picture picture = new Picture(path + z +  "low." + type);
//            SeamCarver sc = new SeamCarver(picture);
//            System.out.println(path + z +  "low." + type);
//            int times = Math.min(picture.height(),picture.width()) - 75;
//            System.out.println("TIMES: " +  times);
//            Picture ptmp;
//            for(int i = 0; i < times;i++){
//                if(i%50==0) System.out.println(i);
//                int[] seam = sc.findVerticalSeam();
//                sc.removeVerticalSeam(seam);
//
//                seam = sc.findHorizontalSeam();
//                sc.removeHorizontalSeam(seam);
//
//
//                if(i%5==0){
//                    ptmp = new Picture(sc.width(),sc.height());
//                    for(int k = 0; k < sc.width();k++){
//                        for(int j = 0; j < sc.height();j++){
//                            ptmp.setRGB(k,j,sc.getRGB(k,j));
//                        }
//                    }
//                    long tmp = System.nanoTime()/10000000;
//                    ptmp.save(path + "ROFL/" + picName + tmp + ".png");
//
//                }
//
//
//            }
//
//
//        }


    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    public static Picture resizeImage(Picture picture, String path){
        String picName = "pic";
        long time = System.nanoTime()/10000000;
        String saveFile = path + picName + "_" + time + ".png";
        picture.save(saveFile);


        BufferedImage image = null;
        // READ IMAGE
        try {
            File input_file = new File(saveFile); //image file path
            image = new BufferedImage(picture.width(), picture.height(), BufferedImage.TYPE_INT_ARGB);

            image = ImageIO.read(input_file);

            System.out.println("Reading complete.");
        } catch(IOException e) {
            System.out.println("Error: "+e);
        }

        float newWidth = 600;
        float newHeight = picture.height() * newWidth / picture.width();
        Image newImage = image.getScaledInstance((int)newWidth, (int)newHeight, Image.SCALE_FAST);
        image = toBufferedImage(newImage);

        // WRITE IMAGE
        try {
            // Output file path
            File output_file = new File(saveFile);

            // Writing to file taking type and path as
            ImageIO.write(image, "png", output_file);

            System.out.println("Writing complete.");
        } catch(IOException e) {
            System.out.println("Error: "+e);
        }

        picture = new Picture(saveFile);
        // deleting file
        File file = new File(saveFile);
        file.delete();
        return picture;
    }

    public static Picture getPicture(int mode, String path, String picName, String type){
        Picture picture;
        if(mode > 0 ){
            picture = new Picture(path + picName + "." + type);
        }else{
            picture = new Picture("https://thispersondoesnotexist.com/image");
        }
        return picture;
    }

    public static void main(String[] args) {
        String path = "_pics/";
        long time;
        String saveFile;
        long startTime = System.nanoTime();
        int mode = 1; // 1 - svou picture, 0 - random
        String picName = "pic"; // picName input from console
        String picType = "png";
        if(mode == 1){
            Scanner in = new Scanner(System.in);
            System.out.println("Enter picture name:");
            picName = in.nextLine();
            System.out.println("Enter picture type(png, jpg):");
            picType = in.nextLine();
        }
        for (int i = 0; i < 1; i++) {
            Picture picture = getPicture(mode, path, picName, picType);
            if (mode == 0) picture.save(path + picName + "." + picType);


            // CREATING DIR : createDir(path,picName);


//        picture =new Picture(path + picName + "." + type);

            System.out.println(picture.width() + "x" + picture.height());
            //picture = resizeImage(picture, path);
            System.out.println(picture.width() + "x" + picture.height());

            SeamCarver sc = new SeamCarver(picture);


            // normal mode : // not finished ( no creating directory)
//        if(mode == 1){
//            normalMode(path, picture,sc, picName);
//        }else{
//            normalMode(picture,sc);
//        }

            // ROFL MODE :
            if (mode == 1) {
                ROFLmode(path, picture, sc, picName);
            } else {
                ROFLmode(path, picture, sc);
            }


            long endTime = System.nanoTime();
            long totalTime = (endTime - startTime) / 1000000000;
            System.out.println();
            System.out.println("TIME: " + totalTime + " seconds");
        }


//        p.show();

    }

}
