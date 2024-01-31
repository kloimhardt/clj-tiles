## Technical Details
[back to main document](README.md)
### Parser

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

### Inspect and a philosophical remark on Types
By right clicking on the individual blocks and inspect, you can trace the program execution and get some type information.

[back to main document](README.md)
