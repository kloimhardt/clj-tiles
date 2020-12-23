(ns cljtiles.tutorials-0
  (:require [cljtiles.genblocks :as gb]))

(def vect
  (gb/chapter
   (gb/page (gb/shift-coords 1 [0 0])
            (gb/text "Hello, World!"))
   (gb/page (gb/shift-coords 2 [0 0] [0 50])
            (gb/text "Hello, World!")
            (gb/text "I feel great."))
   (gb/page (gb/shift-coords 2)
            (gb/fun "println" (gb/text "Hello, World!"))
            (gb/text "I feel great."))
   (gb/pg [[0 0] [0 50]]
          "Hello, World!"
          ["println" gb/slot])
   (gb/pg [[0 0] [0 50] [0 100]]
          ["str" gb/slot gb/slot]
          "Clo"
          "jure")
   (gb/pg [[0 0] [100 0] [0 50] [0 100] [100 100]]
          "World" " "
          ["str" gb/slot gb/slot gb/slot gb/slot]
          "Hello," "!" )
   (gb/pg [[0 0] [50 0] [100 0] [150 0] [0 50]]
          1 2 " " " "
          ["str" 3 gb/slot gb/slot " " ["str" gb/slot gb/slot "Blast off!"]])
   (gb/pg [[0 0] [0 50] [150 50] [0 100] [0 150] [100 150]]
          "Hello"
          ["count" gb/slot] " "
          "Hello, World!")
   (gb/pg [[0 0] [0 50] [0 100]]
          false
          ["println" true])
   (gb/pg [[0 0] [0 50]]
          ["println" "Nobody's home:" gb/slot]
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
          ["average" (gb/num "5.0") (gb/num "10.0")]
          ["/" ["+" (gb/num "a") (gb/num "b")] 2]
          ["defn" gb/slot gb/slot gb/slot]
          (gb/args gb/slot gb/slot)
          (gb/num "b")
          (gb/num "a")
          (gb/num "average"))
   (gb/rpg [[0 0] [170 0] [300 50] [350 100] [0 140] [0 200] [0 250] [0 300]]
           '(defn :tiles/slot :tiles/slot :tiles/slot)
           '(do (println "chatty-average function called")
                :tiles/slot :tiles/slot :tiles/slot)
           '[a b]
           'chatty-average
           '(/ (+ a b) 2)
           '(println "** first argument:" a)
           '(println "** second argument:" b)
           '(chatty-average (:tiles/num "5.0") (:tiles/num "10.0")))
   (gb/rpg []
           4
           [1 :tiles/slot 3 :tiles/slot]
           2)
   (gb/rpg []
           "four"
           [1 :tiles/slot 3 :tiles/slot]
           "two")
   (gb/rpg []
           5
           [:tiles/slot 3 "four" :tiles/slot]
           true)
   (gb/rpg []
           [1 :tiles/slot :tiles/slot]
           6
           [true 3 "four" 5])
   (gb/rpg []
           [:tiles/slot :tiles/slot :tiles/slot]
           0
           [1 [true 3 "four" 5] 6]
           7)
   (gb/rpg []
           3
           "four"
           '(vector :tiles/slot :tiles/slot :tiles/slot :tiles/slot)
           true
           5)
   (gb/rpg [[0 0] [0 50] [0 100] [0 150] [0 250]]
           'books
           "Getting Clojure"
           'books
           '(def :tiles/slot ["Emma" :tiles/slot "War and Peace"])
           '(count :tiles/slot))
   (gb/rpg [[0 0] [0 100] [0 150] [0 200]]
           '(def books ["Emma" "Getting Clojure" "War and Peace"])
           '(first :tiles/slot)
           'books
           '(nth books 1))
   (gb/rpg []
           '(def books
              (println "a" "b" "c")))

   ))
