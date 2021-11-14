# Challenge 5
The [challenge](https://secure.ecs.soton.ac.uk/student/wiki/w/COMP1202/Space_Cadets/SCChallengeSpiro) for this week was to create a GUI application that generates spirographs. I decided to create graphical window whilst only outputting to the standard output.

## Methodology
To do this I used the parametric equations to generate an x and y value at t, increasing the t value by a certain step. The generated x and y values could then be added to a treemap, which ensured that they were correctly sorted from 0,0 to 0,9 to 9,9 instead of insertion order. The parametric equations are just the ones from the [wiki](https://en.wikipedia.org/wiki/Spirograph).

The treemap can then be used to print out the different points to the standard output. This is done by going through each point and printing the correct amount of whitespace to get to the next point. I then decided to create a window around the generated artefact so as to create a truly genuine GUI experience. Ultimately this was all done with a lot of fiddling about to find the right characters as well as ensure that everything was correctly aligned.

I also produced two other windows, to generate them I used bitmap files, with each black pixel counting as a coordinate to be added the standard output. This allowed me to quickly prototype window layouts as I could easily generate the text in paint and see how it looked in my program.

## Overview
- Main which setups the welcome state, gets the spirograph values then creates a loading screen and then finally shows the spirograph.
- The Window which is an abstract class that will create a header and box around the output.
- The Coordinate which describes a coordinate, it's used to sort the treemap.
- The Spirograph/BMPReader generate the treemap values using their own different methods.

## Images
![image](https://user-images.githubusercontent.com/49870539/141699564-c94d787b-90c6-4199-b464-9064a602dc25.png)
![image](https://user-images.githubusercontent.com/49870539/141699724-4def0f52-aaef-4636-aebc-5c2d5bab7d7c.png)
![image](https://user-images.githubusercontent.com/49870539/141699596-e639e8a7-53ab-485a-8099-ba9543668247.png)
