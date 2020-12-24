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
   (gb/rpg [[0 0] [0 150] [0 200]]
           '(def books
              (:tiles/fvert
               (println
                 "Hi! Please copy+paste the Vector block from the previous workspace."
                 "Go to the previous workspace, left-click on the blue area, so that it has a yellow border. Then press Ctrl+C (maybe cmd+c works on your keyboard)."
                 "Return to this workspace here, press Ctrl+V (or cmd+v). Make sure to remove the println block before pasting (remove it by right clicking in the blue area)")))
           'books
           '(vec-rest :tiles/slot))
   (gb/rpg [[0 0] [0 100] [0 150] [0 200]]
           '(def books ["Emma" "Getting Clojure" "War and Peace"])
           'books
           '(vec-rest :tiles/slot)
           '(vec-rest :tiles/slot))
   (gb/rpg []
           [:tiles/slot]
           "Getting Clojure"
           '(vec-rest :tiles/slot))
   (gb/rpg [[0 0] [0 100] [0 150] [0 200]]
           '(def books ["Emma" "Getting Clojure" "War and Peace"])
           "Carrie"
           '(conj :tiles/slot :tiles/slot)
           'books)
   (gb/rpg [[0 0] [100 50] [0 100] [0 150] [0 200]]
           '(def books :tiles/slot)
           "copy+paste the Vector from previous workspace"
           "Carrie"
           '(vec-cons :tiles/slot :tiles/slot)
           'books)
   (gb/rpg [[0 0] [0 100] [0 150] [0 250] [0 300] [100 300] [200 300]]
           '(def books ["Emma" "Getting Clojure" "War and Peace"])
           '(conj books "Carrie")
           '(def :tiles/slot :tiles/slot)
           '(println :tiles/slot)
           'books
           'more-books
           'more-books)
   (gb/rpg [[100 0] [0 20] [300 20] [150 50] [0 100] [300 100] [100 250]]
           "Getting Clojure"
           "title"
           2018
           '(:tiles/fvert (hash-map :tiles/slot :tiles/slot :tiles/slot
                                    :tiles/slot :tiles/slot :tiles/slot))
           "Russ Olson"
           "published"
           "author")
   (gb/rpg [[0 0] [0 200] [100 200] [200 200] [0 250] [0 350]]
           '(:tiles/fvert (hash-map "title" "Getting Clojure" "author"
                                    "Russ Olson" "published" 2018))
           'book
           'book
           "published"
           '(def :tiles/slot :tiles/slot)
           '(get :tiles/slot :tiles/slot))
   (gb/page (gb/shift-coords 4 [0 0] [0 50] [150 50] [0 150])
            (gb/text "Russ Olson")
            (gb/t-map-vert [:title gb/slot] [:author gb/slot] [:published gb/slot])
            (gb/num 2018)
            (gb/text "Getting Clojure")
            )

   ))
