(ns cljtiles.tutorials-advent1
  (:require [cljtiles.codeexplode :as explode]
            [clojure.string :as str]
            [cljtiles.utils :as utils]
            [cljs.reader :as edn]))

;; insert tiles/vert after reading the string
;; reload the code when you press the color button
;; show code immediately when puzzle is shown via show Code
;; in curated puzzle, replace run button with "scrambe up the puzzle" random puzzle generatior button, only show green run button when solved

(def url
  "https://raw.githubusercontent.com/mentat-collective/fdg-book/main/clojure/org/chapter001.org")

(defonce file (atom nil))

(defn make-request []
  (-> (js/fetch url)
      (.then #(.text %))
      (.then #(reset! file %))))

(make-request)

(defn read-tuts [url]
  (->> (str/split @file #"\#\+end_src")
       (map #(last (str/split % #"\#\+begin_src clojure")))
       (map #(utils/twosplit % "\n"))
       (filter (complement #(str/ends-with? (first %) ":exports none")))
       (map second)
       ))


(def a (nth (read-tuts url) 2))
(def b (edn/read-string a))

(def sol1 ['(def book
             {:title "Getting Clojure",
              :author "Russ Olson",
              :published 2018})
           '(defn hi [] (:title book))])

(def sol2 [
          '(:published book)])

(def e-vect
  [(assoc (explode/explode [b])
          :solpos-yx [[0 0] [100 0] [200 0]]
          :solution [b])
   (assoc (explode/explode sol1)
          :solpos-yx [[0 0] [100 0] [200 0]]
          :solution sol1)
   (assoc (explode/explode sol2)
          :solpos-yx [[0 0] [100 0] [200 0]]
          :solution sol2)])

(def chapnames ["Advent"])
(def chaps [(count e-vect)])

(def content {:tutorials e-vect :chapnames chapnames :chaps chaps})
