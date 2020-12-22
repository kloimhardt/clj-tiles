(ns cljtiles.genblocks
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]))

(defmulti gen (fn [m _] (:type m)))

(defmethod gen :slot [] nil)

(defn blockmap [type givenid]
   {:type type :id (str type givenid)})

(defmethod gen :num [{:keys [type nummer]} givenid]
  [:block (blockmap type givenid)
   [:field {:name "nummer"} nummer]])

(defmethod gen :fun [{:keys [kopf arity argsvec subtype]} givenid]
  (let [type (keyword (str subtype "-" arity "-inp"))
        {:keys [id] :as bm} (blockmap type givenid)]
    (into [:block bm
           [:field {:name "kopf"} kopf]]
          (map-indexed (fn [idx v]
                         [:value {:name (str "args-" (+ idx 2))}
                          (gen v (str (+ idx 2) "-" id))]) argsvec))))

(defn addcoords [block [x y]]
  (update block 1 #(-> %
                       (assoc :x x)
                       (assoc :y y)
                       (assoc :id (str (:id %) "-" x "-" y)))))

(defn page [coords & blocks]
  (->> blocks
       (map-indexed (fn [idx blk] (addcoords (gen blk) (coords idx))))
       (into [:xml])
       html))

(defn text [txt]
  {:type :num :nummer (str "\"" txt "\"")})

(defn num [nummer]
  {:type :num :nummer nummer})

(defn fun [name & argsvec]
  {:type :fun :subtype "funs-h" :arity (inc (count argsvec)) :kopf name :argsvec argsvec})

(defn fun-inli [name & argsvec]
  (assoc (apply fun name argsvec) :subtype "inli-h"))

(defn fun-vert [name & argsvec]
  (assoc (apply fun name argsvec) :subtype "funs-v"))

(def slot {:type :slot})

(defn coords [& thecoords]
  (mapv (fn [[x y]] [(+ x 10) (+ y 10)]) thecoords))

(defn chapter [& pages] (into [] pages))

(defn exp [v]
  (if (vector? v)
    (let [erst (first v)
          appl (fn [fuct] (apply fuct erst (map exp (into [] (rest v)))))]
      (cond
        (and (= (count v) 3) (#{"/" "+" "*" "-"} erst)) (appl fun-inli)
        (#{"def" "defn"} erst) (appl fun-vert)
        :else (appl fun)))
    (cond
      (map? v) v
      (nil? v) (num "nil")
      (string? v) (text v)
      :else (num v))))

(defn pg [coords & blocks]
  (let [shifted (mapv (fn [[x y]] [(+ x 10) (+ y 10)]) coords)]
    (->> blocks
         (map-indexed (fn [idx blk] (addcoords (gen (exp blk)) (shifted idx))))
         (into [:xml])
         html)))
