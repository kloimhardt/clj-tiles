(ns cljtiles.utils
  (:require [clojure.string :as str]))

(comment

  (identity (js/LCSubStr "GeeksforGeeks" "GeeksQuiz"))

  :end)

(def a "g (a b c d) h")
(def b "p (a b c d) h")

(def c (js/LCSubStr a b))

(first (val (first (into (sorted-map) (group-by count (map first (re-seq #"\((.+)\)" c)))))))

(def d "g (a b c d) {o p r i j} h")
(def e "g (a b c d) {o p r i j} h")

(defn find-biggest-in-str [sol puzz]
  (some->> (js/LCSubStr sol puzz)
       (re-seq #"(\((.+)\)|\[(.+)\]|\{(.+)\})")
       (map first)
       (group-by count)
       (into (sorted-map))
       last
       val
       first))

(def f [d])

(map (fn [s] (let [found (find-biggest-in-str s e)] [found (str/split s found)])) f)

(defn find-biggest-in-str-vec [sol-vec puzz]
  (map (fn [s]
         (let [found (find-biggest-in-str s puzz)]
           [[found] (str/split s found)]))
       sol-vec))

(def h ["" [d]])

(def g (find-biggest-in-str-vec f e))
(first g)
(find-biggest-in-str-vec (second (first g)) e)

(mapcat #(find-biggest-in-str-vec (second %) e) g)

(def i [["" [d]]])

(def j (mapcat #(find-biggest-in-str-vec (second %) e) i))
(def k (mapcat #(find-biggest-in-str-vec (second %) e) j))

k;; => (["(a b c d)" ["g " " "]] [nil [" h"]])

(defn condense [erg]
  (reduce (fn [[all-found all-sol] [found-vec sol-vec]]
            (if (first found-vec)
              [(concat all-found found-vec) (concat all-sol sol-vec)]
              [all-found all-sol]))
          [[] []] erg))

(defn find-biggest [[found-vec sol-vec] puzz]
  (let [[new-found-vec new-sol-vec] (condense (find-biggest-in-str-vec sol-vec puzz))]
    [(concat found-vec new-found-vec) new-sol-vec]))

(defn find-biggest-fn [puzz]
  (fn [[found-vec sol-vec]]
    (let [[new-found-vec new-sol-vec] (condense (find-biggest-in-str-vec sol-vec puzz))]
      [(concat found-vec new-found-vec) new-sol-vec])))

(def l [[] [d]])
(def m (condense (find-biggest-in-str-vec (second l) e)))

(find-biggest (find-biggest (find-biggest (find-biggest l e) e) e) e)

(def fun (find-biggest-fn e))
(fun (fun l))

(defn find-contents [sol puzz]
  (let [fun (find-biggest-fn puzz)]
    (ffirst (drop-while #(seq (second %)) (iterate fun [[] [sol]])))))

(find-contents d e)




