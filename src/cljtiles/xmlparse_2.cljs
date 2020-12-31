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

(defmethod type-m :default [_ content _id] content)

(defmethod type-m "text" [_ content _id]
  (or (first (:content (first content))) " "))

(defmethod type-m "funs" [_ content id]
  (let [erg
        (apply list (symbol (first (:content (first content))))
               (map #(tag-m % id) (rest content)))
        augment-arg (fn [e] (if (vector? e) e [e]))]
    (if (= 'defn (first erg))
      (apply list 'defn (nth erg 1) (augment-arg (nth erg 2))
             (drop 3 erg))
      erg)))

(defmethod type-m "num_" [attributes content id]
  (def u [attributes content id])
  (let [erg (symbol (first (:content (first content))))]
    (if (= id (:id attributes))
      (list 'do (list 'println "inspect" erg) erg)
      erg)))

(defmethod type-m "infi" [_ content id]
  (type-m {:type "funs"} content id))

(defmethod type-m "args" [_ content id]
  (mapv #(tag-m % id) content))

(defmethod type-m "list" [_ content id]
  (map #(tag-m % id) content))

(defn to-hashmap [v]
  (let [c (/ (count v) 2)]
    (into {} (map (fn [k v] [(symbol k) v])
                  (take c v)
                  (take-last c v)))))

(defmethod type-m "map-" [_ content id]
  (to-hashmap (map #(tag-m % id) content)))

(defn parse [edn inspect-id]
  (tag-m edn inspect-id))
