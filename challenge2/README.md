# Challenge 3
The [challenge](https://secure.ecs.soton.ac.uk/student/wiki/w/COMP1202/Space_Cadets/SCChallengeBareBonesExtended) for this week was to improve the BareBones interpreter we did last [week](https://github.com/miam-miam100/SpaceCadets/tree/challenge2/challenge2).

## Methodology
To do this I used RegEx and a lot of OOP. At first I tried using my own custom recursive RegEx parser and after many hours of work I found out that recursive RegEx expressions don't actually capture their matches when called recursively. As such I decided to simplify the RegEx so that it now only found the commands for each line.

I then tried using a C/C++/Rust way to parse the source code as I forgot that Java was garbage collected so was keeping everything in separate lists. (My failed experiment can be found [here](https://github.com/miam-miam100/SpaceCadets/tree/challenge2-procedural/challenge2)). I felt that whilst it did work my solution was being hindered by Java. Thus, as any normal person would do I decided to scrap everything (even though it was working) and rewrote my solution using a much more complex (and probably slower) OOP solution whereby every command was an object. 

When adding functions I unfortunately had to remove the transpiler's ability to write java code as it would not have easily been possible (in a single file) to have the same function take in paramaters by val in one instance and by ref in an another. This would have easily been possible to do using a tuple but unfortunately Java doesn't let you do that in their standard library.

## Overview
- Main which can be run with a variety of different arguments from creating a .py/.rs/.cpp file from a BareBones file (.bb), correctly reformatting a .bb file or just running the whole thing through an interpreter.
- The Transpiler that correctly writes out the source code into another programming language.
- The Parser which parses everything into a single code block containing commands to be interpreted or transpiled.

## Language Features
- There are 5 commands: Incr, Clear, Decr, While, End, Func and ${func_name}()
- Each command must be terminated with a semicolon, this allows you to write everything on a single line if you really fancy doing so.
- Variables are stored as signed 32-bit integers (except for Python as ints are unbounded) and can become negative in all but the interpreter.
- As such Overflowing is considered UB and the interpreter will error out if it does encounter an overflow.
- Since I have added functions I have had to make all variables scoped as it could otherwise lead to weird behaviour when interacting with global stuff inside a function.
- Functions can take in any number of arguments using commas.
- Function parameters can either be passed by value or by reference, to pass something by reference just use the & character.
- Comments can be added using //.
