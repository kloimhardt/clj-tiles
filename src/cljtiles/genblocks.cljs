(ns cljtiles.genblocks
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]))

(defmulti gen (fn [m _] (:type m)))

(defn blockmap [type givenid]
   {:type type :id (str type givenid)})

(defmethod gen :text [{:keys [type dertext]} givenid]
  [:block (blockmap type givenid)
   [:field {:name "dertext"} dertext]])

(defmethod gen :funs-h-2-inp [{:keys [type kopf args-2]} givenid]
  (let [{:keys [id] :as bm} (blockmap type givenid)]
    [:block bm
     [:field {:name "kopf"} kopf]
     [:value {:name "args-2"} (gen args-2 id)]]))

(defn addcoords [block x y]
  (update block 1 #(-> %
                       (assoc :x x)
                       (assoc :y y)
                       (assoc :id (str (:id %) "-" x "-" y)))))

(defn xml [& triples]
  (->> triples
       (map (fn [[m x y]] (addcoords (gen m) x y)))
       (into [:xml])
       html))
