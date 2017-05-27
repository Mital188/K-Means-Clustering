import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.Arrays;
import java.util.Random;


public class KMeans {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
            return;
        }
        try {
            BufferedImage originalImage = ImageIO.read(new File(args[0]));
            int k = Integer.parseInt(args[1]);
            BufferedImage kmeansJpg = kmeans_helper(originalImage, k);
            ImageIO.write(kmeansJpg, "jpg", new File(args[2]));

            File original_image = new File(args[0]);
            File compressed_image = new File(args[2]);
            double compression_ratio = (double)(compressed_image.length())/(double) (original_image.length());

            System.out.println("Original File size : " + original_image.length() + " Bytes");
            System.out.println("Compressed File size : " + compressed_image.length() + " Bytes");
            System.out.println("Compression ratio : " + compression_ratio);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k) {
        int w = originalImage.getWidth();
        int h = originalImage.getHeight();
        BufferedImage kmeansImage = new BufferedImage(w, h, originalImage.getType());
        Graphics2D g = kmeansImage.createGraphics();
        g.drawImage(originalImage, 0, 0, w, h, null);
        // Read rgb values from the image
        int[] rgb = new int[w * h];
        int count = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                rgb[count++] = kmeansImage.getRGB(i, j);
            }
        }
        // Call kmeans algorithm: update the rgb values
        kmeans(rgb, k);

        // Write the new rgb values to the image
        count = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                kmeansImage.setRGB(i, j, rgb[count++]);
            }
        }
        return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k){

        int[] centroids = new int[k];
        Random r = new Random();

        //Randomly picking out unique centroids from the rgb array
        for(int i=0;i<k;i++){
            int rand = r.nextInt(rgb.length);
            boolean repeat = true;
            if(i==0){
                centroids[i] = rgb[rand];
            } else{
                while(repeat){
                    rand = r.nextInt(rgb.length);
                    for(int j = 0; j<i;j++){
                        if(j == i-1 && centroids[j] !=rgb[rand]){
                            centroids[i] = rgb[rand];
                            repeat = false;
                        }else if(centroids[j]==rgb[rand]) {
                            j=i;
                        }
                    }
                }
            }
        }
        System.out.print("Initial K values : ");
        System.out.println(Arrays.toString(centroids));



        int[] assignment = clustering(centroids,rgb,k);
        //updating the rgb array with the updated cluster centroid value
        for(int i = 0; i < rgb.length;i++){
            rgb[i]=centroids[assignment[i]];
        }
        System.out.print("Final K values : ");
        System.out.println(Arrays.toString(centroids));
    }

    private static int[] clustering(int[] centroids,int[] rgb, int k){

        int[] cluster_class = new int[rgb.length]; // Assigning centroid to each rgb value
        int[] cluster_value = new int[k];
        int[] Total_red = new int[k];
        int[] Total_green = new int[k];
        int[] Total_blue = new int[k];
        int num_iterations = 100; // Total number of iterations
        int k_cluster; // K for each pixel in the rgb array
        int euclidean_red,euclidean_green,euclidean_blue; // distance for each color from the centroids
        double minimum_distance;
        double euclidean_distance;
        Color picture;
        Color mid_point;


        while (num_iterations > 0) {
            // initial all the arrays to 0
            Arrays.fill(Total_red,0);
            Arrays.fill(Total_green,0);
            Arrays.fill(Total_blue,0);
            Arrays.fill(cluster_value,0);

            for (int i = 0; i < rgb.length; i++) {

                minimum_distance = Double.MAX_VALUE; // Setting the minimum distance to a maximum value
                k_cluster = 0;
                picture = new Color(rgb[i]);

                // compare instance's RGB value to each cluster point
                for (int j = 0; j < k; j++) {
                    mid_point = new Color(centroids[j]);
                    euclidean_red = (picture.getRed() - mid_point.getRed());
                    euclidean_green = (picture.getGreen() - mid_point.getGreen());
                    euclidean_blue = (picture.getBlue() - mid_point.getBlue());
                    
                    euclidean_distance = Math.sqrt( euclidean_red * euclidean_red + euclidean_green * euclidean_green + euclidean_blue * euclidean_blue);
                    if (euclidean_distance < minimum_distance) {
                        minimum_distance = euclidean_distance;
                        k_cluster = j;
                    }
                }
                // Assign pixel to cluster
                cluster_class[i] = k_cluster;
                cluster_value[k_cluster]++;
                // Calculating the total values for each color, to calculate the average to be assigned to the cluster.

                Total_red[k_cluster] += picture.getRed();
                Total_green[k_cluster] += picture.getGreen();
                Total_blue[k_cluster] += picture.getBlue();
            }
            int red,green,blue;
            // update previous assignments list
            for (int i = 0; i < k; i++) {
                red = Total_red[i] / cluster_value[i];
                green = Total_green[i] / cluster_value[i];
                blue = Total_blue[i] / cluster_value[i];
                // Converting individual rgb value into a single int value.
                centroids[i] = new Color(red,green,blue).getRGB();
            }
            num_iterations--;
        }

     return cluster_class;
    }

}