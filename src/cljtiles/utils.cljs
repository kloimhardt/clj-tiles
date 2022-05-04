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

(defn condense [erg]
  (reduce (fn [[all-found all-sol] [found sol-vec]]
            (if found
              [(conj all-found found) (concat all-sol sol-vec)]
              [all-found all-sol]))
          [[] []] erg))

(defn find-biggest-in-str-vec [sol-vec puzz]
  (condense
   (map (fn [s]
          (let [found (find-biggest-in-str s puzz)]
            [found (str/split s found)]))
        sol-vec)))

(defn find-biggest-fn [puzz]
  (fn [[found-vec sol-vec]]
    (let [[new-found-vec new-sol-vec]
           (find-biggest-in-str-vec sol-vec puzz)]
      [(concat found-vec new-found-vec) new-sol-vec])))

(defn find-contents [sol puzz]
  (let [fun (find-biggest-fn puzz)]
    (->> [[] [sol]]
         (iterate fun)
         (drop-while #(seq (second %)))
         ffirst)))

(defn get-symbols [s]
  (-> s
      (str/replace #"[\(\)\[\]\{\}]" "")
      (str/split " ")))

(defn remove-first-from-coll [coll el]
  (let [[f s] (split-with (complement #{el}) coll)]
    (concat f (rest s))))

(defn find-lacking [sol puzz]
  (reduce remove-first-from-coll (get-symbols puzz) (get-symbols sol)))

(def sol "g (a b c d) koi {o p r i j} uio [h a l l o]")
(def puzz "(a b c d) kuuu {o p r i j} koi [h a l l o] hooo")
(def cont (find-contents sol puzz))
(def lack (find-lacking sol puzz))

(defn s-i-e-recur [ml pl modifier1 modifier2]
  (if (seq ml)
    (interpose (modifier1 (first ml))
               (map (fn [p]
                      (s-i-e-recur (rest ml)
                                   (str/split p (first (rest ml)))
                                   modifier1
                                   modifier2))
                    pl))
    (modifier2 (apply str pl))))

(defn split-into-expressions [puzz exprs modifier1 modifier2]
  (flatten (s-i-e-recur exprs (str/split puzz (first exprs)) modifier1 modifier2)))

(defn expr-g [x] ^:green x)
(defn expr-y [x] ^:yellow x)

(split-into-expressions puzz cont expr-g expr-y)
