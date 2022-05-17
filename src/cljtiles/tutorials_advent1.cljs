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

(defn generate-content-and-call [txt init-fn]
  (let [tuts
        (->> (str/split txt #"\#\+end_src")
             (map #(last (str/split % #"\#\+begin_src clojure")))
             (map #(utils/twosplit % "\n"))
             (filter (complement #(str/ends-with? (first %) ":exports none")))
             (map second))
        a (nth tuts 2)
        b (edn/read-string a)
        c (assoc (explode/explode [b])
                 :solpos-yx [[0 0]]
                 :solution [b])
        content {:tutorials [c] :chapnames ["Advent"] :chaps [(count [c])]}]
    (init-fn [content])))

(defn init-advent [init-fn]
  (-> (js/fetch url)
      (.then #(.text %))
      (.then #(generate-content-and-call % init-fn))))
