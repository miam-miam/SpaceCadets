# Challenge 9

The challenge for this week was to write code that had a Christmas theme. I decided to write a Screensaver that will show Snowflakes once the pc becomes idle.

## Methodology

For this challenge I decided to go back to Java as whilst I very much hate the language, I still think it is important to try out a breadth of different languages. As it turns out using Java may not have been the best choice since I wanted to call the following native windows [function](https://docs.microsoft.com/en-us/windows/win32/api/winuser/nf-winuser-getlastinputinfo). Unfortunately, due to Java's policy of it runs anywhere the JVM exists, accessing native functions can be a pain. In Java the main way to access OS specific functions is through a module called the JNA. Because of how it is set up, it requires you to declare the functions yourself (a bit like header files) which can be problematic as they are only resolved at runtime and thus cannot be checked at compile time. Overall nowadays, I think there is little point in having a VM since there are very few architectures that need to be supported and ultimately a VM is not needed to create code that can run on multiple architectures/oses. 

## Overview

- The Win32Calls package which deals with producing functions and data structures for the JNA to get the correct functions from native Windows.
- The Drawing Panel that draws Snowflakes onto the screen.
- The PointerChecker which is used as a fallback method if you are not using Windows.

## Image
![image](https://user-images.githubusercontent.com/49870539/145993740-ff8d2f3e-8554-4922-913e-7c1f4754516a.png)
