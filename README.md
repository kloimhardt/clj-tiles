# clj-tiles
## Description
Visual programming for STEM students and instructors at university level.

If you like videos, watch my presentation for the [Visual Tools meeting](https://www.youtube.com/watch?v=m1HbWpWiTk4&t=506s).  
Make sure to interactively follow along with clj-tiles for [Visual Geometry](https://kloimhardt.github.io/cljtiles.html?page=FDG001).

Being describable as a mixture of Wordle and Scratch, clj-tiles is for every student who, at a young age, has been gotten hooked as a Princess of Science (see the episode with Prof. Johanna Pirker on ZDFtivi).

External links: [ZDFtivi](https://www.zdf.de/kinder/princess-of-science/diy-spielentwicklung-100.html), [STEM](https://en.wikipedia.org/wiki/Science,_technology,_engineering,_and_mathematics), [Scratch](https://scratch.mit.edu), [Wordle](https://www.nytimes.com/games/wordle/index.html)

## Features
* Solve over 100 graphical puzzles in the browser
* Get Wordle inspired feedback
* Live coding in the lecture room without the need of programming experience
* Learn functional programming to your inner child
* Load more puzzles from the web with the built in parser

## Roadmap
* Out of this documentation, being a conglomeration, make a peer-reviewed journal article
* Prepare a series of lectures on basic Linear Algebra
* Add as main calculation engine JAX + Python
* Perform a control group study to assess the efficacy of the lectures

A long-term research goal is the improvement of non-verbal plot building of the visual tutorials.

External links: [JAX](https://jax.readthedocs.io), [Python](https://www.python.org)

## Detailed Description

### Competence before Comprehension
As an introduction, we start with the following picture which appears in clj-tiles for [Visual Algebra](https://kloimhardt.github.io/cljtiles.html?org=https://raw.githubusercontent.com/kloimhardt/clj-tiles/master/public/org/sicm-book-vscheme-part1.org).
![cljtiles_polar_1](https://kloimhardt.github.io/blog/images/cljtiles_polar_1.png)

You notice the `Get the Puzzle` button and press it.
![cljtiles_polar_2](https://kloimhardt.github.io/blog/images/cljtiles_polar_2.png)

This looks rather complicated, but you start to notice that the content of the graphical blocks are the same as in the first picture. There is `R`, `phi`, `sin` and so on.
As a hint of how to proceed, we show (with arrows) the first steps to complete the puzzle.
![cljtiles_polar_4](https://kloimhardt.github.io/blog/images/cljtiles_polar_4.png)

After these first steps, you continue...
![cljtiles_polar_5](https://kloimhardt.github.io/blog/images/cljtiles_polar_5.png)

... and arrive at the following picture.
![cljtiles_polar_6](https://kloimhardt.github.io/blog/images/cljtiles_polar_6.png)

Interesting here is the `Color` button. It indicates that you are on the right track. It is modelled after Wordle. Can you finish the puzzle now? After all, you can look up the solution in the first picture above.

## Wordle-inspired feedback
When completing a puzzle, the colouring of the generated code changes gradually. As soon as an expression matches, it turns green. Shown below are the according stages of the above example.

![wordle_1](https://github.com/kloimhardt/clj-tiles/raw/master/screenshots/wordle_1.png)
![wordle_2](https://github.com/kloimhardt/clj-tiles/raw/master/screenshots/wordle_2.png)
![wordle_3](https://github.com/kloimhardt/clj-tiles/raw/master/screenshots/wordle_3.png)
![wordle_4](https://github.com/kloimhardt/clj-tiles/raw/master/screenshots/wordle_4.png)


## Comprehension of the Domain
The domain of the above "Visual Algebra" tutorial is known to any high school student: conversion of polar coordinates (`R` and `phi`) to the usual rectangular ones.

But this is just a start. By completing all the puzzle-workspaces of this visual tutorial, a physics student in the 3rd semester can learn Classical Mechanics. Indeed she first gets competent by solving the puzzles, maybe just learning them by heart. Then, she will gain a much deeper comprehension when reading more carefully the book "Structure and Interpretation of Classical Mechanics".

Admittedly, the learning curve of this book is very steep. That is way I created a somewhat simpler physics tutorial with my own descriptions and hand-crafted puzzles in the tutorial [Visual Motion](https://kloimhardt.github.io/cljtiles.html?page=116).

This set of tutorials also contains the simplest chapter of all: completing Bob Dylan lyrics. That chapter also shows that the scope of clj-tiles might be broader than STEM. It was also the chapter I opened my talk at the [reClojure conference 2021 (youtube playlist)](https://www.youtube.com/playlist?list=PLchX49hOw0Gapr28Gs4yUmJkuJWaRYXMn).

External link: [Structure and Interpretation of Classical Mechanics](https://mitp-content-server.mit.edu/books/content/sectbyfn/books_pres_0/9579/sicm_edition_2.zip/chapter001.html)

### Teaching
clj-tiles is suited not only for learning but especially also teaching STEM. Live coding in front of an audience is made easy. The instructor does not need to have any programming experience, he just solves puzzles in front of the audience. Typos like in editor based live coding are thus avoided. Students can follow along in real-time on their own computers.

In the literature this method of learning and teaching is known as Parsons puzzle. With this, teaching itself becomes a subject of an interdisciplinary sociological study. Questionnaires and Control Groups can be used to assess the efficacy of the method.

I tried to more explain this topic in the context of teaching advanced physics classes in an early preliminary [draft for a paper on visual programming](https://github.com/kloimhardt/werkbank/blob/master/latex/ga_pro_kla_mech.pdf).

External link: [Parsons puzzle](https://en.wikipedia.org/wiki/Parsons_problem)

### JAX and Functional Programming for the Win
The implementation of the clj-tiles app is based on Clojure, a functional programming language from the LISP family. Indeed, many tutorials in clj-tiles are for learning this great language, starting with ![hello](https://github.com/kloimhardt/clj-tiles/raw/master/screenshots/hello_world.png). More advanced tutorials make use of the splendid Emmy Computer Algebra System.

The blocks of clj-tiles are meant to exclusively represent a functional notation. This is the all important difference between clj-tiles and the visual building tools Scratch and BlockPy.

As opposed to the very popular Python, Clojure is a niche language. Nevertheless, I tried to show, in comparison to common Python syntax, the advantages of the functional notation for STEM in a [preprint article on how to design a functional interface](https://arxiv.org/abs/2312.13295).

However, recently there has been introduced a Python library which rapidly gains popularity and embraces functional notation, namely JAX: "the kinds of program transformations that JAX enables are much more feasible in functional-style programs". Casually said, JAX is Clojure bolted onto Python.

JAX+Python opens up a fruitful future path for clj-tiles. It is perfectly feasible, via the Hy language, to generate JAX syntax out of the already existing block notation. In this way, STEM students are immediately introduced to functional notation via clj-tiles whilst being able to switch into any professional Python development environment at will as clj-tiles is not meant for building programs but for understanding already existing code.

External link: [Clojure](https://www.clojure.org), [Emmy Computer Algebra System](https://github.com/mentat-collective/emmy) [BlockPy](https://think.cs.vt.edu/blockpy/) [functional-style JAX](https://jax.readthedocs.io/en/latest/jax-101/01-jax-basics.html#differences-from-numpy), [Hy](http://hylang.org)

### Gamification
In interactive experiences, points, badges and achievements are not attributes of true Gamification but rather hollow structures without content. To me, gamification is about non-verbal plot building, encouraging mimicry and providing immediate feedback. The Wordle inspiration and most of all the hand-crafted puzzles mentioned above are testimony to this.

In clj-tiles, the user first needs to solve a puzzle before getting the solution to the next one. Another way of awarding the user with content is the `Shuffle` button which makes solving much harder. There is also a cheat mode which in principle can be deduced from the in-game descriptions (and spoilt in the "Visual Tools" video above). Among my inspirations for all this is the video game "The Witness", especially the included "Secret of Psalm 46".

Also, inspired by talking chess computers, text- and point-and-click adventure games, I experimented with the "inspect" feature of clj-tiles. The according workspace appears in clj-tiles for [Visual Interaction](https://kloimhardt.github.io/cljtiles.html?page=freeparticle).
![freeparticle](https://github.com/kloimhardt/clj-tiles/raw/master/screenshots/pendulum_begin.png)

As this workspace is not yet self explanatory, I provide a [walkthrough video](https://www.youtube.com/watch?v=DHcZkmXKp04).

External link: [Witness creator on youtube](https://www.youtube.com/watch?v=qWFScmtiC44), [The Secret of Psalm 46](https://web.archive.org/web/20200422041019/http://ludix.com/moriarty/psalm46.html), [Scarlett adventure](https://games.zuderstorfer.com/Adventure_1_en.html)

## Parser, Execution and Block Inspection

While by pressing the `Run` button a whole workspace is executed, the user can inspect individual blocks by right clicking on them. The result of the expression represented by the respective block is displayed. Indicated along with the result is the type of the data.

You can load a new tutorial from the web. Open the dedicated parser by right clicking on a white space. Select a tutorial in the drop down menu: a URL appears in the text field. Press insert.

There is also a more direct way to create new puzzles. Paste the following code into the text field:

```
(sort [2 1 3])
```

More on the parser can be found in the

## Technical Details
which are in [this document](https://github.com/kloimhardt/clj-tiles/raw/master/screenshots/technical_details.md).

[GPL v3](LICENSE) Copyright © 2021 Markus Kloimwieder
