# Challenge 8

The challenge for this week was to write code to a "bare-metal" machine, this meant we had to bootstrap our own OS/kernel. I decided to create a way to use the [barebones](https://github.com/miam-miam/SpaceCadets/tree/main/challenge2) language on bare metal. The OS itself supports hardware interrupts, paging, allocations and asynchronous execution.

## Methodology

I decided to use Rust to make the program as whilst in spirit I should be using Java, I did not want to spend hours trying to wrangle with the JVM. Additionally, Rust has two incredibly nice features which I thought could come in handy; You can easily disable the standard library using the #![no_std] tag and compile to any target you like by specifying a json file. And in Rust it's super easy to add new crates (think Python modules and pip) which would surely come in handy when I would need to import numerous different things to get my program running.

I found this really useful [blog](https://os.phil-opp.com/) by Philipp Oppermann which goes through all the steps needed to get a full-on OS working in Rust. However, the blog used an outdated version of the bootloader crate which at the time did not have support for UEFI. This may seem like a small change but as it turns out the new version had to deprecate a lot of features to ensure it was up to the UEFI spec. One such feature was the VGA text buffer which allowed you to directly write text into a buffer which would then be outputted to the screen. Using UEFI this was replaced by framebuffer in which I had to draw each character separately. After getting it working, I then moved on to making keyboard interrupts work unfortunately the blog used a really old interrupt controller called [8259 PICP](https://en.wikipedia.org/wiki/Intel_8259) (introduced back in 1976) which as you can imagine was no longer supported. Its successor was too complicated to use (hence the reason Philipp was still using the old PIC) and as such I was unable to get it working. I was then forced to start from [scratch](https://github.com/miam-miam/SpaceCadets/tree/challenge8-UEFI) and use the older bootloader version.

To add the barebones language, I used a rust macro to convert it into rust code during compilation, this meant that I was able to broadcast errors right in the IDE and use Rust's type system to ensure that the program was "correct". The errors even showed the exact line where you made the mistake! This was all done using spans, whereby I would set the generated rust code to have the same line has the barebones code so that all errors would point back to it. Since I used a Rust macro, I was able to directly interact with the barebones code and allow the code to increment rust variables directly using "\`". Finally, to run code I used asynchronous functions whereby they would activate upon interrupts and ensure that the system was wasting CPU cycles waiting for the keyboard to do something.

## Overview

- bb-macro a separate library that contains a procedural macro to convert Barebones into Rust code. Procedural macros are run during the compilation stage and are given a token stream from which they must then return a new token stream which would be used for the rest of the compilation.
- Executor which allows you to execute async functions, if you would like to execute a new async function just return a Vec containing the futures you want to execute.
- A bunch of other files to get the kernel working.

## Images

![image](https://user-images.githubusercontent.com/49870539/144943869-8b271cd1-2602-4833-9b87-cd32b47ae59c.png)
![image](https://user-images.githubusercontent.com/49870539/144944025-fa8a0b51-196e-4a66-a4bc-5661aacf445d.png)
![image](https://user-images.githubusercontent.com/49870539/144943971-f6f03010-065a-4f6e-aaeb-7523baaf9086.png)
