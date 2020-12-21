(ns cljtiles.tutorials-0
  (:require [cljtiles.genblocks :as gb]))

(def vect
  [(gb/xml [(gb/text "Hello, World!") 10 10])
   (gb/xml [(gb/text "Hello, World!") 10 10]
           [(gb/text "I feel great.") 10 50])
   (gb/xml [(gb/fun-inp "println" 2 (gb/text "Hello, World!")) 10 10]
           [(gb/text "I feel great.") 10 60])
   (gb/xml [(gb/text "Hello World!") 10 60]
           [(gb/fun-inp "println" 2) 10 10])
   (gb/xml [(gb/fun-inp "str" 3) 10 10]
           [(gb/text "Clo") 10 50]
           [(gb/text "jure") 10 90])
   (gb/xml [(gb/text "World") 10 10]
           [(gb/text " ") 150 10]
           [(gb/fun-inp "str" 5) 10 70]
           [(gb/text "!") 10 130]
           [(gb/text "Hello,") 90 130])])
