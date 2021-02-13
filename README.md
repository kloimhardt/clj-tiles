# Clj-Tiles

* Start to code in Clojure by solving graphical puzzles [in the browser](https://kloimhardt.github.io/cljtiles.html?page=12).
* Over 50 puzzels built in, leading to sophisticated examples taken from theoretical physics.
* Investigate puzzles with the built in tracer.
* Make new puzzle tutorials by using the built in parser.
* Live coding made easy, just prepare a puzzle and solve it in front of the audience.

Watch the [demo video](https://www.youtube.com/watch?v=DHcZkmXKp04)

## Solve puzzles

The first and most simple example looks like this:

![hello](screenshots/hello_world.png)

Just press the `Run` button, as this is already a fully functional program.

A more typical puzzle looks like this:

![pendulum](screenshots/pendulum_begin.png)

After completion, it represents a working computer program which can be executed. This one is based on the sophisticated [SICMutils](https://github.com/sicmutils/sicmutils) library. It has descriptions and explanations, and the solution is provided. By right clicking on the tiles and `inspect`, you can trace the program execution and get some type information.

## Create a new puzzle

Open the parser by right clicking on a white space. Paste the following code:
```
{:blockpos [[0 0] [0 100] [0 170] [100 170] [0 220]]
 :code [(:tiles/vert {:title "Getting Clojure"
                      :author "Russ Olson"
                      :published 2018})
        (def :tiles/slot :tiles/slot)
        book
        book
        (:published :tiles/slot)]}
```

Or paste:
```
{:blockpos []
 :code [(def book
        {:title "Getting Clojure",
         :author "Russ Olson",
         :published 2018})
        (:published book)]}
```
Or paste:
```
(:author book)
```
Notice that in the simplest last case, the workspace is not cleared before insert.

You can paste everything in the [examples](https://github.com/kloimhardt/clj-tiles/tree/master/examples) directory. It contains code from the [sicmutils Jupyter notebook](https://github.com/sicmutils/sicmutils/blob/master/jupyter/book-examples.ipynb).

## A kind note on types

You can inspect the blocks by right clicking on them. The type of the according data is displayed as the program is running.

Like in the pysical world, where things are of some kind or another (like sheep, cow, electron), within programs, many types of things appear as well: vectors, functions, numbers, symbols. There is an infinite number of types, as programmers invent them all the time. E.g. the data holding the address of a person can be attributed the type "collection of strings" or even "address" or both, depending on software design, printing tools and not least the user of the program.


Within the [SICMutils](https://github.com/littleredcomputer/sicmutils) library (and accoring clj-tiles tutorial), programs codify physical theories. It is helpful to identify and show types along with the data. So the different things can be recognized properly. Things that admittadly do not exist in the city of Vienna but are abstract things of aesthtetic experience. Things that in any case act back on the phyical world in laboratiories and technical applications. Things like an expression containing the rule to calculate the orbital angular momentum of a satellite in space.

The blocks are attempts to picture those things, just like drawings and figures of [Santa Claus](https://en.wikipedia.org/wiki/Yes,_Virginia,_there_is_a_Santa_Claus) do. But one and the same block can become different things in the process of a running program (I like the comparison to [Neutrino oszillations](https://en.wikipedia.org/wiki/Neutrino_oscillation)). Thus, it makes the picture more accurate to display the types along with the data in the running program.

The place where those things appear in the theory of classical Mechnics can be found out by playing with them. This leads to an understanding of the theory. After having understood the theory and the place of things, the display of types will not be necessary anymore as the user develops her own sophisticated picture.

## Setup

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
## Related projects
* [Werkbank](https://github.com/kloimhardt/werkbank)
* [Blockly](https://developers.google.com/blockly)
* [BlockPy](https://think.cs.vt.edu/blockpy/) 
* [SCI](https://github.com/borkdude/sci)
* [sicmutils](https://github.com/sicmutils/sicmutils)
