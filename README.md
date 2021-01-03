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

You can ispect the blocks by right clicking on them. You will see the types of the according data in the running program.

Like in the pysical world, where things are of some kind or another (like sheep, cow, electron), mathematical theories are made out of many types of things as well: vectors, functions, numbers, symbols. It is important to display those types in order to recognize the things. Things that admittadly do not exist in the city of Vienna but are things of aesthtetic experience. Things that in any case act back on the phyical world in laboratiories and technical applications. 

The blocks are attempts to picture those things, just like drawings and figures of [Santa Claus](https://en.wikipedia.org/wiki/Yes,_Virginia,_there_is_a_Santa_Claus) do. But one and the same block can become different things in the process of a running program. The indication of types along with the resulting data makes the picture more accurate. 
