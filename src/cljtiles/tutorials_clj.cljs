(ns cljtiles.tutorials-clj
  (:require [cljtiles.genblocks :as gb]))

(def chaps [11 6 14 9 9 1])

(def chapnames [" I" "II" "III" "IV" " V" "Rocket"])

(def scroll [])
(def desc [])

(def vect
  (gb/chapter
   (gb/page (gb/shift-coords 1 [0 0])
            (gb/text "Hello, World!"))
   (gb/page (gb/shift-coords 2 [0 0] [0 50])
            (gb/text "Hello, World!")
            (gb/text "I feel great."))
   (gb/page (gb/shift-coords 2)
            (gb/fun "println" (gb/text "Hello, World!"))
            (gb/text "I feel great."))
   (gb/pg [[0 0] [0 50]]
          "Hello, World!"
          ["println" gb/slot])
   (gb/pg [[0 0] [0 50] [0 100]]
          ["str" gb/slot gb/slot]
          "Clo"
          "jure")
   (gb/pg [[0 0] [100 0] [0 50] [0 100] [100 100]]
          "World" " "
          ["str" gb/slot gb/slot gb/slot gb/slot]
          "Hello," "!")
   (gb/pg [[0 0] [50 0] [100 0] [200 0] [0 50]]
          1 2 " " " "
          ["str" 3 gb/slot gb/slot " " ["str" gb/slot gb/slot "Blast off!"]])
   (gb/pg [[0 0] [0 50] [150 50] [0 100] [0 150] [100 150]]
          "Hello"
          ["count" gb/slot] " "
          "Hello, World!")
   (gb/pg [[0 0] [0 50] [0 100]]
          false
          ["println" true])
   (gb/pg [[0 0] [0 50]]
          ["println" "Nobody's home:" gb/slot]
          nil)
   (gb/pg [[0 0] [0 50] [0 100] [100 100]]
          nil
          ["println" "We can print many things:" gb/slot gb/slot gb/slot]
          true false)
   (gb/pg [[0 0] [0 50] [150 50]]
          ["/" gb/slot 2]
          ["+" gb/slot (gb/num "40.0")] 20)
   (gb/pg [[0 0] [0 50] [0 150] [0 200]]
          (gb/num "the-average")
          ["def" gb/slot ["/" ["+" 20 (gb/num "40.0")] 2]]
          (gb/num "the-average")
          ["println" gb/slot])
   (gb/pg [[0 0] [0 50] [0 150] [0 200]]
          (gb/num "what")
          ["defn" (gb/num "say-welcome")
           gb/slot
           ["println" "Welcome to" gb/slot]]
          (gb/num "what")
          ["say-welcome" "Clojure"])
   (gb/pg [[0 0] [0 50] [0 150] [150 150] [0 200] [0 250] [150 250]]
          (gb/num "what")
          ["defn" (gb/num "say-welcome") gb/slot gb/slot]
          ["println" gb/slot gb/slot]
          (gb/num "what")
          ["say-welcome" gb/slot]
          "Welcome to"
          "Clojure")
   (gb/pg [[0 0] [0 50] [0 120] [150 120] [0 220] [50 220] [0 260]]
          ["average" (gb/num "5.0") (gb/num "10.0")]
          ["/" ["+" (gb/num "a") (gb/num "b")] 2]
          ["defn" gb/slot gb/slot gb/slot]
          (gb/args gb/slot gb/slot)
          (gb/num "b")
          (gb/num "a")
          (gb/num "average"))
   (gb/rpg [[0 0] [170 0] [300 50] [350 100] [0 140] [0 200] [0 250] [0 300]]
           '(defn :tiles/slot :tiles/slot :tiles/slot)
           '(do (println "chatty-average function called")
                :tiles/slot :tiles/slot :tiles/slot)
           '[a b]
           'chatty-average
           '(/ (+ a b) 2)
           '(println "** first argument:" a)
           '(println "** second argument:" b)
           '(chatty-average (:tiles/num "5.0") (:tiles/num "10.0")))
   (gb/rpg []
           4
           [1 :tiles/slot 3 :tiles/slot]
           2)
   (gb/rpg []
           "four"
           [1 :tiles/slot 3 :tiles/slot]
           "two")
   (gb/rpg []
           5
           [:tiles/slot 3 "four" :tiles/slot]
           true)
   (gb/rpg []
           [1 :tiles/slot :tiles/slot]
           6
           [true 3 "four" 5])
   (gb/rpg []
           [:tiles/slot :tiles/slot :tiles/slot]
           0
           [1 [true 3 "four" 5] 6]
           7)
   (gb/rpg []
           3
           "four"
           '(vector :tiles/slot :tiles/slot :tiles/slot :tiles/slot)
           true
           5)
   (gb/rpg [[0 0] [0 50] [0 100] [0 150] [0 250]]
           'books
           "Getting Clojure"
           'books
           '(def :tiles/slot ["Emma" :tiles/slot "War and Peace"])
           '(count :tiles/slot))
   (gb/rpg [[0 0] [0 100] [0 150] [0 200]]
           '(def books ["Emma" "Getting Clojure" "War and Peace"])
           '(first :tiles/slot)
           'books
           '(nth books 1))
   (gb/rpg [[0 0] [0 150] [0 200]]
           '(def books
              (:tiles/vert
               (println
                "Hi! Please copy+paste the Vector block from the previous workspace."
                "Go to the previous workspace, left-click on the blue area, so that it has a yellow border. Then press Ctrl+C (maybe cmd+c works on your keyboard)."
                "Return to this workspace here, press Ctrl+V (or cmd+v). Make sure to remove the println block before pasting (remove it by right clicking in the blue area)")))
           'books
           '(vec-rest :tiles/slot))
   (gb/rpg [[0 0] [0 100] [0 150] [0 200]]
           '(def books ["Emma" "Getting Clojure" "War and Peace"])
           'books
           '(vec-rest :tiles/slot)
           '(vec-rest :tiles/slot))
   (gb/rpg []
           [:tiles/slot]
           "Getting Clojure"
           '(vec-rest :tiles/slot))
   (gb/rpg [[0 0] [0 100] [0 150] [0 200]]
           '(def books ["Emma" "Getting Clojure" "War and Peace"])
           "Carrie"
           '(conj :tiles/slot :tiles/slot)
           'books)
   (gb/rpg [[0 0] [100 50] [0 100] [0 150] [0 200]]
           '(def books :tiles/slot)
           "copy+paste the Vector from previous workspace"
           "Carrie"
           '(vec-cons :tiles/slot :tiles/slot)
           'books)
   (gb/rpg [[0 0] [0 100] [0 150] [0 250] [0 300] [100 300] [250 300]]
           '(def books ["Emma" "Getting Clojure" "War and Peace"])
           '(conj books "Carrie")
           '(def :tiles/slot :tiles/slot)
           '(println :tiles/slot)
           'books
           'more-books
           'more-books)
   (gb/rpg [[100 0] [0 20] [300 20] [150 50] [0 100] [300 100] [100 250]]
           "Getting Clojure"
           "title"
           2018
           '(:tiles/vert (hash-map :tiles/slot :tiles/slot :tiles/slot
                                   :tiles/slot :tiles/slot :tiles/slot))
           "Russ Olson"
           "published"
           "author")
   (gb/rpg [[0 0] [0 200] [100 200] [200 200] [0 250] [0 350]]
           '(:tiles/vert (hash-map "title" "Getting Clojure" "author"
                                   "Russ Olson" "published" 2018))
           'book
           'book
           "published"
           '(def :tiles/slot :tiles/slot)
           '(get :tiles/slot :tiles/slot))
   (gb/page (gb/shift-coords 4 [0 0] [0 50] [150 50] [0 150])
            (gb/text "Russ Olson")
            (-> (gb/t-map [:title gb/slot] [:author gb/slot] [:published gb/slot])
                (assoc :inline? false))
            (gb/num 2018)
            (gb/text "Getting Clojure"))
   (gb/rpg [[0 0] [0 100] [0 170] [100 170] [0 220]]
           '(:tiles/vert {:title "Getting Clojure"
                          :author "Russ Olson"
                          :published 2018})
           '(def :tiles/slot :tiles/slot)
           'book
           'book
           '(:published :tiles/slot))
   (gb/rpg [[0 0] [0 100] [0 170] [100 170] [0 220]]
           '(def book :tiles/slot)
           '(:please-select-keyword book)
           "copy+paste the Hash-Map from previous workspace")
   (gb/rpg [[0 0] [0 150]]
           '(def book
              (:tiles/vert {:title "Getting Clojure"
                            :author "Russ Olson"
                            please-rename-me 2018}))
           '(please-rename-me book))
   (gb/rpg [[0 0] [0 150] [0 200] [200 200] [0 250]]
           '(def great-book
              (:tiles/vert {:title "Getting Clojure"
                            :author "Russ Olson"
                            :year 2018}))
           :page-count
           '(assoc :tiles/slot :tiles/slot :tiles/slot)
           270
           'great-book)
   (gb/rpg [[0 0] [0 150] [220 150] [120 170] [0 200]
            [0 250] [220 250]]
           '(def great-book
              (:tiles/vert {:title "Getting Clojure"
                            :author "Russ Olson"
                            :year 2018}))
           :title
           270
           '(:tiles/vert
             (assoc :tiles/slot :tiles/slot :tiles/slot
                    :tiles/slot :tiles/slot))
           'great-book
           :page-count
           "Clojure for the brave and true")
   (gb/rpg [[0 0] [0 150] [0 200] [250 200] [0 250] [0 300]]
           '(def great-book
              (:tiles/vert {:title "Getting Clojure"
                            :author "Russ Olson"
                            :year 2018}))
           :title
           '(dissoc great-book :tiles/slot :tiles/slot)
           :favorite-zoo-animal
           :author
           '(println great-book))
   (gb/rpg [[0 0] [150 0] [0 50] [150 50] [0 100] [150 100]]
           '(println :tiles/slot)
           '@app-state
           '(inc :tiles/slot)
           '@app-state
           '(println :tiles/slot)
           '@app-state)
   (gb/rpg [[0 0] [150 0] [0 50] [150 50] [250 50] [0 100] [0 100]]
           '(println :tiles/slot)
           'app-state
           '(swap! :tiles/slot :tiles/slot)
           'inc
           '@app-state
           '(println @app-state))
   (gb/rpg []
           [:div :tiles/slot]
           [:h1 "Hello, World!"])
   (gb/rpg [[0 0] [100 0] [0 50] [0 100]]
           [:hr]
           [:hr]
           :div
           '(:tiles/vert [:tiles/slot :tiles/slot [:h1 "Being in the World"] :tiles/slot]))
   (gb/rpg [[0 0] [0 50] [130 170]]
           [:button :tiles/slot]
           '(:tiles/vert [:div [:hr] [:h1 "Being in the World"] :tiles/slot [:hr]])
           "Hello, World. I don't do much.")
   (gb/rpg [[0 0] [0 200] [0 250]]
           '(:tiles/vert [:div
                          [:hr]
                          [:h1 "Being in the World"]
                          [:button :tiles/slot "Hello, World. I still don't do much."]
                          [:hr]])
           nil
           {:id "first-button" :on-click :tiles/slot})
   (gb/rpg [[0 0] [150 0] [0 50] [150 50] [0 150]]
           'click-function
           'click-function
           '(defn :tiles/slot _dummy :tiles/slot)
           '(println @app-state)
           '(:tiles/vert [:div
                          [:hr]
                          [:h1 "Being in the World"]
                          (:tiles/vert [:button {:id "first-button" :on-click :tiles/slot} "Hello, World. I print zero in the Output area"])
                          [:hr]]))
   (gb/rpg [[0 0] [150 0] [300 0] [0 50] [0 200] [200 400]]
           '@app-state
           'app-state
           'inc
           '(defn click-function _
              (do (swap! :tiles/slot :tiles/slot)
                  (println :tiles/slot)))
           '(:tiles/vert [:div
                          [:hr]
                          [:h1 "Being in the World"]
                          (:tiles/vert [:button {:id "first-button" :on-click click-function} "Hello, World. I count!"])
                          [:hr]]))
   (gb/rpg [[0 0] [0 150] [0 200] [200 400]]
           '(defn click-function _
              (do (swap! app-state inc)
                  (println @app-state)))
           '(str " " :tiles/slot)
           '(:tiles/vert [:div
                          [:hr]
                          [:h1 "Being in the World"]
                          (:tiles/vert [:button {:id "first-button" :on-click click-function} "Hello, World. I obviously count."])
                          :tiles/slot
                          [:hr]])
           '@app-state)
   (gb/rpg [[0 0] [0 100] [0 200] [0 250]]
           '(defn click-function
              _
              (swap! app-state inc))
           '(defn start-rocket
              _
              (start-timer click-function
                           30
                           200
                           "Timer started"))
           '(reset! app-state 0)
           '(:tiles/vert
             [:div
              [:button
               {:id "launch", :on-click start-rocket}
               "Launch rocket"]
              (:tiles/vert
               [:svg {:height 200 :id "rocket"}
                [:image
                 {:href "rocket.png",
                  :x 50,
                  :y (- 160 @app-state)}]])]))))

(def e-vect
  (-> (map (fn [xml-code]
             {:xml-code xml-code})
           vect)
      vec
      (assoc-in [13 :lable] :defn)
      (assoc-in [13 :error-message-fn]
                 (fn [{:keys [sci-error]} ifo msg-fn]
                   (let [frm (last ifo)]
                     (cond
                       (= "Var name should be simple symbol." (subs sci-error 0 33))
                       "You cannot inspect this block."
                       (= (subs sci-error 0 30) "Could not resolve symbol: what")
                       "You need to connect the \"what\" block."
                       ))))
      (assoc-in [13 :message-fn]
                 (fn [{:keys [result inspect stdout]} ifo goto-page!]
                   (let [frm (last ifo)
                         frmcoll (when (coll? frm) frm)]
                     (cond
                       (= 'defn (first frmcoll))
                       "This is a \"defn\" block, it defines a function. The return value of this block is the name of the function in a somewhat crypric from. You suspect that a function needs to be called to reveal its purpose."
                       (and (> (count inspect) 0)
                            (nil? (first inspect))
                            (first stdout))
                       [:<>
                        [:p "The result is of type Nothing, which obviously means that there is no result. But by pressing the \"run\" button, you see that there is an output to the console: "]
                        (map-indexed (fn [idx val] ^{:key idx}[:pre val])
                                     stdout)
                        [:p "You remember from previous experience that this is typical for the println function: it does not return a result but writes to the console. You use the arrow-button > to work through the next two workspaces in order to refresh your knowledge about defining and calling functions."]]
                       ))))
      (assoc-in [49 :description]
                [:div "For more scripting of react-ive web apps in Clojure, look at the "
                 [:a {:href "https://github.com/kloimhardt/bb-web"} "bb-web"] " repository."])
      )
  )

(def content {:tutorials e-vect :chapnames chapnames :chaps chaps})
