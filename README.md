# Clj-Tiles

Follow up to the [Werkbank](https://github.com/kloimhardt/werkbank) repository. Works fully [on-line](https://kloimhardt.github.io/cljtiles.html?page=freeparticle). Also has two way parsing Clojure->XML->Clojure. The typical fly-out toolbar of Blockly is removed, the Blocks are programmed directly in Clojure.

For development install Clojure/Java and do:
 ```
 npm init -y
 npm install shadow-cljs
 npm install blockly
 npm install sax
 npm install odex
 npm install complex.js
 npm install fraction.js
 npm install mathjax@2
 
 shadow-cljs watch cljtiles
 ```

Go to https://kloimhardt.github.io/cljtiles.html?page=freeparticle in your browser (Safari does not work).

Try out some tutorials. You can inspect the blocks by right clicking on them. You will see the type of the according data in the running program.

Like in the pysical world, where things are of some kind or another (like sheep, cow, electron), within programs, many types of things appear as well: vectors, functions, numbers, symbols. There is an infinite number of types, as programmers invent them all the time. E.g. the data holding the address of a person can be attributed the type "collection of strings" or even "address" or both. This does not depend on whether or not the programmer explicitely specifies the type within the Clojure code. Maybe different readers and users of some program see different types.

Within the [SICMutils](https://github.com/littleredcomputer/sicmutils) library (and accoring clj-tiles tutorial), programs codify physical theories. It is an important step to identify and show the types to new users. So the different things can be recognized properly. Things that admittadly do not exist in the city of Vienna but are abstract things of aesthtetic experience. Things that in any case act back on the phyical world in laboratiories and technical applications. Things like an expression containing the rule to calculate the orbital angular momentum of a satellite in space.

The blocks are attempts to picture those things, just like drawings and figures of [Santa Claus](https://en.wikipedia.org/wiki/Yes,_Virginia,_there_is_a_Santa_Claus) do. But one and the same block can become different things in the process of a running program. The indication of types along with the data makes the picture more accurate.

The place where those things appear in the theory of classical Mechnics can be found out by playing with them. This leads to an understanding of the theory. After having understood the theory and the place of things, the display of types will not be necessary anymore.
