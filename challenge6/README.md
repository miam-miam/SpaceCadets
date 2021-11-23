# Challenge 6
The [challenge](https://secure.ecs.soton.ac.uk/student/wiki/w/COMP1202/Space_Cadets/SCChallengeCircleDetection) for this week was to create a GUI application that can detect circles from a webcam (preferably in real time).

## Methodology
To do this I had to use the GPU to manipulate the webcam image in a reasonably fast time. Unfortunately, Java has very little support for running stuff on the GPU and so I decided to use an OpenCL wrapper called JOCL. My implementation runs just about in real-time, but it would be nice if I could optimise it a bit more.

To detect circles I used the [Sobel Operator](https://en.wikipedia.org/wiki/Sobel_operator) to create an image that captures the "gradient" of any point. This gradient is then used in a [Hough Transform](https://en.wikipedia.org/wiki/Hough_transform) to create a voting space where each pixel "votes" on the most likely space the circle origin would be in. We then extract the highest voted spot and draw a target around it.

As for the webcam I used this very nice [module](https://github.com/sarxos/webcam-capture) from Bartosz Firyn which works for most webcams. I then just call the animation function every time a new image is given by the webcam. All the GPU code is written in C and is compiled when the program is run.

## Overview
- CircleDetector which contains the code for running OpenCL code and initialising everything.
- Animation which is run every time the webcam gets a new image and runs the algorithms in CircleDetector.

## Images
![image](https://user-images.githubusercontent.com/49870539/142956084-7c9e792c-eb15-4ef0-b737-792097fe103f.png)
![image](https://user-images.githubusercontent.com/49870539/142957642-d2377a15-f31b-45ca-8f4a-8f735f0ad37a.png)
![image](https://user-images.githubusercontent.com/49870539/142957649-1901f6f2-2980-41e5-8209-b84f87e81f77.png)
