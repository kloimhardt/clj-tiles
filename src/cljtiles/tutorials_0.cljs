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
          ["println" gb/slot])
   (gb/pg [[0 0] [0 50] [0 100]]
          ["str" gb/slot gb/slot]
          "Clo"
          "jure")
   (gb/pg [[0 0] [100 0] [0 50] [0 100] [100 100]]
          "World" " "
          ["str" gb/slot gb/slot gb/slot gb/slot]
          "!" "Hello,")
   (gb/pg [[0 0] [50 0] [100 0] [150 0] [0 50]]
          1 2 " " " "
          ["str" 3 gb/slot gb/slot " " ["str" gb/slot gb/slot "Blast off!"]])
   (gb/pg [[0 0] [0 50] [150 50] [0 100] [0 150] [100 150]]
          "Hello"
          ["count" gb/slot] " "
          "Hello World!"
          ["count" gb/slot] ["count" gb/slot])
   (gb/pg [[0 0] [0 50] [0 100]]
          false
          ["println" true]
          ["println" gb/slot])
   (gb/pg [[0 0] [0 50]]
          ["println" "Nobody's home" gb/slot]
          nil)
   (gb/pg [[0 0] [0 50] [0 100] [100 100]]
          nil
          ["println" "We can print many things:" gb/slot gb/slot gb/slot]
          true false)
   (gb/pg [[0 0] [0 50] [150 50]]
          ["/" gb/slot 2]
          ["+" gb/slot (gb/num "40.0")] 20)
   (gb/pg [[0 0] [0 50] [0 150] [0 200]]
          (gb/num "the-average")
          ["def" gb/slot ["/" ["+" 20 (gb/num "40.0")] 2]]
          (gb/num "the-average")
          ["println" gb/slot])
   (gb/pg [[0 0] [0 50] [0 150] [0 200]]
          (gb/num "what")
          ["defn" (gb/num "say-welcome")
           gb/slot
           ["println" "Welcome to" gb/slot]]
          (gb/num "what")
          ["say-welcome" "Clojure"])
   (gb/pg [[0 0] [0 50] [0 150] [150 150] [0 200] [0 250] [150 250]]
          (gb/num "what")
          ["defn" (gb/num "say-welcome") gb/slot gb/slot]
          ["println" gb/slot gb/slot]
          (gb/num "what")
          ["say-welcome" gb/slot]
          "Welcome to"
          "Clojure")
   (gb/pg [[0 0] [0 50] [0 120] [150 120] [0 220] [50 220] [0 260]]
          ["average" 5 10]
          ["/" ["+" (gb/num "a") (gb/num "b")] 2]
          ["defn" gb/slot gb/slot gb/slot]
          (gb/args gb/slot gb/slot)
          (gb/num "a")
          (gb/num "b")
          (gb/num "average"))
   (gb/pg [[0 0] [170 0] [300 50] [350 100] [0 140] [0 200] [0 250] [0 300]]
          ["defn" gb/slot gb/slot gb/slot]
          ["do" ["println" "chatty-average function called"]
           gb/slot gb/slot gb/slot]
          (gb/args (gb/num "a") (gb/num "b"))
          (gb/num "chatty-average")
          ["/" ["+" (gb/num "a") (gb/num "b")] 2]
          ["println" "** first argumnet " (gb/num "a")]
          ["println" "** second argumnet " (gb/num "b")]
          ["chatty-average" (gb/num "5.0") (gb/num "10.0")])
   (gb/pg []
          4
          (gb/args (gb/num 1) gb/slot (gb/num 3) gb/slot)
          2)
   (gb/pg []
          "four"
          (gb/args (gb/num 1) gb/slot (gb/num 3) gb/slot)
          "two")
   (gb/pg []
          5
          (gb/args gb/slot (gb/num 3)  (gb/text "four") gb/slot)
          true)
   (gb/pg []
          (gb/args (gb/num 1) gb/slot gb/slot)
          6
          (gb/args (gb/num true) (gb/num 3)  (gb/text "four") (gb/num 5)))
   (gb/pg []
          (gb/args gb/slot gb/slot gb/slot)
          0
          (gb/args (gb/num 1)
                   (gb/args (gb/num true) (gb/num 3)  (gb/text "four") (gb/num 5))
                   (gb/num 6))
          7)
   (gb/pg []
          3
          "four"
          ["vector" gb/slot gb/slot gb/slot gb/slot]
          true
          5)
   (gb/pg []
          (gb/num "books")
          "Getting Clojure"
          (gb/num "books")
          ["def" gb/slot (gb/args (gb/text "Emma") gb/slot (gb/text "War and Peace"))]
          ["count" gb/slot]

          )
   ))
