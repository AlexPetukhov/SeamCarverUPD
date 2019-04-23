import com.sun.jdi.IntegerValue;
import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.awt.image.BufferedImage;
import java.io.*;
import java.awt.*;
import java.util.*;
import javax.imageio.ImageIO;


public class SeamCarver {
    private int[][] colors;
    private int[][] Energy;

    public SeamCarver(Picture picture) {
        if (picture == null) throw new NullPointerException();
        colors = new int[picture.width()][picture.height()];
        Energy = new int[picture.width()][picture.height()];
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                colors[i][j] = picture.get(i, j).getRGB();
            }
        }
    }

    private int getAvgRGB(int pix1, int pix2) {
        int red = (red(pix1) + (red(pix2))) / 2;
        red = red << 16;
        int green = (green(pix1) + (green(pix2))) / 2;
        green = green << 8;
        int blue = (blue(pix1) + blue(pix2)) / 2;
        int finalPix = red + green + blue;
        return finalPix;
    }

    void addVerticalSeam(int num) {
        int[][] updatedColor = new int[width() + num][height()];
        int seam[];
        int leftpix; //left pixel to be inserted
        int rightpix; // right one
        int rgbNewPixel;
        int tmp1, tmp2;
        for (int i = 0; i < height(); i++)
            for (int j = 0; j < width(); j++)
                updatedColor[j][i] = colors[j][i];
        for (int k = 0; k < num; k++) {
            seam = findVerticalSeam();
            for (int i = 0; i < height(); i++) {
                int x = seam[i];

                if (x == 0) { //find averages for left and right pixels
                    rgbNewPixel = (getRGB(x + 1, i) + getRGB(x, i)) / 2;
                    rightpix = getAvgRGB(updatedColor[x + 1][i], updatedColor[x][i]);
                    leftpix = updatedColor[x][i];
                } else if (x == width() - 1) {
                    rgbNewPixel = (getRGB(x - 1, i) + getRGB(x, i)) / 2;
                    leftpix = getAvgRGB(updatedColor[x - 1][i], updatedColor[x][i]);
                    rightpix = updatedColor[x][i];
                } else {
                    rgbNewPixel = (getRGB(x + 1, i) + getRGB(x, i)) / 2;
                    leftpix = getAvgRGB(updatedColor[x - 1][i], updatedColor[x][i]);
                    rightpix = getAvgRGB(updatedColor[x + 1][i], updatedColor[x][i]);
                }
                colors[x][i] = rgbNewPixel;

                if (x != 0) { // insert pixels
                    tmp2 = updatedColor[x][i];
                    updatedColor[x - 1][i] = leftpix;
                    updatedColor[x][i] = rightpix;
                    for (int j = x + 1; j < width() + num - 1; j++) {
                        tmp1 = updatedColor[j][i];
                        updatedColor[j][i] = tmp2;
                        tmp2 = tmp1;
                    }
                    updatedColor[width() + num - 1][i] = tmp2;
                } else {
                    tmp2 = updatedColor[x + 1][i];
                    updatedColor[x][i] = leftpix;
                    updatedColor[x + 1][i] = rightpix;
                    for (int j = x + 2; j < width() + num - 1; j++) { // shift remaining pixels
                        tmp1 = updatedColor[j][i];
                        updatedColor[j][i] = tmp2;
                        tmp2 = tmp1;
                    }
                    updatedColor[width() + num - 1][i] = tmp2;
                }
            }
        }
        colors = updatedColor;
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
//        if (x < 0 || x > this.width() - 1 || y < 0 || y > this.height() - 1) {
//            throw new IndexOutOfBoundsException();
//        }
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

    public void  calcEnergy(){
//        System.out.println(this.Energy.length + " _ " + this.Energy[0].length);
//        System.out.println(this.colors.length + " _ " + this.height());
        for(int i = 0; i < this.width(); i++){
            for(int j = 0; j < this.height(); j++){
                this.Energy[i][j] = energy(i,j);
            }
        }
    }

    public int[] findHorizontalSeam() {
        this.colors = transpose(this.colors);
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
        int index = 0;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                int energ = energy(j, i);
                int ind2 = index(j, i);
                int value = distTo[ind2] + energ;
                for (int k = -1; k <= 1; k++) {
                    if (!(j + k < 0 || j + k > this.width() - 1 || i + 1 > this.height() - 1)) {
                        int ind = index(j + k, i + 1);
                        if (distTo[ind] > value) {
                            distTo[ind] = value;
                            nodeTo[ind] = ind2;
                        }
                    }
                }
                if (i == height() - 1) {
                    // find min dist in the last row
                    if (distTo[j + width() * (height() - 1)] < min) {
                        index = j + width() * (height() - 1);
                        min = distTo[j + width() * (height() - 1)];
                    }
                }
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

    public int[][] TESTfindHorizontalSeam(int mult){
        this.colors = transpose(this.colors);
        this.Energy = transpose(this.Energy);
        int[][] seam = TESTfindVerticalSeam(mult);
        this.colors = transpose(this.colors);
        this.Energy = transpose(this.Energy);
        return seam;
    }

    public int[][] TESTfindVerticalSeam(int mult) {
        int n = this.width() * this.height();
        int[][] seam = new int[this.height()][mult];
        calcEnergy();
        // ENERGY(pixel) == -1 <=> pixel is blocked!!!
        for(int curSeam = 0; curSeam < mult; curSeam++) {
            int[] nodeTo = new int[n];
            int[] distTo = new int[n];

            for (int i = 0; i < n; i++) distTo[i] = Integer.MAX_VALUE;

            int index = 0;
            int min = Integer.MAX_VALUE;

            for(int i = 0; i < width();i++){ // top row distTO = Energy
                int ind = index(i,height() - 1);
                distTo[ind] = Energy[i][height() - 1];

            }
            int f = 0;
            for(int j = height() - 2; j >= 0;j--){
                for(int i = 0; i < width();i++){
                    if(Energy[i][j] == -1) continue;
                    int ind = index(i, j);
                    for (int k = -1; k <= 1; k++) {
                        // [i + k][j - 1]
                        if(i + k >= 0 && i + k < width() && j + 1 < height() && Energy[i + k][j + 1] != -1){
                            int indTO = index(i + k, j + 1);
                            if(distTo[indTO] == Integer.MAX_VALUE) continue;
                            if(distTo[indTO] + Energy[i][j] < distTo[ind]){
                                distTo[ind] = distTo[indTO] + Energy[i][j];
                                nodeTo[ind] = indTO;
                            }
                        }
                    }
                }
            }

            // find min dist in the bottom row
            for(int i = 0; i < width();i++){
                // j = 0
                int ind = index(i,0);
                if(distTo[ind] < min){
                    min = distTo[ind];
                    index = ind;
                }
            }

            // find seam one by one from bot to top
            for(int j = 0; j < height(); j++){
                int y = j;
                int x = index - y * width();
                seam[y][curSeam] = x;
                index = nodeTo[index];
                Energy[x][y] = -1;
            }
        }

        return seam;
    }

    public void removeHorizontalSeam(int[] seam) {
//        if (height() <= 1) throw new IllegalArgumentException();
//        if (seam == null) throw new NullPointerException();
//        if (seam.length != width()) throw new IllegalArgumentException();
//
//        for (int i = 0; i < seam.length; i++) {
//            if (seam[i] < 0 || seam[i] > height() - 1)
//                throw new IllegalArgumentException();
//            if (i < width() - 1 && Math.pow(seam[i] - seam[i + 1], 2) > 1)
//                throw new IllegalArgumentException();
//        }

        int[][] updatedColor = new int[width()][height() - 1];
        for (int i = 0; i < seam.length; i++) { // seam.length = width
            if (seam[i] == 0) {
                System.arraycopy(this.colors[i], 1, updatedColor[i], 0, height() - 1);
            } else if (seam[i] == height() - 1) {
                System.arraycopy(this.colors[i], 0, updatedColor[i], 0, height() - 1);
            } else {
                System.arraycopy(this.colors[i], 0, updatedColor[i], 0, seam[i]);
                System.arraycopy(this.colors[i], seam[i] + 1, updatedColor[i], seam[i], height() - seam[i] - 1);
            }
        }
        this.colors = updatedColor;
    }

    public void TESTremoveHorizontalSeam(int[][] seam, int mult) {
        int [][] updColor = new int[width()][height() - mult];
        // seam[width][mult]
//        System.out.println(seam.length);
        for(int i = 0; i < seam.length; i++){ // seam.length cycle (i < seam.length)
            // height cycle
            // seam[i] = int[mult]
            int [] col = new int[mult];
            col = seam[i];
            Arrays.sort(col);
            int last = 0;
            int updLast = 0;
            for(int j = 0; j < mult; j++){
                if(col[j] - last > 0){
                    System.arraycopy(this.colors[i], last, updColor[i], updLast, col[j] - last);
                }
                updLast += col[j] - last;
                last = col[j] + 1;
                if(col[j] == height() - 1)break;
            }
            if(last < height()){
                System.arraycopy(this.colors[i], last, updColor[i], updLast, height() - last);
            }
        }
        this.colors = updColor;
    }

    public void removeVerticalSeam(int[] seam) {
        this.colors = transpose(this.colors);
        removeHorizontalSeam(seam);
        this.colors = transpose(this.colors);
    }

    public void TESTremoveVerticalSeam(int[][] seam,int mult) {
        this.colors = transpose(this.colors);
        TESTremoveHorizontalSeam(seam,mult);
        this.colors = transpose(this.colors);
    }

    private int red(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    private int green(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    private int blue(int rgb) {
        return (rgb) & 0xFF;
    }

    private int index(int x, int y) {
        return width() * y + x;
    }

    private int[][] transpose(int[][] origin) {
//        if (origin == null) throw new NullPointerException();
//        if (origin.length < 1) throw new IllegalArgumentException();
        int[][] result = new int[origin[0].length][origin.length];
        for (int i = 0; i < origin[0].length; i++) {
            for (int j = 0; j < origin.length; j++) {
                result[i][j] = origin[j][i];
            }
        }
        return result;
    }

    public static String createDir(String path, String dirName, int... mode) {
        String dirPath;
        if (mode.length > 0) {
            // need to create OUTPUT directory
            dirPath = path + "OUTPUT";
            File theDir = new File(dirPath);
            if (!theDir.exists()) {
                System.out.println("creating directory: " + theDir.getName());
                boolean result = false;
                try {
                    theDir.mkdir();
                    result = true;
                } catch (SecurityException se) {
                    //handle it
                }
                if (result) {
                    System.out.println("DIR \"" + dirPath + "\" created");
                }
            }
        } else {
            dirPath = path + "OUTPUT/" + dirName;
            File theDir = new File(dirPath);
            if (!theDir.exists()) {
                System.out.println("creating directory: " + theDir.getName());
                boolean result = false;
                try {
                    theDir.mkdir();
                    result = true;
                } catch (SecurityException se) {
                    //handle it
                }
                if (result) {
                    System.out.println("DIR \"" + dirPath + "\" created");
                }
            } else {
                System.out.println("DIR \"" + dirPath + "\" exists");
            }
        }
        return dirPath;
    }

    public static void ROFLmode(String path, Picture picture, SeamCarver sc, long startTime, String... pName) {
        // ROFL mode :
        String picName;
        if (pName.length == 0) picName = "A" + System.nanoTime() / 10000000;
        else picName = pName[0] + "_" + System.nanoTime() / 1000000000;
        String dirPath = createDir(path, picName);

        System.out.println("Finished creating directory time: " + (System.nanoTime() - startTime) / 1000000000 + " seconds");
        int times = Math.min(picture.height(), picture.width()) - 100;
        System.out.println("TIMES: " + times);
        Picture ptmp;
        for (int i = 0; i < times; i++) {
            if (i % 50 == 0) System.out.println(i);

            int[] seam = sc.findVerticalSeam();
            sc.removeVerticalSeam(seam);

            seam = sc.findHorizontalSeam();
            sc.removeHorizontalSeam(seam);


            if (i % 5 == 0) {
                ptmp = new Picture(sc.width(), sc.height());
                for (int k = 0; k < sc.width(); k++) {
                    for (int j = 0; j < sc.height(); j++) {
                        ptmp.setRGB(k, j, sc.getRGB(k, j));
                    }
                }
                long tmp = System.nanoTime() / 10000000;
                ptmp.save(dirPath + "/" + picName + "_" + tmp + ".png");
            }
        }
    }

    public static void normalMode(String path, Picture picture, SeamCarver sc, String picName) { // not finished!!
        int times = Math.abs(picture.width() - picture.height());
        System.out.println("TIMES: " + times);
        // making picture quadratic
        if (picture.width() > picture.height()) {
            // go vertical
            for (int i = 0; i < times; i++) {
                if (i % 50 == 0) System.out.println(i);
                int[] verticalSeam = sc.findVerticalSeam();
                sc.removeVerticalSeam(verticalSeam);
            }
        } else {
            // go horizontal
            for (int i = 0; i < times; i++) {
                if (i % 50 == 0) System.out.println(i);
                int[] verticalSeam = sc.findHorizontalSeam();
                sc.removeHorizontalSeam(verticalSeam);
            }
        }

        // saving picture :
        System.out.println("Size of the picture after carving: " + sc.colors.length + "x" + sc.colors[0].length);

        Picture p = new Picture(sc.width(), sc.height());
        for (int i = 0; i < sc.width(); i++) {
            for (int j = 0; j < sc.height(); j++) {
                p.setRGB(i, j, sc.getRGB(i, j));
            }
        }

        long time = System.nanoTime() / 10000000;
        String saveFile = path + picName + "_" + time + ".png";
        System.out.println("Saving picture: " + saveFile);
        sc.picture().save(saveFile);
    }

    public static void TESTnormalMode(String path, Picture picture, SeamCarver sc, String picName){ // doing sqrt(times) seams at one time
        int times = Math.abs(picture.width() - picture.height());
        int mult = (int)Math.sqrt(times); // number of seams to be removed at one time
//        mult = 50;
        System.out.println("Full times: " + times);
        times = times / mult;
        System.out.println("TEST MODE!");
        System.out.println("Actuall times: " + times + " MULT: " + mult);

        // making picture quadratic
        if (picture.width() > picture.height()) {
            // go vertical
            for (int i = 0; i < times / mult; i++) {
                if (i % 5 == 0) System.out.println(i);
                int[][] seam = sc.TESTfindVerticalSeam(mult);
                sc.TESTremoveVerticalSeam(seam, mult);
            }
        } else {
            // go horizontal
            for (int i = 0; i < times; i++) {
                if (i % 5 == 0) System.out.println(i);
                int[][] seam = sc.TESTfindHorizontalSeam(mult);
                sc.TESTremoveHorizontalSeam(seam, mult);
            }
        }

        // saving picture :
        System.out.println("Size of the picture after carving: " + sc.colors.length + "x" + sc.colors[0].length);

        Picture p = new Picture(sc.width(), sc.height());
        for (int i = 0; i < sc.width(); i++) {
            for (int j = 0; j < sc.height(); j++) {
                p.setRGB(i, j, sc.getRGB(i, j));
            }
        }

        long time = System.nanoTime() / 10000000;
        String saveFile = path + picName + "_" + time + ".png";
        System.out.println("Saving picture: " + saveFile);
        sc.picture().save(saveFile);
    }

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

    public static Picture resizeImage(Picture picture, String path, float... wid) {
        System.out.println("Staring to resize image");
        String picName = "pic";
        long time = System.nanoTime() / 10000000;
        String saveFile = path + picName + "_" + time + ".png";
        picture.save(saveFile);


        BufferedImage image = null;
        // READ IMAGE
        try {
            File input_file = new File(saveFile); //image file path
            image = new BufferedImage(picture.width(), picture.height(), BufferedImage.TYPE_INT_ARGB);

            image = ImageIO.read(input_file);

            System.out.println("Reading complete.");
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
        float newWidth;
        if (wid.length == 0) newWidth = 600;
        else newWidth = wid[0];
        float newHeight = picture.height() * newWidth / picture.width();
        Image newImage = image.getScaledInstance((int) newWidth, (int) newHeight, Image.SCALE_FAST);
        image = toBufferedImage(newImage);

        // WRITE IMAGE
        try {
            // Output file path
            File output_file = new File(saveFile);

            // Writing to file taking type and path as
            ImageIO.write(image, "png", output_file);

            System.out.println("Writing complete.");
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }

        picture = new Picture(saveFile);
        // deleting file
        File file = new File(saveFile);
        file.delete();
        return picture;
    }

    public static Picture getPicture(int mode, String path, String picName, String type) {
        Picture picture;
        if (mode > 0) {
            picture = new Picture(path + picName + "." + type);
        } else {
            picture = new Picture("https://thispersondoesnotexist.com/image");
        }
        return picture;
    }

    public static void main(String[] args) {
        String path = "_pics/";

        int mode = 1; // 1 - svou picture, 0 - random
        int crop = 1; //1 - crop, 0 extend
        int resize = 1; // 1 - resize, 0 - keep origin size
        int TESTmode = 1; // 1 - TESTmode, 0 - not testing
        String picName = "pic"; // picName input from console
        String picType = "png";



//        if (mode == 1) {
//            Scanner in = new Scanner(System.in);
//            System.out.println("Enter picture name:");
//            picName = in.nextLine();
//            System.out.println("Enter picture type(png, jpg):");
//            picType = in.nextLine();
//        }
        picName = "_emillow";
        picType = "png";

        Picture picture = getPicture(mode, path, picName, picType);

        long startTime = System.nanoTime();
        if (mode == 0) picture.save(path + picName + "." + picType);


        System.out.println(picture.width() + "x" + picture.height());
//        if (resize == 1  { // need to resize if picture.width() > newWidth
        if(picture.width() > 700){
            picture = resizeImage(picture, path);
            System.out.println(picture.width() + "x" + picture.height());
            System.out.println("Resize time : " + (System.nanoTime() - startTime) / 1000000000 + " seconds");
        }

        createDir(path, picName, 1); // creating OUTPUT directory if needed
        SeamCarver sc = new SeamCarver(picture);


        // normal mode : // finished
//        normalMode(path, picture,sc, picName);

        if(TESTmode == 0){
            if (crop == 1) {
                // ROFL MODE :
                if (mode == 1) {
                    ROFLmode(path, picture, sc, startTime, picName);
                } else {
                    ROFLmode(path, picture, sc, startTime);
                }
            } else {
                int num = sc.width() / 5;
                sc.addVerticalSeam(num);
                long tmp = System.nanoTime() / 10000000;
                String name = picName + tmp;
                String dirPath = createDir(path, name);
                Picture ptmp = new Picture(sc.width(), sc.height());
                for (int k = 0; k < sc.width(); k++) {
                    for (int j = 0; j < sc.height(); j++) {
                        ptmp.setRGB(k, j, sc.getRGB(k, j));
                    }
                }
                ptmp.save(dirPath + "/" + name + ".png");
            }
        }else{
            TESTnormalMode(path, picture, sc, picName);
//            normalMode(path, picture, sc, picName);
        }


        long endTime = System.nanoTime();
        long totalTime = (endTime - startTime) / 100000000;
        System.out.println();
        System.out.println("TIME: " + totalTime/10 + "." + totalTime%10 + " seconds");
//        System.out.println(picture.width() + "x" + picture.height());
    }

}