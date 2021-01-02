# Clj-Tiles

Follow up to the Werkbank repository. Works fully on-line without Java installation. Also has two way parsing Clojure->XML->Clojure. The typical fly-out toolbar of Blockly is removed, the Blocks are programmed directly in Clojure.

For development do:
 ```
 npm init -y
 npm install shadow-cljs
 npm install blockly
 npm install sax
 npm install odex
 npm install complex.js
 npm install fraction.js
 npm install mathjax@2
 ```
copy mathjax into the "public" directory

`shadow-cljs watch cljtiles`

Go to http://localhost:8080/cljtiles.html in your browser.

You can ispect the blocks by right clicking on them. You will see the all important types of the expressions.

Like in the pysical world, where things have types (like sheep, cow, electron), mathematical theories are made out of things as well: vectors, functions, numbers, symbols. It is important to display the type along with the things in order to recognize them as what they are: things.
