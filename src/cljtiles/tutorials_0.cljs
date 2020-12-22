(ns cljtiles.tutorials-0
  (:require [cljtiles.genblocks :as gb]))

(def vect
  [(gb/xml [(gb/text "Hello, World!") 10 10])
   (comment
     (gb/xml [(gb/text "Hello, World!") 10 10]
             [(gb/text "I feel great.") 10 50])
     (gb/xml [(gb/fun "println" 2 (gb/text "Hello, World!")) 10 10]
             [(gb/text "I feel great.") 10 60])
     (gb/xml [(gb/text "Hello World!") 10 10]
             [(gb/fun "println" 2) 10 60])
     (gb/xml [(gb/fun "str" 3) 10 10]
             [(gb/text "Clo") 10 50]
             [(gb/text "jure") 10 90])
     (gb/xml [(gb/text "World") 10 10]
             [(gb/text " ") 150 10]
             [(gb/fun "str" 5) 10 70]
             [(gb/text "!") 10 130]
             [(gb/text "Hello,") 90 130])
     (gb/xml [(gb/num 1) 10 10]
             [(gb/num 2) 50 10]
             [(gb/text " ") 100 10]
             [(gb/text " ") 150 10]
             [(gb/fun "str" 6 (gb/num 3) gb/null gb/null (gb/text " ")
                      ) 10 50]))])
