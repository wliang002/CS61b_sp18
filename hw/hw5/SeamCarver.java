import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {
    private Picture pic;
    private int height;
    private int width;

    public SeamCarver(Picture picture) {
        pic = new Picture(picture);
        height = picture.height();
        width = picture.width();
    }

    // current picture
    public Picture picture() {
        return new Picture(pic);
    }

    // width of current picture
    public int width() {
        return width;
    }

    // height of current picture
    public int height() {
        return height;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || x > width - 1 || y < 0 || y > height - 1) {
            throw new java.lang.IndexOutOfBoundsException();
        }
        Color left;
        Color right;
        Color top;
        Color bottom;

        if (x == 0) {
            left = pic.get(width - 1, y);
        } else {
            left = pic.get(x - 1, y);
        }

        if (x == pic.width() - 1) {
            right = pic.get(0, y);
        } else {
            right = pic.get(x + 1, y);
        }

        if (y == 0) {
            top = pic.get(x, height - 1);
        } else {
            top = pic.get(x, y - 1);
        }

        if (y == pic.height() - 1) {
            bottom = pic.get(x, 0);
        } else {
            bottom = pic.get(x, y + 1);
        }

        return calcEnergy(left, right) + calcEnergy(top, bottom);
    }

    private double calcEnergy(Color c1, Color c2) {
        double red = c1.getRed() - c2.getRed();
        double green = c1.getGreen() - c2.getGreen();
        double blue = c1.getBlue() - c2.getBlue();
        return red * red + green * green + blue * blue;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        Picture img = new Picture(pic.height(), pic.width());
        for (int i = 0; i < img.width(); i++) {
            for (int j = 0; j < img.height(); j++) {
                img.set(i, j, pic.get(j, i));
            }
        }
        int[] hSeam = new SeamCarver(img).findVerticalSeam();
        return hSeam;
    }



    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        int[] vSeam = new int[height];
        double[][] energyMatrix = calcEMatrix();
        int smallest = 0;
        double minVal = Double.MAX_VALUE;
        if (this.width == 1) {
            for (int i = 0; i < height; i++) {
                vSeam[i] = 0;
            }
            return vSeam;
        }

        for (int i = 0; i < width; i++) {
            if (energyMatrix[i][height - 1] < minVal) {
                minVal = energyMatrix[i][height - 1];
                smallest = i;
            }
        }
        vSeam[height - 1] = smallest;
        for (int i = height - 1; i > 0; i--) {
            smallest = vSeam[i];
            if (smallest == 0) {
                if (energyMatrix[smallest][i - 1] <= energyMatrix[smallest + 1][i - 1]) {
                    vSeam[i - 1] = smallest;
                } else if (energyMatrix[smallest][i - 1] > energyMatrix[smallest + 1][i - 1]) {
                    vSeam[i - 1] = smallest + 1;
                }
            } else if (smallest == width - 1) {
                if (energyMatrix[smallest - 1][i - 1] <= energyMatrix[smallest][i - 1]) {
                    vSeam[i - 1] = smallest - 1;
                } else if (energyMatrix[smallest][i - 1] < energyMatrix[smallest - 1][i - 1]) {
                    vSeam[i - 1] = smallest;
                }
            } else if (smallest <  width - 1) {
                double up = energyMatrix[smallest][i - 1];
                double upR = energyMatrix[smallest + 1][i - 1];
                double upL = energyMatrix[smallest - 1][i - 1];
                if (upL == Math.min(upL, Math.min(up, upR))) {
                    vSeam[i - 1] = smallest - 1;
                } else if (up == Math.min(up, Math.min(upL, upR))) {
                    vSeam[i - 1] = smallest;
                } else if (upR == Math.min(upR, Math.min(upL, up))) {
                    vSeam[i - 1] = smallest + 1;
                }
            }
        }
        return vSeam;
    }



    private double[][] calcEMatrix() {
        double[][] energyMatrix = new double[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (y == 0) {
                    energyMatrix[x][y] = energy(x, y);
                } else if (this.width == 1 && x - 1 >= 0) {
                    energyMatrix[x][y] = energy(x, y) + energyMatrix[x - 1][y];
                } else if (x == 0 && y - 1 >= 0 && x + 1 < width - 1) {
                    double up = energyMatrix[x][y - 1];
                    double upR = energyMatrix[x + 1][y - 1];
                    energyMatrix[x][y] = energy(x, y) + Math.min(up, upR);
                } else if (x == width - 1 && y - 1 >= 0 && x - 1 >= 0) {
                    double up = energyMatrix[x][y - 1];
                    double upL = energyMatrix[x - 1][y - 1];
                    energyMatrix[x][y] = energy(x, y) + Math.min(up, upL);
                } else if (x < width - 1 && y - 1 >= 0 && x - 1 >= 0) {
                    double upL = energyMatrix[x - 1][y - 1];
                    double up = energyMatrix[x][y - 1];
                    double upR = energyMatrix[x + 1][y - 1];
                    energyMatrix[x][y] = energy(x, y)
                            + Math.min(Math.min(upL, up), upR);
                }
            }
        }
        return energyMatrix;
    }

    // remove horizontal seam from picture
    public void removeHorizontalSeam(int[] seam) {
        if (seam.length < 0 || seam.length > this.width) {
            throw new IllegalArgumentException();
        }
        for (int i = 1; i < seam.length - 1; i++) {
            if (Math.abs(seam[i - 1] - seam[i]) > 1) {
                throw new IllegalArgumentException();
            }
        }
        pic = SeamRemover.removeHorizontalSeam(pic, seam);
        height = pic.height();
        width = pic.width();
    }

    // remove vertical seam from picture
    public void removeVerticalSeam(int[] seam) {
        if (seam.length < 0 || seam.length > this.height) {
            throw new IllegalArgumentException();
        }
        for (int i = 1; i < seam.length - 1; i++) {
            if (Math.abs(seam[i - 1] - seam[i]) > 1) {
                throw new IllegalArgumentException();
            }
        }
        pic = SeamRemover.removeVerticalSeam(pic, seam);
        height = pic.height();
        width = pic.width();
    }





}
