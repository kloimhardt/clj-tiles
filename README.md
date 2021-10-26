# Clj-Tiles

* Start to learn functional programming by solving graphical puzzles [in the browser](https://kloimhardt.github.io/cljtiles.html).
* There are over 100 puzzles built in. Starting with simple examples, they continue to basic web-programming and lead to sophisticated examples taken from theoretical physics.
* Investigate puzzles with the built in value inspector.
* Make new puzzle tutorials by using the built in parser.
* Live coding made easy, just prepare a puzzle and solve it in front of the audience.

Watch the [demo video](https://www.youtube.com/watch?v=DHcZkmXKp04).

## Solve puzzles
The first few puzzles consist of completing the lyrics of a famous song. Then, the following coding puzzles are taken from the book [Getting Clojure](https://pragprog.com/titles/roclojure/getting-clojure/). Another resource is [Clojure for the Brave and True](https://www.braveclojure.com). Do you notice the similarities between writing code and poetry?

The most simple example looks like this:

![hello](screenshots/hello_world.png)

Just press the `Run` button, as this is already a fully functional program.

A more sophisticated example looks like this:

![pendulum](screenshots/pendulum_begin.png)

After completion, this puzzle (which uses the built in [SICMutils](https://github.com/sicmutils/sicmutils) library) represents a working program. By right clicking on the individual blocks and `inspect`, you can trace the program execution and get some type information. This special puzzle also provides an experimental feature: the value inspector is enhanced to provide additional descriptions and explanations. Also the completed puzzle is provided in a separate example.

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

The [examples](https://github.com/kloimhardt/clj-tiles/tree/master/examples) directory contains code from the [SICMutils Jupyter notebook](https://github.com/sicmutils/sicmutils/blob/master/jupyter/book-examples.ipynb). It can be readily pasted into the clj-tiles parser. Reading the first pages of the book `Structure and Interpretation of Classical Mechanics` ([original](https://mitpress.mit.edu/sites/default/files/titles/content/sicm_edition_2/book.html), [reprint](https://tgvaughan.github.io/sicm/)) is mandatory for understanding.

## A kind note on types and the role of graphical blocks

You can inspect the blocks by right clicking on them. The type of the according data is displayed as the program is running.

Like in the physical world, where things are of some kind or another (like sheep, cow, electron), within programs, many types of things appear as well: vectors, functions, numbers, symbols and their combinations, mathematical expressions. There is an infinite number of types, as programmers invent them all the time. E.g. the data holding the address of a person can be attributed the type "collection of strings" or even "address" or both, depending on software design.

Within the [SICMutils](https://github.com/littleredcomputer/sicmutils) library (and according clj-tiles tutorial), programs codify physical theories. It is helpful to identify and show types along with the data. So the different things can be recognised properly. Things that admittedly do not exist in the city of Vienna but are abstract things of aesthetic experience. Things that in any case act back on the physical world in laboratories and technical applications. Things like a mathematical expression containing the rule to calculate the orbital angular momentum of a satellite in space.

The blocks are attempts to picture those things, comparable to drawings and figures of [Santa Claus](https://en.wikipedia.org/wiki/Yes,_Virginia,_there_is_a_Santa_Claus). A block has a graphical appearance and can be moved around on the workspace. But while its appearance does not change, one and the same block can result in different things during the process of a running program. Thus, to complete the picture it is necessary to be able to display the types and data the blocks result into during the execution of the program.

The way in which the theory of classical Mechanics makes new things appear can be found out by playing with them. By combining and inspecting the graphical blocks. This hopefully leads to a deeper understanding of the theory. After having understood the theory and the places of things, the display of types will not be necessary anymore as the user develops her own sophisticated picture. The final goal is to be able to read, grasp, modify and run sophisticated code examples displayed in a text editor.

## Setup

For development install Clojure/Java and Node and do:
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
* [Blockly](https://developers.google.com/blockly)
* [BlockPy](https://think.cs.vt.edu/blockpy/) 
* [blockoid](https://github.com/ParkerICI/blockoid)
* [Werkbank](https://github.com/kloimhardt/werkbank)
* [sicmutils](https://github.com/sicmutils/sicmutils)
* [SCI](https://github.com/borkdude/sci)
