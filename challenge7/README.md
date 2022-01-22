# Challenge 7

The [challenge](Challenge.md) for this week was to create an android application. I decided to make a type racer clone but for your phone! You can find the app (apk) on the [releases](https://github.com/miam-miam100/SpaceCadets/releases/tag/challenge7) page.

## Methodology

Making the app didn't involve too much code so I don't have too much to say, I spent most of my time wrangling with the xml files to produce a working UI. For some strange reason the drag and drop feature in android studio barely works. I just gave up and ended up reverting back to directly editing the xml files. I thought building a UI using drag and drop would make life a lot easier but turns out I was just flat out wrong.

I also encountered a few build errors along the way which were a pain to solve. When I added the quotes to the app the build script kept on failing with a NullPointerException as such I thought (as did [StackOverlow](https://stackoverflow.com/questions/22583418/execution-failed-for-task-appmergedebugresources-android-studio)) that the quotes were too large and so the build script was running out of memory. This would explain the NullPointerException as the system could be trying to allocate new memory, fail, return a null pointer and then the var it was meant to allocate gets used and produces the Exception. But no after many tries and incrementally adding quotes until it failed. I found out that there are certain characters that need to be escaped in xml files. For some strange reason this included question marks and may I add that none of this is shown to you through an IDE. Please, please, please if you are ever going to build a program that can fail make sure you have proper exception handling! 

I guess, one other thing of interest was me trying to scrape typerracer, it turns out that someone had already done it. And since there was no reason to reinvent the wheel I decided to try and use it. Unfortunately, the whole thing used Python 2 (Yay!) and a ton of deprecated modules. So I quickly made up my own version in half the time I spent trying to figure out the stuff that needed fixing. Moral of the story whilst reusing code is nice and easy it is often better to ditch it and start again at the first hiccup. 

## Overview

- MainActivity which is run when the app is first opened, it sets up everything.
- WPMUpdater which updates the WPM text to show the user the how they are doing. 
- TypingWatcher which is called whenever the user writes something in the input text and updates the highlighting for the main text.
- getTyperracerTexts which is a python script that scrapes the typeracer database to get some quotes for the app.

## Images

[<img src="https://user-images.githubusercontent.com/49870539/143959973-ac3ebfa8-d8a1-49b3-8f26-e1be5eb263c5.png" width="300"/>]()
[<img src="https://user-images.githubusercontent.com/49870539/143960115-45b5ac00-ae0b-423a-949e-fd4faa1d9182.png" width="300"/>]()
[<img src="https://user-images.githubusercontent.com/49870539/143960120-ae201def-7c7c-4065-a6e1-dc978c645f96.png" width="300"/>]()
