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

(defmethod gen :funs-h-inp [{:keys [kopf arity argsvec]} givenid]
  (let [type (keyword (str "funs-h-" arity "-inp"))
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
  {:type :funs-h-inp :arity (inc (count argsvec)) :kopf name :argsvec argsvec})

(def slot {:type :slot})

(defn coords [& thecoords]
  (mapv (fn [[x y]] [(+ x 10) (+ y 10)]) thecoords))

(defn chapter [& pages] (into [] pages))
