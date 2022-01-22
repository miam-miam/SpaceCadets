# Challenge 1

Have a look at the Web page at https://www.ecs.soton.ac.uk/people/dem. This is a departmental
information page which gives all sorts of information about a member of staff. The Web address is
constructed from a departmental email id (in this case dem). If I have someone else's email id, I
can look up their name from one of these Web pages.

## Suggested Method

Write a program which converts an email id into a name by

1. Constructing a BufferedReader object so that can read an email id from System.in (you will need
   some intermediate objects to help you here. Look it up!)
2. Constructing the full Web page address by string concatenation
3. Constructing a URL object from the Web address
4. Constructing a BufferedReader object that can read from the URL (you will need some intermediate
   objects to help you here. Look in the book!)
5. Ignoring the first lines of input from the Web page and saving the one which contains the name (
   Hint: <... property="name">)
6. Use the indexOf() and substring() methods to find and extract the name from the line
7. Print out the result

## Possible Extensions

This kind of data extraction is actually quite simple because we know exactly where the data will
appear in the file - it gets much more difficult when we don't know the exact format of the page
returned to us.

Space Cadets is based on the idea of a central challenge that people are encouraged to extend - or
even bypass altogether! Why not have a go at some variations of this theme (or add your own
suggestions to the list below):

- Write a program to get related people for this page for
  example https://secure.ecs.soton.ac.uk/people/dem/related_people [Please note you have to authenticate to enter this page]
- Write a program to find the URL of anyone's home page by using the first page that Google
  returns (try Queen Elizabeth, Paul Whitehouse, Tinky Winky, Kylie Minogue...).
- Write a program that prints anagrams of your name by invoking the Internet Anagram Server
  at http://wordsmith.org/anagram/
- Write a program that converts from English to French by using the Google Translate web page
  at http://translate.google.com/. This is a little more complex because it requires an HTTP POST
  rather than a GET and the page has been 'minified' which makes it difficult to read.
- Write a program that creates a .txt file with information (how much is at your discretion) about
  the person (If you REALLY want to expand on that, automate it to go through EVERY page)