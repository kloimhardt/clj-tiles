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



(def sol "g (a b c d) k {o p r i j} uio [h a l l o]")
(def puzz "gii (a b c d) kuuu {o p r i j} [h a l l o] hooo")
(def cont (find-contents sol puzz))

(defn hui [seplist puzzvec]
  (mapcat #(interpose (first seplist)
                       (str/split % (first seplist)))
          puzzvec))

(def a (hui cont [puzz]))
(def b (hui (rest cont) a))
(hui (rest (rest cont)) b)

(defn hui2 [seplist puzzvec]
  (if (seq seplist)
    (mapcat #(interpose (first seplist)
                        (hui2 (rest seplist) (str/split % (first seplist))))
            puzzvec)
    nil))

(defn hui3 [mlist [a e]]
  (if (seq mlist)
    [(when a (hui3 (rest mlist) (str/split a (first mlist))))
     (first mlist)
     (when e (hui3 (rest mlist) (str/split e (first mlist))))]
    [a e]))

(defn hui7 [ml [a e]]
  (let [erg
        (if (seq ml)
          (if e
            [(hui7 (rest ml) (str/split a (first (rest ml))))
             (first ml)
             (hui7 (rest ml) (str/split e (first (rest ml))))]
            [(hui7 (rest ml) (str/split a (first (rest ml))))])
          (str a e))]
    erg))

(def anfange (str/split puzz (first cont)))
(flatten (hui7 cont anfange))

(defn hui8 [ml pl modifier]
  (if (seq ml)
    (interpose (modifier (first ml))
               (map (fn [p]
                      (hui8 (rest ml)
                            (str/split p (first (rest ml)))
                            modifier))
                    pl))
    (apply str pl)))

(defn split-into-expressions [puzz exprs modifier]
  (flatten (hui8 exprs (str/split puzz (first exprs)) modifier)))

(defn expr-fu [x] {:word x})

(split-into-expressions puzz cont expr-fu)
