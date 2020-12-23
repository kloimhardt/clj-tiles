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
          (gb/num "average")


          )



   ))
