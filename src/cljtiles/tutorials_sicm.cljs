(ns cljtiles.tutorials-sicm
  (:require [cljtiles.genblocks :as gb]))

(def vect
  (gb/chapter
    (gb/rpg []
            '(defn hui [] (up 5 6))
            '(kind? (up 1 2 3))
            '(kind? 3)
            '(kind? 'apple)
            '(kind? hui)
            )))
