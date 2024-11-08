#_"SPDX-License-Identifier: GPL-3.0"

(ns cljtiles.codeexplode
  (:require [cljtiles.utils :as utils]))

(def dbg (atom nil))

(defn store-dbg-info [data] (reset! dbg data))

(defn remove-functions-and-keys [exprns]
  (->>  exprns
        (reduce (fn [[idxs acc] [symbs el]]
                  (let [new-idxs (->> symbs
                                      (reduce (fn [new-idxs symb]
                                                (update new-idxs symb inc))
                                              idxs))]
                    (if ((fnil pos? false) (get new-idxs el))
                      [(update new-idxs el dec) acc]
                      [new-idxs (conj acc el)])))
                [{} []])
        (#(do (when (seq (filter (fn [x] (not= 0 x)) (vals (first %))))
                (prn "error: first of the result must be a map where all values are zero"))
              %))
        last))

(defn empty-colls [exprns]
  (->> exprns
       (mapv (fn [x]
               (cond
                 (and (list? x) (#{:tiles/vert :tiles/infix} (first x)))
                 nil

                 (and (list? x) (#{:tiles/keep} (first x)))
                 [[] (last x)]

                 (#{:tiles/vert :tiles/slot :tiles/infix} x)
                 nil

                 (map-entry? x)
                 nil

                 (and (list? x) (= 'quote (first x)))
                 [x x]

                 (and (list? x) (not (coll? (first x))))
                 [[(first x)] (cons (first x) (repeat (dec (count x)) :tiles/slot))]

                 (and (list? x) (list? (first x)))
                 [[] (apply list (repeat (count x) :tiles/slot))]

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

(defn detect-offset [exprn _next-exprn] ;;next-exprn only needed when coll should be in first place
  (let [newline (and (coll? exprn) (not (#{'quote} (first exprn))))
        #_(and (coll? next-exprn) (not (#{'quote} (first next-exprn))))]
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

(defn mod-tree-seq [xs]
  (mapcat #(if (= :tiles/keep (first %))
             (list %)
             (tree-seq coll? seq %))
          xs))

(defn explode [solution shuffle?]
  (let [code (-> (mod-tree-seq (reverse solution))
                 empty-colls
                 remove-functions-and-keys
                 reverse
                  ;;if reverse is before remove-functions-and-keys
                  ;;it does not work correctly
                 (cond-> shuffle? shuffle))]
    {:blockpos-yx (blockpos-yx code)
     :code code}))
