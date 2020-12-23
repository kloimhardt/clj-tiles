(ns cljtiles.genblocks
  (:require-macros [hiccups.core :as hiccups :refer [html]])
  (:require [hiccups.runtime :as hiccupsrt]))

(defmulti gen (fn [m _] (:type m)))

(defmethod gen :slot [] nil)

(defn blockmap [type givenid]
   {:type type :id (str type givenid)})

(defmethod gen :num [{:keys [nummer]} givenid]
  [:block (blockmap "num" givenid)
   [:field {:name "nummer"} nummer]])

;; is really a vector
(defmethod gen :args [{:keys [argsvec]} givenid]
  (let [xml-block-type (str "args-" (count argsvec))
        {:keys [id] :as bm} (blockmap xml-block-type givenid)]
    (into [:block bm]
          (map-indexed (fn [idx v]
                         [:value {:name (str "arg_" (+ idx 1))}
                          (gen v (str (+ idx 1) "-" id))]) argsvec))))

(defmethod gen :fun [{:keys [kopf argsvec subtype]} givenid]
  (let [xml-block-type (str subtype "-" (inc (count argsvec)) "-inp")
        {:keys [id] :as bm} (blockmap xml-block-type givenid)]
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
  {::intern true :type :num :nummer (str "\"" txt "\"")})

(defn num [nummer]
  {::intern true :type :num :nummer nummer})

(defn fun [name & argsvec]
  {::intern true :type :fun :subtype "funs-h" :kopf name :argsvec argsvec})

(defn fun-inli [name & argsvec]
  (assoc (apply fun name argsvec) :subtype "inli-h"))

(defn fun-vert [name & argsvec]
  (assoc (apply fun name argsvec) :subtype "funs-v"))

(def slot {::intern true :type :slot})

(defn args [& argsvec]
  {::intern true :type :args :argsvec argsvec})

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

(defn parse [l]
  (if (list? l)
    (let [erst (str (first l))
          appl (fn [fuct] (apply fuct erst (map exp (rest l))))]
      (cond
        (and (= (count l) 3) (#{"/" "+" "*" "-"} erst)) (appl fun-inli)
        (#{"def" "defn" "do"} erst) (appl fun-vert)
        :else (appl fun)))
    (cond
      (and (map? l) (::intern l)) l
      (nil? l) (num "nil")
      (string? l) (text l)
      :else (num l))))

(comment

(parse '(hi 3))

  )

(defn shift-coords [nofblocks & coords]
  (->> (range 0 nofblocks)
       (mapv (fn [x] [0 (* 50 x)]))
       (concat coords)
       (mapv (fn [[x y]] [(+ x 10) (+ y 10)]))))

(defn pg [coords & blocks]
  (let [shifted (apply shift-coords (count blocks) coords)]
    (->> blocks
         (map-indexed (fn [idx blk]
                        (addcoords (gen (exp blk)) (shifted idx))))
         (into [:xml])
         html)))

(comment

  (gen (num 2))
  (= (num 2)
     (exp 2))
  (= (page (shift-coords [0 0]) (num 2))
     (pg [[0 0]] (num 2))
     (pg [[0 0]] 2))

  (gen (fun "hu" (num 2)))
  (= (fun "hu" (num 2))
     (exp ["hu" 2]))
  (not= (exp ["hu" 2]) (exp (fun "hu" 2)))
  (= (page (coords [0 0]) (fun "hu" (num 2)))
     (pg [[0 0]] ["hu" 2]))

  (gen (args (num 2)))

  )
