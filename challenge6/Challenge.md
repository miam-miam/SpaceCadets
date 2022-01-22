# Challenge 6

Unfortunately, the (Battlestar Battleshippy McBattleshipFace) and its crew have been decimated due
to the enemy exploiting a buffer overflow caused by the long ship's name; That because of the
vulnerability was broadcasting the ship's location in its communication array pings. Not only that
all communication was decrypted because the buffer overflow burped the word "McBattleshipFace" in
plain text they were able to eavesdrop on the fleet communication for weeks. On the Battlestar
Battleshippy McBattleshipFace the last transmission indicated that the enemy is headed our way.

The few remaining officers have devised a plan to detonate an EMP when the enemy space jumps on top
of us. Which meant frying our systems with the enemy as well but at least it means we have a
fighting chance.

Few ancient defence Barretts from 2017 have been shielded to withstand the EMP blast because it was
built at a time we almost destroyed humanity by nuclear fallout. You are tasked with building an
automated targeting system to shoot down anything that space jumps after the EMP.

## Suggested Methods

Steps for circle location detection:

1. Load image
2. Convert image to greyscale
3. Do edge detection (Sobel operator)
4. Do circle detection at various radii (Hough transform)
5. Draw circle around the most prominent circle(s)

### Convert to greyscale

Set TYPE_BYTE_GRAY

Tip: If you know the colour of the circle you are detecting, you can filter for this too.

### Sobel operator

https://en.wikipedia.org/wiki/Sobel_operator

For every pixel, apply this
matrix: https://upload.wikimedia.org/math/2/a/5/2a56db5dad1226aebd954d0a1e59f59d.png

The value of edge detection is
then: https://upload.wikimedia.org/math/3/d/8/3d82fd5595a30ee764dc968e29101012.png

Pick a threshold at which to indicate that this is an edge.

### Hough transform

https://en.wikipedia.org/wiki/Hough_transform

    For every radius you're looking for
        For every pixel in edge detection (over certain threshold)
            In a new image (where each pixel is a vote), draw circle around point at radius
    Count votes to find most likely (x,y) for a circle

Note: you will also need an algorithm for plotting points on the edge of a circle. You can do this
with some clever sin and cos wrangling (loop round 360° and plot in the x and y). There is also
something called the midpoint algorithm, which is an optimisation of this, that you might consider
researching.

## Possible Extensions

Make the entire process run in real-time using a gpu.