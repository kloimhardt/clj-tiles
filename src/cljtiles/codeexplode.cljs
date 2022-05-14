(ns cljtiles.codeexplode
  (:require [cljtiles.utils :as utils]))

(def dbg (atom nil))

(defn store-dbg-info [data] (reset! dbg data))

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
                 (and (list? x) (#{:tiles/vert} (first x)))
                 nil
                 (#{:tiles/vert :tiles/slot} x)
                 nil
                 (map-entry? x)
                 nil
                 (and (list? x) (= 'quote (first x)))
                 [x x]
                 (and (list? x) (not (coll? (first x))))
                 [[(first x)] (cons (first x) (repeat (dec (count x)) :tiles/slot))]
                 (and (list? x) (list? (first x)))
                 [[] (list :tiles/slot :tiles/slot)]
                 (map? x)
                 [(keys x) (into {} (map (fn [[k _]] [k :tiles/slot])) x)]
                 (coll? x)
                 [[] (utils/list-into-same-coll x (repeat (count x) :tiles/slot))]
                 :else
                 [[] x])))
       (filter identity)))

(defn graphical-length [c]
  (let [char-len 6
        slot-len 35]
    (cond
      (map? c) (+ (* char-len (count (apply str (keys c))))
                  (* slot-len (count c)))
      (vector? c) (* slot-len (count c))
      (coll? c) (+ (* char-len (count (str (first c))))
                   (* slot-len (count (rest c))))
      (string? c) (+ 40 (* char-len (count c)))
      :else (* char-len (count (str c))))))

(defn detect-offset [exprn next-exprn]
  (let [newline (and (coll? next-exprn) (not (#{'quote} (first next-exprn))))]
    [(cond-> 0
       (and (coll? exprn) (#{'def} (first exprn))) (+ 30)
       (and (coll? exprn) (#{'defn} (first exprn))) (+ 45)
       newline (+ 50))
     (when-not newline
       (+ 50 (graphical-length exprn)))]))

(defn blockpos-yx [code]
  (->> (partition-all 2 1 code)
       (reduce (fn [coords [exprn next-exprn]]
                 (let [[y x] (last coords)
                       [y-offset x-offset] (detect-offset exprn next-exprn)]
                   (conj coords [(+ y y-offset) (if x-offset (+ x x-offset) 0)])))
               [[0 0]])))

(defn explode [solution]
  (let [code (->> (rest (tree-seq coll? seq solution))
                  empty-colls
                  remove-functions-and-keys)]
    {:blockpos-yx (blockpos-yx code)
     :code code}))

