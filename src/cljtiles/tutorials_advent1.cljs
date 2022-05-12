(ns cljtiles.tutorials-advent1
  (:require [cljtiles.utils :as utils]))

(def sol ['(def book
             {:title "Getting Clojure",
              :author "Russ Olson",
              :published 2018})
          [1 2 3 4 5]
          '(:published book)])

;;(def sol ['(def ha {:asdfasfasfas 1 :bsdfasfsdfa 2} [3 4 5 6 7 8])])

(defn remove-functions-and-keys [exprns]
  (let [idxs (->> (map first exprns)
                  (reduce (fn [acc symbols]
                            (concat acc symbols))
                          [])
                  frequencies)]
    (->> (map last exprns)
         (reduce (fn [[idxs acc] el]
                   (if ((fnil pos? false) (get idxs el))
                     [(update idxs el dec) acc]
                     [idxs (conj acc el)]))
                 [idxs []])
         last)))

(defn empty-colls [exprns]
  (->> exprns
       (mapv (fn [x]
               (cond
                 (and (list? x) (not (coll? (first x))))
                 [[(first x)] (cons (first x) (repeat (dec (count x)) :tiles/slot))]
                 (map? x)
                 [(keys x) (into {} (map (fn [[k _]] [k :tiles/slot])) x)]
                 (map-entry? x)
                 nil
                 (coll? x)
                 [[] (utils/list-into-same-coll x (repeat (count x) :tiles/slot))]
                 :else
                 [[] x])))
       (filter identity)))

(def code
  (->> (rest (tree-seq coll? seq sol))
       empty-colls
       remove-functions-and-keys))

(defn graphical-length [c]
  (let [char-len 10
        slot-len 35]
    (cond
      (map? c) (+ (* char-len (count (apply str (keys c))))
                  (* (+ 5 slot-len) (int (/ (count c) 2))))
      (vector? c) (* slot-len (count c))
      (coll? c) (+ (* char-len (count (str (first c))))
                   (* slot-len (count (rest c))))
      :else (* char-len (count (str c))))))

(defn detect-offset [exprn next-exprn]
  [(cond-> 0
     (and (coll? exprn) (#{'def} (first exprn))) (+ 30)
     (and (coll? exprn) (#{'defn} (first exprn))) (+ 45)
     (coll? next-exprn) (+ 50))
   (when-not (coll? next-exprn)
     (+ 50 (graphical-length exprn)))])

(def blockpos-yx
  (->> (partition-all 2 1 code)
       (reduce (fn [coords [exprn next-exprn]]
                 (let [[y x] (last coords)
                       [y-offset x-offset] (detect-offset exprn next-exprn)]
                   (conj coords [(+ y y-offset) (if x-offset (+ x x-offset) 0)])))
               [[0 0]])))

(def e-vect
  [{:blockpos-yx blockpos-yx
    :code code
    :solution sol}])

(def chapnames ["Advent"])
(def chaps [(count e-vect)])

(def content {:tutorials e-vect :chapnames chapnames :chaps chaps})
