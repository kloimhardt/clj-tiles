(ns cljtiles.tutorials-advent1
  (:require [cljtiles.codeexplode :as explode]))

(def sol1 ['(def book
             {:title "Getting Clojure",
              :author "Russ Olson",
              :published 2018})])

(def sol2 [
          '(:published book)])

(def e-vect
  [(assoc (explode/explode sol1)
          :solpos-yx [[0 0] [100 0] [200 0]]
          :solution sol1)
   (assoc (explode/explode sol2)
          :solpos-yx [[0 0] [100 0] [200 0]]
          :solution sol2)])

(def chapnames ["Advent"])
(def chaps [(count e-vect)])

(def content {:tutorials e-vect :chapnames chapnames :chaps chaps})
