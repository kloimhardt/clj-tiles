(ns cljtiles.org.four)

;; 4Cl #57 implement comp

(defn my-comp [& functions]
  (reduce (fn [f g]
            (fn [& args]
              (f (apply g args))))
          functions))

(= (inc (inc 0))
   ((my-comp inc inc) 0))

(defn my-group-by [f xs]
  (reduce (fn [x y] (update x (f y) (fnil conj []) y)) {} xs))

(my-group-by (fn [x] (> x 5)) [1 3 6 8])

(defn powerset [s]
  (reduce
   (fn [r e]
     (into r (map (fn [x] (conj x e)) r)))
   (hash-set #{})
   s))

(powerset (hash-set 1 2 3))



(defn pronounce [s]
  (mapcat (juxt count first)
          (partition-by identity s)))

(comment
  (pronounce [1]) ;; => (1 1)
  (pronounce [1 1]) ;; => (2 1)
  (pronounce [2 1]) ;; => (1 2 1 1)
  )

