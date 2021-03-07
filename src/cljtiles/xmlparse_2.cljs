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
        augment-arg (fn [ee]
                      (let [e (if (list? ee) (last ee) ee)]
                        (if (and (symbol? e) (not= "[" (first (str e))))
                          [ee]
                          ee)))
        modi-erg ;; modify (defn x x) -> (defn [x] x)
        (cond
          (and (= 'defn (first erg)) true (> (count erg) 2))
          (apply list 'defn (nth erg 1) (augment-arg (nth erg 2))
                 (drop 3 erg))
          (and (= 'fn (first erg)) true (> (count erg) 1))
          (apply list 'fn (augment-arg (nth erg 1))
                 (drop 2 erg))
          :else erg)]
    (->> modi-erg
         (inspect attributes id))))

(defmethod type-m "num_" [attributes content id]
  (->> (symbol (first (:content (first content))))
       (inspect attributes id)))

(defmethod type-m "infi" [{:keys [id] :as attributes} content select-id]
  (->> (type-m {:type "funs" :id id} content select-id)
       (inspect attributes id)))

(defmethod type-m "args" [attributes content id]
  (->> (mapv #(tag-m % id) content)
       (inspect attributes id)))

(defmethod type-m "list" [attributes content id]
  (->> (map #(tag-m % id) content)
       (inspect attributes id)))

(defn to-hashmap [v]
  (let [c (/ (count v) 2)]
    (into {} (map (fn [k v] [(symbol k) v])
                  (take c v)
                  (take-last c v)))))

(defmethod type-m "map-" [attributes content id]
  (->> (to-hashmap (map #(tag-m % id) content))
       (inspect attributes id)))

(defn parse [edn inspect-id]
  (tag-m edn inspect-id))
