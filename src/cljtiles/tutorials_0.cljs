(ns cljtiles.tutorials-0
  (:require [cljtiles.genblocks :as gb]))

(def vect
  (let [hw {:type :text :dertext "Hello, World!"}
        ifg {:type :text :dertext "I feel great."}
        prnln {:type :funs-h-2-inp :x 10 :y 10 :kopf "println" :args-2 hw}]
    [(gb/xml [hw 10 10])
     (gb/xml [hw 10 10] [ifg 10 50])
     (gb/xml [prnln 10 10] [ifg 10 60])]))
