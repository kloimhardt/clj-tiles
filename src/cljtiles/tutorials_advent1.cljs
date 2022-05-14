(ns cljtiles.tutorials-advent1
  (:require [cljtiles.codeexplode :as explode]))

(def sol ['(def book
             {:title "Getting Clojure",
              :author "Russ Olson",
              :published 2018})
          [1 2 3 4 5]
          '(:published book)])

(def e-vect
  [(assoc (explode/explode sol)
          :solpos-yx [[0 0] [100 0] [200 0]]
          :solution sol)
   (assoc (explode/explode sol)
          :solpos-yx [[0 0] [100 0] [200 0]]
          :solution sol)])

(def chapnames ["Advent"])
(def chaps [(count e-vect)])

(def content {:tutorials e-vect :chapnames chapnames :chaps chaps})
