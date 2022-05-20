(ns cljtiles.tutorials-advent1
  (:require [cljtiles.codeexplode :as explode]
            [clojure.string :as str]
            [cljtiles.utils :as utils]
            [cljs.reader :as edn]
            [clojure.walk :as walk]))

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
(def on-newline 'on-newline)

(defn insert-newline [s-next]
  (let [[b1 b2] (map #(re-find #"^\s*\(" %) s-next)
        t (str/trim (first s-next))]
    (if (and b1 b2 (not= b1 b2))
      (apply str "(" on-newline " " (rest t))
      t)))

(defn insert-tiles-vert [code]
  (walk/postwalk
    (fn [x]
      (if (and (coll? x) (seq x) (= (first x) on-newline))
        (list :tiles/vert (rest x))
        x))
    code))

(defn concat-strings [xs]
  (str "[" (apply str (interpose " " xs)) "]"))

(defn extended-read-string [s]
  (->> (str/split s #"\n")
       (remove #(re-find #"^\s*;" %))
       (partition-all 2 1)
       (map insert-newline)
       (concat-strings)
       (edn/read-string)
       (insert-tiles-vert)))

(defn ers [s]
  (let [nl #{:n1 :n2}
        nls (str " " (apply str (interpose " " nl)) " ")
        a (str/replace s #";.*?\n" "") ;;remove comment lines
        b (str/replace a #"\n" " :n1 :n2 ")
        c (edn/read-string (str "[" b "]"))]
    (->> (into [] (remove #{:n1 :n2}  c)) ;;remove last newline
         (walk/postwalk (fn [x]
                          (if (and (coll? x) (some #{:n1} x))
                            (list :tiles/vert
                                  (utils/list-into-same-coll
                                   x
                                   (remove #{:n1 :n2} x)))
                            x))))))

(defn generate-content-and-call [txt init-fn]
  (let [tuts
        (->> (str/split txt #"\#\+end_src")
             (map #(last (str/split % #"\#\+begin_src clojure")))
             (map #(utils/twosplit % "\n"))
             (filter (complement #(str/ends-with? (first %) ":exports none")))
             (map second))
        tuts-start 0
        nof-read-tuts 20 ;;(dec (count tuts))
        a (take nof-read-tuts (drop tuts-start tuts))
        ;; b (map #(edn/read-string (str "[" % "]")) a) ;;version without :tiles/vert
        ;; b (map extended-read-string a)
        b (map ers a)
        c (map #(assoc (explode/explode %)
                       :solpos-yx [[0 0]]
                       :solution %) b)
        content {:tutorials c :chapnames ["Advent"] :chaps [(count c)]}]
    (init-fn [content])))

(defn init-advent [init-fn]
  (-> (js/fetch url)
      (.then #(.text %))
      (.then #(generate-content-and-call % init-fn))))
