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
          :code sol
          :solution sol)])

(def chapnames ["Advent"])
(def chaps [(count e-vect)])

(def content {:tutorials e-vect :chapnames chapnames :chaps chaps})
