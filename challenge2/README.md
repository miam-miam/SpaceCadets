# Challenge 2
The [challenge](Challenge.md) for this week was to make an interpreter for a simple language called BareBones.

## Methodology
To do this I used RegEx and a lot of OOP. At first I tried using my own custom recursive RegEx parser and after many hours of work I found out that recursive RegEx expressions don't actually capture their matches when called recursively. As such I decided to simplify the RegEx so that it now only found the commands for each line.

I then tried using a C/C++/Rust way to parse the source code as I forgot that Java was garbage collected so was keeping everything in separate lists. (My failed experiment can be found [here](https://github.com/miam-miam100/SpaceCadets/tree/challenge2-procedural/challenge2)). I felt that whilst it did work my solution was being hindered by Java. Thus, as any normal person would do I decided to scrap everything (even though it was working) and rewrote my solution using a much more complex (and probably slower) OOP solution whereby every command was an object. 

## Overview
- Main which can be run with a variety of different arguments from creating a .py/.rs/.cpp/.java file from a BareBones file (.bb), correctly reformatting a .bb file or just running the whole thing through an interpreter.
- The Transpiler that correctly writes out the source code into another programming language.
- The Parser which parses everything into a single code block containing commands to be interpreted or transpiled.

## Language Features
- There are 5 commands: Incr, Clear, Decr, While and End
- Each command must be terminated with a semicolon, this allows you to write everything on a single line if you really fancy doing so.
- Variables are stored as signed 32-bit integers (except for Python as ints are unbounded) and can become negative in all but the interpreter.
- As such Overflowing is considered UB and the interpreter will error out if it does encounter an overflow.
- Comments can be added using //.
