package imgProcess;

import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Session;
import org.tensorflow.Tensor;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;


public class Processing {


    static{

        nu.pattern.OpenCV.loadLocally();

    }

    public ArrayList<double[]> pixels = new ArrayList<>();

    public static void main(String[] args) throws Exception {

        Mat in = Imgcodecs.imread("C:\\Users\\Thinkpad\\Pictures\\obvod.jpg");
        Imgproc.cvtColor(in, in, Imgproc.COLOR_RGB2GRAY);

        loadModel(in);

    }

    private static Mat toGrayScale(File image) {


        String path = image.getAbsolutePath();
        Mat source = Imgcodecs.imread(path);

        Mat destination = new Mat();

        // Converting the image to gray scale and
        // saving it in the dst matrix
        Imgproc.cvtColor(source, destination, Imgproc.COLOR_RGB2GRAY);

        return destination;

    }

    public static Image processImage(File image) {

        Mat source = toGrayScale(image);

        //binarizing the image
        int adapt = Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
        int thresh = Imgproc.THRESH_BINARY_INV;
        Imgproc.adaptiveThreshold(source, source, 225, adapt, thresh, 15, 2);


        source = divideImg(source);

        MatOfByte byteMat = new MatOfByte();
        Imgcodecs.imencode(".bmp", source, byteMat);


        return new Image(new ByteArrayInputStream(byteMat.toArray()));


    }


    public static float[] matToFloatArray(Mat mat) {
        float[] result = new float[mat.rows() * mat.cols() * mat.channels()];
        int index = 0;
        for (int row = 0; row < mat.rows(); row++) {
            for (int col = 0; col < mat.cols(); col++) {
                double[] pixel = mat.get(row, col);
                for (int channel = 0; channel < mat.channels(); channel++) {
                    result[index++] = (float) pixel[channel];
                }
            }
        }
        return result;
    }

    public static Mat divideImg(Mat image) {

        //loopin
        double white = 225.0;
        Mat finished = new Mat();
        image.copyTo(finished);
        double part_width = finished.width()/5;
        double part_height = finished.height()/5;
        System.out.println(image.cols());
        for (double i = 0; i < image.rows(); i += part_height) {
            for (double j = 0; j < image.cols(); j += part_width) {
                Rect roi = new Rect((int) j, (int)i, (int)part_width, (int)part_height);
                Mat predImg = image.submat(roi).clone();
                Imgproc.rectangle(finished, new Point(j, i), new Point(j + part_width, i + part_height), new Scalar(255.0, 255.0, 255.0));
                HighGui.imshow("test", finished);
                float[] input = matToFloatArray(predImg);

            }
        }

        return finished;
    }



    private static byte[] convertMat(Mat img){

        float[] pixels = new float[(int) img.total() * img.channels()];
        img.get(0, 0, pixels);

        float[] mean = new float[img.channels()];
        for (int i = 0; i < pixels.length; i++) {
            mean[i] += pixels[i];
        }
        for (int i = 0; i < mean.length; i++) {
            mean[i] /= (img.total());
        }

        float[] std = new float[img.channels()];
        for (int i = 0; i < pixels.length; i++) {
            std[i] += Math.pow(pixels[i] - mean[i], 2);
        }for (int i = 0; i < std.length; i++) {
            std[i] = (float) Math.sqrt(std[i] / (img.total()));
        }

        float[] normalizedPixels = new float[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            normalizedPixels[i] = (pixels[i] - mean[i]) / std[i];
        }

        ByteBuffer inputBuffer = ByteBuffer.allocateDirect(normalizedPixels.length * 4)
                .order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = inputBuffer.asFloatBuffer();
        floatBuffer.put(normalizedPixels);

        byte[] byteArray = new byte[inputBuffer.remaining()];

        return byteArray;

    }


    public static void loadModel(Mat img) throws Exception {
        SavedModelBundle smb = SavedModelBundle.load(String.valueOf(Paths.get("src/main/java/imgProcess")), "serve");
        Session s = smb.session();


        if(img.cols() != 28 || img.rows() != 28){
            Imgproc.resize(img, img, new Size(28, 28));
        }

        FloatBuffer fb = FloatBuffer.allocate(784);
        byte[] input = convertMat(img);

        for (byte b :
                input) {
            fb.put((float) (b & 0xFF) / 255.0f);
        }
        fb.rewind();

        float[] keep_prob_arr = new float[1024];
        Arrays.fill(keep_prob_arr, 1f);

        Tensor inputTensor = Tensor.create(new long[] {784}, fb);
        Tensor outputTensor = Tensor.create(new long[] {1, 1024}, FloatBuffer.wrap(keep_prob_arr));

        Tensor result = s.runner()
                .feed("input_tensor", inputTensor)
                .feed("output_tensor", outputTensor)
                .fetch("output")
                .run().get(0);

        float[][] m = new float[1][15];
//        m[0] = new float[10];
//        Arrays.fill(m[0], 0);

        float[][] matrix = (float[][]) result.copyTo(m);
        float maxVal = 0;
        int inc = 0;
        int predict = -1;
        for (float val :
                matrix[0]) {
            if(val > maxVal){
                predict = inc;
                maxVal = val;
            }
            inc++;
        }
        System.out.println(predict);






    }








}
