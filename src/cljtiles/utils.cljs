(ns cljtiles.utils
  (:require [clojure.string :as str]))

(defn find-biggest-in-str [sol puzz]
  (some->> (js/LCSubStr sol puzz)
       (re-seq #"(\((.+)\)|\[(.+)\]|\{(.+)\})")
       (map first)
       (group-by count)
       (into (sorted-map))
       last
       val
       first))

(defn find-biggest-in-str-vec [sol-vec puzz]
  (map (fn [s]
         (let [found (find-biggest-in-str s puzz)]
           [[found] (str/split s found)]))
       sol-vec))

(defn condense [erg]
  (reduce (fn [[all-found all-sol] [found-vec sol-vec]]
            (if (first found-vec)
              [(concat all-found found-vec) (concat all-sol sol-vec)]
              [all-found all-sol]))
          [[] []] erg))

(defn find-biggest-fn [puzz]
  (fn [[found-vec sol-vec]]
    (let [[new-found-vec new-sol-vec]
          (condense (find-biggest-in-str-vec sol-vec puzz))]
      [(concat found-vec new-found-vec) new-sol-vec])))

(defn find-contents [sol puzz]
  (let [fun (find-biggest-fn puzz)]
    (->> [[] [sol]]
         (iterate fun)
         (drop-while #(seq (second %)))
         ffirst)))

(comment
  (find-contents
   "g (a b c d) {o i j} h"
   "g (a b c d) {o p r i j} h"))




