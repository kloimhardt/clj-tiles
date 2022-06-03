(ns cljtiles.genblocks
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]))

(defmulti gen (fn [m _] (:type m)))

(defmethod gen :slot [] nil)

(defn blockmap [type givenid & [inline?]]
  (cond-> {:type type :id (str type givenid)}
    (some? inline?) (assoc :inline (str inline?))))

(defmethod gen :num [{:keys [nummer]} givenid]
  [:block (blockmap "num" givenid)
   [:field {:name "nummer"} nummer]])

(defmethod gen :text [{:keys [dertext]} givenid]
  [:block (blockmap "text" givenid)
   [:field {:name "dertext"} dertext]])

;; is really a vector
(defmethod gen :args [{:keys [argsvec inline?]} givenid]
  (let [xml-block-type (str "args-" (count argsvec))
        {:keys [id] :as bm} (blockmap xml-block-type givenid inline?)]
    (into [:block bm]
          (map-indexed (fn [idx v]
                         [:value {:name (str "arg_" (+ idx 1))}
                          (gen v (str (+ idx 1) "-" id))]) argsvec))))

(defmethod gen :list [{:keys [argsvec inline?]} givenid]
  (let [xml-block-type (str "list-h-" (count argsvec))
        {:keys [id] :as bm} (blockmap xml-block-type givenid inline?)]
    (into [:block bm]
          (map-indexed (fn [idx v]
                         [:value {:name (str "args-" (+ idx 1))}
                          (gen v (str (+ idx 1) "-" id))]) argsvec))))


(defmethod gen :fun [{:keys [kopf argsvec subtype inline?]} givenid]
  (let [xml-block-type (str subtype "-" (inc (count argsvec)) "-inp")
        {:keys [id] :as bm} (blockmap xml-block-type givenid inline?)]
    (into [:block bm
           [:field {:name "kopf"} kopf]]
          (map-indexed (fn [idx v]
                         [:value {:name (str "args-" (+ idx 2))}
                          (gen v (str (+ idx 2) "-" id))]) argsvec))))

(defn rearrange [m]
  (let [a (partition 2 m)]
    (concat (map first a) (map second a))))

(defmethod gen :map [{:keys [argsvec subtype inline?]} givenid]
  (let [xml-block-type (str subtype "-" (* (count argsvec) 2) "-inp")
        {:keys [id] :as bm} (blockmap xml-block-type givenid inline?)]
    (into [:block bm]
          (rearrange ;;(rearrange [1 2 3 4]) => (1 3 2 4)
            ;;the Blockly ui does this rearrangemnt to the XML and
            ;;the end->code/parse depends on it
            ;;namely that in the XML all the :field are given first and
            ;;then the :value
            (apply concat
                   (map-indexed (fn [idx v]
                                  (let [i (inc (* idx 2))]
                                    [[:field {:name (str "key-" i)}
                                      (str (first v))]
                                     [:value {:name (str "val-" (inc i))}
                                      (gen (second v) (str (inc i) "-" id))]]))
                                argsvec))))))


(defn addcoords [block [x y]]
  (update block 1 #(-> %
                       (assoc :x x)
                       (assoc :y y))))

(defn gen-str [[x y]]
  (str "-" x "-" y))

(defn page [coords & blocks]
  (->> blocks
       (map-indexed (fn [idx blk] (addcoords (gen blk (gen-str (coords idx)))
                                             (coords idx))))
       (into [:xml])
       html))

(def slot {:type :slot})

(defn num [nummer]
  {:type :num :nummer nummer})

(defn kw [k]
  {:type :num :nummer (str k)})

(defn text [txt]
  {:type :text :dertext txt})

(defn tiles-deref [e]
  {:type :num :nummer (str "@" e)})

(defn fun [name & argsvec]
  {:type :fun :subtype "funs-h" :kopf name :argsvec argsvec})

(defn fun-infi [name & argsvec]
  (assoc (apply fun name argsvec) :subtype "infi-h"))

(defn args [& argsvec]
  {:type :args :argsvec argsvec})

(defn lst [& argsvec]
  {:type :list :argsvec argsvec})

(defn t-map [& argsvec]
  (if (> (count argsvec) 1)
    {:type :map :subtype "map-h" :argsvec argsvec}
    (text "clj-tiles error: one-entry map not allowed")))

(defn chapter [& pages] (into [] pages))

(defn exp [v]
  (if (vector? v)
    (let [erst (first v)
          appl (fn [fuct] (apply fuct erst (map exp (into [] (rest v)))))]
      (cond
        (and (= (count v) 3) (#{"/" "+" "*" "-"} erst)) (appl fun-infi)
        (#{"def" "defn" "do"} erst) (assoc (appl fun) :inline? false)
        :else (appl fun)))
    (cond
      (map? v) v
      (nil? v) (num "nil")
      (string? v) (text v)
      :else (num v))))

(defn infix-sensible? [lst]
  (when (and (list? lst) (= (count lst) 3))
    (let [[f s t] lst]
      (and  (#{"/" "+" "*" "-"} (str f))
            (or (symbol? s) (number? s) (infix-sensible? s))
            (or (symbol? t) (number? t) (infix-sensible? t))))))

(defn parse [l]
  (cond
    (list? l)
    (let [erst (str (first l))
          appl (fn [fuct] (apply fuct erst (map parse (rest l))))]
      (cond
        (or (list? (first l)) (= ":tiles/slot" erst)) (apply lst (map parse l))
        (= ":tiles/vert" erst) (assoc (parse (second l)) :inline? false)
        (= ":tiles/num" erst) (num (second l))
        (= "clojure.core/deref" erst) (tiles-deref (second l))
        (= "quote" erst) (num (str "'" (second l)))
        ;;(infix-sensible? l) (appl fun-infi) ;;detect infix automatically for simple expressions
        (= ":tiles/infix" erst) (assoc (parse (second l)) :subtype "infi-h")
        (#{"def" "defn" "do"} erst) (assoc (appl fun) :inline? false)
        :else (appl fun)))
    (vector? l)
    (if (empty? l)
      (num "[ ]")
      (apply args (map parse l)))
    (map? l)
    (cond
      (empty? l)
      (num "{ }")
      (:tiles/numslot l)
      slot
      :else
      (apply t-map (map (fn [[k v]] [k (parse v)]) l)))
    (and (set? l) (empty? l))
    (num "#{ }")
    (= :tiles/slot l) slot
    (nil? l) (num "nil")
    (string? l) (text l)
    (keyword? l) (kw l)
    :else (num l)))

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
                          (addcoords (gen (parser-fn blk) (gen-str (shifted idx)))
                                     (shifted idx))))
           (into [:xml])
           html))))

(def pg (p-gen exp))
(def rpg (p-gen parse))

(comment

  (= (num 2)
     (exp 2)
     (parse 2))

  (gen (num 2))
  (gen (kw :a))
  (gen (text "hi"))

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

  (html (gen (parse '(def x 3))))
  )

