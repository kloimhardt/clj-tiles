(ns cljtiles.xmlparse-2)

(defmulti tag-m (fn [x _id] (:tag x)))

(defmulti type-m (fn [attributes _content _id]
                   (subs (str (:type attributes) "____") 0 4)))

(defmethod tag-m :default [x _id] x)

(defmethod tag-m :xml [{:keys [content]} id]
  (mapv #(tag-m % id) content))

(defmethod tag-m :block [{:keys [attributes content]} id]
  (type-m attributes content id))

(defmethod tag-m :value [{:keys [content]} id]
  (tag-m (first content) id))

(defmethod tag-m :field [{:keys [content]} _id]
  (first content))

(defn inspect [attributes {:keys [id fun]} expre]
  (if (= id (:id attributes))
    (fun expre) expre))

(defmethod type-m :default [_ content _id] content)

(defmethod type-m "text" [attributes content id]
  (->> (or (first (:content (first content))) " ")
       (inspect attributes id)))

(defmethod type-m "funs" [attributes content id]
  (let [erg
        (apply list (symbol (first (:content (first content))))
               (map #(tag-m % id) (rest content)))
        augment-arg (fn [e] (if (and (symbol? e) (not= "[" (first (str e)))) [e] e))
        modi-erg ;; modify (defn x x) -> (defn [x] x)
        (if (and (= 'defn (first erg)) true (> (count erg) 2))
          (apply list 'defn (nth erg 1) (augment-arg (nth erg 2))
                 (drop 3 erg))
          erg)]
    (->> modi-erg
         #_(inspect attributes id))
    ))

(defmethod type-m "num_" [attributes content id]
  (->> (symbol (first (:content (first content))))
       (inspect attributes id)))

(defmethod type-m "infi" [attributes content id]
  (type-m {:type "funs"} content id))

(defmethod type-m "args" [attributes content id]
  (mapv #(tag-m % id) content))

(defmethod type-m "list" [attributes content id]
  (map #(tag-m % id) content))

(defn to-hashmap [v]
  (let [c (/ (count v) 2)]
    (into {} (map (fn [k v] [(symbol k) v])
                  (take c v)
                  (take-last c v)))))

(defmethod type-m "map-" [attributes content id]
  (to-hashmap (map #(tag-m % id) content)))

(defn parse [edn inspect-id]
  (tag-m edn inspect-id))
