(ns cljtiles.tutorials-advent1
  (:require [cljtiles.codeexplode :as explode]
            [clojure.string :as str]
            [cljtiles.utils :as utils]
            [cljs.reader :as edn]))

;; load exports none, e.g. Cartan is needed, read also crashing #17
;;#+begin_src clojure :noweb yes
;;<<Cartan>>
;;#+end_src

;; does not work because beige block only takes one arg, -> introduce additonal brownish block
#_(defn Lc [mass metric coordsys]
    (let [e (coordinate-system->vector-basis coordsys)]
      (fn [[_ x v]]
        ((L2 mass metric) ((point coordsys) x) (* e v)))))
;; make sure you make a try catch in such a case, to that not the whole org file crashes!
;; but make an stdout with the expression

;; insert tiles/vert after reading the string

;;------------

;; include load file in context menu (with input field and a default box with urls from Sam's fdg)

;; make sure sucmutils env is loaded so that all puzzles run

;; make ->tex-equation work with TeX

;;------------

;; move all the defs (chaps etc) into data-store and provide data-store as an atom to components

;; read 1/2 as 1/2 and not 0.5

;; update spec, respectively replace with malli

;; make inline of only one slot possible in UI context menu (maybe upgrade blockly)

 (def url
   "https://raw.githubusercontent.com/mentat-collective/fdg-book/main/clojure/org/chapter001.org")

(defn generate-content-and-call [txt init-fn]
  (let [nof-read-tuts 4
        tuts
        (->> (str/split txt #"\#\+end_src")
             (map #(last (str/split % #"\#\+begin_src clojure")))
             (map #(utils/twosplit % "\n"))
             (filter (complement #(str/ends-with? (first %) ":exports none")))
             (map second))
        a (take nof-read-tuts tuts)
        b (map #(edn/read-string (str "[" % "]")) a)
        c (map #(assoc (explode/explode %)
                       :solpos-yx [[0 0]]
                       :solution %) b)
        content {:tutorials c :chapnames ["Advent"] :chaps [(count c)]}]
    (init-fn [content])))

(defn init-advent [init-fn]
  (-> (js/fetch url)
      (.then #(.text %))
      (.then #(generate-content-and-call % init-fn))))
