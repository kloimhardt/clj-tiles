(ns cljtiles.tutorials-advent1)

(def e-vect
  [{:blockpos [[0 0] [0 150]]
    :code ['(def book
              {:title "Getting Clojure",
               :author "Russ Olson",
               :published 2018})
           [1 2 3 4 5]
           '(:published book)]
    :solution ['(def book
                  {:title "Getting Clojure",
                   :author "Russ Olson",
                   :published 2018})
               [1 2 3 4 5]
               '(:published book)]}])

(def chapnames ["Adevent"])
(def chaps [(count e-vect)])

(def content {:tutorials e-vect :chapnames chapnames :chaps chaps})
