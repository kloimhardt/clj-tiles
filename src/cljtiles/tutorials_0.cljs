(ns cljtiles.tutorials-0
  (:require [cljtiles.genblocks :as gb]))

(def vect
  (gb/chapter
   (gb/pg [[0 0]]
          "Hello, World!")
   (gb/pg [[0 0] [0 50]]
          "Hello, World!"
          "I feel great.")
   (gb/pg [[0 0] [0 50]]
          ["println" "Hello, World!"]
          "I feel great.")
   (gb/pg [[0 0] [0 50]]
          "Hello World!"
          ["println" :slot])
   (gb/pg [[0 0] [0 50] [0 100]]
          ["str" :slot :slot]
          "Clo"
          "jure")
   (gb/pg [[0 0] [100 0] [0 50] [0 100] [100 100]]
          "World" " "
          ["str" :slot :slot :slot :slot]
          "!" "Hello,")
   (gb/pg  [[0 0] [50 0] [100 0] [150 0] [0 50]]
           1 2 " " " "
           ["str" 3 :slot :slot " " ["str" :slot :slot "Blast off!"]])
   (gb/pg [[0 0] [0 50] [150 50] [0 100] [0 150] [100 150]]
          "Hello"
          ["count" :slot] " "
          "Hello World!"
          ["count" :slot] ["count" :slot])
   (gb/pg  [[0 0] [0 50] [0 100]]
           false
           ["println" true]
           ["println" :slot])
   (gb/pg [[0 0] [0 50]]
          ["println" "Nobody's home" :slot]
          nil)
   (gb/pg [[0 0] [0 50] [0 100] [100 100]]
            nil
            ["println" "We can print many things:" :slot :slot :slot]
            true false)
   (gb/page (gb/coords [0 0] [0 50] [150 50])
            (gb/fun-inli "/" gb/slot (gb/num 2))
            (gb/fun-inli "+" gb/slot (gb/num "40.0"))
            (gb/num 20))
   (gb/page (gb/coords [0 0] [0 50] [0 150] [0 200])
            (gb/num "the-average")
            (gb/fun-vert "def"
                         gb/slot
                         (gb/fun-inli "/"
                                      (gb/fun-inli "+"
                                                   (gb/num 20)
                                                   (gb/num "40.0"))
                                      (gb/num 2)))
            (gb/num "the-average")
            (gb/fun "println" gb/slot))))
