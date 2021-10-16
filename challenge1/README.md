# Challenge 1
The [challenge](https://secure.ecs.soton.ac.uk/student/wiki/w/COMP1202/Space_Cadets/SCChallengeEmail) for this week was to make a web scraper that could get the names and emails of any person in ecs.

## Methodology
To do this I used the [secure ecs website](https://secure.ecs.soton.ac.uk/) to get all the names using it's search function. Unfortunately this meant I needed a way to authenticate the program as the website is not public. As such I decided to ask the user to provide an authentication cookie to be able to use the program. Since I know that writing out cookie values can be quite tedious the program will try to save the cookie so that it can be used next time it is run.

## Overview
To scrape the website I have used the [JSoup](https://jsoup.org/) library as it helps parse html other than that there are two other files: 
- Main which scrapes and correctly reformats the website for the console.
- Cookie which deals with saving as well as getting the authentication cookie to scrape the website.
