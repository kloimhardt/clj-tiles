(ns cljtiles.tutorials-sicm
  (:require [cljtiles.genblocks :as gb]))

(def chaps [1])
(def chapnames ["sicm"])

(def e-vect
  ["desc 1"
   (gb/rpg []
           '(defn Path-of-a-Free-Particle time :tiles/slot)
           )

   "desc 2"
   (gb/rpg []
           '(defn Path-of-a-Free-Particle :tiles/slot :tiles/slot)
           )

   "desc 3"
   (gb/rpg []
           '(defn :tiles/slot :tiles/slot :tiles/slot)
           )]
  )

(def desc (take-nth 2 e-vect))
(def vect (take-nth 2 (rest e-vect)))
