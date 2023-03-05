package imgProcess;

import javafx.scene.image.Image;
import nu.pattern.OpenCV;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.tensorflow.*;
import java.io.*;
import java.nio.FloatBuffer;



public class Processing {



    private static Mat binarize(File image) {




        String path = image.getAbsolutePath();
        Mat source = Imgcodecs.imread(path);

        Mat destination = new Mat();

        // Converting the image to gray scale and
        // saving it in the dst matrix
        Imgproc.cvtColor(source, destination, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(destination, destination, new Size(15, 15), 0);

        //binarizing the image
        int adapt = Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
        int thresh = Imgproc.THRESH_BINARY_INV;
        Imgproc.adaptiveThreshold(destination, destination, 225, adapt, thresh, 15, 2);

        return destination;

    }

    public static Image processImage(File image) {



        try{
        Mat source = binarize(image);

        source = divideImg(source);

        MatOfByte byteMat = new MatOfByte();
        Imgcodecs.imencode(".bmp", source, byteMat);



        return new Image(new ByteArrayInputStream(byteMat.toArray()));

        } catch (Exception e){
            return null;
        }


    }

    public static Mat divideImg(Mat image) throws Exception {

        Mat finished = new Mat();
        image.copyTo(finished);
        double part_width = finished.width()/5;
        double part_height = finished.height()/5;
        System.out.println(image.cols());
        for (double i = 0; i < image.rows() - part_height; i += part_height) {
            for (double j = 0; j < image.cols() - part_width; j += part_width) {
                Rect roi = new Rect((int) j, (int)i, (int)part_width, (int)part_height);
                //dividing Image to equal squares
                Mat predImg = image.submat(roi).clone();
                Imgproc.rectangle(finished, new Point(j, i), new Point(j + part_width, i + part_height), new Scalar(255.0, 255.0, 255.0));
                //loadModel(predImg);


            }
        }

        return finished;
    }



    private static void loadModel(Mat image) throws IOException {
        SavedModelBundle model = SavedModelBundle.load("src/main/java/imgProcess/model", "serve");

        Session session = model.session();

        Imgproc.resize(image, image, new Size(299, 299));

        float[][][] temp = imgToArray(image);

        Tensor<Float> inputTensor = (Tensor<Float>) Tensor.create(temp);


        Tensor input = session.runner()
                .feed("input_1", inputTensor)
                .fetch("dense_1")
                .run()
                .get(0);

        System.out.println(input);

    }

    private static float[][][] imgToArray(Mat image){

        float[][][] imgArray = new float[299][299][3];

        for(int i = 0; i < image.rows(); i++){
            for(int y = 0; y < image.cols(); y++){
                if (image.get(i, y) == new double[]{255.0}) {
                    imgArray[i][y][0] = 255;
                    imgArray[i][y][1] = 255;
                    imgArray[i][y][2] = 255;
                }else{
                    imgArray[i][y][0] = 0;
                    imgArray[i][y][1] = 0;
                    imgArray[i][y][2] = 0;
                }
            }
        }

        return imgArray;

    }


    }
