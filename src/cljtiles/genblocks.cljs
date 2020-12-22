(ns cljtiles.genblocks
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]))

(defmulti gen (fn [m _] (:type m)))

(defmethod gen :null [] nil)

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

(defn text [txt]
  {:type :num :nummer (str "\"" txt "\"")})

(defn num [nummer]
  {:type :num :nummer nummer})

(defn fun [name arity & argsvec]
  {:type :funs-h-inp :arity arity :kopf name :argsvec argsvec})

(def null {:type :null})
