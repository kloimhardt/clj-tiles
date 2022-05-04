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
  (filter #(pos? (count %))
  (reduce remove-first-from-coll (get-symbols puzz) (get-symbols sol))))

(def sol "g (a b c d) koi {o p r i j} uio [h a l l o]")
(def puzz "(a b c d) kuuu  {o p r i j} koi kaba [h a l l o]")
(def cont (find-contents sol puzz))
(def lack (find-lacking sol puzz))

(defn s-i-e-recur [ml pl]
  (if (seq ml)
      (cond->> (map (fn [p]
                      (s-i-e-recur (rest ml)
                                   (str/split p (first (rest ml)))))
                    pl)
        (first ml) (interpose (first ml)))
      pl))

(defn split-into-expressions [puzz exprs]
  (butlast (flatten (s-i-e-recur (cons nil exprs)  [(str puzz "ende999")]))))

(comment

  (def gr1 (split-into-expressions puzz cont))
  (flatten (s-i-e-recur (cons nil lack) gr1))

  :end)

(defn s-i-e-recur2 [ml pl color-kw]
  (if (seq ml)
    (cond->> (map (fn [p]
                    (if (:yellow p)
                      (s-i-e-recur2 (rest ml)
                                    (map (fn [txt]
                                           {:yellow true :text txt})
                                         (str/split (:text p)
                                                    (first (rest ml))))
                                    color-kw)
                      p))
                  pl)
      (first ml) (interpose {color-kw true :text (first ml)}))
    pl))

(defn split-into-expressions2 [puzz exprs color-kw]
  (flatten (s-i-e-recur2 (cons nil exprs) puzz color-kw)))

(defn split-into-expressions2-initial [puzz exprs color-kw startstr endstr]
  (split-into-expressions2 [{:text (str startstr puzz endstr) :yellow true}]
                           exprs
                           color-kw))

(defn mark-green-and-black [puzz green-expr black-expr]
  (-> (split-into-expressions2-initial puzz green-expr :green
                                       (str (random-uuid))
                                       (str (random-uuid)))
      (split-into-expressions2 black-expr :black)
      (rest)
      (butlast)))

(comment

  (mark-green-and-black puzz cont lack)
  (mark-green-and-black puzz [] lack) ;;TDOD edge case

  :end)
