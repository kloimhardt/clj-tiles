(ns cljtiles.genblocks
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]
            [clojure.string :as str]))

(defmulti gen (fn [m _] (:type m)))

(defmethod gen :slot [] nil)

(defn blockmap [type givenid inline?]
  {:type type :id (str type givenid) :inline (str inline?)})

(defmethod gen :num [{:keys [nummer inline?]} givenid]
  [:block (blockmap "num" givenid inline?)
   [:field {:name "nummer"} nummer]])

;; is really a vector
(defmethod gen :args [{:keys [argsvec inline?]} givenid]
  (let [xml-block-type (str "args-" (count argsvec))
        {:keys [id] :as bm} (blockmap xml-block-type givenid inline?)]
    (into [:block bm]
          (map-indexed (fn [idx v]
                         [:value {:name (str "arg_" (+ idx 1))}
                          (gen v (str (+ idx 1) "-" id))]) argsvec))))

(defmethod gen :fun [{:keys [kopf argsvec subtype inline?]} givenid]
  (let [xml-block-type (str subtype "-" (inc (count argsvec)) "-inp")
        {:keys [id] :as bm} (blockmap xml-block-type givenid inline?)]
    (into [:block bm
           [:field {:name "kopf"} kopf]]
          (map-indexed (fn [idx v]
                         [:value {:name (str "args-" (+ idx 2))}
                          (gen v (str (+ idx 2) "-" id))]) argsvec))))

(defmethod gen :map [{:keys [argsvec subtype inline?]} givenid]
  (let [xml-block-type (str subtype "-" (* (count argsvec) 2) "-inp")
        {:keys [id] :as bm} (blockmap xml-block-type givenid inline?)]
    (into [:block bm]
          (apply concat
                 (map-indexed (fn [idx v]
                                (let [i (inc (* idx 2))]
                                  [[:field {:name (str "key-" i)}
                                    (str (first v))]
                                   [:value {:name (str "val-" (inc i))}
                                    (gen (second v) (str (inc i) "-" id))]]))
                              argsvec)))))

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
  {:type :fun :subtype "funs-h" :kopf name :argsvec argsvec})

(defn fun-inli [name & argsvec]
  (assoc (apply fun name argsvec) :subtype "infi-h"))

(defn fun-vert [name & argsvec]
  (assoc (apply fun name argsvec) :inline? false))

(def slot {:type :slot})

(defn args [& argsvec]
  {:type :args :argsvec argsvec})

(defn t-map [& argsvec]
  {:type :map :subtype "map-h" :argsvec argsvec :inline? true})

(defn t-map-vert [& argsvec]
  (assoc (apply t-map argsvec) :inline? false))

(defn chapter [& pages] (into [] pages))

(defn exp [v]
  (if (vector? v)
    (let [erst (first v)
          appl (fn [fuct] (apply fuct erst (map exp (into [] (rest v)))))]
      (cond
        (and (= (count v) 3) (#{"/" "+" "*" "-"} erst)) (appl fun-inli)
        (#{"def" "defn" "do"} erst) (appl fun-vert)
        :else (appl fun)))
    (cond
      (map? v) v
      (nil? v) (num "nil")
      (string? v) (text v)
      :else (num v))))

(defn parse [l & [opt]]
  (cond
    (list? l)
    (let [erst (str (first l))
          appl (fn [fuct] (apply fuct erst (map parse (rest l))))]
      (cond
        (str/starts-with? erst ":tiles") (parse (second l) erst)
        (and (= (count l) 3) (#{"/" "+" "*" "-"} erst)) (appl fun-inli)
        (or (#{"def" "defn" "do"} erst)
            (= ":tiles/fvert" opt)) (appl fun-vert)
        :else (appl fun)))
    (vector? l) (apply args (map parse l))
    :else
    (cond
      (= ":tiles/num" opt) (num l)
      (nil? l) (num "nil")
      (string? l) (text l)
      (= :tiles/slot l) slot
      :else (num l))))

(defn shift-coords [nofblocks & coords]
  (->> (range 0 nofblocks)
       (mapv (fn [x] [0 (* 50 x)]))
       (concat coords)
       (mapv (fn [[x y]] [(+ x 10) (+ y 10)]))))

(defn p-gen [parser-fn]
  (fn [coords & blocks]
    (let [shifted (apply shift-coords (count blocks) coords)]
      (->> blocks
           (map-indexed (fn [idx blk]
                          (addcoords (gen (parser-fn blk)) (shifted idx))))
           (into [:xml])
           html))))

(def pg (p-gen exp))
(def rpg (p-gen parse))

(comment

  (= (num 2)
     (exp 2)
     (parse 2))
  (gen (num 2))
  (= (page (shift-coords 1 [0 0]) (num 2))
     (pg [[0 0]] (num 2))
     (pg [[0 0]] 2)
     (rpg [[0 0]] 2))

  (= (fun "hu" (num 2))
     (exp ["hu" (num 2)])
     (exp ["hu" 2])
     (parse '(hu 2)))
  (not= (exp ["hu" 2]) (exp (fun "hu" 2)))
  (gen (fun "hu" (num 2)))

  (= (page (shift-coords 1 [0 0]) (fun "hu" (num 2)))
     (pg [[0 0]] ["hu" 2])
     (rpg [[0 0]] '(hu 2)))

  (= (args (num 2) slot)
     (exp (args (num 2) slot))
     (parse [2 :tiles/slot]))

  (gen (t-map ["a" (text "v1")] ["b" (text "v2")] ["c" (text "v3")]) "id1")
  (gen (t-map [:a (text "v1")] [:b (text "v2")] [:c (text "v3")]) "id1")

  (html (into [:xml (gen (t-map-vert [:a (text "v1")] [:b (text "v2")] [:c (text "v3")]) "id1")]))

  )

