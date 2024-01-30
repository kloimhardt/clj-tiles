# clj-tiles
Visual programming for STEM students and instructors at university level.

See my presentation on youtube for the [Visual Tools meeting](https://www.youtube.com/watch?v=m1HbWpWiTk4&t=506s).  
Make sure to interactively follow along: [clj-tiles link to Github pages](https://kloimhardt.github.io/cljtiles.html?page=FDG001).

Describable as a mixture of Wordle and Scratch, clj-tiles is for every student who, at a young age, has been gotten hooked as a Princess of Science (see the episode with Prof. Johanna Pirker on ZDFtivi).

External links: [ZDFtivi](https://www.zdf.de/kinder/princess-of-science/diy-spielentwicklung-100.html), [STEM](https://en.wikipedia.org/wiki/Science,_technology,_engineering,_and_mathematics), [Scratch](https://scratch.mit.edu), [Wordle](https://www.nytimes.com/games/wordle/index.html)

## Competence before Comprehension
As an introduction, we start with the following picture also appears when you click on this [clj-tiles link](https://kloimhardt.github.io/cljtiles.html?org=https://raw.githubusercontent.com/kloimhardt/clj-tiles/master/public/org/sicm-book-vscheme-part1.org).
![cljtiles_polar_1](https://kloimhardt.github.io/blog/images/cljtiles_polar_1.png)

The picture will not tell you much at first. For fun, you can press the `Run` button. But you also might notice the `Get the Puzzle` button. Press it.
![cljtiles_polar_2](https://kloimhardt.github.io/blog/images/cljtiles_polar_2.png)

This looks rather complicated, but you start to notice that the content of the graphical blocks are the same as in the first picture. There is `R`, `phi`, `sin` and so on.
As a hint of how to proceed, we show (with arrows) the first steps to complete the puzzle.
![cljtiles_polar_4](https://kloimhardt.github.io/blog/images/cljtiles_polar_4.png)

After these first steps, you continue...
![cljtiles_polar_5](https://kloimhardt.github.io/blog/images/cljtiles_polar_5.png)

... and arrive at the following picture. Interesting here is the `Color` button. It indicates that you are on the right track. It is modeled after Wordle. Can you finish the puzzle now? After all, you can look up the solution in the first picture above.
![cljtiles_polar_6](https://kloimhardt.github.io/blog/images/cljtiles_polar_6.png)

After proving that you finished the puzzle correctly (all code is green), the   `>` button becomes green itself. This means that, when you move to the next page, you already get the solution to that next puzzle.

So you need to solve a puzzle to get the solution of the next. But if you watch the video mentioned at the beginning, you will find out the trick how to see all puzzles solved without any work. On the other hand, the `Shuffle` button makes it all much harder.

## Comprehension of the Domain
The above example is known to any high school student: conversion of polar coordinates (`R` and `phi`) to the usual rectangular ones.

But this is just a start. By completing all the puzzles of this visual tutorial, a Physics student in the 3rd semester can learn Classical Mechanics. Indeed she first gets competent by solving the puzzles, maybe just learning them by heart. Then, she will gain a much deeper comprehension when reading more carefully the book "Structure and Interpretation of Classical Mechanics".

clj-tiles is perfect for all STEM domains. Given my background, mathematical finance comes to mind. Indeed, the examples shown in the video above are pure mathematics (from the book "Functional Differential Geometry").

Admittedly, the learning curve of these two books is very steep. That is way I created a somewhat simpler physics tutorial with my own descriptions and also hand-crafted puzzles (click [here](https://kloimhardt.github.io/cljtiles.html?page=116)).

This set of tutorials also contains the simplest chapter of all: completing Bob Dylan lyrics. This shows that the scope might be broader than just STEM fields. This was also the chapter I opened my talk with at the reClojure conference of 2021 (see [this youtube playlist](https://www.youtube.com/playlist?list=PLchX49hOw0Gapr28Gs4yUmJkuJWaRYXMn)).

External links: [Structure and Interpretation of Classical Mechanics](https://mitp-content-server.mit.edu/books/content/sectbyfn/books_pres_0/9579/sicm_edition_2.zip/chapter001.html), [Functional Differential Geometry](https://mitpress.mit.edu/books/functional-differential-geometry).

## Teaching
clj-tiles is suited for not only learining but especially also teaching STEM. Live coding in front of an audicence is made easy. The instructor just prepares puzzles and solves them in front of the audience. Typos like in editor based live coding are impossible. Students can follow along in real-time on thier own computers.

In the literature this method of learning and teaching is known as Parsons puzzle. With this, teaching itself becomes a subject of an interdisciplinary sociological study. Questionnaires and Control Groups can be used for assessing the efficacy of the method.

I tried to explain this more in a very preliminary draft of a [paper on visual programming](https://github.com/kloimhardt/werkbank/blob/master/latex/ga_pro_kla_mech.pdf).

External link: [Parsons puzzle](https://en.wikipedia.org/wiki/Parsons_problem)

## JAX and Functional Programming for the Win
The tutorials as well as the implementation of clj-tiles are based on Clojure, a functional programming language from the LISP family. The blocks of clj-tiles are meant to exclusively represent a functional notation. This is the all imprtant difference between clj-tiles and the multi-purpose visual building tools Scratch and BlockPy.

The other main difference is that clj-tiles is not meant for building programs but for understading already existing code.

Clojure is a niche language, as opposed to the very popular Python. I tried to show the advantages of the functional notation for STEM (compared to common Python syntax) in a [preprint on how to design a functional interface](https://arxiv.org/abs/2312.13295).

However, recently there has been introduced a Python libaray which rapidly gains popularity, namely JAX: "the kinds of program transformations that JAX enables are much more feasible in functional-style programs". Casually said, JAX is LISP bolted onto Python.

This opens up a fruitful future path for clj-tiles. It is perfectly feasible, via the Hy language, to generate JAX syntax out of the already existing block notation. In this way, STEM student are, while using popular JAX+Python, introduced to functional notation without any distraction.

External link: [Clojure](https://www.clojure.org), [BlockPy](https://think.cs.vt.edu/blockpy/) [JAX](https://jax.readthedocs.io/en/latest/jax-101/01-jax-basics.html#differences-from-numpy), [Hy](http://hylang.org)

## Gamification
In interactive experiences, points and achievements are not attributes of Gamification but hollow structures without content. To me, under the influence of Jon Blow and his game "The Witness", gamification is about plot building by extending oral language, by mimicry, by immediate feedback. The hand-crafted puzzles mentioned above are testimony to this.

Also, inspired by talking chess computers, text- and point-and-click adventure games, I experimented with the "inspect" feature of clj-tiles. The according interactive workspace appears when you click on this [clj-tiles link](https://kloimhardt.github.io/cljtiles.html?page=freeparticle).
![freeparticle](screenshots/pendulum_begin.png)

As this workspace is not yet self explanatory, I provide a [walkthrough video](https://www.youtube.com/watch?v=DHcZkmXKp04).

External link: [Jon Blow on youtube](https://www.youtube.com/watch?v=qWFScmtiC44), [Scarlett adventure](https://games.zuderstorfer.com/Adventure_1_en.html)

## Additional Features
### Inspect and Types


## Old Intro
> I read your treatise the way a curious child listens in suspense to the solution of a puzzle that has plagued him for a long time, and I delight in the beauties revealed to the eye.

> [Max Planck](https://phaidra.univie.ac.at/o:1542358) to Erwin Schrödinger on 2 April 1926

## Features

* Solve graphical [puzzles](https://kloimhardt.github.io/cljtiles.html) in the browser.
* Learn functional programming to your inner child.
* Over 100 puzzles built in. Start with simple examples, continue to basic web-programming and move to sophisticated examples taken from theoretical physics.
* Investigate puzzles with the built in value inspector.
* [Load more puzzles](https://kloimhardt.github.io/cljtiles.html?page=FDG001) from the web (and also extend them) with the built in parser.
* Create textfiles with whole new puzzle tutorials.
* Live coding made easy, just prepare a puzzle and solve it in front of the audience.

## Videos
* [Talk](https://www.youtube.com/watch?v=bHBALgxjeLo) presented at the [re-clojure](https://www.reclojure.org/2021/speaker/markus-kloimwieder) 2021 conference, with [panel discussion](https://www.youtube.com/watch?v=euH8iZGbJD8).
*  [Visual tools meeting](https://clojureverse.org/t/visual-tools-meeting-9-summary-video-clj-tiles-obsidian-wielder-cardigan-bay-platypub/9081) from July 2nd 2022. It shows the [first chapter](https://github.com/mentat-collective/fdg-book) of the open-access book [Functional Differential Geometry](https://mitpress.mit.edu/books/functional-differential-geometry), you can try it yourself [here](https://kloimhardt.github.io/cljtiles.html?page=FDG001).

* Earlier [demo video](https://www.youtube.com/watch?v=DHcZkmXKp04).

## Solve puzzles
The first few puzzles consist of completing the lyrics of a famous song. Then, the following coding puzzles are taken from the book [Getting Clojure](https://pragprog.com/titles/roclojure/getting-clojure/). Another resource is [Clojure for the Brave and True](https://www.braveclojure.com). Do you notice the similarities between writing code and poetry?

The most simple example looks like this:

![hello](screenshots/hello_world.png)

Just press the `Run` button, as this is already a fully functional program.

A [more sophisticated example](https://kloimhardt.github.io/cljtiles.html?page=freeparticle) looks like this:

![pendulum](screenshots/pendulum_begin.png)

After completion, this puzzle (which uses the built in [SICMutils](https://github.com/sicmutils/sicmutils) library) represents a working program. By right clicking on the individual blocks and `inspect`, you can trace the program execution and get some type information. This special puzzle also provides an experimental feature: the value inspector is enhanced to provide additional descriptions and explanations (see also the [demo video](https://www.youtube.com/watch?v=DHcZkmXKp04)).

## Load or create new puzzles

Open the parser by right clicking on a white space. Select a tutorial in the drop down menu: a URL appears in the text field. Press `insert`. You just loaded an new tutorial from the web. 

The URL in the text field can be changed, so you can provide your own files (they need to follow the format of the provided tutorials).

However, there is a more direct way to create new puzzles. Paste the following code into the text field:
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

The file [book_examples_1.cljs](https://github.com/kloimhardt/clj-tiles/blob/master/public/org/book_examples_1.cljs) contains code from the [SICMutils Jupyter notebook](https://github.com/sicmutils/sicmutils/blob/master/jupyter/book-examples.ipynb). It can be readily pasted into the clj-tiles parser. Reading the first pages of the book `Structure and Interpretation of Classical Mechanics` ([original](https://mitpress.mit.edu/sites/default/files/titles/content/sicm_edition_2/book.html), [reprint](https://tgvaughan.github.io/sicm/)) is mandatory for understanding.

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
 npm install --save-dev shadow-cljs
 npm install blockly
 npm install sax
 npm install odex
 npm install complex.js
 npm install fraction.js
 npm install mathjax@2
 
 shadow-cljs watch cljtiles
 ```
 then open `http://localhost:8080/cljtiles.html` in your browser.
 
## Related projects
* [ClojureBlocks](https://codeberg.org/jhandke/C# Clj-Tiles


* [Blockly](https://developers.google.com/blockly)
* [BlockPy](https://think.cs.vt.edu/blockpy/) 
* [blockoid](https://github.com/ParkerICI/blockoid)
* [Werkbank](https://github.com/kloimhardt/werkbank)
* [sicmutils](https://github.com/sicmutils/sicmutils)
* [SCI](https://github.com/borkdude/sci)
