# Challenge 5

The challenge this week is to create a virtual version of
a ["Spirograph"](http://www.math.psu.edu/dlittle/java/parametricequations/spirograph/SpiroGraph1.0/index.html) (
a childrens toy back in the dark ages). A Spirograph consisted of a set of small toothed wheels that
rolled around the inside of larger wheels. You put a pen inside a hole in the smaller wheels and
drew out a pattern as it looped around the bigger wheel. Your challenge is to write an Applet or
JApplet or AWT application or Swing application or JavaFX application that draws a simple
hypercycloid.

## Suggested Methods

Mathematically, it is a parametric curve, and the x and y co-ordinates of the points on the curve
are given by the equations:

    x = (R-r)*cos(t) + O*cos(((R-r)/r)*t)
    y = (R-r)*sin(t) - O*sin(((R-r)/r)*t)

where the radius of the fixed circle is R, the radius of moving circle is r, and the offset of the
pen point in the moving circle is O. (The equations are derived from those in here). Here are some
examples of the kind of patterns you can achieve with suitable sizes of inner circle, outer ring,
pen colour and pen position inside the inner circle.

In this challenge I'd like you to focus on creating an Object Oriented solution. For example, you
might create a separate class that represents a hypocycloid, which has instance variables for (among
other things) the radius of the circles. Your class should provide a sensible constructor so that a
hypocycloid of a particular shape can be easily created. The class should provide a paint() method
so that it can be easily drawn by the applet's paint() method.

You should think about convenient ways that the object can be used: what things are properties of
the shape itself, and what depend on each occurrence of the drawing. For example, should location,
size, color, r, R and O be parameters to the constructor or the paint method?

## Possible Extensions

- Can you extend your object oriented model to incorporate these more complex spirograph shapes (
  i.e. patterns with multiple hypocycloid curves)? Consider how best to organise your collection of
  hypocycloid objects and think how they might be wrapped up in a class.
- You may also think how to model other types of patterns such as fractals, can you model them in a
  similar way to the hypocycloids?
- And if you still have some strength left, what about a simple GUI that allows the user to alter
  the parameters of the patterns being drawn?