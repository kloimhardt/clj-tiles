(ns cljtiles.tutorials-sicm
  (:require [cljtiles.genblocks :as gb]))

(def vect
  [
   (gb/rpg []
           '(defn Path-of-a-Free-Particle time :tiles/slot)
           )
   (gb/rpg []
           '(defn Path-of-a-Free-Particle :tiles/slot :tiles/slot)
           )
   (gb/rpg []
           '(defn :tiles/slot :tiles/slot :tiles/slot)
           )]
  )
