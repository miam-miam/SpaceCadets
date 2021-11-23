import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
 * A CircleDetector using JOCL to interface with the GPU. A lot of the code you see is boilerplate
 * as JOCL is a one-to-one wrapper around the OpenCL (a C API)
 */
public class CircleDetector {

  // The different radius to look for.
  private static final int minR = 40;
  private static final int maxR = 70;
  private static final int sizeR = maxR - minR;

  /**
   * The source code of the kernel to execute. It will transform an image into it's Sobel form
   * (https://en.wikipedia.org/wiki/Sobel_operator) and then do a Hough transform
   * (https://en.wikipedia.org/wiki/Hough_transform). I use %s to add dynamic vars to the source
   * code.
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

      // Convert a colour pixel to gray scale.
      float getGrayScale(uint4 pixel)
      {
          float4 color = convert_float4(pixel) / 255;
          return 0.2126*color.x + 0.7152*color.y + 0.0722*color.z;
      }
            
      __kernel void sobel(__read_only image2d_t sourceImage, __write_only image2d_t targetImage, __constant float * xFilter, __constant float * yFilter)
      {
          int gidX = get_global_id(0);
          int gidY = get_global_id(1);
          int2 pos = {gidX, gidY};
          
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
          // Currently not square rooting but this doesn't really matter.
          uint4 change = convert_uint4_rte(hypot(xChange * 255, yChange * 255));
          if (change.x > 170) {
              write_imageui(targetImage, pos, change);
          }
      }

      __kernel void hough(__read_only image2d_t sourceImage, __global int * accumulator, __constant float * theta) {
          int gidX = get_global_id(0);
          int gidY = get_global_id(1);
          int2 pos = {gidX, gidY};
          
          int width = get_image_width(sourceImage);
          int height = get_image_height(sourceImage);
          
          
          uint4 pixel = read_imageui(sourceImage, samplerIn, pos);
          if (pixel.x > 170) {
              for (int r=%s; r < %s; r++) {
                  for (int t=0; t<360; t++) {
                      float angle = theta[t];
                      int a = gidX - (float) r * cos(angle);
                      int b = gidY - (float) r * sin(angle);  //polar coordinate for center
                      if ( a >= 0 && a < width && b >= 0 && b < height) {
                          int index = a + b * width + (r-%s) * width * height;
                          // Using atomic_inc to increment the accumulator whilst ensuring the numbers stay correct.
                          atomic_inc(&accumulator[index]);
                      }
                  }
              }
          }
      }
      """.formatted(minR, maxR, minR);

  final Webcam webcam;
  final int imageSizeX;
  final int imageSizeY;
  private final BufferedImage outputImage;
  BufferedImage inputImage;
  private float[] theta;
  private cl_mem inputImageMem;
  private cl_mem outputImageMem;
  private cl_mem accumulatorMem;
  private cl_mem thetaMem;
  private cl_mem xMatrix;
  private cl_mem yMatrix;
  private cl_context context;
  private cl_command_queue commandQueue;
  private cl_kernel kernelSobel;
  private cl_kernel kernelHough;


  /**
   * Creates the CircleDetector.
   */
  public CircleDetector() {
    // Read the input image file and create the output images
    webcam = Webcam.getDefault();
    webcam.setViewSize(WebcamResolution.VGA.getSize());
    imageSizeX = WebcamResolution.VGA.getSize().width;
    imageSizeY = WebcamResolution.VGA.getSize().height;
    inputImage = new BufferedImage(imageSizeX, imageSizeY, BufferedImage.TYPE_INT_RGB);

    outputImage = new BufferedImage(imageSizeX, imageSizeY, BufferedImage.TYPE_INT_RGB);
    JLabel outputLabel = new JLabel(new ImageIcon(outputImage));
    JLabel finalLabel = new JLabel(new ImageIcon(inputImage));
    initCL();
    initImageMem();

    // Create the main frame
    JFrame frame = new JFrame("Circle detector");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setLayout(new GridLayout(2, 2));
    frame.add(new WebcamPanel(webcam, WebcamResolution.VGA.getSize(), true));
    frame.add(outputLabel);
    frame.add(finalLabel);
    frame.pack();
    frame.setVisible(true);
    startAnimation(new Component[]{outputLabel, finalLabel});
  }

  /**
   * Entry point for this detector.
   *
   * @param args not used
   */
  public static void main(String[] args) {
    SwingUtilities.invokeLater(CircleDetector::new);
  }

  /**
   * Starts the thread which will attach to the webcam and run the animation.
   *
   * @param outputComponents The components to repaint after each step
   */
  void startAnimation(final Component[] outputComponents) {
    System.out.println("Starting animation...");
    Thread thread = new Thread(new Animation(outputComponents, this));
    thread.setDaemon(true);
    thread.start();
  }

  /**
   * Initialize the OpenCL context, command queue and kernel
   */
  void initCL() {
    final int platformIndex = 0;
    final long deviceType = CL.CL_DEVICE_TYPE_ALL;
    final int deviceIndex = 0;

    // Enable exceptions and subsequently omit error checks in this sample
    CL.setExceptionsEnabled(true);

    // Obtain the number of platforms
    int[] numPlatformsArray = new int[1];
    CL.clGetPlatformIDs(0, null, numPlatformsArray);
    int numPlatforms = numPlatformsArray[0];

    // Obtain a platform ID
    cl_platform_id[] platforms = new cl_platform_id[numPlatforms];
    CL.clGetPlatformIDs(platforms.length, platforms, null);
    cl_platform_id platform = platforms[platformIndex];

    // Initialize the context properties
    cl_context_properties contextProperties = new cl_context_properties();
    contextProperties.addProperty(CL.CL_CONTEXT_PLATFORM, platform);

    // Obtain the number of devices for the platform
    int[] numDevicesArray = new int[1];
    CL.clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
    int numDevices = numDevicesArray[0];

    // Obtain a device ID
    cl_device_id[] devices = new cl_device_id[numDevices];
    CL.clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
    cl_device_id device = devices[deviceIndex];

    // Create a context for the selected device
    context = CL.clCreateContext(contextProperties, 1, new cl_device_id[]{device}, null, null,
        null);

    // Check if images are supported
    int[] imageSupport = new int[1];
    CL.clGetDeviceInfo(
        device, CL.CL_DEVICE_IMAGE_SUPPORT, Sizeof.cl_int, Pointer.to(imageSupport), null);
    System.out.println("Images supported: " + (imageSupport[0] == 1));
    if (imageSupport[0] == 0) {
      System.out.println("Images are not supported");
      System.exit(1);
      return;
    }
    // Create a command-queue for the selected device
    cl_queue_properties properties = new cl_queue_properties();
    properties.addProperty(CL.CL_QUEUE_PROFILING_ENABLE, 1);
    properties.addProperty(CL.CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE, 1);
    commandQueue = CL.clCreateCommandQueueWithProperties(context, device, properties, null);

    // Create the program
    System.out.println("Creating program...");
    cl_program program =
        CL.clCreateProgramWithSource(context, 1, new String[]{programSource}, null, null);

    // Build the program
    System.out.println("Building program...");
    CL.clBuildProgram(program, 0, null, null, null, null);

    // Create the kernel
    System.out.println("Creating kernel...");
    kernelSobel = CL.clCreateKernel(program, "sobel", null);
    kernelHough = CL.clCreateKernel(program, "hough", null);
  }

  /**
   * Initialize the memory objects for the input and output images
   */
  void initImageMem() {

    cl_image_format imageFormat = new cl_image_format();
    imageFormat.image_channel_order = CL.CL_RGBA;
    imageFormat.image_channel_data_type = CL.CL_UNSIGNED_INT8;

    cl_image_desc imageDesc = new cl_image_desc();
    imageDesc.image_height = imageSizeY;
    imageDesc.image_width = imageSizeX;
    imageDesc.image_row_pitch = (long) imageSizeX * Sizeof.cl_uint;
    imageDesc.image_type = CL.CL_MEM_OBJECT_IMAGE2D;

    outputImageMem = CL.clCreateImage(context, CL.CL_MEM_READ_WRITE, imageFormat, imageDesc, null,
        null);

    inputImageMem = CL.clCreateImage(
        context,
        CL.CL_MEM_READ_WRITE,
        imageFormat,
        imageDesc,
        null,
        null);

    float[] xFloats = new float[]{-1, 0, 1, -2, 0, 2, -1, 0, 1};
    float[] yFloats = new float[]{-1, -2, -1, 0, 0, 0, 1, 2, 1};

    theta = new float[360];
    for (int t = 0; t < 360; t++) {
      theta[t] = (float) (Math.PI * t / 180);
    }

    xMatrix = CL.clCreateBuffer(context,
        CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * 9, Pointer.to(xFloats),
        null);
    yMatrix = CL.clCreateBuffer(context,
        CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * 9, Pointer.to(yFloats),
        null);

    accumulatorMem = CL.clCreateBuffer(context, CL.CL_MEM_READ_WRITE, Sizeof.cl_int * (
        (long) imageSizeX * imageSizeY * sizeR), null, null);

    thetaMem = CL.clCreateBuffer(context,
        CL.CL_MEM_READ_ONLY | CL.CL_MEM_COPY_HOST_PTR, Sizeof.cl_float * 360, Pointer.to(theta),
        null);

  }

  /**
   * Run the sobel operator on the input camera image.
   */
  void sobelImage() {
    // Set up the work size and arguments, and execute the kernel
    long[] globalWorkSize = new long[2];
    globalWorkSize[0] = imageSizeX;
    globalWorkSize[1] = imageSizeY;

    DataBufferInt dataBufferSrc = (DataBufferInt) inputImage.getRaster().getDataBuffer();

    CL.clEnqueueWriteImage(commandQueue, inputImageMem, true, new long[3],
        new long[]{imageSizeX, imageSizeY, 1},
        (long) imageSizeX * Sizeof.cl_uint, 0, Pointer.to(dataBufferSrc.getData()), 0, null, null);

    CL.clSetKernelArg(kernelSobel, 0, Sizeof.cl_mem, Pointer.to(inputImageMem));
    CL.clSetKernelArg(kernelSobel, 1, Sizeof.cl_mem, Pointer.to(outputImageMem));
    CL.clSetKernelArg(kernelSobel, 2, Sizeof.cl_mem, Pointer.to(xMatrix));
    CL.clSetKernelArg(kernelSobel, 3, Sizeof.cl_mem, Pointer.to(yMatrix));
    CL.clEnqueueNDRangeKernel(commandQueue, kernelSobel, 2, null, globalWorkSize, null, 0, null,
        null);

    // Read the pixel data into the output image
    DataBufferInt dataBufferDst = (DataBufferInt) outputImage.getRaster().getDataBuffer();
    int[] dataDst = dataBufferDst.getData();
    CL.clEnqueueReadImage(
        commandQueue,
        outputImageMem,
        true,
        new long[3],
        new long[]{imageSizeX, imageSizeY, 1},
        (long) imageSizeX * Sizeof.cl_uint,
        0,
        Pointer.to(dataDst),
        0,
        null,
        null);
  }

  /**
   * Run the hough transform on the image produced by the Sobel operator.
   *
   * @return the index of the highest voted number of points.
   */
  int houghImage() {
    // Set up the work size and arguments, and execute the kernel
    long[] globalWorkSize = new long[2];
    globalWorkSize[0] = imageSizeX;
    globalWorkSize[1] = imageSizeY;

    CL.clSetKernelArg(kernelHough, 0, Sizeof.cl_mem, Pointer.to(outputImageMem));
    CL.clSetKernelArg(kernelHough, 1, Sizeof.cl_mem, Pointer.to(accumulatorMem));
    CL.clSetKernelArg(kernelHough, 2, Sizeof.cl_mem, Pointer.to(thetaMem));
    CL.clEnqueueNDRangeKernel(commandQueue, kernelHough, 2, null, globalWorkSize, null, 0, null,
        null);

    int[] accumulator = new int[imageSizeX * imageSizeY * sizeR];
    CL.clEnqueueReadBuffer(
        commandQueue,
        accumulatorMem,
        true,
        0,
        ((long) imageSizeX * imageSizeY * sizeR) * Sizeof.cl_int,
        Pointer.to(accumulator),
        0,
        null,
        null);

    // Find the highest voted point.
    int max = 0;
    int currentIndex = 0;
    int maxIndex = 0;
    for (int i : accumulator) {
      if (i > max) {
        max = i;
        maxIndex = currentIndex;
      }
      currentIndex += 1;
    }
    CL.clEnqueueFillImage(commandQueue, outputImageMem, Pointer.to(new int[]{0, 0, 0, 0}),
        new long[3],
        new long[]{imageSizeX, imageSizeY, 1},
        0,
        null, null);
    long start = System.currentTimeMillis();
    CL.clEnqueueFillBuffer(
        commandQueue,
        accumulatorMem,
        Pointer.to(new float[0]),
        1,
        0,
        ((long) imageSizeX * imageSizeY * sizeR) * Sizeof.cl_int,
        0,
        null,
        null);

    // Reset Image ready for next read.
    return maxIndex;
  }

  /**
   * Draws a target at the specified index.
   *
   * @param index the index of the highest voted number of points.
   */
  void drawTarget(int index) {
    int r = index / (imageSizeX * imageSizeY);
    index = index % (imageSizeX * imageSizeY);
    int y = index / imageSizeX;
    index = index % imageSizeX;
    int x = index;
    r += minR;
    for (int t = 0; t < 360; t++) {
      float angle = theta[t];
      int a = (int) (x - (float) r * Math.cos(angle));
      int b = (int) (y - (float) r * Math.sin(angle));

      if (a > 0 && a < imageSizeX - 1 && b > 0 && b < imageSizeY - 1) {
        inputImage.setRGB(a + 1, b, 0x1c871c);
        inputImage.setRGB(a - 1, b, 0x1c871c);
        inputImage.setRGB(a, b + 1, 0x1c871c);
        inputImage.setRGB(a, b - 1, 0x1c871c);
        inputImage.setRGB(a, b, 0x1c871c);
      }
    }

    if (x > 8 && x < imageSizeX - 8 && y > 8 && y < imageSizeY - 8) {
      for (int step = -8; step <= 8; step++) {
        inputImage.setRGB(x + step, y, 0x1c871c);
        inputImage.setRGB(x, y + step, 0x1c871c);
      }
    }

  }
}
