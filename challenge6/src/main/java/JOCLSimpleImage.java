/*
 * JOCL - Java bindings for OpenCL
 *
 * Copyright 2009-2019 Marco Hutter - http://www.jocl.org/
 */

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.CL_DEVICE_TYPE_ALL;
import static org.jocl.CL.CL_MEM_OBJECT_IMAGE2D;
import static org.jocl.CL.CL_MEM_READ_ONLY;
import static org.jocl.CL.CL_MEM_USE_HOST_PTR;
import static org.jocl.CL.CL_MEM_WRITE_ONLY;
import static org.jocl.CL.CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE;
import static org.jocl.CL.CL_QUEUE_PROFILING_ENABLE;
import static org.jocl.CL.CL_RGBA;
import static org.jocl.CL.CL_UNSIGNED_INT8;
import static org.jocl.CL.clBuildProgram;
import static org.jocl.CL.clCreateBuffer;
import static org.jocl.CL.clCreateCommandQueueWithProperties;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clCreateImage;
import static org.jocl.CL.clCreateKernel;
import static org.jocl.CL.clCreateProgramWithSource;
import static org.jocl.CL.clEnqueueNDRangeKernel;
import static org.jocl.CL.clEnqueueReadImage;
import static org.jocl.CL.clGetDeviceIDs;
import static org.jocl.CL.clGetDeviceInfo;
import static org.jocl.CL.clGetPlatformIDs;
import static org.jocl.CL.clSetKernelArg;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jocl.CL;
import org.jocl.Pointer;
import org.jocl.Sizeof;
import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;
import org.jocl.cl_image_desc;
import org.jocl.cl_image_format;
import org.jocl.cl_kernel;
import org.jocl.cl_mem;
import org.jocl.cl_platform_id;
import org.jocl.cl_program;
import org.jocl.cl_queue_properties;

/**
 * A simple example demonstrating image handling between JOCL and Swing. It shows an animation of a
 * rotating image, which is rotated using an OpenCL kernel involving some basic image operations.
 */
public class JOCLSimpleImage {
  /**
   * The source code of the kernel to execute. It will rotate the input image by the given angle and
   * write the result into the output image.
   */
  private static final String programSource = """
const sampler_t samplerIn =
    CLK_NORMALIZED_COORDS_FALSE |
    CLK_ADDRESS_CLAMP |
    CLK_FILTER_NEAREST;

const sampler_t samplerOut =
    CLK_NORMALIZED_COORDS_FALSE |
    CLK_ADDRESS_CLAMP |
    CLK_FILTER_NEAREST;


//__kernel void rotateImage(__read_only  image2d_t sourceImage, __write_only image2d_t targetImage, float angle)
//{
//    int gidX = get_global_id(0);
//    int gidY = get_global_id(1);
//    int2 pos = {gidX, gidY};
//
//    uint4 pixel = read_imageui(sourceImage, samplerIn, pos);
//    float4 color = convert_float4(pixel) / 255;
//    color.xyz = 0.2126*color.x + 0.7152*color.y + 0.0722*color.z;
//    pixel = convert_uint4_rte(color * 255);
//
//    write_imageui(targetImage, pos, pixel);
//}

float getGrayScale(uint4 pixel)
{
    float4 color = convert_float4(pixel) / 255;
    return 0.2126*color.x + 0.7152*color.y + 0.0722*color.z;
}
__kernel void convolute(__read_only image2d_t sourceImage, __write_only image2d_t targetImage, __constant float * xFilter, __constant float * yFilter)
{
    int gidX = get_global_id(0);
    int gidY = get_global_id(1);
    int2 pos = {gidX, gidY};
//    write_imageui(targetImage, pos, convert_uint4_rte(getGrayScale(read_imageui(sourceImage, samplerIn, pos)) * 255));
    
    
    // Starting from 0
    int width = get_image_width(sourceImage) - 1;
    int height = get_image_height(sourceImage) - 1;

    int fIndex = 0;
    float xChange = (float) 0.0;
    float yChange = (float) 0.0;
    int2 curPos;
    for (int y = -1; y <= 1; y++)
    {
        for (int x = -1; x <= 1; x++)
        {
            // Bounds check.
            int2 curPos = {max(min(width, gidX + x), 0), max(min(height, gidY + y), 0)};
            float pixel = getGrayScale(read_imageui(sourceImage, samplerIn, curPos));
            xChange += xFilter[fIndex] * pixel;
            yChange += yFilter[fIndex] * pixel;
            fIndex++;
        }
    }
    uint4 change = convert_uint4_rte(sqrt(hypot(xChange * 255, yChange * 255)));
    write_imageui(targetImage, pos, change);
}
      """;

  private final BufferedImage inputImage;
  private final BufferedImage outputImage;
  private final int imageSizeX;
  private final int imageSizeY;
  private cl_context context;
  private cl_command_queue commandQueue;
  private cl_kernel kernel;
  private cl_mem inputImageMem;
  private cl_mem outputImageMem;
  private cl_mem xMatrix;
  private cl_mem yMatrix;
  /** Creates the JOCLSimpleImage sample */
  public JOCLSimpleImage() {
    // Read the input image file and create the output images
    String fileName = "src/main/resources/data/lena512color.png";

    inputImage = createBufferedImage(fileName);
    imageSizeX = inputImage.getWidth();
    imageSizeY = inputImage.getHeight();

    outputImage = new BufferedImage(imageSizeX, imageSizeY, BufferedImage.TYPE_INT_RGB);

    // Create the panel showing the input and output images
    JPanel mainPanel = new JPanel(new GridLayout(2, 2));
    JLabel inputLabel = new JLabel(new ImageIcon(inputImage));
    mainPanel.add(inputLabel);
    JLabel outputLabel = new JLabel(new ImageIcon(outputImage));
    mainPanel.add(outputLabel);

    // Create the main frame
    JFrame frame = new JFrame("Circle detector");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new BorderLayout());
    frame.add(mainPanel, BorderLayout.CENTER);
    frame.pack();
    frame.setVisible(true);

    initCL();
    initImageMem();
    startAnimation(outputLabel);
  }

  /**
   * Entry point for this sample.
   *
   * @param args not used
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(JOCLSimpleImage::new);
  }

  /**
   * Creates a BufferedImage of with type TYPE_INT_RGB from the file with the given name.
   *
   * @param fileName The file name
   * @return The image, or null if the file may not be read
   */
  private static BufferedImage createBufferedImage(String fileName) {
    BufferedImage image;
    try {
      image = ImageIO.read(new File(fileName));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }

    int sizeX = image.getWidth();
    int sizeY = image.getHeight();

    BufferedImage result = new BufferedImage(sizeX, sizeY, BufferedImage.TYPE_INT_RGB);
    Graphics g = result.createGraphics();
    g.drawImage(image, 0, 0, null);
    g.dispose();
    return result;
  }

  /**
   * Starts the thread which will advance the animation state and call the animation method.
   *
   * @param outputComponent The component to repaint after each step
   */
  private void startAnimation(final Component outputComponent) {
    System.out.println("Starting animation...");
    Thread thread =
        new Thread(
            () -> {
              while (true) {
                sobelImage();
                outputComponent.repaint();
                try {
                  Thread.sleep(33);
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                  return;
                }
              }
            });
    thread.setDaemon(true);
    thread.start();
  }

  /** Initialize the OpenCL context, command queue and kernel */
  void initCL() {
    final int platformIndex = 0;
    final long deviceType = CL_DEVICE_TYPE_ALL;
    final int deviceIndex = 0;

    // Enable exceptions and subsequently omit error checks in this sample
    CL.setExceptionsEnabled(true);

    // Obtain the number of platforms
    int[] numPlatformsArray = new int[1];
    clGetPlatformIDs(0, null, numPlatformsArray);
    int numPlatforms = numPlatformsArray[0];

    // Obtain a platform ID
    cl_platform_id[] platforms = new cl_platform_id[numPlatforms];
    clGetPlatformIDs(platforms.length, platforms, null);
    cl_platform_id platform = platforms[platformIndex];

    // Initialize the context properties
    cl_context_properties contextProperties = new cl_context_properties();
    contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

    // Obtain the number of devices for the platform
    int[] numDevicesArray = new int[1];
    clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
    int numDevices = numDevicesArray[0];

    // Obtain a device ID
    cl_device_id[] devices = new cl_device_id[numDevices];
    clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
    cl_device_id device = devices[deviceIndex];

    // Create a context for the selected device
    context = clCreateContext(contextProperties, 1, new cl_device_id[] {device}, null, null, null);

    // Check if images are supported
    int[] imageSupport = new int[1];
    clGetDeviceInfo(
        device, CL.CL_DEVICE_IMAGE_SUPPORT, Sizeof.cl_int, Pointer.to(imageSupport), null);
    System.out.println("Images supported: " + (imageSupport[0] == 1));
    if (imageSupport[0] == 0) {
      System.out.println("Images are not supported");
      System.exit(1);
      return;
    }
    // Create a command-queue for the selected device
    cl_queue_properties properties = new cl_queue_properties();
    properties.addProperty(CL_QUEUE_PROFILING_ENABLE, 1);
    properties.addProperty(CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE, 1);
    commandQueue = clCreateCommandQueueWithProperties(context, device, properties, null);

    // Create the program
    System.out.println("Creating program...");
    cl_program program =
        clCreateProgramWithSource(context, 1, new String[] {programSource}, null, null);

    // Build the program
    System.out.println("Building program...");
    clBuildProgram(program, 0, null, null, null, null);

    // Create the kernel
    System.out.println("Creating kernel...");
    kernel = clCreateKernel(program, "convolute", null);
  }

  /** Initialize the memory objects for the input and output images */
  private void initImageMem() {
    // Create the memory object for the input- and output image
    DataBufferInt dataBufferSrc = (DataBufferInt) inputImage.getRaster().getDataBuffer();
    int[] dataSrc = dataBufferSrc.getData();

    cl_image_format imageFormat = new cl_image_format();
    imageFormat.image_channel_order = CL_RGBA;
    imageFormat.image_channel_data_type = CL_UNSIGNED_INT8;

    cl_image_desc imageDesc = new cl_image_desc();
    imageDesc.image_height = imageSizeY;
    imageDesc.image_width = imageSizeX;
    imageDesc.image_row_pitch = (long) imageSizeX * Sizeof.cl_uint;
    imageDesc.image_type = CL_MEM_OBJECT_IMAGE2D;

    inputImageMem =
        clCreateImage(
            context,
            CL_MEM_READ_ONLY | CL_MEM_USE_HOST_PTR,
            imageFormat,
            imageDesc,
            Pointer.to(dataSrc),
            null);

    outputImageMem = clCreateImage(context, CL_MEM_WRITE_ONLY, imageFormat, imageDesc, null, null);

    float[] xFloats = new float[] {-1, 0, 1, -2, 0, 2, -1, 0, 1};
    float[] yFloats = new float[] {-1, -2, -1,0, 0, 0,1, 2, 1};

    xMatrix = clCreateBuffer(context,
        CL_MEM_READ_ONLY | CL_MEM_USE_HOST_PTR, Sizeof.cl_float * 9, Pointer.to(xFloats), null);
    yMatrix = clCreateBuffer(context,
        CL_MEM_READ_ONLY | CL_MEM_USE_HOST_PTR, Sizeof.cl_float * 9, Pointer.to(yFloats), null);
  }

  /**
   * Rotate the input image by the given angle, and write it into the output image
   *
   *
   */
  void sobelImage() {
    // Set up the work size and arguments, and execute the kernel
    long[] globalWorkSize = new long[2];
    globalWorkSize[0] = imageSizeX;
    globalWorkSize[1] = imageSizeY;
    clSetKernelArg(kernel, 0, Sizeof.cl_mem, Pointer.to(inputImageMem));
    clSetKernelArg(kernel, 1, Sizeof.cl_mem, Pointer.to(outputImageMem));
    clSetKernelArg(kernel, 2, Sizeof.cl_mem, Pointer.to(xMatrix));
    clSetKernelArg(kernel, 3, Sizeof.cl_mem, Pointer.to(yMatrix));
    clEnqueueNDRangeKernel(commandQueue, kernel, 2, null, globalWorkSize, null, 0, null, null);

    // Read the pixel data into the output image
    DataBufferInt dataBufferDst = (DataBufferInt) outputImage.getRaster().getDataBuffer();
    int[] dataDst = dataBufferDst.getData();
    clEnqueueReadImage(
        commandQueue,
        outputImageMem,
        true,
        new long[3],
        new long[] {imageSizeX, imageSizeY, 1},
        (long) imageSizeX * Sizeof.cl_uint,
        0,
        Pointer.to(dataDst),
        0,
        null,
        null);
  }
}
