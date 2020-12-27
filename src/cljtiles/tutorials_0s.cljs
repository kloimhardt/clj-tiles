(ns cljtiles.tutorials-0s
  (:require [cljtiles.genblocks :as gb]))

(def vect-code
  [["Hello, World!"]
   ["Hello, World!"
    "I feel great."]
   ['(println "Hello, World!")
    "I feel great."]
   ['(println "Hello, World!")]
   ['(str "Clo" "jure")]
   ['(str "Hello," " " "World" "!")]
   ['(str 3
          " " 2
          " " (str 1 " " "Blast off!"))]
   ["Hello"
    "Hello, World!"
    '(count " ")]
   [false
    '(println true)]
   ['(println "Nobody's home:" nil)]
   ['(println "We can print many things:" nil
              true false)]
   ['(/ (+ 20 40.0) 2)]
   ['(def the-average (/ (+ 20 40.0) 2))
    '(println the-average)]
   ['(defn say-welcome
       [what]
       (println "Welcome to" what))
    '(say-welcome "Clojure")]
   ['(defn say-welcome
       [what]
       (println "Welcome to" what))
    '(say-welcome "Clojure")]
   ['(defn average [a b] (/ (+ a b) 2))
    '(average 5.0 10.0)]
   ['(defn chatty-average
       [a b]
       (do (println
             "chatty-average function called")
           (println "** first argument:" a)
           (println "** second argument:" b)
           (/ (+ a b) 2)))
    '(chatty-average 5.0 10.0)]
   [[1 2 3 4]]
   [[1 "two" 3 "four"]]
   [[true 3 "four" 5]]
   [[1 [true 3 "four" 5] 6]]
   [[0 [1 [true 3 "four" 5] 6] 7]]
   [(vector true 3 "four" 5)]
   ['(def books
       ["Emma" "Getting Clojure"
        "War and Peace"])
    '(count books)]
   ['(def books
       ["Emma" "Getting Clojure"
        "War and Peace"])
    '(first books)
    '(nth books 1)]
   ['(defn vec-rest
       "added by Blockly parser"
       [x]
       (let [r (rest x)]
         (if (seq? r) (vec r) r)))
    '(def books
       ["Emma" "Getting Clojure"
        "War and Peace"])
    '(vec-rest books)]
   ['(defn vec-rest
       "added by Blockly parser"
       [x]
       (let [r (rest x)]
         (if (seq? r) (vec r) r)))
    '(def books
       ["Emma" "Getting Clojure"
        "War and Peace"])
    '(vec-rest (vec-rest books))]
   ['(defn vec-rest
       "added by Blockly parser"
       [x]
       (let [r (rest x)]
         (if (seq? r) (vec r) r)))
    '(vec-rest ["Getting Clojure"])]
   ['(def books
       ["Emma" "Getting Clojure"
        "War and Peace"])
    '(conj books "Carrie")]
   ['(defn vec-cons
       "added by Blockly parser"
       [x coll]
       (let [c (cons x coll)]
         (if (seq? c) (vec c) c)))
    '(def books
       ["Emma" "Getting Clojure"
        "War and Peace"])
    '(vec-cons "Carrie" books)]
   ['(def books
       ["Emma" "Getting Clojure"
        "War and Peace"])
    '(def more-books (conj books "Carrie"))
    '(println books)
    'more-books]
   ['(hash-map "title" "Getting Clojure"
               "author" "Russ Olson"
               "published" 2018)]
   ['(def book
       (hash-map "title" "Getting Clojure"
                 "author" "Russ Olson"
                 "published" 2018))
    '(get book "published")]
   [{:author "Russ Olson",
     :published 2018,
     :title "Getting Clojure"}]
   ['(def book
       {:title "Getting Clojure",
        :author "Russ Olson",
        :published 2018})
    '(:published book)]
   ['(def book
       {:title "Getting Clojure",
        :author "Russ Olson",
        :published 2018})
    '(:title book)]
   ['(def book
       {:title "Getting Clojure",
        :author "Russ Olson",
        :please-rename-me 2018})
    '(:please-rename-me book)]
   ['(def great-book
       {:title "Getting Clojure",
        :author "Russ Olson",
        :year 2018})
    '(assoc great-book :page-count 270)]
   ['(def great-book
       {:title "Getting Clojure",
        :author "Russ Olson",
        :year 2018})
    '(assoc great-book
            :title "Clojure for the brave and true"
            :page-count 270)]
   ['(def great-book
       {:title "Getting Clojure",
        :author "Russ Olson",
        :year 2018})
    :title
    '(println great-book)
    '(dissoc great-book
             :author
             :favorite-zoo-animal)]
   ['(println @app-state)
    '(inc @app-state)
    '(println @app-state)]
   ['(println @app-state)
    '(swap! app-state inc)
    '(println @app-state)]
   [[:div [:p "Hello, World!"]]]
   [[:div [:hr] [:h1 "Being in the World"]
     [:hr]]]
   [[:div [:hr] [:h1 "Being in the World"]
     [:button
      "Hello, World. I don't do much."]
     [:hr]]]
   [[:div [:hr] [:h1 "Being in the World"]
     [:button
      {:id "first-button", :on-click nil}
      "Hello, World. I still don't do much."]
     [:hr]]]
   ['(defn click-function
      [ ]
      (println @app-state))
    [:div [:hr] [:h1 "Being in the World"]
     [:button
      {:id "first-button",
       :on-click 'click-function}
      "Hello, World. I print zero in the Output area"]
     [:hr]]]
   ['(defn click-function
      [ ]
      (do (swap! app-state inc)
          (println @app-state)))
    [:div [:hr] [:h1 "Being in the World"]
     [:button
      {:id "first-button",
       :on-click 'click-function}
      "Hello, World. I count!"] [:hr]]]
   ['(defn click-function
      [ ]
      (do (swap! app-state inc)
          (println @app-state)))
    [:div [:hr] [:h1 "Being in the World"]
     [:button
      {:id "first-button",
       :on-click 'click-function}
      "Hello, World. I obviously count."]
     '(str " " @app-state) [:hr]]]
   ['(defn click-function
      [ ]
      (swap! app-state inc))
    '(defn start-rocket
      [ ]
      (start-timer click-function
                   30
                   200
                   "Timer started"))
    '(reset! app-state 0)
    '[:div
     [:button
      {:id "launch", :on-click start-rocket}
      "Launch rocket"]
     [:svg {:height 200, :id "rocket"}
      [:image
       {:href "rocket.png",
        :x 50,
        :y (- 160 @app-state)}]]]]
   ])

(def vect
  (apply gb/chapter (map #(apply gb/rpg [] %) vect-code))
   )
